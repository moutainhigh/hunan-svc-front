package cn.trawe.etc.hunanfront.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ptc.board.log.BizDigestLog;

import cn.trawe.etc.hunanfront.config.ParamModel;
import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.rocketmq.sender.RocketMqSender;
import cn.trawe.etc.hunanfront.service.ApplyOrderQueryService;
import cn.trawe.etc.hunanfront.service.ApplyOrderSyncService;
import cn.trawe.etc.hunanfront.service.MediaTransferService;
import cn.trawe.etc.hunanfront.service.PrecheckService;
import cn.trawe.etc.hunanfront.service.UserBlacklistSyncService;
import cn.trawe.etc.hunanfront.service.secondissue.HunanSecondIssueBussinessImpl;
import cn.trawe.etc.hunanfront.service.secondissue.image.ImageVehicleService;
import cn.trawe.util.LogUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jianjun.chai
 */
@Slf4j
@RestController
@RequestMapping("/etc")
public class FrontHunanController {
    @Autowired
    private PrecheckService precheckService;

    @Autowired
    private MediaTransferService mediaTransferService;

    @Autowired
    private ApplyOrderSyncService applyOrderSyncService;

    @Autowired
    private ApplyOrderQueryService applyOrderQueryService;

    @Autowired
    private UserBlacklistSyncService userBlacklistSyncService;
//    @Autowired
//    private ApplyOrderSyncListener applyOrderSyncListener;
    @Autowired
    HunanSecondIssueBussinessImpl  HunanSecondIssueBussinessImpl;
    @Autowired
    ImageVehicleService ImageVehicleService;

    @Autowired
    private RocketMqSender rocketMqSender;
    
    @GetMapping(value = "/")
    public String index() {
        return "湖南ETC在线网发平台";
    }

    @BizDigestLog(bizType = "车牌预检", version = "1.0")
    @ApiOperation(value = "/precheck")
    @RequestMapping(value = "/precheck", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseResponse precheck(@ParamModel BaseRequest req) {
        LogUtil.info(log, "precheck", "车牌预检请求：", req);
        BaseResponse resp = precheckService.precheck(req);
        LogUtil.info(log, "precheck", "车牌预检响应：", resp);
        return resp;
    }

    
    @PostMapping("/media_transfer")
    public BaseResponse mediaTransfer(@ParamModel BaseRequest req) {
    	LogUtil.info(log, "media_transfer", "多媒体传输请求：", req.getAppId());
        BaseResponse resp = mediaTransferService.mediaTransfer(req);
        LogUtil.info(log, "media_transfer", "多媒体传输响应：", resp);
        return resp;
    }

    @BizDigestLog(bizType = "申请单同步", version = "1.0")
    @PostMapping("/apply_order_sync")
    public BaseResponse applyOrderSync(@ParamModel  BaseRequest req) {
        LogUtil.info(log, "apply_order_sync", "申请单同步请求：", req);
        BaseResponse resp = applyOrderSyncService.applyOrderSync(req);
        LogUtil.info(log, "apply_order_sync", "申请单同步响应：", resp);
        return resp;
    }

    @PostMapping("/apply_order_query")
    public BaseResponse applyOrderQuery(@ParamModel BaseRequest req) {
        LogUtil.info(log, "apply_order_query", "订单查询请求：", req);
        BaseResponse resp = applyOrderQueryService.applyOrderQuery(req);
        LogUtil.info(log, "apply_order_query", "订单查询响应：", resp);
        return resp;
    }

    @Deprecated
    //@PostMapping("/user_blacklist_sync")
    public BaseResponse userBlacklistSync(@ParamModel BaseRequest req) {
        LogUtil.info(log, null, "黑名单同步请求：", req);
        BaseResponse resp = userBlacklistSyncService.userBlacklistSync(req);
        LogUtil.info(log, null, "黑名单同步响应：", resp);
        return resp;
    }

//    @GetMapping("/test")
//    public void test() {
//        rocketMqSender.sendStringMsg("test");
//    }
//
//    @PostMapping("reSend")
//    public void reSend(@RequestParam String json) {
//        ApplyOrderSyncReq req = JSON.parseObject(json, ApplyOrderSyncReq.class);
//        applyOrderSyncListener.saveOrUpdateOrderInfo(req);
//    }
    
    @PostMapping("/second_issue")
    public BaseResponse second_issue(@ParamModel BaseRequest req) {
    	LogUtil.info(log, "second_issue", "在线二发请求：", req);
    	BaseResponse resp = HunanSecondIssueBussinessImpl.autoIssue(req);
    	LogUtil.info(log, "second_issue", "在线二发请求响应：", resp);
    	return resp;
    }
    
    @PostMapping("/image/vehicle")
    public BaseResponse image_vehicle(@ParamModel BaseRequest req) {
    	LogUtil.info(log, "image/vehicle", "在线二发提交车头照片请求：", req.getAppId());
    	BaseResponse doService = ImageVehicleService.doService(req);
    	//BaseResponse resp = HunanSecondIssueBussinessImpl.autoIssue(req);
    	LogUtil.info(log, "image/vehicle", "在线二发提交车头照片响应：", doService);
    	return doService;
    }
    
    

/*    @GetMapping("/test1")
    public void test1() {
//        rocketMqSender.sendStringMsg("test");
        RocketmqManager.produce("alipayordersync", "ordersync", "this is a demo message on "+System.currentTimeMillis());
        RocketMqSender.sendStringMsg("mq更改配置测试。。。。--==345678");
    }*/


}
