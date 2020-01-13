package cn.trawe.etc.hunanfront.rocketmq.listener;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.enums.AuditType;
import cn.trawe.etc.hunanfront.enums.CardType;
import cn.trawe.etc.hunanfront.enums.OrderOpType;
import cn.trawe.etc.hunanfront.feign.ApiClient;
import cn.trawe.etc.hunanfront.request.ApplyOrderSyncReq;
import cn.trawe.etc.hunanfront.service.ApplyHunanSyncService;
import cn.trawe.etc.hunanfront.service.ThirdOrderSycnRecordService;
import cn.trawe.etc.hunanfront.service.ThirdPartnerService;
import cn.trawe.pay.common.enums.PlateColor;
import cn.trawe.pay.common.etcmsg.EtcResponse;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.enums.ChannelType;
import cn.trawe.pay.expose.request.SaveOrUpdateDrivingLicenseReq;
import cn.trawe.pay.expose.request.SaveOrUpdateIdCardReq;
import cn.trawe.pay.expose.request.UserReq;
import cn.trawe.pay.expose.request.issue.IssueOrderSubmitReq;
import cn.trawe.pay.expose.request.issue.ThirdOutOrderQueryReq;
import cn.trawe.pay.expose.request.issue.ThirdOutOrderSaveReq;
import cn.trawe.pay.expose.response.IdRes;
import cn.trawe.pay.expose.response.issue.IssueOrderSubmitResp;
import cn.trawe.pay.expose.response.issue.IssueSaveResp;
import cn.trawe.pay.expose.response.issue.ThirdOutOrderQueryResp;
import cn.trawe.pay.expose.response.issue.ThirdOutOrderSaveResp;
import cn.trawe.util.LogUtil;
import cn.trawe.utils.DateUtils;
import cn.trawe.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Jiang Guangxing
 */
@Slf4j
//@Component
public class ApplyOrderSyncListener implements MessageListener,InitializingBean {
    @Value("${api.token}")
    private String token;
    @Autowired
    ThirdPartnerService ThirdPartnerService;

    @Override
    @Transactional
    public Action consume(Message message, ConsumeContext consumeContext) {
        String body = new String(message.getBody());
        LogUtil.info(log, message.getMsgID(), "收到申请单同步消息", body);
        ApplyOrderSyncReq applyOrderSyncReq = JSON.parseObject(body, ApplyOrderSyncReq.class);
        try {
            if (!(String.valueOf(OrderOpType.SUBMIT.ordinal()).equals(applyOrderSyncReq.getOpType()))) {
                LogUtil.warn(log, applyOrderSyncReq.getOrderId(), "不支持的申请单同步操作", applyOrderSyncReq.getOpType());
                return Action.CommitMessage;
            }
            //提交订单
            //优化V1.1 如果存在该订单号映射关系 如果存在订单号并且 订单状态为2
            this.saveOrUpdateOrderInfo(applyOrderSyncReq);
            //recordService.valid(applyOrderSyncReq.getRecordId());
        } catch (Exception e) {
            LogUtil.error(log, message.getMsgID(), "申请单同步消息处理失败", e);
            //this.orderRejectStatusSync(applyOrderSyncReq, e.getMessage());
            //recordService.addRetryTimes(applyOrderSyncReq.getRecordId());
            return Action.CommitMessage;
        }
        return Action.CommitMessage;
    }

   //基础服务现在不支持更新
    public void saveOrUpdateOrderInfo(ApplyOrderSyncReq applyOrderSyncReq) {
        //保存用户信息
        long uid = this.saveOrUpdateUser(applyOrderSyncReq);
        //保存订单
        String orderNo = null;
        orderNo = this.saveOrUpdateOrder(applyOrderSyncReq, 2, orderNo);
        if (ValidateUtil.isEmpty(orderNo))
            return;
        //保存订单映射
        this.saveThirdOutOrder(orderNo, applyOrderSyncReq.getOrderId(),Long.valueOf(applyOrderSyncReq.getChannelNo()));
        //保存身份证信息
        this.saveOrUpdateIdCard(applyOrderSyncReq, uid, orderNo);
        //保存行驶证信息
        this.saveOrUpdateDrivingLicense(applyOrderSyncReq, uid, orderNo);
        this.transferImages(applyOrderSyncReq.getOrderId(), orderNo);
        //提交订单给业主
        boolean firstSubmit = true;
        this.submitOrder(applyOrderSyncReq.getOrderId(), orderNo, "4301", firstSubmit);
    }
    
    //orderId 第三方订单号  orderNo 系统订单号
    private void transferImages(String orderId, String orderNo) {
    	cn.trawe.pay.expose.request.TransferImagesReq req = new cn.trawe.pay.expose.request.TransferImagesReq();
        req.setOrderId(orderId);
        req.setOrderNo(orderNo);
        LogUtil.info(log, orderNo, "照片信息转移请求", req);
        EtcResponse res = apiClient.transferImages(req, token);
        LogUtil.info(log, orderNo, "照片信息转移响应", res);
        if (res == null)
            throw new RuntimeException("照片信息转移响应失败,res为空");
        if (res.getCode() != 0)
            throw new RuntimeException("照片信息转移响应失败,code不为0");
    }

    private long saveOrUpdateUser(ApplyOrderSyncReq applyOrderSyncReq) {
        UserReq req = new UserReq();
        req.setChannelType(ChannelType.ALIPAY);
        req.setRealName(applyOrderSyncReq.getViOwnerInfo().getViOwnerName());
        req.setCertNo(applyOrderSyncReq.getViOwnerInfo().getViOwnerCertNo());
        LogUtil.info(log, applyOrderSyncReq.getOrderId(), "保存用户信息请求", req);
        IdRes res = apiClient.saveOrUpdateUser(req, applyOrderSyncReq.getBuyerUid(), token);
        LogUtil.info(log, applyOrderSyncReq.getOrderId(), "保存用户信息响应", res);
        if (res == null)
            throw new RuntimeException("保存用户信息响应失败,res为空");
        if (res.getCode() != 0)
            throw new RuntimeException("保存用户信息响应失败,code不为0");
        return res.getId();
    }

    private void saveOrUpdateIdCard(ApplyOrderSyncReq applyOrderSyncReq, long uid, String orderNo) {
        SaveOrUpdateIdCardReq req = new SaveOrUpdateIdCardReq();
        req.setName(applyOrderSyncReq.getViOwnerInfo().getViOwnerName());
        req.setIdCardNo(applyOrderSyncReq.getViOwnerInfo().getViOwnerCertNo());
        req.setChannelUserId(uid);
        req.setOrderNo(orderNo);
        LogUtil.info(log, orderNo, "保存身份证信息请求", req);
        IdRes res = apiClient.saveOrUpdateIdCard(req, token);
        LogUtil.info(log, orderNo, "保存身份证信息响应", res);
        if (res == null)
            throw new RuntimeException("保存身份证信息响应失败,res为空");
        if (res.getCode() != 0)
            throw new RuntimeException("保存身份证信息响应失败,code不为0");
    }

    private void saveOrUpdateDrivingLicense(ApplyOrderSyncReq applyOrderSyncReq, long uid, String orderNo) {
        ApplyOrderSyncReq.ViInfo viInfo = applyOrderSyncReq.getViInfo();
        SaveOrUpdateDrivingLicenseReq req = new SaveOrUpdateDrivingLicenseReq();
        req.setChannelUserId(uid);
        req.setOrderNo(orderNo);
        req.setEngineNo(viInfo.getEngineNo());
        req.setAddr(viInfo.getViOwnerAddress());
        req.setApprovedLoad(null);//暂时没传
        req.setEnergyType(null);//暂时没传
        req.setFileNo(viInfo.getViLicenseNo());
        if(StringUtils.isBlank(viInfo.getViTotalMass())) {
      	  req.setGrossMass("1900");
        }
        else {
      	  req.setGrossMass(viInfo.getViTotalMass());
        }
        req.setInspectionRecord(viInfo.getViInspectionRecord());
        req.setIssueDate(viInfo.getViGrantTime());
        req.setModel(viInfo.getViModelName());
        req.setOrderPlateNo(viInfo.getViNumber());
        if (ValidateUtil.isNotEmpty(viInfo.getViLength()) && ValidateUtil.isNotEmpty(viInfo.getViWidth()) && ValidateUtil.isNotEmpty(viInfo.getViHeight())) {
      	  req.setOverallDimension(viInfo.getViLength() + "X" + viInfo.getViWidth() + "X" + viInfo.getViHeight());
        }
        else {
      	  req.setOverallDimension("1920" + "X" + "1120" + "X" + "1390");
        }
        req.setOwner(viInfo.getViOwnerName());
        req.setPlateNo(viInfo.getViNumber());
        req.setRegisterDate(viInfo.getViStartTime());
        if(StringUtils.isBlank(viInfo.getViTractionMass())) {
        	  req.setTractionMass("1900");
          }
          else {
        	  req.setTractionMass(viInfo.getViTractionMass());
          }
        if(StringUtils.isBlank(viInfo.getViReadinessMass())) {
      	  req.setUnladenMass("1900");
        }
        else {
      	  req.setUnladenMass(viInfo.getViReadinessMass());
        }
        req.setVehicleType(viInfo.getViType());
        req.setVehicleUseCharacter(viInfo.getViUseType());
        req.setVin(viInfo.getViVin());
        try {
            req.setPlateColor(PlateColor.values()[Integer.valueOf(viInfo.getViPlateColor())]);
        } catch (NumberFormatException e) {
            LogUtil.warn(log, applyOrderSyncReq.getOrderId(), "车牌颜色转换失败", viInfo.getViPlateColor());
        }
        try {
            req.setSeats(Integer.valueOf(viInfo.getViAc()));
        } catch (Exception e) {
            LogUtil.warn(log, applyOrderSyncReq.getOrderId(), "座位数转换失败", viInfo.getViAc());
        }
        LogUtil.info(log, applyOrderSyncReq.getOrderId(), "保存行驶证信息请求", req);
        IdRes res = apiClient.saveOrUpdateDrivingLicense(req, token);
        LogUtil.info(log, applyOrderSyncReq.getOrderId(), "保存行驶证信息响应", res);
        if (res == null)
            throw new RuntimeException("保存行驶证信息响应失败,res为空");
        if (res.getCode() != 0)
            throw new RuntimeException("保存行驶证信息响应失败,code不为0");
    }

    private String saveOrUpdateOrder(ApplyOrderSyncReq syncReq, Integer oderStatus, String orderNo) {
      String hrkey = "note1";
  	  String hrpartnerId="partner_id";
  	  //查询渠道用户名和密码
  	    ThirdPartner partner = ThirdPartnerService.getPartnerByChannelNo(syncReq.getChannelNo());
        JSONObject order = new JSONObject();
        //渠道ID  
        //install_type 5：自提，6：快递
        order.put("alipay_user_id",syncReq.getBuyerUid());
        if("1".equals(syncReq.getInstallStatus())) {
        	order.put("install_type","5");
        	//处理银行外拓用户名和密码
      	    JSONObject object = new JSONObject();
        	object.put("Account", syncReq.getAccountNo());
        	object.put("Password", syncReq.getPassword());
        	order.put(hrkey, object.toJSONString());
        	order.put(hrpartnerId, syncReq.getChannelNo());
        }
        else if("2".equals(syncReq.getInstallStatus())){
        	order.put("install_type","6");
        	String accountNo = partner.getAccountNo();
        	String password = partner.getPassword();
      	    JSONObject object = new JSONObject();
        	object.put("Account", accountNo);
        	object.put("Password", password);
        	order.put(hrkey, object.toJSONString());
        	order.put(hrpartnerId, syncReq.getChannelNo());
        }
        else if("3".equals(syncReq.getInstallStatus())) {
        	order.put("install_type","7");
        	String accountNo = partner.getAccountNo();
        	String password = partner.getPassword();
      	    JSONObject object = new JSONObject();
        	object.put("Account", accountNo);
        	object.put("Password", password);
        	order.put(hrkey, object.toJSONString());
        	order.put(hrpartnerId, syncReq.getChannelNo());
        }
        else {
        	order.put("install_type","6");
        	String accountNo = partner.getAccountNo();
        	String password = partner.getPassword();
      	    JSONObject object = new JSONObject();
      	    
        	object.put("Account", accountNo);
        	object.put("Password", password);
        	order.put(hrkey, object.toJSONString());
        	order.put(hrpartnerId, syncReq.getChannelNo());
        }
        order.put("org_code", syncReq.getChannelNo());
        order.put("owner_code", "4301");
        order.put("plate_no", syncReq.getViInfo().getViNumber());
        order.put("plate_color", syncReq.getViInfo().getViPlateColor());
        order.put("card_type", CardType.warp("1"));
        ApplyOrderSyncReq.DeliveryInfo deliveryInfo = syncReq.getDeliveryInfo();
        order.put("receiver_name", deliveryInfo.getContactName());
        order.put("receiver_phone", deliveryInfo.getContactTel());
        //修改收货地址增加省市区
        String address = deliveryInfo.getProvinceName()+deliveryInfo.getCityName()+deliveryInfo.getDistrictName();
        order.put("receiver_address", address+deliveryInfo.getAddress());
        order.put("pay_channel", ChannelType.ALIPAY.ordinal());
        order.put("total_fee", syncReq.getTotalAmount());
        order.put("obu_fee", syncReq.getDeviceAmount());
        order.put("service_fee", syncReq.getServiceAmount());
        order.put("pd_fee", syncReq.getDeliveryAmount());
        order.put("province_id", "43");
        //渠道接入默认支付成功
        order.put("order_status", "2");
        if(StringUtils.isBlank(syncReq.getPhoneNumber())) {
        	order.put("note3",deliveryInfo.getContactTel() );
        }
        else {
        	order.put("note3",syncReq.getPhoneNumber());
        }
        LogUtil.info(log, syncReq.getOrderId(), "保存订单信息请求", order);
        IssueSaveResp res = apiClient.saveOrder(order, syncReq.getBuyerUid(), token);
        LogUtil.info(log, syncReq.getOrderId(), "保存订单信息响应", res);
        if (res == null)
            throw new RuntimeException("保存订单信息响应失败,res为空");
        if (res.getCode() != 0) {
        	throw new RuntimeException("保存订单信息响应失败,res!=0");
           
        }
        return res.getOrderNo();
    }
    
    private void orderRejectStatusSync(ApplyOrderSyncReq syncReq, String msg) {
        JSONObject bizContent = new JSONObject();
        bizContent.put("order_status", "2");
        if (ValidateUtil.isNotEmpty(msg))
            bizContent.put("censor_info", msg);
        bizContent.put("order_id", syncReq.getOrderId());
        bizContent.put("order_update_time", DateUtils.format(new Date(), DateUtils.YYYY_MM_DD_HH_MM_SS));
        try {
        	ThirdPartner thirdPartner = ThirdPartnerService.getPartnerByChannelNo(syncReq.getChannelNo());
			applySyncService.invoke(bizContent,thirdPartner);
			
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e.fillInStackTrace());
		}
    }
     
    //现在不支持发票功能
//    private void saveOrUpdateInvoice(ApplyOrderSyncReq syncReq, String orderNo) {
//        ApplyOrderSyncReq.InvoiceInfo invoiceInfo = syncReq.getInvoiceInfo();
//        if (ValidateUtil.isEmpty(invoiceInfo.getNeedInvoice()) || !invoiceInfo.getNeedInvoice().equals("1"))
//            return;
//        EtcUserInvoice req = new EtcUserInvoice();
//        req.setOrderNo(orderNo);
//        req.setAlipayUserId(syncReq.getBuyerUid());
//        req.setInvoiceType(Integer.valueOf(invoiceInfo.getInvoiceTitleType()));
//        req.setInvoiceName(invoiceInfo.getInvoiceTitle());
//        req.setDutyNumber(invoiceInfo.getDutyNo());
//        req.setEmail(invoiceInfo.getEmail());
//
//        LogUtil.info(log, syncReq.getOrderId(), "保存发票信息请求", req);
//        GlobalResponse<NullResp> res = apiClient.saveInvoice(req, syncReq.getBuyerUid(), token);
//        LogUtil.info(log, syncReq.getOrderId(), "保存发票信息响应", res);
//        if (res == null)
//            throw new RuntimeException("保存发票信息响应失败,res为空");
//        if (res.getCode() != 0)
//            throw new RuntimeException("保存发票信息响应失败,code不为0");
//    }

    private void saveThirdOutOrder(String orderNo, String orderId,Long channelId) {
    	
    	//关联渠道ID
        ThirdOutOrderSaveReq req = new ThirdOutOrderSaveReq();
        //网发平台订单号
        req.setOrderNo(orderNo);
        //银行渠道的订单号
        req.setOutOrderId(orderId);
        req.setOutType(1);
        req.setThirdId(channelId);
        //req.setBankCode(channelId.toString());
        LogUtil.info(log, orderId, "保存订单映射信息请求", req);
        ThirdOutOrderSaveResp res = apiClient.saveThirdOutOrder(req, token);
        LogUtil.info(log, orderId, "保存订单映射信息响应", res);
        if (res == null)
            throw new RuntimeException("保存订单映射信息响应失败,res为空");
        if (res.getCode() != 0)
            throw new RuntimeException("保存订单映射信息响应失败,code不为0");
    }

    private void submitOrder(String orderId, String orderNo, String sellerId, boolean firstSubmit) {
        IssueOrderSubmitReq req = new IssueOrderSubmitReq();
        req.setOrderNo(orderNo);
        req.setAuditType(AuditType.getAuditType(sellerId, firstSubmit));
        LogUtil.info(log, orderNo, "提交订单信息请求", req);
        IssueOrderSubmitResp res = apiClient.orderSubmit(req, token);
        LogUtil.info(log, orderNo, "提交订单信息响应", res);
        if (res == null)
            throw new RuntimeException("提交订单信息响应失败,res为空");
        if (res.getCode() != 0)
        	throw new RuntimeException("提交订单信息响应失败,code !=0");
            //this.orderRejectStatusSync(orderId, res.getMsg());
    }

//    private ImageClient.UploadImages uploadImages(String orderId) {
//        ImageClient.UploadImagesReq req = new ImageClient.UploadImagesReq();
//        req.setOrderId(orderId);
//        LogUtil.info(log, orderId, "照片上传请求", req);
//        EtcObjectResponse<ImageClient.UploadImages> res = imageClient.uploadImages(req);
//        LogUtil.info(log, orderId, "照片上传响应", res);
//        if (res == null)
//            throw new RuntimeException("照片上传响应失败,res为空");
//        if (res.getCode() != 0)
//            throw new RuntimeException("照片上传响应失败,code不为0");
//        return res.getData();
//    }
    
    
     //支付宝保存图片接口湖南不使用
//    private void saveOrUpdateImages(ImageClient.UploadImages images, String orderNo, String orderId) {
//        SaveOrUpdateImagesReq req = new SaveOrUpdateImagesReq();
//        req.setOrderNo(orderNo);
//        List<SaveOrUpdateImagesReq.Images> saveImages = new ArrayList<>();
//        for (ImageClient.UploadImages.Image img : images.getImages()) {
//            SaveOrUpdateImagesReq.Images image = new SaveOrUpdateImagesReq.Images();
//            image.setBizType(UploadFileReq.BizType.values()[img.getBizType()]);
//            image.setSavePath(img.getSavePath());
//            image.setExtType(img.getMediaType());
//            image.setSaveType(img.getMediaType());
//            image.setSize(img.getImageSize());
//            saveImages.add(image);
//        }
//        req.setImages(saveImages);
//        LogUtil.info(log, orderId, "保存图片请求", req);
//        EtcResponse res = apiClient.saveOrUpdateImages(req, token);
//        LogUtil.info(log, orderId, "保存图片响应", res);
//        if (res == null)
//            throw new RuntimeException("保存图片响应失败,res为空");
//        if (res.getCode() != 0)
//            throw new RuntimeException("保存图片响应失败,code不为0");
//    }

//    private void orderRejectStatusSync(String orderId, String msg) {
//        JSONObject bizContent = new JSONObject();
//        bizContent.put("order_status", OrderStatus.REJECT.ordinal());
//        if (ValidateUtil.isNotEmpty(msg))
//            bizContent.put("censor_info", msg);
//        bizContent.put("order_id", orderId);
//        bizContent.put("order_update_time", DateUtils.format(new Date(), DateUtils.YYYY_MM_DD_HH_MM_SS));
//        applySyncService.invoke(bizContent);
//    }

    private EtcIssueOrder getOrderByOrderId(String orderId) {
        //查询订单映射信息
        ThirdOutOrderQueryReq req = new ThirdOutOrderQueryReq();
        req.setOutOrderId(orderId);
        ThirdOutOrderQueryResp thirdOutOrderQueryResp = applySyncService.queryThirdOutOrder(req);
        if (ValidateUtil.isEmpty(thirdOutOrderQueryResp.getOrderNo()))
            return null;
        //查询订单
        return applySyncService.orderQuery(thirdOutOrderQueryResp.getOrderNo());
    }
//
//    private void thirdSignSave(ApplyOrderSyncReq syncReq, String orderNo) {
//        ThirdSignSaveReq req = new ThirdSignSaveReq();
//        req.setAgreementNo(syncReq.getAgreementNo());
//        req.setOrderNo(orderNo);
//        req.setPartnerId(syncReq.getBuyerUid());
//        req.setStatus(Status.VALID.ordinal());
//        LogUtil.info(log, syncReq.getOrderId(), "保存协议信息请求", req);
//        cn.trawe.pay.expose.response.BaseResponse res = apiClient.thirdSignSave(req, token);
//        LogUtil.info(log, syncReq.getOrderId(), "保存协议信息响应", res);
//        if (res == null)
//            throw new RuntimeException("保存协议信息响应失败,res为空");
//        if (res.getCode() != 0)
//            throw new RuntimeException("保存协议信息响应失败,code不为0");
//    }

    @Autowired
    private ApiClient apiClient;
    @Lazy
    @Autowired
    private ApplyHunanSyncService applySyncService;
    @Autowired
    private ThirdOrderSycnRecordService recordService;
    //@Autowired
    //private ImageClient imageClient;
    
//    @Autowired
//    MqConfigLoader configLoader;
    @Override
    public void afterPropertiesSet() {        
//    	configLoader.loadConfig();
//        RocketmqManager.consume("alipay", "hunan-netpub-ordersync", this);
    }

    
    
}
