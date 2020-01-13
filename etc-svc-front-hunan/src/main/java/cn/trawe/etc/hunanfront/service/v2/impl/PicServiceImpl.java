package cn.trawe.etc.hunanfront.service.v2.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.trawe.etc.hunanfront.config.InterfaceCenter;
import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.expose.v2.req.PicVehicleFrontReq;
import cn.trawe.etc.hunanfront.expose.v2.resp.PicVehicleFrontResp;
import cn.trawe.etc.hunanfront.service.v2.PicServiceI;
import cn.trawe.etc.hunanfront.utils.ImageUtils;
import cn.trawe.pay.expose.request.issue.ImageUploadReq;
import cn.trawe.pay.expose.response.issue.BizResp;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class PicServiceImpl extends BaseServiceImpl implements PicServiceI {
	
	
	@Value("${image.size}")
	private int config_imagesize;
	
	@Override
	public PicVehicleFrontResp vehicleFrontUpload(PicVehicleFrontReq req, ThirdPartner partner) {
		PicVehicleFrontResp resp = new PicVehicleFrontResp();
		try {
			
			LogUtil.info(log, req.getOrderId(), "车头照请求:"+req.toString());
			if(config_imagesize>0) {
	    		Integer imageSizeFront = ImageUtils.imageSize(req.getImage());
	    		if(imageSizeFront>config_imagesize) {
	    			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
	    			resp.setErrorMsg("图片大小不合法，单张图片大小不超过"+config_imagesize+"KB"+",当前图片大小为"+imageSizeFront+"KB");
	    			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
	    			LogUtil.info(log, req.getOrderId(), "车头照响应:"+JSON.toJSONString(resp));
	    			return resp;
	    		}
	    		
	    	}
			ImageUploadReq centerReq = new ImageUploadReq();
			LogUtil.info(log, req.getOrderId(), "车头照保存中台请求:"+req.toString());
			BizResp centerResp = IssueCenterApi.imageUpload(centerReq);
			LogUtil.info(log, req.getOrderId(), "车头照保存中台响应:"+JSON.toJSONString(centerResp));
			if(InterfaceCenter.TIMEOUT.getCode()==centerResp.getCode()) {
				resp.setErrorMsg(centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
				queryMonitorServiceImpl.send("imageUpload", "服务降级");
				LogUtil.info(log, req.getOrderId(), "车头照响应:"+JSON.toJSONString(resp));
				return resp;
			}
			if(InterfaceCenter.FAIL.getCode()==centerResp.getCode()) {
				resp.setErrorCode(centerResp.getErrorCode());
				resp.setErrorMsg(centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "车头照响应:"+JSON.toJSONString(resp));
				return resp;
			}
			resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
			resp.setErrorMsg(centerResp.getMsg());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "车头照响应:"+JSON.toJSONString(resp));
			return resp;
		}
		catch(Exception e) {
			LogUtil.error(log, req.getOrderId(), e.getMessage(), e.getCause());
			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
			resp.setErrorMsg(e.getMessage());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "车头照响应:"+JSON.toJSONString(resp));
			return resp;
		}
		
	}

}
