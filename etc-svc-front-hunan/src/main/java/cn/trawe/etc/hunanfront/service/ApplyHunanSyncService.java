package cn.trawe.etc.hunanfront.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alipay.api.AlipayApiException;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.feign.ApiClient;
import cn.trawe.etc.hunanfront.feign.v2.IssueCenterApi;
import cn.trawe.etc.hunanfront.service.http.HttpAPIService;
import cn.trawe.etc.hunanfront.service.http.HttpResult;
import cn.trawe.etc.hunanfront.utils.DateUtil;
import cn.trawe.etc.hunanfront.utils.SignUtil2;
import cn.trawe.pay.common.etcmsg.EtcResponse;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.entity.IssueEtcCard;
import cn.trawe.pay.expose.request.issue.IssueOrderQueryReq;
import cn.trawe.pay.expose.request.issue.QueryByOrderReq;
import cn.trawe.pay.expose.request.issue.ThirdOutOrderQueryReq;
import cn.trawe.pay.expose.response.issue.IssueEtcCardResp;
import cn.trawe.pay.expose.response.issue.IssueOrderQueryResp;
import cn.trawe.pay.expose.response.issue.ThirdOutOrderQueryResp;
import cn.trawe.util.LogUtil;
import cn.trawe.utils.DateUtils;
import cn.trawe.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jiang Guangxing
 */
@Slf4j
@Service
public class ApplyHunanSyncService {
    @Value("${api.token}")
    private String token;
    
    @Autowired
	protected  IssueCenterApi  IssueCenterApi;
    
	
	@Autowired
    private ThirdPartnerService thirdPartnerService;
	
	@Autowired
	private HttpAPIService  HttpAPIService;
	@Autowired
	RestTemplate  RestTemplate;
	
	public ThirdOutOrderQueryResp queryThirdOrder(String orderNo) {
		ThirdOutOrderQueryReq req = new ThirdOutOrderQueryReq();
        req.setOrderNo(orderNo);
        ThirdOutOrderQueryResp outOrderQueryRes;
        try {
            outOrderQueryRes = this.queryThirdOutOrder(req);
        } catch (Exception e) {
            LogUtil.error(log, orderNo, "查询订单映射信息失败", e);
            return null;
        }
        //银行接入系统外部订单号
        return outOrderQueryRes;
	}
	
	public IssueEtcCard queryCardInfo(String orderNo,int ownerCode) {
		QueryByOrderReq req = new QueryByOrderReq();
        req.setOwnerCode(ownerCode);
        req.setOrderNo(orderNo);
        LogUtil.info(log, orderNo, "查询卡信息请求", JSON.toJSONString(req) );
        IssueEtcCardResp<IssueEtcCard> cardResp = IssueCenterApi.queryByOrderNo(req);
        LogUtil.info(log, orderNo, "查询卡信息响应", cardResp);
        if (cardResp == null)
            return null;
        return cardResp.getResult();
	}

    public EtcResponse applySync(String orderNo) {
        log.info("订单【{}】同步状态", orderNo);

        EtcResponse res = new EtcResponse();
        if (ValidateUtil.isEmpty(orderNo))
            return failed(res, "订单号不能为空");
        EtcIssueOrder order;
                try {
            order = this.orderQuery(orderNo);
        } catch (Exception e) {
            LogUtil.error(log, orderNo, "查询订单失败", e);
            return failed(res, "查询订单失败");
        }
        if (order == null)
            return failed(res, "订单信息不存在");

        ThirdOutOrderQueryResp queryThirdOrder = queryThirdOrder(orderNo);

        if (queryThirdOrder==null) {
            LogUtil.error(log, orderNo, "渠道信息为空");
            return failed(res, "第三方映射订单不存在");
        }
        ThirdPartner partner = thirdPartnerService.getPartnerByChannelNo(queryThirdOrder.getThirdId().toString());
        if (partner==null) {
            LogUtil.error(log, orderNo, "渠道信息为空");
            return failed(res, "渠道信息为空");
        }

        JSONObject bizContent = new JSONObject();
        bizContent.put("order_id", queryThirdOrder.getOutOrderId());
        bizContent.put("out_biz_no", "");
        
        //bizContent.put("order_status", applyOrderQueryService.convertOrderStatus(order.getOrderStatus(), order.getOwnerCode()).ordinal());
        switch(order.getOrderStatus()) {
        case 0:{
        	bizContent.put("order_status","0");
    		break;
    	}
    	case 1:{
    		bizContent.put("order_status","0");
    		break;
    	}
    	case 2:{
    		bizContent.put("order_status","2");
    		break;
    	}
    	
    	case 3:{
    		bizContent.put("order_status","1");
    		break;
    	}
    	
    	case 4:{
    		bizContent.put("order_status","6");
    		break;
    	}
    	
    	case 5:{
    		bizContent.put("order_status","2");
    		break;
    	}
    	
    	case 6:{
    		bizContent.put("order_status","4");
    		break;
    	}
    	case 7:{
    		bizContent.put("order_status","4");
    		break;
    	}
    	case 8:{
    		bizContent.put("order_status","4");
    		break;
    	}
    	case 9:{
    		bizContent.put("order_status","4");
    		break;
    	}
    	case 10:{
    		bizContent.put("order_status","4");
    		bizContent.put("device_status", "1");

    		break;
    	}
    	case 11:{
    		bizContent.put("order_status","7");
    		bizContent.put("device_status", "2");
    		break;
    	}
    	case 12:{
    		
    		break;
    	}
    	case 13:{
    		bizContent.put("order_status","5");
    		break;
    	}
    }
        bizContent.put("order_update_time", DateUtils.format(order.getUpdateTime(), DateUtils.YYYY_MM_DD_HH_MM_SS));
        if (ValidateUtil.isNotEmpty(order.getAuditDesc()))
            bizContent.put("censor_info", order.getAuditDesc());
        if (ValidateUtil.isNotEmpty(order.getDeliveryName())) {
            bizContent.put("delivery_name", order.getDeliveryName());
        }
        if (ValidateUtil.isNotEmpty(order.getDeliveryCode()))
            bizContent.put("delivery_no", order.getDeliveryCode());
        
        IssueEtcCard card = queryCardInfo(orderNo,order.getOwnerCode());

        if (card != null) {
            String tenYearsLaterDate = DateUtil.tenYearsLaterDate(card.getCreateTime());
            bizContent.put("device_type", "");//todo 设备类型 暂时无法确定有哪些类型
            bizContent.put("card_expiry_date", tenYearsLaterDate);
            bizContent.put("device_expiry_date", tenYearsLaterDate);
            bizContent.put("card_no", card.getCardNo());
            bizContent.put("device_no", card.getObuCode());
            bizContent.put("device_status", "1");
        }
        
        
        try {
			invoke(bizContent,partner);
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage(),e.fillInStackTrace());
		}
		 return new EtcResponse();
       
    }

    
    public void invoke(JSONObject bizContent,ThirdPartner partner ) throws AlipayApiException {
        log.info("渠道信息 : "+JSON.toJSONString(partner));
        
        //最多重试5次
        int retryTimes = 2;
        for (int i = 1; i <= retryTimes; i++) {
            try {
                //Thread.sleep(5000 * (i - 1));
                //升级该接口支持不通渠道分发调用
                Map<String, String> map= new HashMap<String,String>();
                map.put("biz_content", bizContent.toJSONString());
                map.put("charset", "UTF-8");
                map.put("version", "1.0");
                map.put("service", "trawe.etc.publish");
                map.put("utc_timestamp", String.valueOf(System.currentTimeMillis()));
                map.put("pid", "2088234111");
                map.put("app_id", partner.getAppId());
                map.put("sign", SignUtil2.signRequestForMap(map,partner.getTrawePrivateKey(),"UTF-8"));
                map.put("sign_type", "RSA2");
                log.info("渠道 notify_url :" +partner.getNotifyUrl());
                log.info("网发 request : " +map.toString());
                HttpResult channelResp = HttpAPIService.doPost(partner.getNotifyUrl(), map);
                log.info("渠道 response : " +channelResp.toString());
                JSONObject channelRespJsonObj = JSON.parseObject(channelResp.getBody());
                //排序
                String respContentSort = JSON.toJSONString(channelRespJsonObj.get("response"), SerializerFeature.MapSortField); 
                String respSign =  JSON.toJSONString(channelRespJsonObj.get("sign")); 
                //log.info("渠道 验证签名内容：" +respContentSort);
                //log.info("渠道 sign : "+ respSign);
                //验证签名
                boolean verifyResponse = SignUtil2.verifyResponse(respContentSort, respSign, partner.getPublicKey(), "UTF-8");
                if(verifyResponse) {
                	 log.info("验证签名成功");
                	 JSONObject content = JSON.parseObject(respContentSort);
                	 if (content.getString("code") != null && "10000".equals(content.getString("code"))) {
                		 break;
                	 }
                	 log.info("接口响应 code 不等于 10000");
                }
                else {
                	 log.info("验证签名失败");
                }
                LogUtil.info(log, bizContent.getString("order_id"), "第" + i + "次调用申请单状态同步接口请求", map);
               
            } catch (Exception e) {
                LogUtil.error(log, bizContent.getString("order_id"), "第" + i + "次调用申请单状态同步接口失败", e);
            }
        }
    }

    private EtcResponse failed(EtcResponse res, String msg) {
        res.setMsg(msg);
        res.setCode(1);
        return res;
    }

    public ThirdOutOrderQueryResp queryThirdOutOrder(ThirdOutOrderQueryReq req) {
        LogUtil.info(log, req.getOrderNo(), "查询订单映射信息请求", req);
        ThirdOutOrderQueryResp res = null;
        LogUtil.info(log, req.getOrderNo(), "查询订单映射信息响应", res);
        if (res == null)
            throw new RuntimeException("查询订单映射信息响应失败,res为空");
        return res;
    }

    public EtcIssueOrder orderQuery(String orderNo) {
        IssueOrderQueryReq req = new IssueOrderQueryReq();
        req.setOrderNo(orderNo);
        req.setPageNo(1);
        req.setPageSize(10);
        LogUtil.info(log, orderNo, "查询订单信息请求", req);
        IssueOrderQueryResp res = IssueCenterApi.orderQuery(req, "");
        LogUtil.info(log, orderNo, "查询订单信息响应", res);
        if (res == null)
            throw new RuntimeException("查询订单信息响应失败,res为空");
        if (res.getCode() != 0)
            throw new RuntimeException("查询订单射信息响应失败,code不为0");
        List<EtcIssueOrder> result = res.getResult();
        if (ValidateUtil.isEmpty(result))
            return null;
        return result.get(0);
    }

    @Autowired
    private ApplyOrderQueryService applyOrderQueryService;
//    @Autowired
//    private TaskExecutor taskExecutor;
    @Autowired
    private ApiClient apiClient;
//    @Autowired
//    private AlipayClient alipayClient;
    @Autowired
    private ExpressInfoService expressInfoService;
    
//    public static void main(String[] args) {
//		String a ="{\"response\":{\"msg\":\"??\",\"code\":\"10000\",\"out_biz_no\":\"999\",\"order_id\":\"1561778865164\",\"sync_result\":\"1\"},\"sign\":\"Fa6ujoUGPrlQ/ZIFBbKFuABCLHt0HARbJ0USFAfgeUfAqIH9EmQ8NKpGBEw6gHBcC4vvEOmmsMUZq+xgXfQCiqSbRz/ckzN6q7Cv1Oa4Liz1HWTzRSVzoRoTBpEWQiMLtiq4M50c97oHDqxHAtuv2OuzQlFtEeTLvSRk8EP6jjIL8IuNWxVDEpJtEX7M6r4tcAbdNcjPeq+uU75HswAWr55Z1y5CdIsMhukjdL2OIwDllGavJbQUXr/6pXVOQamopY4377Lk77p1nXUkApMc9S7qr0aKS2WB0jml7Piru5vJqUm/CCEu4os3j9cIz1Z1ahsr7mAZnqr6jAhm+GUTFw==\"}";
//		JSONObject parseObject = JSON.parseObject(a);
//		net.sf.json.JSONObject responsebody = net.sf.json.JSONObject.fromObject(a);
//		System.out.println(responsebody.getString("response"));
//        String jsonString = JSON.toJSONString(parseObject.get("response"), SerializerFeature.MapSortField); 
//        System.out.println(jsonString);
//    }

}
