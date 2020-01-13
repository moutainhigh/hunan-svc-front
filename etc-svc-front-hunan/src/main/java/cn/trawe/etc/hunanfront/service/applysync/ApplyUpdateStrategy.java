package cn.trawe.etc.hunanfront.service.applysync;

import org.springframework.beans.factory.annotation.Autowired;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.request.ApplyOrderSyncReq;
import lombok.extern.slf4j.Slf4j;

/**
 * 订单同步策略（更新）
 *
 * @author Jiang Guangxing
 */
@Slf4j
//@Component("applySyncStrategy2")
public class ApplyUpdateStrategy extends ApplySyncStrategy {
    @Override
    public BaseResponse sync(String charset, ApplyOrderSyncReq req,ThirdPartner ThirdPartner) {
        return applySyncStrategy1.sync(charset, req,ThirdPartner);
    }

    private ApplySyncStrategy applySyncStrategy1;

    @Autowired
    public ApplyUpdateStrategy(ApplySyncStrategy applySyncStrategy1) {
        this.applySyncStrategy1 = applySyncStrategy1;
    }
}
