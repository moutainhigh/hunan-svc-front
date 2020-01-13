package cn.trawe.etc.hunanfront.service.applysync;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.trawe.etc.hunanfront.dao.ThirdOrderSycnRecordDao;
import cn.trawe.etc.hunanfront.entity.ThirdOrderSycnRecord;
import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.feign.ApiClient;
import cn.trawe.etc.hunanfront.request.ApplyOrderSyncReq;
import cn.trawe.etc.hunanfront.rocketmq.sender.RocketMqSender;
import cn.trawe.etc.hunanfront.utils.ValidUtils;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.request.issue.IssueOrderQueryReq;
import cn.trawe.pay.expose.request.issue.ThirdOutOrderQueryReq;
import cn.trawe.pay.expose.response.issue.IssueOrderQueryResp;
import cn.trawe.pay.expose.response.issue.ThirdOutOrderQueryResp;
import cn.trawe.util.LogUtil;
import cn.trawe.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 订单同步策略（提交）
 *
 * @author Jiang Guangxing
 */
@Slf4j
@Component("applySyncStrategy1")
public class ApplySubmitStrategy extends ApplySyncStrategy {
	
	  @Autowired
	  private ApiClient apiClient;

    @Override
    public BaseResponse sync(String charset, ApplyOrderSyncReq req,ThirdPartner ThirdPartner) {
        String err = check(req);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, charset,ThirdPartner);

        ThirdOutOrderQueryReq reqOut = new ThirdOutOrderQueryReq();
        reqOut.setOutOrderId(req.getOrderId());
        ThirdOutOrderQueryResp outOrderQueryRes;
        try {
            outOrderQueryRes = this.queryThirdOutOrder(reqOut);
        } catch (Exception e) {
            LogUtil.error(log, req.getOrderId(), "查询订单映射信息失败", e);
            return succeedResponseError( charset,req,ThirdPartner,"查询订单映射信息失败");
        }
        // code ==1  代表没有提交到改订单
        if(outOrderQueryRes.getCode()==1) {
        	 long id = saveRecord(req);
             //入队是否成功
             if (id != 0 && !sendMsg(req.setRecordId(id)))
                 return systemError(charset,ThirdPartner);

             return succeedResponseCheck(charset, req,ThirdPartner);
        }
        else {
        	return succeedResponseError( charset,req,ThirdPartner,"重复订单提交");
        }
      
        
       
    }

    private String check(ApplyOrderSyncReq applyOrderSyncReq) {
        String err = ValidUtils.validateBean(applyOrderSyncReq);
//        if (!AuditType.canWarp(applyOrderSyncReq.getSellerId()))
//            err += ",发行方编号不正确";
        if (applyOrderSyncReq.getViInfo() != null) {
            String viInfoErr = ValidUtils.validateBean(applyOrderSyncReq.getViInfo());
            if (ValidateUtil.isNotEmpty(viInfoErr))
                err += "," + viInfoErr;
        }
        if (applyOrderSyncReq.getViOwnerInfo() != null) {
            String viOwnerInfoErr = ValidUtils.validateBean(applyOrderSyncReq.getViOwnerInfo());
            if (ValidateUtil.isNotEmpty(viOwnerInfoErr))
                err += "," + viOwnerInfoErr;
        }
        if (applyOrderSyncReq.getDeliveryInfo() != null) {
            String deliveryInfoErr = ValidUtils.validateBean(applyOrderSyncReq.getDeliveryInfo());
            if (ValidateUtil.isNotEmpty(deliveryInfoErr))
                err += "," + deliveryInfoErr;
        }
//        if (applyOrderSyncReq.getInvoiceInfo() != null
//                && "1".equals(applyOrderSyncReq.getInvoiceInfo().getNeedInvoice())) {
//            String invoiceInfoErr = ValidUtils.validateBean(applyOrderSyncReq.getInvoiceInfo());
//            if (ValidateUtil.isNotEmpty(invoiceInfoErr))
//                err += "," + invoiceInfoErr;
//            if ("1".equals(applyOrderSyncReq.getInvoiceInfo().getInvoiceTitleType())
//                    && ValidateUtil.isEmpty(applyOrderSyncReq.getInvoiceInfo().getDutyNo()))
//                err += ",发票信息税号不能为空";
//        }
        if (err.startsWith(","))
            err = err.substring(1);
        return err;
    }

    private long saveRecord(ApplyOrderSyncReq applyOrderSyncReq) {
        if (applyOrderSyncReq.getRecordId() == 0)
        	//保存数据库返回主键ID
            return thirdOrderSycnRecordDao.saveAndReturnKey(new ThirdOrderSycnRecord().setReqJson(JSON.toJSONString(applyOrderSyncReq)));
        else {
            try {
                thirdOrderSycnRecordDao.addRetryTimes(applyOrderSyncReq.getRecordId());
            } catch (Exception e) {
                LogUtil.error(log, applyOrderSyncReq.getOrderId(), "更新订单同步记录失败", e);
                return 0;
            }
            return applyOrderSyncReq.getRecordId();
        }
    }

    private boolean sendMsg(ApplyOrderSyncReq applyOrderSyncReq) {
        //请求入队
        try {
            RocketMqSender.sendStringMsg(JSON.toJSONString(applyOrderSyncReq));
        } catch (Exception e) {
            LogUtil.error(log, applyOrderSyncReq.getOrderId(), "发送消息失败", e);
            return false;
        }
        return true;
    }

    private ThirdOrderSycnRecordDao thirdOrderSycnRecordDao;

    @Autowired
    public ApplySubmitStrategy(RocketMqSender rocketMqSender, ThirdOrderSycnRecordDao thirdOrderSycnRecordDao) {
        this.thirdOrderSycnRecordDao = thirdOrderSycnRecordDao;
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
    
    public ThirdOutOrderQueryResp queryThirdOutOrder(ThirdOutOrderQueryReq req) {
        LogUtil.info(log, req.getOrderNo(), "查询订单映射信息请求", req);
        ThirdOutOrderQueryResp res = null;
        LogUtil.info(log, req.getOrderNo(), "查询订单映射信息响应", res);
        if (res == null)
            throw new RuntimeException("查询订单映射信息响应失败,res为空");
        return res;
    }
}
