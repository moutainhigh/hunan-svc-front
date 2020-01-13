package cn.trawe.etc.hunanfront.service.secondissue.replace;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.trawe.etc.hunanfront.config.InterfaceCenter;
import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.feign.entity.hunan.HunanGatewayBaseResp;
import cn.trawe.etc.hunanfront.feign.entity.hunan.OrderEquipmentReq;
import cn.trawe.etc.hunanfront.feign.entity.hunan.OrderExamineReq;
import cn.trawe.etc.hunanfront.feign.entity.hunan.OrderSubmitReq;
import cn.trawe.etc.hunanfront.feign.v2.GatewayHunanApiImpl;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueResp;
import cn.trawe.etc.hunanfront.service.BaseService;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.ObuInfoApdu;
import cn.trawe.etc.hunanfront.utils.HexUtils;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GatewayReplaceObuInit  extends BaseService {
	
	@Autowired
	private GatewayHunanApiImpl gateWay;
	
	public SecondIssueResp init(SecondIssueReq req, ThirdPartner partner) {
		LogUtil.info(log, req.getOrderId(), "拓展订单提交请求:"+JSON.toJSONString(req));
		SecondIssueResp  resp = new SecondIssueResp();
		//处理渠道编号
		JSONObject object = new JSONObject();
    	
		String accountNo = partner.getAccountNo();
      	String password = partner.getPassword();
      	object.put("Account", accountNo);
      	object.put("Password", password);
      	OrderSubmitReq  gateWaySumbitReq = new OrderSubmitReq();
      	gateWaySumbitReq.setBussType(14);
      	//gateWaySumbitReq.setFaceCardNum(req.getCardNo().substring(4));
      	gateWaySumbitReq.setSerialNumber(req.getOldObuNo());
      	EtcIssueOrder order = getOrder("",req.getOrderId());
      	//判断是否存在未完成的替换订单
      	String orderNo = order.getOrderNo();
      	if(order.getOrderStatus()==10||order.getOrderStatus()==11) {
      		
      		orderNo = getOrderNo("4301");
      		order.setNote2(orderNo);
			order.setOrderStatus(14);
			order.setUpdateTime(new Date());
			modifyOrder(order);
      	}
      	
      	
      	gateWaySumbitReq.setListNo(orderNo);
      	
      	gateWaySumbitReq.setPicData(req.getPicData());
      	gateWaySumbitReq.setReceiveAddress(order.getReceiverAddress());
      	gateWaySumbitReq.setReceiveName(order.getReceiverName());
      	gateWaySumbitReq.setReceiveTel(order.getReceiverPhone());
      	
      	//拓展订单提交
      	HunanGatewayBaseResp gateWaySumbitResp = gateWay.orderSubmit(gateWaySumbitReq, object.toJSONString());
      	LogUtil.info(log, req.getOrderId(), "拓展订单提交请求:"+JSON.toJSONString(gateWaySumbitReq));
		if(InterfaceCenter.SUCCESS.getCode()!=gateWaySumbitResp.getCode()) {
			resp.setErrorCode(StringUtils.isBlank(gateWaySumbitResp.getErrorCode())?BaseResponseData.ErrorCode.SYSTEM_ERROR.toString():gateWaySumbitResp.getErrorCode());
			resp.setErrorMsg(gateWaySumbitResp.getMsg());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "拓展订单提交响应:"+JSON.toJSONString(resp));
			return resp;
		}
		
		//拓展订单审核
		OrderExamineReq gateWayAuditReq = new OrderExamineReq();
		gateWayAuditReq.setListNo(orderNo);
		gateWayAuditReq.setReason("通过");
		gateWayAuditReq.setResult(0);
		HunanGatewayBaseResp gateWayAuditResp = gateWay.expandOrderExamine(gateWayAuditReq, object.toJSONString());
		LogUtil.info(log, req.getOrderId(), "拓展订单审核请求:"+JSON.toJSONString(gateWayAuditReq));
		if(InterfaceCenter.SUCCESS.getCode()!=gateWayAuditResp.getCode()) {
			resp.setErrorCode(StringUtils.isBlank(gateWayAuditResp.getErrorCode())?BaseResponseData.ErrorCode.SYSTEM_ERROR.toString():gateWayAuditResp.getErrorCode());
			resp.setErrorMsg(gateWayAuditResp.getMsg());
			resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
			LogUtil.info(log, req.getOrderId(), "拓展订单审核响应:"+JSON.toJSONString(resp));
			return resp;
		}
		order.setOrderStatus(15);
		modifyOrder(order);
		resp.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
		resp.setErrorMsg("成功");
		resp.setSuccess(BaseResponseData.Success.SUCCEED.toString());
		LogUtil.info(log, req.getCardNo(), "拓展订单登记响应:"+JSON.toJSONString(resp));
		return resp;
		
		
	}
	
	

}
