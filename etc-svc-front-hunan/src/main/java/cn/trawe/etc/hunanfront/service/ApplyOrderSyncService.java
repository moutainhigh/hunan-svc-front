package cn.trawe.etc.hunanfront.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.request.ApplyOrderSyncReq;
import cn.trawe.etc.hunanfront.response.ApplyOrderSync;
import cn.trawe.etc.hunanfront.service.applysync.ApplySyncFactory;
import cn.trawe.etc.hunanfront.utils.SignUtil2;
import cn.trawe.etc.hunanfront.utils.ValidUtils;
import cn.trawe.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jiang Guangxing
 */
@Slf4j
@Service
public class ApplyOrderSyncService extends BaseService {

    @Autowired
    private ThirdPartnerService thirdPartnerService;
    @Autowired
    PickService  PickService;

    public BaseResponse applyOrderSync(BaseRequest req) {

        ThirdPartner partner = thirdPartnerService.getPartner(req);
        if(partner == null){
            return paramsError("验签失败", req.getCharset(),partner);
        }
        boolean flag = thirdPartnerService.check(req, partner);
        if(!flag){
            return paramsError("验签失败", req.getCharset(),partner);
        }

        ApplyOrderSyncReq applyOrderSyncReq = JSON.parseObject(req.getBizContent(), ApplyOrderSyncReq.class);
        /**
          * 
         */
       //校验参数是否合法
        if("1".equals(applyOrderSyncReq.getOpType())) {
      	  String err = check(applyOrderSyncReq);
            if (ValidateUtil.isNotEmpty(err)) {
            	 ApplyOrderSync applyOrderSync = new ApplyOrderSync().setOrderId(applyOrderSyncReq.getOrderId()).setOutBizNo("").setOrderStatus("2");
                 applyOrderSync.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString()).setSuccess(BaseResponseData.Success.SUCCEED.toString()).setErrorMsg(err);

                 BaseResponse<ApplyOrderSync> resp = new BaseResponse<ApplyOrderSync>();
                 resp.setResponse(applyOrderSync);
                 try{
                     resp.setSign(SignUtil2.signResponse(applyOrderSync,partner.getTrawePrivateKey(),"UTF-8",true));
                 }catch (AlipayApiException e){
                     log.error(e.getMessage(),e.fillInStackTrace());
                 }
                 return resp;
            }
               
        }
        if("1".equals(applyOrderSyncReq.getInstallStatus())){
        	
        	
        	 return PickService.doService(applyOrderSyncReq,partner);
        }
        return applySyncFactory.getApplyCancelStrategy(applyOrderSyncReq.getOpType()).sync(req.getCharset(), applyOrderSyncReq,partner);
    }

    @Autowired
    public ApplyOrderSyncService(ApplySyncFactory applySyncFactory) {
        this.applySyncFactory = applySyncFactory;
    }
    
    private String check(ApplyOrderSyncReq applyOrderSyncReq) {
        String err = ValidUtils.validateBean(applyOrderSyncReq);
//        if (!AuditType.canWarp(applyOrderSyncReq.getSellerId()))
//            err += ",发行方编号不正确";
        if (applyOrderSyncReq.getViInfo() != null) {
        	//校验日期
        	Pattern yyyy_MM_dd = Pattern.compile("^\\d{4}\\-\\d{2}\\-\\d{2}$");
    		Matcher m1 = yyyy_MM_dd.matcher(applyOrderSyncReq.getViInfo().getViGrantTime());
    		Pattern yyyyMMdd = Pattern.compile("^\\d{4}\\d{2}\\d{2}$");
    		Matcher m2 = yyyyMMdd.matcher(applyOrderSyncReq.getViInfo().getViGrantTime());
    		if(!m1.matches()&&!m2.matches()) 
    		{
    			 err += "," + "行驶证发证日期不合法";
    		    
    		}
    		Matcher m3 = yyyy_MM_dd.matcher(applyOrderSyncReq.getViInfo().getViStartTime());
    		Matcher m4 = yyyyMMdd.matcher(applyOrderSyncReq.getViInfo().getViStartTime());
    		if(!m3.matches()&&!m4.matches()) 
    		{
    			 err += "," + "行驶证注册日期不合法";
    		    
    		}
            String viInfoErr = ValidUtils.validateBean(applyOrderSyncReq.getViInfo());
            if (ValidateUtil.isNotEmpty(viInfoErr))
                err += "," + viInfoErr;
        }
        if (applyOrderSyncReq.getViOwnerInfo() != null) {
            String viOwnerInfoErr = ValidUtils.validateBean(applyOrderSyncReq.getViOwnerInfo());
            if (ValidateUtil.isNotEmpty(viOwnerInfoErr))
                err += "," + viOwnerInfoErr;
        }
        if (applyOrderSyncReq.getDeliveryInfo() != null) {
            String deliveryInfoErr = ValidUtils.validateBean(applyOrderSyncReq.getDeliveryInfo());
            if (ValidateUtil.isNotEmpty(deliveryInfoErr))
                err += "," + deliveryInfoErr;
        }
//        if (applyOrderSyncReq.getInvoiceInfo() != null
//                && "1".equals(applyOrderSyncReq.getInvoiceInfo().getNeedInvoice())) {
//            String invoiceInfoErr = ValidUtils.validateBean(applyOrderSyncReq.getInvoiceInfo());
//            if (ValidateUtil.isNotEmpty(invoiceInfoErr))
//                err += "," + invoiceInfoErr;
//            if ("1".equals(applyOrderSyncReq.getInvoiceInfo().getInvoiceTitleType())
//                    && ValidateUtil.isEmpty(applyOrderSyncReq.getInvoiceInfo().getDutyNo()))
//                err += ",发票信息税号不能为空";
//        }
        if (err.startsWith(","))
            err = err.substring(1);
        return err;
    }

    private ApplySyncFactory applySyncFactory;
    
    public static void main(String[] args) {
//      String str = "{\"vi_owner_info\":{\"vi_owner_type\":\"test\"}}";
//      ApplyOrderSyncReq req = JSON.parseObject(str, ApplyOrderSyncReq.class);
//      System.out.println(req);
//      

//		String testStr = "20190109";
//		Pattern p = Pattern.compile("^\\d{4}\\d{2}\\d{2}$");
//		Matcher m = p.matcher(testStr);
//		if(!m.matches()) 
//		{
//			System.out.println("false");
//		    
//		}
//		else {
//			 System.out.println("true");
//		}
    	
    	String  err ="";
    	String viGrangTime ="20190901,";
    	String viStartime ="20190901";
    	Pattern yyyy_MM_dd = Pattern.compile("^\\d{4}\\-\\d{2}\\-\\d{2}$");
		Matcher m1 = yyyy_MM_dd.matcher(viGrangTime);
		Pattern yyyyMMdd = Pattern.compile("^\\d{4}\\d{2}\\d{2}$");
		Matcher m2 = yyyyMMdd.matcher(viGrangTime);
		if(!m1.matches()&&!m2.matches()) 
		{
			 err += "," + "行驶证发证日期不合法";
		    
		}
		Matcher m3 = yyyy_MM_dd.matcher(viStartime);
		Matcher m4 = yyyyMMdd.matcher(viStartime);
		if(!m3.matches()&&!m4.matches()) 
		{
			 err += "," + "行驶证注册日期不合法";
		    
		}
		System.out.println(err);
     
  }
    
 
}
