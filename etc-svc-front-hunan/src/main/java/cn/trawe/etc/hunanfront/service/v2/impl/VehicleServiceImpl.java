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
import cn.trawe.etc.hunanfront.expose.v2.req.VehicleFrontImageReq;
import cn.trawe.etc.hunanfront.expose.v2.req.VehicleSaveReq;
import cn.trawe.etc.hunanfront.expose.v2.req.VehicleSubmitReq;
import cn.trawe.etc.hunanfront.expose.v2.resp.VehicleFrontImageResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.VehicleSaveResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.VehicleSubmitResp;
import cn.trawe.etc.hunanfront.service.v2.VehicleServiceI;
import cn.trawe.etc.hunanfront.utils.ImageUtils;
import cn.trawe.pay.expose.request.issue.CarInfoSaveReq;
import cn.trawe.pay.expose.request.issue.CarInfoSubmitReq;
import cn.trawe.pay.expose.request.issue.ImageUploadReq;
import cn.trawe.pay.expose.response.BaseResponse;
import cn.trawe.pay.expose.response.issue.BizResp;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class VehicleServiceImpl extends BaseServiceImpl implements VehicleServiceI {
	
	@Value("${image.size}")
	private int config_imagesize;

	@Override
	public VehicleSaveResp vehicleSave(VehicleSaveReq req, ThirdPartner partner) {
		LogUtil.info(log, req.getOrderId(), "车辆信息保存请求:"+req.toString());
		CarInfoSaveReq centerReq = new CarInfoSaveReq();
		VehicleSaveResp resp = new VehicleSaveResp();
//		boolean isGetLock = false;
//	    String lockKey = VEHICLE_ORDER_SAVE_LOCKKEY + req.getOrderId();
	        try {

//	            try {
//	                isGetLock = redisClient.tryLock(lockKey, TIMEOUT_LOCK, TRY_LOCK_COUNT);
//	            } catch (Throwable ex) {
//	            	LogUtil.error(log, req.getOrderId(), "获取锁出错异常：" + ex.getMessage());
//	                resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
//	     			resp.setErrorMsg("获取锁出错异常:" + ex.getMessage());
//	     			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
//	     			LogUtil.info(log, req.getOrderId(), "车辆信息保存响应:"+JSON.toJSONString(resp));
//	     			return resp;
//	            }
//
//	            if (!isGetLock) {
//	            	LogUtil.error(log, req.getOrderId(), "重复请求" );
//	            	resp.setErrorCode(BaseResponseData.ErrorCode.OTHER_ERROR.toString());
//	     			resp.setErrorMsg("重复请求");
//	     			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
//	     			LogUtil.info(log, req.getOrderId(), "车辆信息保存响应:"+JSON.toJSONString(resp));
//	     			return resp;
//	            }
			
			centerReq.setOuterOrderId(req.getOrderId());
			centerReq.setPlateNo(req.getViNumber());
			centerReq.setPlateColor(Integer.valueOf(req.getViPlateColor()));
			centerReq.setOwner(req.getViOwnerName());
			centerReq.setModel(req.getViModelName());
			centerReq.setVin(req.getViVin());
			centerReq.setVehicleType(req.getViType());
			centerReq.setRegisterDate(req.getViStartTime());
			centerReq.setIssueDate(req.getViGrantTime());
			centerReq.setEngineNo(req.getEngineNo());
			centerReq.setSeats(Integer.valueOf(req.getViAc()));
			//车辆使用性质
			//TODO
			//映射为2 或者 3
			centerReq.setVehicleUseCharacter(req.getViUseType());
			centerReq.setOverallDimension(req.getViLength() + "X" + req.getViWidth() + "X" + req.getViHeight());
			//总质量
			centerReq.setGrossMass(String.valueOf(req.getViTotalMass()));
			//整备质量
			centerReq.setUnladenMass(String.valueOf(req.getViReadinessMass()));
			//档案编号
			centerReq.setFileNo(req.getViLicenseNo());
			//检验记录
			centerReq.setInspectionRecord(req.getViInspectionRecord());
	        //车轮
			centerReq.setVehicleWheels(String.valueOf(req.getViVehicleWheels()));
			//车轴
			centerReq.setVehicleAxles(String.valueOf(req.getViVehicleAxles()));
			if(config_imagesize>0) {
	    		Integer imageSizeFront = ImageUtils.imageSize(req.getViVehiclePicFront());
	    		if(imageSizeFront>config_imagesize) {
	    			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
	    			
	    				

	    			resp.setErrorMsg("图片大小不合法，单张图片大小不超过"+config_imagesize+"KB"+",当前图片大小为"+imageSizeFront+"KB");
	    			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
	    			LogUtil.info(log, req.getOrderId(), "车辆信息保存响应:"+JSON.toJSONString(resp));
	    			return resp;
	    		}
	    		Integer imageSizeBack = ImageUtils.imageSize(req.getViVehiclePicBack());
	    		if(imageSizeBack>config_imagesize) {
	    			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
	    			resp.setErrorMsg("图片大小不合法，单张图片大小不超过"+config_imagesize+"KB"+",当前图片大小为"+imageSizeBack+"KB");
	    			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
	    			LogUtil.info(log, req.getOrderId(), "车辆信息保存响应:"+JSON.toJSONString(resp));
	    			return resp;
	    		}
	    	}
			centerReq.setDrivingBackPhoto(req.getViVehiclePicBack());
			centerReq.setDrivingFrontPhoto(req.getViVehiclePicFront());
			centerReq.setPhotoType(1);
			centerReq.setCarType(req.getViVehicleType());
			LogUtil.info(log, req.getOrderId(), "车辆信息保存中台请求:"+req.toString());
			BaseResponse centerResp = IssueCenterApi.carInfoSave(centerReq);
			LogUtil.info(log, req.getOrderId(), "车辆信息保存中台响应:"+JSON.toJSONString(centerResp));
			if(InterfaceCenter.TIMEOUT.getCode()==centerResp.getCode()) {
				resp.setErrorMsg(centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
				queryMonitorServiceImpl.send("carInfoSave", "服务降级");
				LogUtil.info(log, req.getOrderId(), "车辆信息保存响应:"+JSON.toJSONString(resp));
				return resp;
			}
			if(InterfaceCenter.SUCCESS.getCode()!=centerResp.getCode()) {
				resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
				resp.setErrorMsg(centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "车辆信息保存响应:"+JSON.toJSONString(resp));
				return resp;
			}
			resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
			resp.setErrorMsg("成功");
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "车辆信息保存响应:"+JSON.toJSONString(resp));
			return resp;
		}
		catch (Exception e) {
			LogUtil.error(log, req.getOrderId(), e.getMessage(), e.getCause());
			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
			resp.setErrorMsg(e.getMessage());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "车辆信息保存响应:"+JSON.toJSONString(resp));
			return resp;
		}
//	        finally {
//            if (isGetLock) {
//                redisClient.unlock(lockKey);
//            }
//        }
		
	}

	@Override
	public VehicleSubmitResp vehicleSubmit(VehicleSubmitReq req, ThirdPartner partner) {
		LogUtil.info(log, req.getOrderId(), "车辆信息提交请求:"+req.toString());
		CarInfoSubmitReq centerReq = new CarInfoSubmitReq();
		VehicleSubmitResp resp = new VehicleSubmitResp();
		boolean isGetLock = false;
	    String lockKey = ISSUE_ORDER_SUBMIT_LOCKKEY + req.getOrderId();
	        try {

	            try {
	                isGetLock = redisClient.tryLock(lockKey, TIMEOUT_LOCK, TRY_LOCK_COUNT);
	            } catch (Throwable ex) {
	            	LogUtil.error(log, req.getOrderId(), "获取锁出错异常：" + ex.getMessage());
	                resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
	     			resp.setErrorMsg("获取锁出错异常:" + ex.getMessage());
	     			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
	     			LogUtil.info(log, req.getOrderId(), "车辆信息提交响应:"+JSON.toJSONString(resp));
	     			return resp;
	            }

	            if (!isGetLock) {
	            	LogUtil.error(log, req.getOrderId(), "重复请求" );
	            	resp.setErrorCode(BaseResponseData.ErrorCode.OTHER_ERROR.toString());
	     			resp.setErrorMsg("重复请求");
	     			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
	     			LogUtil.info(log, req.getOrderId(), "车辆信息提交响应:"+JSON.toJSONString(resp));
	     			return resp;
	            }
			centerReq.setOuterOrderId(req.getOrderId());
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
	    	centerReq.setNote1(object.toJSONString());
			LogUtil.info(log, req.getOrderId(), "车辆信息提交中台请求:"+JSON.toJSONString(centerReq));
			BizResp centerResp = IssueCenterApi.carInfoSubmit(centerReq);
			LogUtil.info(log, req.getOrderId(), "车辆信息提交中台响应:"+	JSON.toJSONString(centerResp));
			if(InterfaceCenter.TIMEOUT.getCode()==centerResp.getCode()) {
				resp.setErrorMsg(centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
				queryMonitorServiceImpl.send("carInfoSubmit", "服务降级");
				LogUtil.info(log, req.getOrderId(), "车辆信息提交响应:"+JSON.toJSONString(resp));
				return resp;
			}
			if(InterfaceCenter.SUCCESS.getCode()!=centerResp.getCode()) {
				resp.setErrorCode(StringUtils.isBlank(centerResp.getErrorCode())?BaseResponseData.ErrorCode.SYSTEM_ERROR.toString():centerResp.getErrorCode());
				resp.setErrorMsg(StringUtils.isBlank(centerResp.getMsg())?IssueV2Constants.SYSTEM_ERROR_INFO:centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "车辆信息提交响应:"+JSON.toJSONString(resp));
				return resp;
			}
			if(!"0000".equals(centerResp.getErrorCode())) {
				resp.setErrorCode(StringUtils.isBlank(centerResp.getErrorCode())?BaseResponseData.ErrorCode.SYSTEM_ERROR.toString():centerResp.getErrorCode());
				resp.setErrorMsg(StringUtils.isBlank(centerResp.getMsg())?IssueV2Constants.SYSTEM_ERROR_INFO:centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "车辆信息提交响应:"+JSON.toJSONString(resp));
				return resp;
			}
			resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
			resp.setErrorMsg(centerResp.getMsg());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "车辆信息提交响应:"+JSON.toJSONString(resp));
			return resp;
		}
		catch (Exception e) {
			LogUtil.error(log, req.getOrderId(), e.getMessage(), e.getCause());
			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
			resp.setErrorMsg(e.getMessage());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "车辆信息提交响应:"+JSON.toJSONString(resp));
			return resp;
		} finally {
            if (isGetLock) {
                redisClient.unlock(lockKey);
            }
        }
		
	}

	@Override
	public VehicleFrontImageResp vehicleFrontPicUpload(VehicleFrontImageReq req, ThirdPartner partner) {
		LogUtil.info(log, req.getOrderId(), "车头照片提交请求:"+req.toString());
		ImageUploadReq centerReq = new ImageUploadReq();
		VehicleFrontImageResp resp = new VehicleFrontImageResp();
		try {
			centerReq.setOuterOrderId(req.getOrderId());
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
	    	centerReq.setNote1(object.toJSONString());
	    	
	    	centerReq.setImageType(5);
	    	centerReq.setPhotoType(1);
			LogUtil.info(log, req.getOrderId(), "车头照片提交中台请求:"+JSON.toJSONString(centerReq));
			centerReq.setImage(req.getImage());
			BizResp centerResp = IssueCenterApi.imageUpload(centerReq);
			LogUtil.info(log, req.getOrderId(), "车头照片提交中台响应:"+	JSON.toJSONString(centerResp));
			if(InterfaceCenter.TIMEOUT.getCode()==centerResp.getCode()) {
				resp.setErrorMsg(centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
				queryMonitorServiceImpl.send("imageUpload", "服务降级");
				LogUtil.info(log, req.getOrderId(), "车头照片提交响应:"+JSON.toJSONString(resp));
				return resp;
			}
			if(InterfaceCenter.SUCCESS.getCode()!=centerResp.getCode()) {
				resp.setErrorCode(StringUtils.isBlank(centerResp.getErrorCode())?BaseResponseData.ErrorCode.SYSTEM_ERROR.toString():centerResp.getErrorCode());
				resp.setErrorMsg(StringUtils.isBlank(centerResp.getMsg())?IssueV2Constants.SYSTEM_ERROR_INFO:centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "车头照片提交响应:"+JSON.toJSONString(resp));
				return resp;
			}
			resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
			resp.setErrorMsg(centerResp.getMsg());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "车头照片提交响应:"+JSON.toJSONString(resp));
			return resp;
		}
		catch (Exception e) {
			LogUtil.error(log, req.getOrderId(), e.getMessage(), e.getCause());
			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
			resp.setErrorMsg(e.getMessage());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "车头照片提交响应:"+JSON.toJSONString(resp));
			return resp;
		}
	}

}
