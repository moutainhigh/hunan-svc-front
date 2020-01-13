package cn.trawe.etc.hunanfront.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.enums.DeviceStatus;
import cn.trawe.etc.hunanfront.enums.OrderStatus;
import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.feign.ApiClient;
import cn.trawe.etc.hunanfront.request.ApplyOrderQueryRequest;
import cn.trawe.etc.hunanfront.response.ApplyOrderQueryResponse;
import cn.trawe.etc.hunanfront.utils.DateUtil;
import cn.trawe.etc.hunanfront.utils.SignUtil2;
import cn.trawe.etc.hunanfront.utils.ValidUtils;
import cn.trawe.pay.common.exception.TraweServiceException;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.entity.IssueEtcCard;
import cn.trawe.pay.expose.request.issue.IssueOrderQueryReq;
import cn.trawe.pay.expose.request.issue.QueryByOrderReq;
import cn.trawe.pay.expose.request.issue.ThirdOutOrderQueryReq;
import cn.trawe.pay.expose.response.issue.IssueEtcCardResp;
import cn.trawe.pay.expose.response.issue.IssueOrderQueryResp;
import cn.trawe.pay.expose.response.issue.ThirdOutOrderQueryResp;
import cn.trawe.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kevis
 * @date 2019/5/9
 */
@Slf4j
@Service
public class ApplyOrderQueryService extends BaseService {
    @Value("${api.token}")
    private String token;
    @Autowired
    private ApiClient apiClient;

    @Autowired
    private ThirdPartnerService thirdPartnerService;

    public BaseResponse applyOrderQuery(BaseRequest req) {

        ThirdPartner partner = thirdPartnerService.getPartner(req);
        if(partner == null){
        	return paramsError("验签失败", req.getCharset(),partner);
        }
        boolean flag = thirdPartnerService.check(req, partner);
        if(!flag){
        	return paramsError("验签失败", req.getCharset(),partner);
        }
        ApplyOrderQueryRequest applyOrderQueryRequest = JSONObject.parseObject(req.getBizContent(), ApplyOrderQueryRequest.class);
        String err = ValidUtils.validateBean(applyOrderQueryRequest);
        if (StringUtils.isNotEmpty(err)) {
            return paramsError(err, req.getCharset(),partner);
        }
        ThirdOutOrderQueryReq thirdOutOrderQueryReq = new ThirdOutOrderQueryReq();
        thirdOutOrderQueryReq.setOutOrderId(applyOrderQueryRequest.getOrderId());
        ThirdOutOrderQueryResp thirdOutOrderQueryResp = null;
        if (thirdOutOrderQueryResp == null || thirdOutOrderQueryResp.getCode() != 0) {
            return paramsError("未找到对应订单", req.getCharset(),partner);
        }
        IssueOrderQueryReq issueOrderQueryReq = new IssueOrderQueryReq();
        issueOrderQueryReq.setOrderNo(thirdOutOrderQueryResp.getOrderNo());
        issueOrderQueryReq.setPageNo(1);
        issueOrderQueryReq.setPageSize(1);
        IssueOrderQueryResp issueOrderQueryResp = IssueCenterApi.orderQuery(issueOrderQueryReq, "");
        EtcIssueOrder etcIssueOrder = null;
        if (issueOrderQueryResp != null && CollectionUtils.isNotEmpty(issueOrderQueryResp.getResult())) {
            etcIssueOrder = issueOrderQueryResp.getResult().get(0);
        }
        //订单表未找到，查询历史表
//        if (etcIssueOrder == null) {
//            IssueHistoryOrderResp issueHistoryOrderResp = apiClient.queryHistoryOrder(issueOrderQueryReq, token);
//            if (issueHistoryOrderResp != null && CollectionUtils.isNotEmpty(issueHistoryOrderResp.getResult())) {
//                EtcIssueOrderHistroy etcIssueOrderHistroy = issueHistoryOrderResp.getResult().get(0);
//                etcIssueOrder = new EtcIssueOrder();
//                BeanUtils.copyProperties(etcIssueOrderHistroy, etcIssueOrder);
//            }
//        }
        if (etcIssueOrder == null) {
            return paramsError("未找到发行订单", req.getCharset(),partner);
        }
        ApplyOrderQueryResponse applyOrderQueryResponse = new ApplyOrderQueryResponse();
        applyOrderQueryResponse.setOrderId(applyOrderQueryRequest.getOrderId());
        applyOrderQueryResponse.setOutBizNo("");
        /**
         * 1 -----》 0  待提交

			2------》 0  待提交
			
			3------》 1  待审核
			
			4-----》  6  审核中
			
			5-----》  2 审核驳回
			
			6-----》 4 审核通过
			
			7,8,9-----》 4审核通过
			
			10-----》4审核通过   已发货1
			
			11----》审核通过    已签收 3
			
			12 
			
			13 撤销 5
			         */
        switch(etcIssueOrder.getOrderStatus()) {
	        case 0:{
	        	applyOrderQueryResponse.setOrderStatus("0");
	    		break;
	    	}
        	case 1:{
        		applyOrderQueryResponse.setOrderStatus("0");
	    		break;
        	}
        	case 2:{
        		
        		applyOrderQueryResponse.setOrderStatus("2");
	    		break;
        	}
        	
        	case 3:{
        		applyOrderQueryResponse.setOrderStatus("1");
	    		break;
        	}
        	
        	case 4:{
        		applyOrderQueryResponse.setOrderStatus("6");
	    		break;
        	}
        	
        	case 5:{
        		applyOrderQueryResponse.setOrderStatus("2");
        		break;
        	}
        	
        	case 6:{
        		applyOrderQueryResponse.setOrderStatus("4");
        		break;
        	}
        	case 7:{
        		applyOrderQueryResponse.setOrderStatus("4");
        		break;
        	}
        	case 8:{
        		applyOrderQueryResponse.setOrderStatus("4");
        		break;
        	}
        	case 9:{
        		applyOrderQueryResponse.setOrderStatus("4");
        		break;
        	}
        	case 10:{
        		applyOrderQueryResponse.setOrderStatus("4");
        		applyOrderQueryResponse.setDeviceStatus("1");
        		break;
        	}
        	case 11:{
        		applyOrderQueryResponse.setOrderStatus("7");
        		applyOrderQueryResponse.setDeviceStatus("2");
        		break;
        	}
        	case 12:{
        		
        		break;
        	}
        	case 13:{
        		applyOrderQueryResponse.setOrderStatus("5");
        		break;
        	}
        }

      
        applyOrderQueryResponse.setCensorInfo(etcIssueOrder.getAuditDesc());
        applyOrderQueryResponse.setOrderUpdateTime(DateUtils.format(etcIssueOrder.getUpdateTime(), DateUtils.YYYY_MM_DD_HH_MM_SS));
        applyOrderQueryResponse.setDeliveryName(etcIssueOrder.getDeliveryName());
        applyOrderQueryResponse.setDeliveryNo(etcIssueOrder.getDeliveryCode());
//        try {
//       	 if (ValidateUtil.isNotEmpty(etcIssueOrder.getAuditPerson()))
//       		applyOrderQueryResponse.setAuditPerson(etcIssueOrder.getAuditPerson());
//            if (ValidateUtil.isNotEmpty(etcIssueOrder.getNote1())) {
//                //处理JSON
//           	 JSONObject adminInfo = JSON.parseObject(etcIssueOrder.getNote1());
//           	 String Account = adminInfo.getString("Account");
//           	applyOrderQueryResponse.setAccountNo(Account);
//            }
//       }
//       catch(Exception e) {
//       	log.error(e.getMessage(),e.fillInStackTrace());
//       }
        try {
        	//查询卡表
            QueryByOrderReq  reqByOrder = new QueryByOrderReq();
            reqByOrder.setOrderNo(issueOrderQueryReq.getOrderNo());
            reqByOrder.setOwnerCode(4301);
           IssueEtcCardResp<IssueEtcCard> issueEtcCardResp = IssueCenterApi.queryByOrderNo(reqByOrder);

//           int dStatus = 0;
//           if(10 == etcIssueOrder.getOrderStatus()){
//               dStatus = 1;
//           }else if(11 == etcIssueOrder.getOrderStatus()){
//               dStatus = 3;
//           }
//           applyOrderQueryResponse.setDeviceStatus(String.valueOf(dStatus));

           if (issueEtcCardResp != null) {
                IssueEtcCard issueEtcCard = issueEtcCardResp.getResult();
                if (issueEtcCard != null) {
                    String tenYearsLaterDate = DateUtil.tenYearsLaterDate(issueEtcCard.getCreateTime());
                    applyOrderQueryResponse.setCardExpiryDate(tenYearsLaterDate);
                    applyOrderQueryResponse.setDeviceExpiryDate(tenYearsLaterDate);
                    applyOrderQueryResponse.setDeviceType("");//todo 设备类型 暂时无法确定有哪些类型
                    applyOrderQueryResponse.setCardNo(issueEtcCard.getCardNo());
                    applyOrderQueryResponse.setDeviceNo(issueEtcCard.getObuCode());
//                    if (issueEtcCard.getActivateStatus() == 1) {
//                        applyOrderQueryResponse.setDeviceStatus("1");
//                    }
                }
            }
        }
        catch(Exception e) {
        	log.error(e.getMessage(),e.fillInStackTrace());
        }

//        }
        applyOrderQueryResponse.setSuccess(BaseResponseData.Success.SUCCEED.toString());
        applyOrderQueryResponse.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
        BaseResponse<ApplyOrderQueryResponse> baseResp = new BaseResponse<ApplyOrderQueryResponse>();
        try{
            baseResp.setResponse(applyOrderQueryResponse);
            baseResp.setSign(SignUtil2.signResponse(applyOrderQueryResponse,partner.getTrawePrivateKey(),req.getCharset(),true));

        }catch (AlipayApiException e){
            log.error(e.getMessage(),e.fillInStackTrace());
        }
        return baseResp;
    }

    DeviceStatus convertDeviceStatus(int orderStatus) {
        switch (orderStatus) {
            case 6:
                return DeviceStatus.NOT_SHIPPED;
            case 8:
                return DeviceStatus.NOT_SHIPPED;
            case 9:
                return DeviceStatus.NOT_SHIPPED;
            case 10:
                return DeviceStatus.SHIPPED;
            case 11:
                return DeviceStatus.RECEIVED;
            default:
                throw new TraweServiceException("无法转换状态");
        }
    }


    OrderStatus convertOrderStatus(int orderStatus, int ownerCode) {
        switch (orderStatus) {
            case 0:
                return OrderStatus.UNSUBMIT;
            case 1:
                return OrderStatus.UNSUBMIT;
            case 2:
                return OrderStatus.UNSUBMIT;
            case 3:
                return OrderStatus.REVIEWING;
            case 4:
                return OrderStatus.REVIEWING;
            case 5:
                if (ownerCode == 3201) {
                    return OrderStatus.REJECT;
                }
                return OrderStatus.TURN_DOWN;
            case 6:
                return OrderStatus.PASS;
            case 7:
                return OrderStatus.REVIEWING;
            case 8:
                return OrderStatus.PASS;
            case 9:
                return OrderStatus.PASS;
            case 10:
                return OrderStatus.PASS;
            case 11:
                return OrderStatus.PASS;
            case 12:
                return OrderStatus.REJECT;
            case 13:
                return OrderStatus.CANCEL;
            default:
                throw new TraweServiceException("无法转换状态");
        }
    }
}
