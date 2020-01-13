package cn.trawe.etc.hunanfront.service.secondissue.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;

import cn.trawe.etc.hunanfront.config.InterfaceCenter;
import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.service.BaseService;
import cn.trawe.etc.hunanfront.service.ThirdPartnerService;
import cn.trawe.etc.hunanfront.service.secondissue.request.ImageVehicleReqHn;
import cn.trawe.etc.hunanfront.utils.SignUtil2;
import cn.trawe.pay.common.etcmsg.EtcObjectResponse;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.request.issue.GetOrderReq;
import cn.trawe.pay.expose.request.issue.ThirdOutOrderQueryReq;
import cn.trawe.pay.expose.request.secondissue.ActivationQueryReq;
import cn.trawe.pay.expose.request.secondissue.ImageVehicleReq;
import cn.trawe.pay.expose.response.issue.ThirdOutOrderQueryResp;
import cn.trawe.pay.expose.response.secondissue.ActivationQueryResp;
import cn.trawe.pay.expose.response.secondissue.ImageVehicleResp;
import cn.trawe.util.LogUtil;
import cn.trawe.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ImageVehicleService extends BaseService{
	
		@Autowired
		private ThirdPartnerService thirdPartnerService;
	
	  public BaseResponse doService(BaseRequest req){
		  
		 //LogUtil.info(log, "image_vehicle", "渠道请求报文 : "+JSON.toJSONString(req));
		 ThirdPartner partner = thirdPartnerService.getPartner(req);
	        if(partner == null){
	            return paramsError("渠道信息不存在", req.getCharset(),partner);
	        }
	        boolean flag = thirdPartnerService.check(req, partner);
	        if(!flag){
	        	return paramsError("验签失败", req.getCharset(),partner);
	        }
	        ImageVehicleReqHn imageReq = JSON.parseObject(req.getBizContent(), ImageVehicleReqHn.class);
	        

//			ThirdOutOrderQueryReq outOrderReq = new ThirdOutOrderQueryReq();
//			outOrderReq.setOutOrderId(imageReq.getOrderId());
//			LogUtil.info(log, imageReq.getOrderId(), "渠道请求报文 : "+imageReq);
//			LogUtil.info(log, imageReq.getOrderId(), "外部订单号 :" +imageReq.getOrderId());
//			LogUtil.info(log, imageReq.getOrderId(), "网发发送基础服务请求："+JSON.toJSONString(outOrderReq));
//			ThirdOutOrderQueryResp queryThirdOutOrder = IssueCenterApi.queryThirdOutOrder(outOrderReq);
//			LogUtil.info(log, imageReq.getOrderId(), "网发发送基础服务响应："+JSON.toJSONString(queryThirdOutOrder));
//			if(queryThirdOutOrder.getCode()!=0) {
//				return otherError("未找到对应的订单网发订单号",req.getCharset(),partner);
//			}
//			LogUtil.info(log, imageReq.getOrderId(),"根据外部订单号查询网发订单号 : " +queryThirdOutOrder.getOrderNo());
	        
	        GetOrderReq centerReq = new GetOrderReq();
			centerReq.setOutOrderId(imageReq.getOrderId());
	    	LogUtil.info(log, imageReq.getOrderId(), "中台订单查询请求:"+JSON.toJSONString(centerReq));
			EtcObjectResponse<EtcIssueOrder> centerResp = IssueCenterApi.getOrder(centerReq);
			LogUtil.info(log, imageReq.getOrderId(), "中台订单查询响应:"+JSON.toJSONString(centerResp));
			
		    if (ValidateUtil.isEmpty(centerResp.getData())) {
		    	return otherError("订单不存在",req.getCharset(),partner);
		    }
		          
		    EtcIssueOrder order =centerResp.getData();
			//开始进度查询
			ActivationQueryReq actReq = new ActivationQueryReq();
			if("0".equals(imageReq.getType())){
				imageReq.setKind("0");
			}
			else if("1".equals(imageReq.getType())){
				imageReq.setKind("1");
			}
			else {
				return otherError("业务类型不合法",req.getCharset(),partner);
			}
			
			actReq.setKind(imageReq.getKind());
			actReq.setOrderNo(order.getOrderNo());
			actReq.setOwnerCode("4301");
			LogUtil.info(log, imageReq.getOrderId(), "查询激活记录请求 :"+JSON.toJSONString(actReq));
			ActivationQueryResp activationQuery = IssueCenterApi.activationQuery(actReq);
			
			LogUtil.info(log, imageReq.getOrderId(), "查询激活记录响应 :"+JSON.toJSONString(activationQuery));
			if(activationQuery.getCode()!=0) {
				return otherError("进度查询失败",req.getCharset(),partner);

			}
			//调用基础服务上传图片
	       ImageVehicleReq twImaVeh = new ImageVehicleReq();
	       twImaVeh.setOwnerCode("4301");
	       twImaVeh.setImageType(Integer.valueOf(imageReq.getImageType()));
	       twImaVeh.setImage(imageReq.getImage());
	       twImaVeh.setKind(imageReq.getKind());
	       twImaVeh.setOrderNo(order.getOrderNo());
	       LogUtil.info(log, imageReq.getOrderId(), "上传OBU 照片 请求:"+"image_type:"+twImaVeh.getImageType()+"kind:"+twImaVeh.getKind());
	       ImageVehicleResp imageVehicle = IssueCenterApi.imageVehicle(twImaVeh);
	       LogUtil.info(log, imageReq.getOrderId(), "上传OBU 照片响应 :"+JSON.toJSONString(actReq));
	       if(imageVehicle.getCode()!=0) {
	    	   return otherError("图片保存失败",req.getCharset(),partner);
	       }
	       //调用基础服务透传图片到省网关
//	       CustomerInfoUploadReq  twUpload = new CustomerInfoUploadReq();
//	       twUpload.setKind(imageReq.getKind());
//	       twUpload.setOrderNo(queryThirdOutOrder.getOrderNo());
//	       twUpload.setOwnerCode("4301");
//	       cn.trawe.pay.expose.response.BaseResponse customerInfoUpload = apiClient.customerInfoUpload(twUpload,token);
//	       if(customerInfoUpload.getCode()!=0) {
//	    	   otherError("上传图片失败",req.getCharset(),partner);
//	       }
	      BaseResponseData  respData = new BaseResponseData();
	      respData.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString()).setSuccess(BaseResponseData.Success.SUCCEED.toString()).setErrorMsg("成功");
		  return succeedResponseImageVehicle(req.getCharset(),imageReq,respData,partner.getTrawePrivateKey());
		
		
	}
	  
	  BaseResponse succeedResponseImageVehicle(String charset, ImageVehicleReqHn req,BaseResponseData BaseResponseData,String privateKey) {

		  LogUtil.info(log, req.getOrderId(), "响应报文 : "+JSON.toJSONString(BaseResponseData));
	        BaseResponse<BaseResponseData> resp = new BaseResponse<BaseResponseData>();
	        resp.setResponse(BaseResponseData);
	        try{
	            resp.setSign(SignUtil2.signResponse(BaseResponseData,privateKey,charset,true));
	        }catch (AlipayApiException e){
	            log.error(e.getMessage(),e.fillInStackTrace());
	        }
			LogUtil.info(log, req.getOrderId(), "响应报文 : "+JSON.toJSONString(resp));
	        return resp;
	    }  

}
