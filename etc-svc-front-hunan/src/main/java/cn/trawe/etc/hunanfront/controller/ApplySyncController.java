package cn.trawe.etc.hunanfront.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.trawe.etc.hunanfront.service.ApplyHunanSyncService;
import cn.trawe.pay.common.etcmsg.EtcResponse;

/**
 * @author Kevis
 * @date 2019/5/9
 */
@RestController
@RequestMapping("/alipay")
public class ApplySyncController {
    @Autowired
    private ApplyHunanSyncService applySyncService;

    @PostMapping("/apply-sync")
    public EtcResponse applySync(@RequestParam("order_no") String orderNo) {
        return applySyncService.applySync(orderNo);
    }
}
