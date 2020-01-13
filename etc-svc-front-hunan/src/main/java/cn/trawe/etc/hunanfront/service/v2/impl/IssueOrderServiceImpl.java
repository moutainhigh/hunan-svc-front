package cn.trawe.etc.hunanfront.service.v2.impl;

import java.util.Date;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.trawe.etc.hunanfront.config.InterfaceCenter;
import cn.trawe.etc.hunanfront.config.IssueV2Constants;
import cn.trawe.etc.hunanfront.dao.EtcIssueOrderHistroyDAO;
import cn.trawe.etc.hunanfront.dao.SecondIssueDAO;
import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseReq;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderCancelReq;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderModifyByAccountReq;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderModifyByPhoneReq;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderQueryReq;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderSubmitReq;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderCancelResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderModifyByAccountResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderModifyByPhoneResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderQueryResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderSubmitResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.SecondIssueQueryResp;
import cn.trawe.etc.hunanfront.feign.entity.hunan.HunanGatewayBaseResp;
import cn.trawe.etc.hunanfront.feign.entity.hunan.UserInfoModifyReq;
import cn.trawe.etc.hunanfront.feign.v2.GatewayHunanApiImpl;
import cn.trawe.etc.hunanfront.feign.v2.UserClient;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueResp;
import cn.trawe.etc.hunanfront.service.BaseService;
import cn.trawe.etc.hunanfront.service.database.EtcCardService;
import cn.trawe.etc.hunanfront.service.secondissue.replace.BussinessReplaceCard;
import cn.trawe.etc.hunanfront.service.secondissue.replace.BussinessReplaceCardAndObu;
import cn.trawe.etc.hunanfront.service.secondissue.replace.BussinessReplaceObu;
import cn.trawe.etc.hunanfront.service.secondissue.replace.GatewayReplaceCardAndObuInit;
import cn.trawe.etc.hunanfront.service.secondissue.replace.GatewayReplaceCardInit;
import cn.trawe.etc.hunanfront.service.secondissue.replace.GatewayReplaceObuInit;
import cn.trawe.etc.hunanfront.service.v2.IssueServiceI;
import cn.trawe.pay.common.etcmsg.EtcObjectResponse;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.entity.EtcIssueOrderHistroy;
import cn.trawe.pay.expose.entity.IssueEtcCard;
import cn.trawe.pay.expose.entity.SecondIssueProcess;
import cn.trawe.pay.expose.request.issue.GetOrderReq;
import cn.trawe.pay.expose.request.issue.IssueThirdOrderCancelReq;
import cn.trawe.pay.expose.request.issue.QueryByOrderReq;
import cn.trawe.pay.expose.request.issue.SubmitObuOrderReq;
import cn.trawe.pay.expose.request.secondissue.ActivationQueryReq;
import cn.trawe.pay.expose.response.GlobalResponse;
import cn.trawe.pay.expose.response.IdCardRes;
import cn.trawe.pay.expose.response.issue.IssueEtcCardResp;
import cn.trawe.pay.expose.response.issue.IssueSaveResp;
import cn.trawe.pay.expose.response.issue.NullResp;
import cn.trawe.pay.expose.response.issue.SubmitObuOrderResp;
import cn.trawe.pay.expose.response.secondissue.ActivationQueryResp;
import cn.trawe.util.LogUtil;
import cn.trawe.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class IssueOrderServiceImpl extends BaseService implements IssueServiceI {
	
	@Autowired
	private BussinessReplaceCard bussinessReplaceCard;
	
	@Autowired
	private BussinessReplaceCardAndObu bussinessReplaceCardAndObu;
	
	@Autowired
	private BussinessReplaceObu bussinessReplaceObu;
	
	@Autowired
	GatewayReplaceCardInit gatewayReplaceCardInit;
	
	@Autowired
	GatewayReplaceObuInit gatewayReplaceObuInit;
	
	@Autowired
	GatewayReplaceCardAndObuInit gatewayReplaceCardAndObuInit;
	
	 @Autowired
	 SecondIssueDAO secondIssueDAO;
	
	@Autowired
	UserClient userClient;
	
	@Autowired
	private GatewayHunanApiImpl gateWayHunan;
	@Autowired  
	EtcCardService  EtcCardService;
	
	  @Autowired
	    EtcIssueOrderHistroyDAO etcIssueOrderHistroyDAO;

	@Override
  	public IssueOrderSubmitResp issueOrderSubmit(IssueOrderSubmitReq req, ThirdPartner partner) {
		LogUtil.info(log, req.getOrderId(), "订单提交请求:"+req.toString());
		IssueOrderSubmitResp resp = new IssueOrderSubmitResp();
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
	     			LogUtil.info(log, req.getOrderId(), "订单提交响应:"+JSON.toJSONString(resp));
	     			return resp;
	            }

	            if (!isGetLock) {
	            	LogUtil.error(log, req.getOrderId(), "重复请求" );
	            	resp.setErrorCode(BaseResponseData.ErrorCode.OTHER_ERROR.toString());
	     			resp.setErrorMsg("重复请求");
	     			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
	     			LogUtil.info(log, req.getOrderId(), "订单提交响应:"+JSON.toJSONString(resp));
	     			return resp;
	            }
	        	SubmitObuOrderReq centerReq = new SubmitObuOrderReq();
	    		centerReq.setOuterOrderId(req.getOrderId());
	    		centerReq.setOrgCode(req.getSignChannel());
	    		centerReq.setSignCardType(req.getSignCardType());
	    		centerReq.setAccountNo(req.getSignAccount());
	    		centerReq.setReceiverName(req.getContactName());
	    		centerReq.setReceiverPhone(req.getContactTel());
	    		centerReq.setReceiverAddress(req.getProvinceName()+req.getCityName()+req.getDistrictName()+req.getAddress());
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
	    		if(IssueV2Constants.FRONT_INSTALL_TYPE_PICK_UP.equals(req.getInstallStatus())) {
	    			centerReq.setInstallType(IssueV2Constants.CENTER_INSTALL_TYPE_PICK_UP);
	    			LogUtil.info(log, req.getOrderId(), "中台订单提交请求:"+JSON.toJSONString(centerReq));
		        	SubmitObuOrderResp centerResp = IssueCenterApi.submitObuOrder(centerReq);
		    		LogUtil.info(log, req.getOrderId(), "中台订单提交响应:"+JSON.toJSONString(centerResp));
		    		if(InterfaceCenter.TIMEOUT.getCode()==centerResp.getCode()) {
		    			resp.setErrorMsg(centerResp.getMsg());
		    			resp.setSuccess(BaseResponseData.Success.FAILED.toString());
		    			queryMonitorServiceImpl.send("submitObuOrder", "服务降级");
		    			LogUtil.info(log, req.getOrderId(), "订单提交响应:"+JSON.toJSONString(resp));
		    			return resp;
		    		}
		    		if(InterfaceCenter.SUCCESS.getCode()!=centerResp.getCode()) {
		    			resp.setErrorCode(StringUtils.isBlank(centerResp.getErrorCode())?BaseResponseData.ErrorCode.SYSTEM_ERROR.toString():centerResp.getErrorCode());
		    			resp.setErrorMsg(centerResp.getMsg());
		    			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		    			LogUtil.info(log, req.getOrderId(), "订单提交响应:"+JSON.toJSONString(resp));
		    			return resp;
		    		}
		    		//TODO
		    		resp.setOrderStatus(IssueV2Constants.orderStatusConvert(centerResp.getOrderStatus()));
		    		resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
		    		resp.setErrorMsg(centerResp.getMsg());
		    		resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		    		LogUtil.info(log, req.getOrderId(), "订单提交响应:"+JSON.toJSONString(resp));
		    		return resp;
	    		}
	    		else if(IssueV2Constants.FRONT_INSTALL_TYPE_POST.equals(req.getInstallStatus())) {
	    			centerReq.setInstallType(IssueV2Constants.CENTER_INSTALL_TYPE_POST);
	    			GetOrderReq getOrder = new GetOrderReq();
	    			getOrder.setOutOrderId(req.getOrderId());
	    	    	LogUtil.info(log, req.getOrderId(), "中台订单查询请求:"+JSON.toJSONString(getOrder));
	    			EtcObjectResponse<EtcIssueOrder> getOrderResp = IssueCenterApi.getOrder(getOrder);
	    			LogUtil.info(log, req.getOrderId(), "中台订单查询响应:"+JSON.toJSONString(getOrderResp));
	    			if(InterfaceCenter.TIMEOUT.getCode()==getOrderResp.getCode()) {
	    				resp.setErrorMsg(getOrderResp.getMsg());
	    				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
	    				LogUtil.info(log, req.getOrderId(), "订单查询响应:"+JSON.toJSONString(resp));
	    				return resp;
	    			}
	    			if(InterfaceCenter.SUCCESS.getCode()!=getOrderResp.getCode()) {
	    				resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
	    				resp.setErrorMsg(getOrderResp.getMsg());
	    				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
	    				LogUtil.info(log, req.getOrderId(), "订单查询响应:"+JSON.toJSONString(resp));
	    				return resp;
	    			}
	    			
	    		    if (ValidateUtil.isEmpty(getOrderResp.getData())) {
	    		    	resp.setErrorCode(BaseResponseData.ErrorCode.OTHER_ERROR.toString());
	    				resp.setErrorMsg("订单不存在");
	    				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
	    				LogUtil.info(log, req.getOrderId(), "订单查询响应:"+JSON.toJSONString(resp));
	    				return resp;
	    		    }
	    		          
	    		    EtcIssueOrder order =getOrderResp.getData();
	    			//修改订单信息
//	    		    centerReq.setOuterOrderId(req.getOrderId());
//		    		centerReq.setOrgCode(req.getSignChannel());
//		    		centerReq.setSignCardType(req.getSignCardType());
//		    		centerReq.setAccountNo(req.getSignAccount());
//		    		centerReq.setReceiverName(req.getContactName());
//		    		centerReq.setReceiverPhone(req.getContactTel());
//		    		centerReq.setReceiverAddress(req.getProvinceName()+req.getCityName()+req.getDistrictName()+req.getAddress());
	    		    order.setReceiverAddress(req.getProvinceName()+req.getCityName()+req.getDistrictName()+req.getAddress());
	    		    order.setReceiverName(req.getContactName());
	    		    order.setReceiverPhone(req.getContactTel());
	    		    order.setAccountNo(req.getSignAccount());
	    		    order.setOrgCode(req.getSignChannel());
	    		    order.setSignCardType(req.getSignCardType());
	    		    order.setAccountNo(req.getSignAccount());
	    		    order.setOrderStatus(3);
	    		    order.setNote1(object.toJSONString());
	    		    order.setInstallType(6);
	    		    order.setAuditDesc("待审核");
	    			LogUtil.info(log, req.getOrderId(), "中台修改订单请求:"+JSON.toJSONString(order));
	    			IssueSaveResp respCenter = IssueCenterApi.edit((JSONObject)JSON.toJSON(order));
	    			LogUtil.info(log, req.getOrderId(), "中台修改订单响应:"+JSON.toJSONString(respCenter));
	    			if(InterfaceCenter.SUCCESS.getCode()!=respCenter.getCode()) {
	    				resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
	    				resp.setErrorMsg(respCenter.getMsg());
	    				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
	    				LogUtil.info(log, req.getOrderId(), "修改订单响应:"+JSON.toJSONString(resp));
	    				return resp;
	    			}
	    			resp.setOrderStatus(IssueV2Constants.FRONT_ORDER_STATUS_WAIT_AUDIT);
		    		resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
		    		resp.setErrorMsg("订单信息保存成功");
		    		resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		    		LogUtil.info(log, req.getOrderId(), "订单提交响应:"+JSON.toJSONString(resp));
		    		return resp;
	    		}
	    		else {
	    			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
    				resp.setErrorMsg("不支持的安装类型");
    				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
    				LogUtil.info(log, req.getOrderId(), "订单提交响应:"+JSON.toJSONString(resp));
    				return resp;
	    		}
	    		
	        	
	        } catch (Exception ex) {
	        	LogUtil.error(log, req.getOrderId(), ex.getMessage(), ex.getCause());
	        	resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
				resp.setErrorMsg(ex.getMessage());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "订单提交响应:"+JSON.toJSONString(resp));
				return resp;
	        } finally {
	            if (isGetLock) {
	                redisClient.unlock(lockKey);
	            }
	        }
	
	}

	@Override
	public IssueOrderCancelResp issueOrderCancel(IssueOrderCancelReq req, ThirdPartner partner) {
		LogUtil.info(log, req.getOrderId(), "订单取消请求:"+req.toString());
		IssueOrderCancelResp resp = new IssueOrderCancelResp();
		boolean isGetLock = false;
        String lockKey = ISSUE_ORDER_CANCEL_LOCKKEY + req.getOrderId();
        try {

            try {
                isGetLock = redisClient.tryLock(lockKey, TIMEOUT_LOCK, TRY_LOCK_COUNT);
            } catch (Throwable ex) {
                LogUtil.error(log, req.getOrderId(), "获取锁出错异常：" + ex.getMessage());
                resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
    			resp.setErrorMsg("获取锁出错异常:" + ex.getMessage());
    			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
    			LogUtil.info(log, req.getOrderId(), "订单取消响应:"+JSON.toJSONString(resp));
    			return resp;
            }

            if (!isGetLock) {
            	LogUtil.error(log, req.getOrderId(), "重复请求" );
                
            	resp.setErrorCode(BaseResponseData.ErrorCode.OTHER_ERROR.toString());
     			resp.setErrorMsg("重复请求");
     			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
     			LogUtil.info(log, req.getOrderId(), "订单取消响应:"+JSON.toJSONString(resp));
     			return resp;
            }
            
//            //是否允许取消
//            ThirdOutOrderQueryReq thirdOutOrderQueryReq = new ThirdOutOrderQueryReq();
//            thirdOutOrderQueryReq.setOutOrderId(req.getOrderId());
//            LogUtil.info(log, req.getOrderId(), "中台三方订单查询请求:"+JSON.toJSONString(thirdOutOrderQueryReq));
//            ThirdOutOrderQueryResp thirdOutOrderQueryResp = IssueCenterApi.queryThirdOutOrder(thirdOutOrderQueryReq);
//            LogUtil.info(log, req.getOrderId(), "中台三方订单查询响应:"+JSON.toJSONString(thirdOutOrderQueryResp));
//            if(thirdOutOrderQueryResp==null) {
//            	throw new RuntimeException("网络异常,请稍后重试");
//            }
//            //未查询到该订单
//            if (thirdOutOrderQueryResp.getCode() != 0) {
//            	resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
//        		resp.setErrorMsg("成功");
//        		resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
//        		LogUtil.info(log, req.getOrderId(), "订单取消响应:"+JSON.toJSONString(resp));
//        		return resp;
//            }
          //根据外部订单号查询网发订单号
            String outOrderId  =req.getOrderId();
    		GetOrderReq centerReq1 = new GetOrderReq();
    		centerReq1.setOutOrderId(outOrderId);
        	LogUtil.info(log, outOrderId, "中台查询订单请求:"+JSON.toJSONString(centerReq1));
    		EtcObjectResponse<EtcIssueOrder> centerResp1 = IssueCenterApi.getOrder(centerReq1);
    		LogUtil.info(log, outOrderId, "中台查询订单响应:"+JSON.toJSONString(centerResp1));
    		if(InterfaceCenter.TIMEOUT.getCode()==centerResp1.getCode()) {
    			
    			throw new RuntimeException("网络异常,请稍后重试");
    		}
    		if(InterfaceCenter.SUCCESS.getCode()!=centerResp1.getCode()) {
    			throw new RuntimeException("系统异常,请稍后重试");
    		}
    		
    	    if (ValidateUtil.isEmpty(centerResp1.getData())) {
    	    	
    	    	resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
        		resp.setErrorMsg("成功");
        		resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
        		LogUtil.info(log, req.getOrderId(), "订单取消响应:"+JSON.toJSONString(resp));
        		return resp;
    	    }
    	          
    	    EtcIssueOrder order =centerResp1.getData();
    	    
    	    QueryByOrderReq reqQueryCard = new QueryByOrderReq();
    		reqQueryCard.setOrderNo(order.getOrderNo());
    		reqQueryCard.setOwnerCode(4301);
    		LogUtil.info(log, outOrderId, "查询卡信息请求", JSON.toJSONString(reqQueryCard));
    		IssueEtcCardResp<IssueEtcCard> cardResp = IssueCenterApi.queryByOrderNo(reqQueryCard);
    		LogUtil.info(log, outOrderId, "查询卡信息响应", cardResp);
    		if(cardResp.getCode()==1) {
    			throw new RuntimeException("系统异常,请稍后重试");
    		}
    		if(cardResp.getCode()==0&&cardResp.getResult()!=null) {
    			throw new RuntimeException("存在卡表信息不允许取消,卡号:"+cardResp.getResult().getCardNo()+"车牌号:"+cardResp.getResult().getPlateNo());
    		}
            IssueThirdOrderCancelReq centerReq = new IssueThirdOrderCancelReq();
    		centerReq.setOuterOrderId(req.getOrderId());
    		
        	LogUtil.info(log, req.getOrderId(), "中台订单取消请求:"+JSON.toJSONString(centerReq));
    		GlobalResponse<NullResp> centerResp = IssueCenterApi.thirdOrderCancel(centerReq);
    		LogUtil.info(log, req.getOrderId(), "中台订单取消响应:"+JSON.toJSONString(centerResp));
    		if(InterfaceCenter.TIMEOUT.getCode()==centerResp.getCode()) {
    			resp.setErrorMsg(centerResp.getMsg());
    			resp.setSuccess(BaseResponseData.Success.FAILED.toString());
    			queryMonitorServiceImpl.send("thirdOrderCancel", "服务降级");
    			LogUtil.info(log, req.getOrderId(), "订单取消响应:"+JSON.toJSONString(resp));
    			return resp;
    		}
    		if(InterfaceCenter.SUCCESS.getCode()!=centerResp.getCode()) {
    			resp.setErrorCode(StringUtils.isBlank(centerResp.getErrorCode())?BaseResponseData.ErrorCode.SYSTEM_ERROR.toString():centerResp.getErrorCode());
    			resp.setErrorMsg(centerResp.getMsg());
    			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
    			LogUtil.info(log, req.getOrderId(), "订单取消响应:"+JSON.toJSONString(resp));
    			return resp;
    		}
    		resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
    		resp.setErrorMsg("订单取消成功");
    		resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
    		LogUtil.info(log, req.getOrderId(), "订单取消响应:"+JSON.toJSONString(resp));
    		return resp;
           
        } catch (Exception ex) {
        	LogUtil.error(log, req.getOrderId(), ex.getMessage(), ex);
        	resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
			resp.setErrorMsg(ex.getMessage());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "订单取消响应:"+JSON.toJSONString(resp));
			return resp;
        } finally {
            if (isGetLock) {
                redisClient.unlock(lockKey);
            }
        }
		
		
	}

	@Override
	public IssueOrderQueryResp issueOrderQuery(IssueOrderQueryReq req, ThirdPartner partner) {
		LogUtil.info(log, req.getOrderId(), "订单查询请求:"+JSON.toJSONString(req));
		IssueOrderQueryResp resp = new IssueOrderQueryResp();
		try {
//			cn.trawe.pay.expose.request.issue.IssueOrderQueryReq  centerReq = new cn.trawe.pay.expose.request.issue.IssueOrderQueryReq();
//			centerReq.setOwnerCode("4301");
//			centerReq.setPageNo(1);
//			centerReq.setPageSize(10);
//			//TODO
//			centerReq.setOrderNo("");
			GetOrderReq centerReq = new GetOrderReq();
			centerReq.setOutOrderId(req.getOrderId());
	    	LogUtil.info(log, req.getOrderId(), "中台订单查询请求:"+JSON.toJSONString(centerReq));
			EtcObjectResponse<EtcIssueOrder> centerResp = IssueCenterApi.getOrder(centerReq);
			LogUtil.info(log, req.getOrderId(), "中台订单查询响应:"+JSON.toJSONString(centerResp));
			if(InterfaceCenter.TIMEOUT.getCode()==centerResp.getCode()) {
				resp.setErrorMsg(centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
				LogUtil.info(log, req.getOrderId(), "订单查询响应:"+JSON.toJSONString(resp));
				return resp;
			}
			if(InterfaceCenter.SUCCESS.getCode()!=centerResp.getCode()) {
				resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
				resp.setErrorMsg(centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "订单查询响应:"+JSON.toJSONString(resp));
				return resp;
			}
			
		    if (ValidateUtil.isEmpty(centerResp.getData())) {
		    	resp.setErrorCode(BaseResponseData.ErrorCode.OTHER_ERROR.toString());
				resp.setErrorMsg("订单不存在");
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "订单查询响应:"+JSON.toJSONString(resp));
				return resp;
		    }
		          
		    EtcIssueOrder order =centerResp.getData();
			resp.setOrderStatus(IssueV2Constants.orderStatusConvert(order.getOrderStatus()));
			if (ValidateUtil.isNotEmpty(order.getAuditDesc()))
				resp.setCensorInfo(order.getAuditDesc());
	        if (ValidateUtil.isNotEmpty(order.getDeliveryName())) {
	        	resp.setDeliveryName(order.getDeliveryName());
	        }
	        if (ValidateUtil.isNotEmpty(order.getDeliveryCode()))
	        	resp.setDeliveryNo(order.getDeliveryCode());
	        
	        IssueEtcCard card = queryCardInfo(req.getOrderId(),order.getOrderNo(),order.getOwnerCode());

	        if (card != null) {
	           
	            resp.setCardNo(card.getCardNo());
	            resp.setObuNo(card.getObuCode());
	            resp.setDeviceStatus(IssueV2Constants.FRONT_CARD_DEVICE_STATUS_FINISH);
	            
	        }
			resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
			resp.setErrorMsg(centerResp.getMsg());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "订单查询响应:"+JSON.toJSONString(resp));
			return resp;
		}
		catch(Exception e) {
			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
			resp.setErrorMsg(e.getMessage());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "订单查询响应:"+JSON.toJSONString(resp));
			return resp;
		}
		
	}

	@Override
	public IssueOrderModifyByPhoneResp issueOrderModifyByPhone(IssueOrderModifyByPhoneReq req, ThirdPartner partner) {
		LogUtil.info(log, req.getOrderId(), "修改手机号请求:"+JSON.toJSONString(req));
		IssueOrderModifyByPhoneResp resp = new IssueOrderModifyByPhoneResp();
		try {
			GetOrderReq centerReq = new GetOrderReq();
			centerReq.setOutOrderId(req.getOrderId());
	    	LogUtil.info(log, req.getOrderId(), "中台查询订单请求:"+JSON.toJSONString(centerReq));
			EtcObjectResponse<EtcIssueOrder> centerResp = IssueCenterApi.getOrder(centerReq);
			LogUtil.info(log, req.getOrderId(), "中台查询订单响应:"+JSON.toJSONString(centerResp));
			if(InterfaceCenter.TIMEOUT.getCode()==centerResp.getCode()) {
				resp.setErrorMsg(centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
				LogUtil.info(log, req.getOrderId(), "修改手机号响应:"+JSON.toJSONString(resp));
				return resp;
			}
			if(InterfaceCenter.SUCCESS.getCode()!=centerResp.getCode()) {
				resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
				resp.setErrorMsg(centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "修改手机号响应:"+JSON.toJSONString(resp));
				return resp;
			}
			
		    if (ValidateUtil.isEmpty(centerResp.getData())) {
		    	resp.setErrorCode(BaseResponseData.ErrorCode.OTHER_ERROR.toString());
				resp.setErrorMsg("订单不存在");
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "修改手机号响应:"+JSON.toJSONString(resp));
				return resp;
		    }
		          
		    EtcIssueOrder order =centerResp.getData();
		    if(!order.getMobile().equals(req.getOldPhone())) {
		    	resp.setErrorCode(BaseResponseData.ErrorCode.OTHER_ERROR.toString());
				resp.setErrorMsg("原手机号不一致");
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "修改手机号响应:"+JSON.toJSONString(resp));
				return resp;
		    }
		    
		    //调用省网关修改手机号
		    IdCardRes findIdCardByOrderNo = userClient.findIdCardByOrderNo(order.getOrderNo());
		    UserInfoModifyReq reqGate = new UserInfoModifyReq();
		    reqGate.setAddress(findIdCardByOrderNo.getAddress());
		    reqGate.setIdNum(findIdCardByOrderNo.getIdCardNo());
		    reqGate.setIdType(0);
		    reqGate.setName(findIdCardByOrderNo.getName());
	    	//req.setNote1();
	    	//req.setOrderNo("4319101010432841015");
		    reqGate.setTel(req.getNewPhone());
		    reqGate.setUserNo(order.getUserId());
		    HunanGatewayBaseResp orderEquipmentResp = gateWay.userModify(reqGate,order.getNote1());
			if(InterfaceCenter.SUCCESS.getCode()!=orderEquipmentResp.getCode()) {
				resp.setErrorCode(StringUtils.isBlank(orderEquipmentResp.getErrorCode())?BaseResponseData.ErrorCode.SYSTEM_ERROR.toString():orderEquipmentResp.getErrorCode());
				resp.setErrorMsg(orderEquipmentResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "修改手机号响应:"+JSON.toJSONString(orderEquipmentResp));
				return resp;
			}
			order.setMobile(req.getNewPhone());
			LogUtil.info(log, req.getOrderId(), "中台修改订单请求:"+JSON.toJSONString(centerReq));
			IssueSaveResp respCenter = IssueCenterApi.edit((JSONObject)JSON.toJSON(order));
			LogUtil.info(log, req.getOrderId(), "中台修改订单响应:"+JSON.toJSONString(respCenter));
			if(InterfaceCenter.SUCCESS.getCode()!=respCenter.getCode()) {
				resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
				resp.setErrorMsg(respCenter.getMsg());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "修改手机号响应:"+JSON.toJSONString(resp));
				return resp;
			}
			resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
			resp.setErrorMsg("成功");
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "修改手机号响应:"+JSON.toJSONString(resp));
			return resp;
		}
		catch(Exception e) {
			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
			resp.setErrorMsg(e.getMessage());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "修改手机号响应:"+JSON.toJSONString(resp));
			return resp;
		}
	}

	@Override
	public IssueOrderModifyByAccountResp issueOrderModifyByAccount(IssueOrderModifyByAccountReq req,
			ThirdPartner partner) {
		LogUtil.info(log, req.getOrderId(), "修改订单库存请求:"+JSON.toJSONString(req));
		IssueOrderModifyByAccountResp resp = new IssueOrderModifyByAccountResp();
		try {
			GetOrderReq centerReq = new GetOrderReq();
			centerReq.setOutOrderId(req.getOrderId());
	    	LogUtil.info(log, req.getOrderId(), "中台查询订单请求:"+JSON.toJSONString(centerReq));
			EtcObjectResponse<EtcIssueOrder> centerResp = IssueCenterApi.getOrder(centerReq);
			LogUtil.info(log, req.getOrderId(), "中台查询订单响应:"+JSON.toJSONString(centerResp));
			if(InterfaceCenter.TIMEOUT.getCode()==centerResp.getCode()) {
				resp.setErrorMsg(centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
				LogUtil.info(log, req.getOrderId(), "修改订单库存响应:"+JSON.toJSONString(resp));
				return resp;
			}
			if(InterfaceCenter.SUCCESS.getCode()!=centerResp.getCode()) {
				resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
				resp.setErrorMsg(centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "修改订单库存响应:"+JSON.toJSONString(resp));
				return resp;
			}
			
		    if (ValidateUtil.isEmpty(centerResp.getData())) {
		    	resp.setErrorCode(BaseResponseData.ErrorCode.OTHER_ERROR.toString());
				resp.setErrorMsg("订单不存在");
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "修改订单库存响应:"+JSON.toJSONString(resp));
				return resp;
		    }
		          
		    EtcIssueOrder order =centerResp.getData();
		    JSONObject object = new JSONObject();
        	
    		String accountNo = req.getAccountNo();
          	String password = req.getPassword();
          	object.put("Account", accountNo);
          	object.put("Password", password);
        	
			order.setNote1(object.toJSONString());
			LogUtil.info(log, req.getOrderId(), "中台修改订单请求:"+JSON.toJSONString(order));
			IssueSaveResp respCenter = IssueCenterApi.edit((JSONObject)JSON.toJSON(order));
			LogUtil.info(log, req.getOrderId(), "中台修改订单响应:"+JSON.toJSONString(respCenter));
			if(InterfaceCenter.FAIL.getCode()==respCenter.getCode()) {
				resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
				resp.setErrorMsg(respCenter.getMsg());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "修改手机号响应:"+JSON.toJSONString(resp));
				return resp;
			}
			resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
			resp.setErrorMsg("成功");
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "修改订单库存账号响应:"+JSON.toJSONString(resp));
			return resp;
		}
		catch(Exception e) {
			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
			resp.setErrorMsg(e.getMessage());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "修改订单库存账号响应:"+JSON.toJSONString(resp));
			return resp;
		}
	}
	
	public IssueEtcCard queryCardInfo(String outOrderId,String orderNo,int ownerCode) {
		QueryByOrderReq req = new QueryByOrderReq();
        req.setOwnerCode(ownerCode);
        req.setOrderNo(orderNo);
        LogUtil.info(log, outOrderId, "中台查询卡信息请求", JSON.toJSONString(req) );
        IssueEtcCardResp<IssueEtcCard> cardResp = IssueCenterApi.queryByOrderNo(req);
        LogUtil.info(log, outOrderId, "中台查询卡信息响应", cardResp);
        if(InterfaceCenter.TIMEOUT.getCode()==cardResp.getCode()) {
			throw new RuntimeException(cardResp.getMsg());
		}
        return cardResp.getResult();
	}
	
	public void isCancel(String outOrderId) {
		
		//根据外部订单号查询网发订单号
		GetOrderReq centerReq = new GetOrderReq();
		centerReq.setOutOrderId(outOrderId);
    	LogUtil.info(log, outOrderId, "中台查询订单请求:"+JSON.toJSONString(centerReq));
		EtcObjectResponse<EtcIssueOrder> centerResp = IssueCenterApi.getOrder(centerReq);
		LogUtil.info(log, outOrderId, "中台查询订单响应:"+JSON.toJSONString(centerResp));
		if(InterfaceCenter.TIMEOUT.getCode()==centerResp.getCode()) {
			
			throw new RuntimeException("网络异常,请稍后重试");
		}
		if(InterfaceCenter.SUCCESS.getCode()!=centerResp.getCode()) {
			throw new RuntimeException("系统异常,请稍后重试");
		}
		
	    if (ValidateUtil.isEmpty(centerResp.getData())) {
	    	
	    	throw new RuntimeException("订单不存在,取消失败");
	    }
	          
	    EtcIssueOrder order =centerResp.getData();
	    
	    QueryByOrderReq reqQueryCard = new QueryByOrderReq();
		reqQueryCard.setOrderNo(order.getOrderNo());
		reqQueryCard.setOwnerCode(4301);
		LogUtil.info(log, outOrderId, "查询卡信息请求", JSON.toJSONString(reqQueryCard));
		IssueEtcCardResp<IssueEtcCard> cardResp = IssueCenterApi.queryByOrderNo(reqQueryCard);
		LogUtil.info(log, outOrderId, "查询卡信息响应", cardResp);
		if(cardResp.getCode()==1) {
			throw new RuntimeException("系统异常,请稍后重试");
		}
		if(cardResp.getCode()==0&&cardResp.getResult()!=null) {
			throw new RuntimeException("存在卡表信息不允许取消,卡号:"+cardResp.getResult().getCardNo()+"车牌号:"+cardResp.getResult().getPlateNo());
		}
		
	}

	@Override
	public SecondIssueQueryResp secondIssueQuery(BaseReq req, ThirdPartner partner) {
		
		LogUtil.info(log, req.getOrderId(), "二发进度查询请求:"+JSON.toJSONString(req));
		SecondIssueQueryResp resp = new SecondIssueQueryResp();
		try {
			GetOrderReq centerReq = new GetOrderReq();
			centerReq.setOutOrderId(req.getOrderId());
	    	LogUtil.info(log, req.getOrderId(), "中台订单查询请求:"+JSON.toJSONString(centerReq));
			EtcObjectResponse<EtcIssueOrder> centerResp = IssueCenterApi.getOrder(centerReq);
			LogUtil.info(log, req.getOrderId(), "中台订单查询响应:"+JSON.toJSONString(centerResp));
			if(InterfaceCenter.TIMEOUT.getCode()==centerResp.getCode()) {
				resp.setErrorMsg(centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.FAILED.toString());
				LogUtil.info(log, req.getOrderId(), "订单查询响应:"+JSON.toJSONString(resp));
				return resp;
			}
			if(InterfaceCenter.SUCCESS.getCode()!=centerResp.getCode()) {
				resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
				resp.setErrorMsg(centerResp.getMsg());
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "订单查询响应:"+JSON.toJSONString(resp));
				return resp;
			}
			
		    if (ValidateUtil.isEmpty(centerResp.getData())) {
		    	resp.setErrorCode(BaseResponseData.ErrorCode.OTHER_ERROR.toString());
				resp.setErrorMsg("订单不存在");
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "订单查询响应:"+JSON.toJSONString(resp));
				return resp;
		    }
		          
		    EtcIssueOrder order =centerResp.getData();
		    //TODO订单未发货不存在激活记录
		    
			LogUtil.info(log, req.getOrderId(), "开始查询激活记录"+JSON.toJSONString(order));
			ActivationQueryReq actReq = new ActivationQueryReq();
			actReq.setKind("0");
			actReq.setOrderNo(order.getOrderNo());
			actReq.setOwnerCode("4301");
			LogUtil.info(log, req.getOrderId(), "查询激活记录请求 :"+JSON.toJSONString(actReq));
			ActivationQueryResp activationQuery = IssueCenterApi.activationQuery(actReq);
			
			LogUtil.info(log, req.getOrderId(), "查询激活记录响应 :"+JSON.toJSONString(activationQuery));
			if(activationQuery.getCode()!=0) {
				resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
				resp.setErrorMsg("系统异常,请稍后重试");
				resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				LogUtil.info(log, req.getOrderId(), "二发进度查询响应:"+JSON.toJSONString(resp));
				return resp; 
			}
			resp.setCardNo(activationQuery.getCardNo());
			resp.setObuNo(activationQuery.getObuNo());
			resp.setCardStatus("0");
			resp.setObuStatus("0");
			resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
			resp.setErrorMsg("成功");
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			
			//0：初始化
			//1：成功
			//2：失败
			// active cardstatus 1 --->0015 成功  5-----》 全部写成功
			//0,初始;1,写0015成功;2,写0015失败;3,写0016成功;4,写0016失败;5；全部写成功
			//
			if(activationQuery.getFinishStatus().equals("1")) {
				resp.setCardStatus("1");
				resp.setObuStatus("1");
				return resp;
			}
			if (activationQuery.getCardStatus().equals("1")||activationQuery.getCardStatus().equals("5")) {
				resp.setCardStatus("1");
			}
			if(activationQuery.getVehicleStatus().equals("1")&&activationQuery.getSystemStatus().equals("1")) {
				resp.setObuStatus("1");
			}
			if(activationQuery.getVehicleStatus().equals("1")&&activationQuery.getSystemStatus().equals("0")) {
				resp.setObuStatus("2");
			}
			if(activationQuery.getCardStatus().equals("2")||activationQuery.getCardStatus().equals("4")) {
				resp.setCardStatus("2");
			}
			if(activationQuery.getVehicleStatus().equals("2")||activationQuery.getSystemStatus().equals("2")) {
				resp.setObuStatus("2");
			}
			if(activationQuery.getCardStatus().equals("0")) {
				QueryByOrderReq reqQueryCard = new QueryByOrderReq();
	    		reqQueryCard.setOrderNo(order.getOrderNo());
	    		reqQueryCard.setOwnerCode(4301);
	    		LogUtil.info(log, req.getOrderId(), "查询卡信息请求", JSON.toJSONString(reqQueryCard));
	    		IssueEtcCardResp<IssueEtcCard> cardResp = IssueCenterApi.queryByOrderNo(reqQueryCard);
	    		LogUtil.info(log, req.getOrderId(), "查询卡信息响应", cardResp);
	    		if(cardResp.getCode()==1) {
	    			throw new RuntimeException("系统异常,请稍后重试");
	    		}
	    		IssueEtcCard result = cardResp.getResult();
				if(result!=null) {
					resp.setCardStatus("2");
	    		}
			}
			if(activationQuery.getVehicleStatus().equals("0")) {
				QueryByOrderReq reqQueryCard = new QueryByOrderReq();
	    		reqQueryCard.setOrderNo(order.getOrderNo());
	    		reqQueryCard.setOwnerCode(4301);
	    		LogUtil.info(log, req.getOrderId(), "查询卡信息请求", JSON.toJSONString(reqQueryCard));
	    		IssueEtcCardResp<IssueEtcCard> cardResp = IssueCenterApi.queryByOrderNo(reqQueryCard);
	    		LogUtil.info(log, req.getOrderId(), "查询卡信息响应", cardResp);
	    		if(cardResp.getCode()==1) {
	    			throw new RuntimeException("系统异常,请稍后重试");
	    		}
	    		IssueEtcCard result = cardResp.getResult();
				if(result!=null) {
					if(StringUtils.isNotBlank(result.getObuCode())) {
						resp.setObuStatus("2");
					}
	    		}
			}
			
			
		   
			LogUtil.info(log, req.getOrderId(), "二发进度查询响应:"+JSON.toJSONString(resp));
			return resp;
		}
		catch(Exception e) {
			resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
			resp.setErrorMsg(e.getMessage());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "二发进度查询响应:"+JSON.toJSONString(resp));
			return resp;
		}
	}

	@Override
	public SecondIssueResp secondIssueReplace(SecondIssueReq req, ThirdPartner partner) {
		
		//
		
		//根据换卡换签类型
		SecondIssueResp resp = new SecondIssueResp() ;
		//req.setRepairType(3);
		switch (req.getRepairType()) {
			case 1:{
				

				resp = bussinessReplaceCard.doService(req,partner);
				break;
			 }
			case 2:{
				
				resp =  bussinessReplaceObu.doService(req,partner);
				break;
			 }
			case 3:{
				
				resp =  bussinessReplaceCardAndObu.doService(req,partner);
				break;
			 }
			default:{
				//不支持该类型
			}
		};
		return resp;
		
		
		
		
		
		
		
		
	}

	@Override
	public SecondIssueResp secondIssueReplaceSumbitOrder(SecondIssueReq req, ThirdPartner partner) {
		//根据业务类型
		//1:提交  2：初始化
		LogUtil.info(log, req.getOrderId(), "换卡换签提交订单请求"+JSON.toJSONString(req));
		SecondIssueResp  resp = new SecondIssueResp();
		EtcIssueOrder order = getOrder("",req.getOrderId());
		
		
		switch (req.getType()) {
		 case "1":{
			 if(order.getOrderStatus()==16) {
				 resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
				 resp.setErrorMsg("换卡换签订单提交成功,不允许重新提交");
				 resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				 LogUtil.info(log, req.getCardNo(), "提交拓展订单提交响应:"+JSON.toJSONString(resp));
				 return resp;
			 }
			 switch(req.getRepairType()) {
				 case 1:{
					 //先去华软初始化
					 if(order.getOrderStatus()!=15) {
						 SecondIssueResp init = gatewayReplaceCardInit.init(req, partner);
						 if(!BaseResponseData.ErrorCode.SUCCEED.toString().equals(init.getErrorCode())) {
								return init;
						 }
					 }
					 
					 //如果tw_order_no 存在值说明已经替换完成
					 
					 Boolean replaceCardNo = EtcCardService.replaceCardNo(order.getOrderNo(),req.getOldCardNo(), req.getCardNo());
					 if(!replaceCardNo) {
						    resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
							resp.setErrorMsg("更新卡号或者二发进度失败");
							resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
							LogUtil.info(log, req.getOrderId(), "提交拓展订单提交响应:"+JSON.toJSONString(resp));
							return resp;
					 }
					 
					 resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
					 resp.setErrorMsg("成功");
					 resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
					 LogUtil.info(log, req.getCardNo(), "提交拓展订单提交响应:"+JSON.toJSONString(resp));
					
					 break; 
				 }
				 case 2:{
					 //先去华软初始化
					 if(order.getOrderStatus()!=15) {
						 SecondIssueResp init = gatewayReplaceObuInit.init(req, partner);
						 if(!BaseResponseData.ErrorCode.SUCCEED.toString().equals(init.getErrorCode())) {
								return init;
						 }
					 }
					 Boolean replaceObuNo = EtcCardService.replaceObuNo(order.getOrderNo(),req.getOldCardNo(), req.getOldObuNo(), req.getObuNo());
					 if(!replaceObuNo) {
						    resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
							resp.setErrorMsg("更新OBU号或者二发进度失败");
							resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
							LogUtil.info(log, req.getOrderId(), "提交拓展订单提交响应:"+JSON.toJSONString(resp));
							return resp;
					 }
					 resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
					 resp.setErrorMsg("成功");
					 resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
					 LogUtil.info(log, req.getCardNo(), "提交拓展订单提交响应:"+JSON.toJSONString(resp));
					 break; 
				 }
				 case 3:{
					//先去华软初始化
					 if(order.getOrderStatus()!=15) {
						 SecondIssueResp init = gatewayReplaceCardAndObuInit.init(req, partner);
						 if(!BaseResponseData.ErrorCode.SUCCEED.toString().equals(init.getErrorCode())) {
								return init;
						 }
					 }
					 Boolean replaceObuNo = EtcCardService.replaceCardNoObuNo(order.getOrderNo(),req.getOldCardNo(), req.getCardNo(),req.getOldObuNo(), req.getObuNo());
					 if(!replaceObuNo) {
						    resp.setErrorCode(BaseResponseData.ErrorCode.SYSTEM_ERROR.toString());
							resp.setErrorMsg("更新卡号和OBU号或者二发进度失败");
							resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
							LogUtil.info(log, req.getOrderId(), "提交拓展订单提交响应:"+JSON.toJSONString(resp));
							return resp;
					 }
					 resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
					 resp.setErrorMsg("成功");
					 resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
					 LogUtil.info(log, req.getCardNo(), "提交拓展订单提交响应:"+JSON.toJSONString(resp));
					 break; 
				 }
			 }
			 break;
			
		 }
		 case "2":{
			//根据订单号
			 SecondIssueProcess sep = secondIssueDAO.findByOrderNoAndOwnerCode(order.getOrderNo(),4301);
             if (!sep.getFinishStatus().equals("1")){
            	 resp.setErrorCode(BaseResponseData.ErrorCode.OTHER_ERROR.toString());
				 resp.setErrorMsg("换卡换签订单未完成,不允许初始化订单");
				 resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
				 LogUtil.info(log, req.getOrderId(), "换卡换签提交订单响应"+JSON.toJSONString(resp));
				 return resp;
             }
			 LogUtil.info(log, req.getOrderId(), "初始化换卡换签订单开始");
			 EtcIssueOrderHistroy orderHistroy = new EtcIssueOrderHistroy();
	         BeanUtils.copyProperties(order, orderHistroy);
	         etcIssueOrderHistroyDAO.save(orderHistroy);
			 order.setAuditDesc("更换设备订单状态:"+order.getOrderStatus()+"变更为"+10);
			 order.setOrderStatus(10);
			 order.setTwOrderNo(null);
			 order.setNote2(null);
			 order.setUpdateTime(new Date());
			 modifyOrder(order);
			 resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
			 resp.setErrorMsg("成功");
			 resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			 LogUtil.info(log, req.getOrderId(), "初始化换卡换签订单响应:"+JSON.toJSONString(resp));
			 break; 
		 }
		}
		//LogUtil.info(log, req.getOrderId(), "换卡换签提交订单响应"+JSON.toJSONString(resp));
		return resp;
	}

}
