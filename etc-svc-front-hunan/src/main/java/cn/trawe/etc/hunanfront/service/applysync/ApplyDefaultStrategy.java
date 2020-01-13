package cn.trawe.etc.hunanfront.service.applysync;

import org.springframework.stereotype.Component;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.request.ApplyOrderSyncReq;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jiang Guangxing
 */
@Slf4j
@Component("applySyncStrategy")
public class ApplyDefaultStrategy extends ApplySyncStrategy {
    @Override
    public BaseResponse sync(String charset, ApplyOrderSyncReq req,ThirdPartner ThirdPartner) {
        return paramsError("暂不支持此类申请单同步操作", charset,ThirdPartner);
    }
}
