package cn.trawe.etc.hunanfront.service.v2.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.trawe.etc.hunanfront.config.InterfaceCenter;
import cn.trawe.etc.hunanfront.config.IssueV2Constants;
import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.expose.v2.req.OpenAccountReq;
import cn.trawe.etc.hunanfront.expose.v2.req.QualificationCheckReq;
import cn.trawe.etc.hunanfront.expose.v2.req.QueryCardStatusReq;
import cn.trawe.etc.hunanfront.expose.v2.resp.OpenAccountResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.QualificationCheckResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.QueryCardStatusResp;
import cn.trawe.etc.hunanfront.service.v2.AccountServiceI;
import cn.trawe.etc.hunanfront.utils.ImageUtils;
import cn.trawe.etc.route.expose.request.CardQueryStatusRequest;
import cn.trawe.etc.route.expose.response.CardQueryResponse;
import cn.trawe.pay.expose.request.issue.CheckVehicleLicenseReq;
import cn.trawe.pay.expose.request.issue.ThirdOutOrderSaveReq;
import cn.trawe.pay.expose.request.issue.UserInfoUploadReq;
import cn.trawe.pay.expose.response.issue.CheckVehicleLicenseResp;
import cn.trawe.pay.expose.response.issue.ThirdOutOrderSaveResp;
import cn.trawe.pay.expose.response.issue.UserInfoUploadResp;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountServiceImpl extends BaseServiceImpl implements AccountServiceI  {
	
	
	@Value("${image.size}")
	private int config_imagesize;


	@Override
	public OpenAccountResp openAccount(OpenAccountReq req,ThirdPartner partner) {
		LogUtil.info(log, req.getOrderId(), "开户请求:"+req.toString());
		UserInfoUploadReq  reqCenter = new UserInfoUploadReq();
		OpenAccountResp  resp = new OpenAccountResp();
		try {
			reqCenter.setOwnerCode(4301);
			reqCenter.setAddress(req.getViOwnerCertAddress());
			reqCenter.setIdCardNo(req.getViOwnerCertNo());
			reqCenter.setName(req.getViOwnerName());
			reqCenter.setOuterOrderId(req.getOrderId());
			
			reqCenter.setPhotoType(1);
			reqCenter.setPartnerId(req.getChannelNo());
			reqCenter.setPhone(req.getViPhoneNumber());
			//处理渠道编号
			JSONObject object = new JSONObject();
	    	if(StringUtils.isBlank(req.getAccountNo())||StringUtils.isBlank(req.getPassword())) {
	    		String accountNo = partner.getAccountNo();
	          	String password = partner.getPassword();
	          	object.put("Account", accountNo);
	          	object.put("Password", password);
	    	}
	    	else {
	    		object.put("Account", req.getAccountNo());
	          	object.put("Password", req.getPassword());
	    	}
	    	reqCenter.setNote1(object.toJSONString());
	    	reqCenter.setPartnerId(req.getChannelNo());
	    	LogUtil.info(log, req.getOrderId(), "中台请求:"+JSON.toJSONString(reqCenter));
	    	//校验图片大小
	    	
	    	if(config_imagesize>0) {
	    		Integer imageSizeFront = ImageUtils.imageSize(req.getViOwnerCertPicFront());
	    		if(imageSizeFront>config_imagesize) {
	    			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
	    			resp.setErrorMsg("图片大小不合法，单张图片大小不超过"+config_imagesize+"KB"+",当前图片大小为"+imageSizeFront+"KB");
	    			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
	    			LogUtil.info(log, req.getOrderId(), "开户响应:"+JSON.toJSONString(resp));
	    			return resp;
	    		}
	    		Integer imageSizeBack = ImageUtils.imageSize(req.getViOwnerCertPicBack());
	    		if(imageSizeBack>config_imagesize) {
	    			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
	    			resp.setErrorMsg("图片大小不合法，单张图片大小不超过"+config_imagesize+"KB"+",当前图片大小为"+imageSizeBack+"KB");
	    			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
	    			LogUtil.info(log, req.getOrderId(), "开户响应:"+JSON.toJSONString(resp));
	    			return resp;
	    		}
	    	}
	    	reqCenter.setIdCardFrontPhoto(req.getViOwnerCertPicFront());
			reqCenter.setIdCardBackPhoto(req.getViOwnerCertPicBack());
			
			UserInfoUploadResp userInfoUpload = IssueCenterApi.userInfoUpload(reqCenter);
			LogUtil.info(log, req.getOrderId(), "中台响应:"+JSON.toJSONString(userInfoUpload));
			if(InterfaceCenter.TIMEOUT.getCode()==userInfoUpload.getCode()) {
				resp.setErrorMsg(userInfoUpload.getMsg());
				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
				LogUtil.info(log, req.getOrderId(), "开户响应:"+JSON.toJSONString(resp));
				queryMonitorServiceImpl.send("userInfoUpload", "服务降级");
				return resp;
			}
			if(InterfaceCenter.SUCCESS.getCode()!=userInfoUpload.getCode()) {
				resp.setErrorCode(StringUtils.isBlank(userInfoUpload.getErrorCode())?BaseResponseData.ErrorCode.SYSTEM_ERROR.toString():userInfoUpload.getErrorCode());
				resp.setErrorMsg(StringUtils.isBlank(userInfoUpload.getMsg())?IssueV2Constants.SYSTEM_ERROR_INFO:userInfoUpload.getMsg());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "开户响应:"+JSON.toJSONString(resp));
				return resp;
			}
			//保存第三方映射订单
			//saveThirdOutOrder(userInfoUpload.getOrderNo(),req.getOrderId(),Long.valueOf(req.getChannelNo()));
			
			resp.setUserNo(userInfoUpload.getUserId());
			resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
			resp.setErrorMsg(userInfoUpload.getMsg());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "开户响应:"+JSON.toJSONString(resp));
			return resp;
		}
		catch(Exception e) {
			LogUtil.error(log, req.getOrderId(), e.getMessage(), e.getCause());
			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
			resp.setErrorMsg(e.getMessage());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "开户响应:"+JSON.toJSONString(resp));
			return resp;
		}
		
	}

	@Override
	public QualificationCheckResp qualificationCheck(QualificationCheckReq req,ThirdPartner partner) {
		LogUtil.info(log, req.getOrderId(), "办理资格请求:"+req.toString());
		QualificationCheckResp resp = new QualificationCheckResp();
		CheckVehicleLicenseReq reqCenter = new CheckVehicleLicenseReq();
		try {
			//处理渠道编号
			JSONObject object = new JSONObject();
	    	if(StringUtils.isBlank(req.getAccountNo())||StringUtils.isBlank(req.getPassword())) {
	    		String accountNo = partner.getAccountNo();
	          	String password = partner.getPassword();
	          	object.put("Account", accountNo);
	          	object.put("Password", password);
	    	}
	    	else {
	    		object.put("Account", req.getAccountNo());
	          	object.put("Password", req.getPassword());
	    	}
	    	reqCenter.setNote1(object.toJSONString());
	    	reqCenter.setOuterOrderId(req.getOrderId());
	    	reqCenter.setVehicleLicenseColor(req.getViPlateColor());
	    	reqCenter.setVehicleLicensePlate(req.getViNumber());
	    	LogUtil.info(log, req.getOrderId(), "中台请求:"+JSON.toJSONString(reqCenter));
			CheckVehicleLicenseResp respCenter = IssueCenterApi.checkVehicleLicense(reqCenter);
			LogUtil.info(log, req.getOrderId(), "中台响应:"+JSON.toJSONString(respCenter));
			if(InterfaceCenter.TIMEOUT.getCode()==respCenter.getCode()) {
				resp.setErrorMsg(respCenter.getMsg());
				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
				queryMonitorServiceImpl.send("checkVehicleLicense", "服务降级");
				LogUtil.info(log, req.getOrderId(), "办理资格响应:"+JSON.toJSONString(resp));
				return resp;
			}
			if(InterfaceCenter.SUCCESS.getCode()!=respCenter.getCode()) {
				resp.setErrorCode(StringUtils.isBlank(respCenter.getErrorCode())?BaseResponseData.ErrorCode.SYSTEM_ERROR.toString():respCenter.getErrorCode());
				resp.setErrorMsg(StringUtils.isBlank(respCenter.getMsg())?IssueV2Constants.SYSTEM_ERROR_INFO:respCenter.getMsg());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "办理资格响应:"+JSON.toJSONString(resp));
				return resp;
			}
			resp.setVehicleId(respCenter.getCarId());
			resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
			resp.setErrorMsg(respCenter.getMsg());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "办理资格响应:"+JSON.toJSONString(resp));
			return resp;
		}
		catch(Exception e) {
			LogUtil.error(log, req.getOrderId(), e.getMessage(), e.getCause());
			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
			resp.setErrorMsg(e.getMessage());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "办理资格响应:"+JSON.toJSONString(resp));
			return resp;
		}
		
	}
	
	  private void saveThirdOutOrder(String orderNo, String outOrderId,Long channelId) {
		  	
		  	//关联渠道ID
		      ThirdOutOrderSaveReq req = new ThirdOutOrderSaveReq();
		      //网发平台订单号
		      req.setOrderNo(orderNo);
		      //银行渠道的订单号
		      req.setOutOrderId(outOrderId);
		      req.setOutType(1);
		      req.setThirdId(channelId);
		      //req.setBankCode(channelId.toString());
		      LogUtil.info(log, outOrderId, "保存订单映射信息请求", req);
		      ThirdOutOrderSaveResp res = IssueCenterApi.saveThirdOutOrder(req);
		      LogUtil.info(log, outOrderId, "保存订单映射信息响应", res);
		      if (res == null)
		          throw new RuntimeException("保存订单映射信息响应失败,res为空");
		      if (res.getCode() != 0)
		          throw new RuntimeException("保存订单映射信息响应失败,code不为0");
		  }

	@Override
	public QueryCardStatusResp queryCardStatus(QueryCardStatusReq req, ThirdPartner partner) {
		LogUtil.info(log, req.getCardNo(), "卡状态查询请求:"+req.toString());
		QueryCardStatusResp resp = new QueryCardStatusResp();
		CardQueryStatusRequest reqCenter = new CardQueryStatusRequest();
		try {
			reqCenter.setCardNo(req.getCardNo());
			//处理渠道编号
			JSONObject object = new JSONObject();
	    	
    		String accountNo = partner.getAccountNo();
          	String password = partner.getPassword();
          	object.put("Account", accountNo);
          	object.put("Password", password);
	    	
	    	
	    	reqCenter.setNote1(object.toJSONString());
	    	reqCenter.setOwnerCode("4301");
	    	LogUtil.info(log, req.getCardNo(), "中台请求:"+JSON.toJSONString(reqCenter));
			CardQueryResponse respCenter = IssueCenterApi.cardQuery(reqCenter);
			LogUtil.info(log, req.getCardNo(), "中台响应:"+JSON.toJSONString(respCenter));
			if(InterfaceCenter.TIMEOUT.getCode()==respCenter.getCode()) {
				resp.setErrorMsg(respCenter.getMsg());
				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
				LogUtil.info(log, req.getCardNo(), "卡状态查询响应:"+JSON.toJSONString(resp));
				return resp;
			}
			if(InterfaceCenter.SUCCESS.getCode()!=respCenter.getCode()) {
				resp.setErrorCode(StringUtils.isBlank(respCenter.getErrorCode())?BaseResponseData.ErrorCode.SYSTEM_ERROR.toString():respCenter.getErrorCode());
				resp.setErrorMsg(respCenter.getMsg());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getCardNo(), "卡状态查询响应:"+JSON.toJSONString(resp));
				return resp;
			}
			resp.setState(respCenter.getStatus().toString());
			resp.setVehiclePlate(respCenter.getPlateNo());
			resp.setVehiclePlateColor(resp.getVehiclePlateColor());
			resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
			resp.setErrorMsg(respCenter.getMsg());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getCardNo(), "卡状态查询响应:"+JSON.toJSONString(resp));
			return resp;
		}
		catch(Exception e) {
			LogUtil.error(log, req.getCardNo(), e.getMessage(), e.getCause());
			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
			resp.setErrorMsg(e.getMessage());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getCardNo(), "卡状态查询响应:"+JSON.toJSONString(resp));
			return resp;
		}
	}

}
