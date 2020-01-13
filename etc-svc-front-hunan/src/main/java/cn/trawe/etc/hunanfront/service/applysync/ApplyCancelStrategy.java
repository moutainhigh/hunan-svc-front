package cn.trawe.etc.hunanfront.service.applysync;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.trawe.etc.hunanfront.dao.ThirdOrderSycnRecordCompletedDao;
import cn.trawe.etc.hunanfront.entity.ThirdOrderSycnRecordCompleted;
import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.feign.ApiClient;
import cn.trawe.etc.hunanfront.request.ApplyOrderSyncReq;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.entity.IssueEtcCard;
import cn.trawe.pay.expose.request.issue.IssueOrderQueryReq;
import cn.trawe.pay.expose.request.issue.IssueThirdOrderCancelReq;
import cn.trawe.pay.expose.request.issue.QueryByOrderReq;
import cn.trawe.pay.expose.request.issue.ThirdOutOrderQueryReq;
import cn.trawe.pay.expose.request.secondissue.ActivationQueryReq;
import cn.trawe.pay.expose.response.GlobalResponse;
import cn.trawe.pay.expose.response.issue.IssueEtcCardResp;
import cn.trawe.pay.expose.response.issue.IssueOrderQueryResp;
import cn.trawe.pay.expose.response.issue.NullResp;
import cn.trawe.pay.expose.response.issue.ThirdOutOrderQueryResp;
import cn.trawe.pay.expose.response.secondissue.ActivationQueryResp;
import cn.trawe.util.LogUtil;
import cn.trawe.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 订单同步策略（撤销）
 *
 * @author Jiang Guangxing
 */
@Slf4j
@Component("applySyncStrategy3")
public class ApplyCancelStrategy extends ApplySyncStrategy {
	
	@Value("${secondissue.cancelType}")
	private String cancelType;
    @Override
    public BaseResponse sync(String charset, ApplyOrderSyncReq req,ThirdPartner ThirdPartner) {
    	try {
    		if (ValidateUtil.isEmpty(req.getOrderId()))
                return paramsError("order_id不能为空", charset,ThirdPartner);
            //long id = saveCompletedRecord(req);
            //if (id == 0)
            //return systemError(charset,ThirdPartner);
            
            //查询订单是否可以撤销
            ThirdOutOrderQueryReq thirdOutOrderQueryReq = new ThirdOutOrderQueryReq();
            thirdOutOrderQueryReq.setOutOrderId(req.getOrderId());
            log.info("网发访问基础服务请求报文 :" +JSON.toJSONString(thirdOutOrderQueryReq));
            ThirdOutOrderQueryResp thirdOutOrderQueryResp = null;
            log.info("网发访问基础服务响应报文 :" +JSON.toJSONString(thirdOutOrderQueryResp));
            if(thirdOutOrderQueryResp==null) {
            	return paramsError("查询外部订单失败,请稍后重试", charset,ThirdPartner);
            }
            //未查询到该订单
            if (thirdOutOrderQueryResp.getCode() != 0) {
            	
            	return succeedResponse(charset, req,ThirdPartner);
            }
            IssueOrderQueryReq issueOrderQueryReq = new IssueOrderQueryReq();
            issueOrderQueryReq.setOrderNo(thirdOutOrderQueryResp.getOrderNo());
            issueOrderQueryReq.setPageNo(1);
            issueOrderQueryReq.setPageSize(1);
            log.info("网发访问基础服务请求报文 :" +JSON.toJSONString(issueOrderQueryReq));
            IssueOrderQueryResp issueOrderQueryResp = IssueCenterApi.orderQuery(issueOrderQueryReq, "");
            log.info("网发访问基础服务返回报文 :" +JSON.toJSONString(issueOrderQueryResp));
            EtcIssueOrder etcIssueOrder = null;
            if (issueOrderQueryResp != null && CollectionUtils.isNotEmpty(issueOrderQueryResp.getResult())) {
                etcIssueOrder = issueOrderQueryResp.getResult().get(0);
            }
            if(etcIssueOrder==null) {
            	return succeedResponse(charset, req,ThirdPartner);
            	//return paramsError("查询发行订单失败,请稍后重试", charset,ThirdPartner);
            }
            if(etcIssueOrder!=null&&(etcIssueOrder.getOrderStatus()==11)) {
            	 return paramsError("订单状态已完成不允许取消，订单状态  :" +etcIssueOrder.getOrderStatus(), charset,ThirdPartner);
            }
            
            
            if(etcIssueOrder.getOrderStatus()==9||etcIssueOrder.getOrderStatus()==10) {
            	
            	//查询是否存在卡表信息
            	if(cancelType.equals("1")) {
            		QueryByOrderReq reqQueryCard = new QueryByOrderReq();
            		reqQueryCard.setOrderNo(etcIssueOrder.getOrderNo());
            		reqQueryCard.setOwnerCode(4301);
            		LogUtil.info(log, etcIssueOrder.getOrderNo(), "查询卡信息请求", JSON.toJSONString(reqQueryCard));
            		IssueEtcCardResp<IssueEtcCard> cardResp = IssueCenterApi.queryByOrderNo(reqQueryCard);
            		LogUtil.info(log, etcIssueOrder.getOrderNo(), "查询卡信息响应", cardResp);
            		if(cardResp.getCode()==1) {
            			return paramsError("查询卡片信息失败,请稍后重试", charset,ThirdPartner);
            		}
            		if(cardResp.getCode()==0&&cardResp.getResult()!=null) {
            			return paramsError("存在卡表信息不允许取消,卡号:"+cardResp.getResult().getCardNo()+"车牌号:"+cardResp.getResult().getPlateNo(), charset,ThirdPartner); 
            		}
                	ActivationQueryReq actReq = new ActivationQueryReq();
        			actReq.setKind("0");
        			actReq.setOrderNo(etcIssueOrder.getOrderNo());
        			actReq.setOwnerCode("4301");
        			LogUtil.info(log, etcIssueOrder.getOrderNo(), "查询激活记录请求 :"+JSON.toJSONString(actReq));
        			ActivationQueryResp activationQuery = IssueCenterApi.activationQuery(actReq);
        			
        			LogUtil.info(log, etcIssueOrder.getOrderNo(), "查询激活记录响应 :"+JSON.toJSONString(activationQuery));
        			if(activationQuery.getCode()!=0) {
        				return paramsError("查询激活记录失败,请稍后重试", charset,ThirdPartner); 
        			}
        			if (!activationQuery.getCardStatus().equals("0")) {
        				LogUtil.info(log, etcIssueOrder.getOrderNo(), "订单已写过设备,不允许取消");
        				return paramsError("订单正在激活中不允许取消，订单状态  :" +etcIssueOrder.getOrderStatus(), charset,ThirdPartner);
        			}
            	}
            	else {
            		 return paramsError("订单状态已完成不允许取消，订单状态  :" +etcIssueOrder.getOrderStatus(), charset,ThirdPartner);
            	}
            	
            }
			
            
            if(etcIssueOrder!=null) {
            	IssueThirdOrderCancelReq cancelReq = new IssueThirdOrderCancelReq();
                cancelReq.setOuterOrderId(req.getOrderId());
                LogUtil.info(log, req.getOrderId(), "取消订单信息请求", cancelReq);
                GlobalResponse<NullResp> res = IssueCenterApi.thirdOrderCancel(cancelReq);
                LogUtil.info(log, req.getOrderId(), "取消订单信息响应", res);
                if (res == null || 0 != res.getCode())
                    return systemErrorCancelFail(charset,ThirdPartner,res.getMsg());
                return succeedResponse(charset, req,ThirdPartner);
            }
            return systemError(charset,ThirdPartner);
      
    	}
    	catch(Exception e) {
    		log.error(e.getMessage(),e.fillInStackTrace());
    		return paramsError("系统异常,请稍后重试", charset,ThirdPartner);
    	}
		
        
        
    }

    private long saveCompletedRecord(ApplyOrderSyncReq applyOrderSyncReq) {
        return recordCompletedDao.saveAndReturnKey(new ThirdOrderSycnRecordCompleted().setReqJson(JSON.toJSONString(applyOrderSyncReq)));
    }

    private ApiClient apiClient;
    private ThirdOrderSycnRecordCompletedDao recordCompletedDao;

    @Autowired
    public ApplyCancelStrategy(ApiClient apiClient, ThirdOrderSycnRecordCompletedDao recordCompletedDao) {
        this.apiClient = apiClient;
        this.recordCompletedDao = recordCompletedDao;
    }
}
