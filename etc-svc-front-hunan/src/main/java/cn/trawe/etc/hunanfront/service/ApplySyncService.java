package cn.trawe.etc.hunanfront.service;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.enums.DeviceStatus;
import cn.trawe.etc.hunanfront.feign.ApiClient;
import cn.trawe.etc.hunanfront.feign.v2.IssueCenterApi;
import cn.trawe.etc.hunanfront.utils.DateUtil;
import cn.trawe.pay.common.etcmsg.EtcResponse;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.entity.IssueEtcCard;
import cn.trawe.pay.expose.enums.EtcIssueOrderStatus;
import cn.trawe.pay.expose.request.issue.IssueOrderQueryReq;
import cn.trawe.pay.expose.request.issue.QueryByOwnerReq;
import cn.trawe.pay.expose.request.issue.ThirdOutOrderQueryReq;
import cn.trawe.pay.expose.response.issue.IssueEtcCardResp;
import cn.trawe.pay.expose.response.issue.IssueOrderQueryResp;
import cn.trawe.pay.expose.response.issue.ThirdOutOrderQueryResp;
import cn.trawe.util.LogUtil;
import cn.trawe.utils.DateUtils;
import cn.trawe.utils.ValidateUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayCommerceTransportEtcApplySyncRequest;
import com.alipay.api.response.AlipayCommerceTransportEtcApplySyncResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Jiang Guangxing
 */
@Slf4j
//@Service
public class ApplySyncService {
    @Value("${api.token}")
    private String token;
    
    @Autowired
	protected  IssueCenterApi  IssueCenterApi;
    
	
	@Autowired
    private ThirdPartnerService thirdPartnerService;

    public EtcResponse applySync(String orderNo) {
        log.info("订单【{}】同步状态", orderNo);
        
        //ThirdPartner partner = thirdPartnerService.getPartner(req);
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

        CompletableFuture<String> orderIdFuture = CompletableFuture.supplyAsync(() -> {
            ThirdOutOrderQueryReq req = new ThirdOutOrderQueryReq();
            req.setOrderNo(order.getOrderNo());
            ThirdOutOrderQueryResp outOrderQueryRes;
            try {
                outOrderQueryRes = this.queryThirdOutOrder(req);
            } catch (Exception e) {
                LogUtil.error(log, orderNo, "查询订单映射信息失败", e);
                return null;
            }
            return outOrderQueryRes.getOutOrderId();
        }, taskExecutor);

        CompletableFuture<IssueEtcCard> cardFuture = CompletableFuture.supplyAsync(() -> {
            QueryByOwnerReq req = new QueryByOwnerReq();
            req.setOwnerCode(order.getOwnerCode());
            LogUtil.info(log, order.getAlipayUserId(), "查询卡信息请求", req);
            IssueEtcCardResp<IssueEtcCard> cardResp = apiClient.queryCardByAlipayUserId(order.getAlipayUserId(), req, token);
            LogUtil.info(log, order.getAlipayUserId(), "查询卡信息响应", cardResp);
            if (cardResp == null)
                return null;
            return cardResp.getResult();
        }, taskExecutor);

        CompletableFuture<String> deliveryCodeFuture = null;
        if (ValidateUtil.isNotEmpty(order.getDeliveryName())) {
            deliveryCodeFuture = CompletableFuture.supplyAsync(() -> {
                LogUtil.info(log, orderNo, "查询物流公司编号", order.getDeliveryName());
                return expressInfoService.getDeliveryCode(order.getDeliveryName());
            }, taskExecutor);
        }

        String orderId = orderIdFuture.join();
        if (ValidateUtil.isEmpty(orderId)) {
            LogUtil.error(log, orderNo, "orderId为空");
            return failed(res, "orderId为空");
        }
        JSONObject bizContent = new JSONObject();
        bizContent.put("order_id", orderId);
        bizContent.put("order_status", applyOrderQueryService.convertOrderStatus(order.getOrderStatus(), order.getOwnerCode()).ordinal());
        bizContent.put("order_update_time", DateUtils.format(order.getUpdateTime(), DateUtils.YYYY_MM_DD_HH_MM_SS));
        if (ValidateUtil.isNotEmpty(order.getAuditDesc()) && order.getOrderStatus() == EtcIssueOrderStatus.FAIL_AUDIT.getCode())
            bizContent.put("censor_info", order.getAuditDesc());
        if (ValidateUtil.isNotEmpty(order.getDeliveryName())) {
            bizContent.put("delivery_name", order.getDeliveryName());
        }
        if (ValidateUtil.isNotEmpty(order.getDeliveryCode()))
            bizContent.put("delivery_no", order.getDeliveryCode());

        if (deliveryCodeFuture != null)
            bizContent.put("delivery_code", deliveryCodeFuture.join());

        IssueEtcCard card = cardFuture.join();
        if (card != null) {
            String tenYearsLaterDate = DateUtil.tenYearsLaterDate(card.getCreateTime());
            bizContent.put("device_type", "");//todo 设备类型 暂时无法确定有哪些类型
            bizContent.put("card_expiry_date", tenYearsLaterDate);
            bizContent.put("device_expiry_date", tenYearsLaterDate);
            bizContent.put("card_no", card.getCardNo());
            bizContent.put("device_no", card.getObuCode());
            int deviceStatus = applyOrderQueryService.convertDeviceStatus(order.getOrderStatus()).ordinal();
            if (card.getActivateStatus() == 1)
                deviceStatus = DeviceStatus.ACTIVATED.ordinal();
            bizContent.put("device_status", deviceStatus);
        }

        CompletableFuture.runAsync(() -> invoke(bizContent), taskExecutor);
        return new EtcResponse();
    }

    @Async("taskExecutor")
    public void invoke(JSONObject bizContent) {
        AlipayCommerceTransportEtcApplySyncRequest applySyncRequest = new AlipayCommerceTransportEtcApplySyncRequest();
        applySyncRequest.setBizContent(bizContent.toJSONString());
        LogUtil.info(log, bizContent.getString("order_id"), "调用申请单状态同步接口请求", bizContent);
        //最多重试5次
        int retryTimes = 5;
        for (int i = 1; i <= retryTimes; i++) {
            try {
                Thread.sleep(5000 * (i - 1));
                AlipayCommerceTransportEtcApplySyncResponse res = alipayClient.execute(applySyncRequest);
                LogUtil.info(log, bizContent.getString("order_id"), "第" + i + "次调用申请单状态同步接口响应", res);
                if (res != null && "10000".equals(res.getCode()))
                    break;
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
//        LogUtil.info(log, req.getOutOrderId(), "查询订单映射信息请求", req);
//        ThirdOutOrderQueryResp res = IssueCenterApi.queryThirdOutOrder(req);
//        LogUtil.info(log, req.getOutOrderId(), "查询订单映射信息响应", res);
//        if (res == null)
//            throw new RuntimeException("查询订单映射信息响应失败,res为空");
        return null;
    }

    public EtcIssueOrder orderQuery(String orderNo) {
        IssueOrderQueryReq req = new IssueOrderQueryReq();
        req.setOrderNo(orderNo);
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
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ApiClient apiClient;
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private ExpressInfoService expressInfoService;

}
