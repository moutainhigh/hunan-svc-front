//package cn.trawe.etc.hunanfront.rocketmq.listener;
//
//import cn.trawe.etc.hunanfront.dao.ThirdOrderSycnRecordDao;
//import cn.trawe.etc.hunanfront.enums.AuditType;
//import cn.trawe.etc.hunanfront.enums.CardType;
//import cn.trawe.etc.hunanfront.enums.OrderOpType;
//import cn.trawe.etc.hunanfront.enums.OrderStatus;
//import cn.trawe.etc.hunanfront.feign.ApiClient;
//import cn.trawe.etc.hunanfront.feign.ImageClient;
//import cn.trawe.etc.hunanfront.request.ApplyOrderSyncReq;
//import cn.trawe.etc.hunanfront.service.ApplySyncService;
//import cn.trawe.etc.hunanfront.service.ThirdOrderSycnRecordService;
//import cn.trawe.pay.common.enums.PlateColor;
//import cn.trawe.pay.common.etcmsg.EtcObjectResponse;
//import cn.trawe.pay.common.etcmsg.EtcResponse;
//import cn.trawe.pay.expose.entity.EtcIssueOrder;
//import cn.trawe.pay.expose.entity.EtcUserInvoice;
//import cn.trawe.pay.expose.enums.ChannelType;
//import cn.trawe.pay.expose.enums.EtcIssueOrderStatus;
//import cn.trawe.pay.expose.request.*;
//import cn.trawe.pay.expose.request.issue.IssueOrderSubmitReq;
//import cn.trawe.pay.expose.request.issue.ThirdOutOrderQueryReq;
//import cn.trawe.pay.expose.request.issue.ThirdOutOrderSaveReq;
//import cn.trawe.pay.expose.response.GlobalResponse;
//import cn.trawe.pay.expose.response.IdRes;
//import cn.trawe.pay.expose.response.OwnerRes;
//import cn.trawe.pay.expose.response.issue.*;
//import cn.trawe.util.LogUtil;
//import cn.trawe.utils.DateUtils;
//import cn.trawe.utils.ValidateUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.aliyun.openservices.ons.api.Action;
//import com.aliyun.openservices.ons.api.ConsumeContext;
//import com.aliyun.openservices.ons.api.Message;
//import com.aliyun.openservices.ons.api.MessageListener;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//
///**
// * @author Jiang Guangxing
// */
//@Slf4j
////@Component
//public class ApplyOrderSyncListener_20190523 implements MessageListener {
//    @Value("${api.token}")
//    private String token;
//
//    @Override
//    @Transactional
//    public Action consume(Message message, ConsumeContext consumeContext) {
//        String body = new String(message.getBody());
//        LogUtil.info(log, message.getMsgID(), "收到申请单同步消息", body);
//        ApplyOrderSyncReq applyOrderSyncReq = JSON.parseObject(body, ApplyOrderSyncReq.class);
//        try {
//            if (!(String.valueOf(OrderOpType.SUBMIT.ordinal()).equals(applyOrderSyncReq.getOpType())
//                    || String.valueOf(OrderOpType.UPDATE.ordinal()).equals(applyOrderSyncReq.getOpType()))) {
//                LogUtil.warn(log, applyOrderSyncReq.getOrderId(), "不支持的申请单同步操作", applyOrderSyncReq.getOpType());
//                return Action.CommitMessage;
//            }
//            //提交订单
//            this.saveOrUpdateOrderInfo(applyOrderSyncReq);
//            recordService.valid(applyOrderSyncReq.getRecordId());
//        } catch (Exception e) {
//            LogUtil.error(log, message.getMsgID(), "申请单同步消息处理失败", e);
//            recordService.addRetryTimes(applyOrderSyncReq.getRecordId());
//            return Action.ReconsumeLater;
//        }
//        return Action.CommitMessage;
//    }
//
//    public void saveOrUpdateOrderInfo(ApplyOrderSyncReq applyOrderSyncReq) {
//        //保存／更新用户信息
//        long uid = this.saveOrUpdateUser(applyOrderSyncReq);
//        //保存／更新订单
//        String orderNo = null;
//        Integer orderStatus = null;
//        EtcIssueOrder order = this.getOrderByOrderId(applyOrderSyncReq.getOrderId());
//        if (order != null) {
//            if (EtcIssueOrderStatus.FAIL_AUDIT.getCode() != order.getOrderStatus() && EtcIssueOrderStatus.PAY_SUCCESS.getCode() != order.getOrderStatus()) {
//                LogUtil.warn(log, applyOrderSyncReq.getOrderId(), "订单当前状态不能更新,order_status:", order.getOrderStatus());
//                return;
//            }
//            orderNo = order.getOrderNo();
//        } else
//            orderStatus = EtcIssueOrderStatus.PAY_SUCCESS.getCode();
//        orderNo = this.saveOrUpdateOrder(applyOrderSyncReq, orderStatus, orderNo);
//        if (ValidateUtil.isEmpty(orderNo))
//            return;
//        //保存／更新订单映射
//        this.saveThirdOutOrder(orderNo, applyOrderSyncReq.getOrderId());
//        //保存／更新身份证信息
//        long idCardId = this.saveOrUpdateIdCard(applyOrderSyncReq, uid, orderNo);
//        //保存／更新行驶证信息
//        long drivingLicenseId = this.saveOrUpdateDrivingLicense(applyOrderSyncReq, uid, orderNo);
////        //保存／更新协议
////        this.thirdSignSave(applyOrderSyncReq, orderNo);
//        //保存／更新发票
//        this.saveOrUpdateInvoice(applyOrderSyncReq, orderNo);
//        //保存照片信息
//      //5,保存照片信息
//        this.transferImages(applyOrderSyncReq.getOrderId(), orderNo);
//        // 直接保存base64
//        ImageClient.UploadImages images = this.uploadImages(applyOrderSyncReq.getOrderId());
//        //保存／更新照片信息
//        saveOrUpdateImages(images, orderNo, applyOrderSyncReq.getOrderId());
//        if (order != null)
//            orderStatus = order.getOrderStatus();
//        //提交订单给业主
//        boolean firstSubmit = true;
//        if (EtcIssueOrderStatus.FAIL_AUDIT.getCode() == orderStatus)
//            firstSubmit = false;
//        this.submitOrder(applyOrderSyncReq.getOrderId(), orderNo, applyOrderSyncReq.getSellerId(), firstSubmit);
//    }
//
//    //orderId 第三方订单号  orderNo 系统订单号
//    private void transferImages(String orderId, String orderNo) {
//    	cn.trawe.pay.expose.request.TransferImagesReq req = new cn.trawe.pay.expose.request.TransferImagesReq();
//        req.setOrderId(orderId);
//        req.setOrderNo(orderNo);
//        LogUtil.info(log, orderId, "照片信息转移请求", req);
//        EtcResponse res = apiClient.transferImages(req, token);
//        LogUtil.info(log, orderId, "照片信息转移响应", res);
//        if (res == null)
//            throw new RuntimeException("照片信息转移响应失败,res为空");
//        if (res.getCode() != 0)
//            throw new RuntimeException("照片信息转移响应失败,code不为0");
//    }
//
//    private long saveOrUpdateUser(ApplyOrderSyncReq applyOrderSyncReq) {
//        UserReq req = new UserReq();
//        req.setChannelType(ChannelType.ALIPAY);
//        req.setRealName(applyOrderSyncReq.getViOwnerInfo().getViOwnerName());
//        req.setCertNo(applyOrderSyncReq.getViOwnerInfo().getViOwnerCertNo());
//        LogUtil.info(log, applyOrderSyncReq.getOrderId(), "保存用户信息请求", req);
//        IdRes res = apiClient.saveOrUpdateUser(req, applyOrderSyncReq.getBuyerUid(), token);
//        LogUtil.info(log, applyOrderSyncReq.getOrderId(), "保存用户信息响应", res);
//        if (res == null)
//            throw new RuntimeException("保存用户信息响应失败,res为空");
//        if (res.getCode() != 0)
//            throw new RuntimeException("保存用户信息响应失败,code不为0");
//        return res.getId();
//    }
//
//    private long saveOrUpdateIdCard(ApplyOrderSyncReq applyOrderSyncReq, long uid, String orderNo) {
//        SaveOrUpdateIdCardReq req = new SaveOrUpdateIdCardReq();
//        req.setName(applyOrderSyncReq.getViOwnerInfo().getViOwnerName());
//        req.setIdCardNo(applyOrderSyncReq.getViOwnerInfo().getViOwnerCertNo());
//        req.setChannelUserId(uid);
//        req.setOrderNo(orderNo);
//        LogUtil.info(log, applyOrderSyncReq.getOrderId(), "保存身份证信息请求", req);
//        IdRes res = apiClient.saveOrUpdateIdCard(req, token);
//        LogUtil.info(log, applyOrderSyncReq.getOrderId(), "保存身份证信息响应", res);
//        if (res == null)
//            throw new RuntimeException("保存身份证信息响应失败,res为空");
//        if (res.getCode() != 0)
//            throw new RuntimeException("保存身份证信息响应失败,code不为0");
//        return res.getId();
//    }
//
//    private long saveOrUpdateDrivingLicense(ApplyOrderSyncReq applyOrderSyncReq, long uid, String orderNo) {
//        ApplyOrderSyncReq.ViInfo viInfo = applyOrderSyncReq.getViInfo();
//        SaveOrUpdateDrivingLicenseReq req = new SaveOrUpdateDrivingLicenseReq();
//        req.setChannelUserId(uid);
//        req.setOrderNo(orderNo);
//        req.setEngineNo(viInfo.getEngineNo());
//        req.setAddr(viInfo.getViOwnerAddress());
//        req.setApprovedLoad(null);//暂时没传
//        req.setEnergyType(null);//暂时没传
//        req.setFileNo(viInfo.getViLicenseNo());
//        req.setGrossMass(viInfo.getViTotalMass());
//        req.setInspectionRecord(viInfo.getViInspectionRecord());
//        req.setIssueDate(viInfo.getViGrantTime());
//        req.setModel(viInfo.getViModelName());
//        req.setOrderPlateNo(viInfo.getViNumber());
//        if (ValidateUtil.isNotEmpty(viInfo.getViLength()) && ValidateUtil.isNotEmpty(viInfo.getViWidth()) && ValidateUtil.isNotEmpty(viInfo.getViHeight()))
//            req.setOverallDimension(viInfo.getViLength() + "X" + viInfo.getViWidth() + "X" + viInfo.getViHeight());
//        req.setOwner(viInfo.getViOwnerName());
//        req.setPlateNo(viInfo.getViNumber());
//        req.setRegisterDate(viInfo.getViStartTime());
//        req.setTractionMass(viInfo.getViTractionMass());
//        req.setUnladenMass(viInfo.getViReadinessMass());
//        req.setVehicleType(viInfo.getViType());
//        req.setVehicleUseCharacter(viInfo.getViUseType());
//        req.setVin(viInfo.getViVin());
//        try {
//            req.setPlateColor(PlateColor.values()[Integer.valueOf(viInfo.getViPlateColor())]);
//        } catch (NumberFormatException e) {
//            LogUtil.warn(log, applyOrderSyncReq.getOrderId(), "车牌颜色转换失败", viInfo.getViPlateColor());
//        }
//        try {
//            req.setSeats(Integer.valueOf(viInfo.getViAc()));
//        } catch (Exception e) {
//            LogUtil.warn(log, applyOrderSyncReq.getOrderId(), "座位数转换失败", viInfo.getViAc());
//        }
//        LogUtil.info(log, applyOrderSyncReq.getOrderId(), "保存行驶证信息请求", req);
//        IdRes res = apiClient.saveOrUpdateDrivingLicense(req, token);
//        LogUtil.info(log, applyOrderSyncReq.getOrderId(), "保存行驶证信息响应", res);
//        if (res == null)
//            throw new RuntimeException("保存行驶证信息响应失败,res为空");
//        if (res.getCode() != 0)
//            throw new RuntimeException("保存行驶证信息响应失败,code不为0");
//        return res.getId();
//    }
//
//    private String saveOrUpdateOrder(ApplyOrderSyncReq syncReq, Integer oderStatus, String orderNo) {
//        LogUtil.info(log, syncReq.getOrderId(), "查询业主信息，ownerCode", syncReq.getSellerId());
//        OwnerRes ownerRes = apiClient.owner(Integer.valueOf(syncReq.getSellerId()), token);
//        LogUtil.info(log, syncReq.getOrderId(), "查询业主信息响应", ownerRes);
//        if (ownerRes == null)
//            throw new RuntimeException("查询业主信息响应失败,res为空");
//        if (ownerRes.getCode() != 0)
//            throw new RuntimeException("查询业主信息响应失败,code不为0");
//        JSONObject order = new JSONObject();
//        //渠道ID
//        order.put("partner_id", 1);
//        order.put("owner_code", syncReq.getSellerId());
//        order.put("plate_no", syncReq.getViInfo().getViNumber());
//        order.put("plate_color", syncReq.getViInfo().getViPlateColor());
//        order.put("card_type", CardType.warp(syncReq.getCardType()));
//        ApplyOrderSyncReq.DeliveryInfo deliveryInfo = syncReq.getDeliveryInfo();
//        order.put("receiver_name", deliveryInfo.getContactName());
//        order.put("receiver_phone", deliveryInfo.getContactTel());
//        order.put("receiver_address", deliveryInfo.getAddress());
//        order.put("pay_channel", ChannelType.ALIPAY.ordinal());
//        order.put("total_fee", syncReq.getTotalAmount());
//        order.put("obu_fee", syncReq.getDeviceAmount());
//        order.put("service_fee", syncReq.getServiceAmount());
//        order.put("pd_fee", syncReq.getDeliveryAmount());
//        order.put("province_id", ownerRes.getProvinceId());
//        if (oderStatus != null) {
//            order.put("order_status", oderStatus);
//        }
//        if (ValidateUtil.isNotEmpty(orderNo)) {
//            order.put("order_no", orderNo);
//        }
////        order.put("vehicle_type", syncReq.getViInfo().getViType());// 传的是字符串，接口需要传int
////        order.put("apply_category", "");//申请卡片类别。1：只购买ETC卡   2：购买OBU   3：ETC和OBU一起买
//        LogUtil.info(log, syncReq.getOrderId(), "保存订单信息请求", order);
//        IssueSaveResp res = apiClient.saveOrder(order, syncReq.getBuyerUid(), token);
//        LogUtil.info(log, syncReq.getOrderId(), "保存订单信息响应", res);
//        if (res == null)
//            throw new RuntimeException("保存订单信息响应失败,res为空");
//        if (res.getCode() != 0) {
//            this.orderRejectStatusSync(syncReq.getOrderId(), res.getMsg());
//            return null;
//        }
//        return res.getOrderNo();
//    }
//
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
//
//    private void saveThirdOutOrder(String orderNo, String orderId) {
//        ThirdOutOrderSaveReq req = new ThirdOutOrderSaveReq();
//        req.setOrderNo(orderNo);
//        req.setOutOrderId(orderId);
//        req.setOutType(1);
//        req.setThirdId(1L);
//        LogUtil.info(log, orderId, "保存订单映射信息请求", req);
//        ThirdOutOrderSaveResp res = apiClient.saveThirdOutOrder(req, token);
//        LogUtil.info(log, orderId, "保存订单映射信息响应", res);
//        if (res == null)
//            throw new RuntimeException("保存订单映射信息响应失败,res为空");
//        if (res.getCode() != 0)
//            throw new RuntimeException("保存订单映射信息响应失败,code不为0");
//    }
//
//    private void submitOrder(String orderId, String orderNo, String sellerId, boolean firstSubmit) {
//        IssueOrderSubmitReq req = new IssueOrderSubmitReq();
//        req.setOrderNo(orderNo);
//        req.setAuditType(AuditType.getAuditType(sellerId, firstSubmit));
//        LogUtil.info(log, orderId, "提交订单信息请求", req);
//        IssueOrderSubmitResp res = apiClient.orderSubmit(req, token);
//        LogUtil.info(log, orderId, "提交订单信息响应", res);
//        if (res == null)
//            throw new RuntimeException("提交订单信息响应失败,res为空");
//        if (res.getCode() != 0)
//            this.orderRejectStatusSync(orderId, res.getMsg());
//    }
//
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
//
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
//
//    private void orderRejectStatusSync(String orderId, String msg) {
//        JSONObject bizContent = new JSONObject();
//        bizContent.put("order_status", OrderStatus.REJECT.ordinal());
//        if (ValidateUtil.isNotEmpty(msg))
//            bizContent.put("censor_info", msg);
//        bizContent.put("order_id", orderId);
//        bizContent.put("order_update_time", DateUtils.format(new Date(), DateUtils.YYYY_MM_DD_HH_MM_SS));
//        applySyncService.invoke(bizContent);
//    }
//
//    private EtcIssueOrder getOrderByOrderId(String orderId) {
//        //查询订单映射信息
//        ThirdOutOrderQueryReq req = new ThirdOutOrderQueryReq();
//        req.setOutOrderId(orderId);
//        ThirdOutOrderQueryResp thirdOutOrderQueryResp = applySyncService.queryThirdOutOrder(req);
//        if (ValidateUtil.isEmpty(thirdOutOrderQueryResp.getOrderNo()))
//            return null;
//        //查询订单
//        return applySyncService.orderQuery(thirdOutOrderQueryResp.getOrderNo());
//    }
////
////    private void thirdSignSave(ApplyOrderSyncReq syncReq, String orderNo) {
////        ThirdSignSaveReq req = new ThirdSignSaveReq();
////        req.setAgreementNo(syncReq.getAgreementNo());
////        req.setOrderNo(orderNo);
////        req.setPartnerId(syncReq.getBuyerUid());
////        req.setStatus(Status.VALID.ordinal());
////        LogUtil.info(log, syncReq.getOrderId(), "保存协议信息请求", req);
////        cn.trawe.pay.expose.response.BaseResponse res = apiClient.thirdSignSave(req, token);
////        LogUtil.info(log, syncReq.getOrderId(), "保存协议信息响应", res);
////        if (res == null)
////            throw new RuntimeException("保存协议信息响应失败,res为空");
////        if (res.getCode() != 0)
////            throw new RuntimeException("保存协议信息响应失败,code不为0");
////    }
//
//    @Autowired
//    private ApiClient apiClient;
//    @Lazy
//    @Autowired
//    private ApplySyncService applySyncService;
//    @Autowired
//    private ThirdOrderSycnRecordService recordService;
//    @Autowired
//    private ImageClient imageClient;
//}
