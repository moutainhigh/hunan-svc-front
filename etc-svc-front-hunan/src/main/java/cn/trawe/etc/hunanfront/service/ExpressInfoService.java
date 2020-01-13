package cn.trawe.etc.hunanfront.service;

import cn.trawe.etc.hunanfront.entity.ExpressInfo;
import cn.trawe.etc.hunanfront.feign.ExpressInfoClient;
import cn.trawe.pay.common.client.RedisClient;
import cn.trawe.util.LogUtil;
import cn.trawe.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jiang Guangxing
 */
@Slf4j
@Service
public class ExpressInfoService {
    @Autowired
    private ExpressInfoClient expressInfoClient;
    @Autowired
    private RedisClient redisClient;

    private static String getDeliveryCodeKey(String deliveryName) {
        return "etc:front:alipay:deliverycode:" + deliveryName;
    }

    public String getDeliveryCode(String deliveryName) {
        if (ValidateUtil.isEmpty(deliveryName))
            return null;
        String data = redisClient.get(getDeliveryCodeKey(deliveryName), null);
        if (!ValidateUtil.isEmpty(data))
            return data;

        String deliveryCode = "UNKNOWN";
        LogUtil.info(log, deliveryName, "查询快递信息请求");
        List<ExpressInfo> expressInfoList = expressInfoClient.expressInfos();
        LogUtil.info(log, deliveryName, "查询快递信息响应", expressInfoList);
        for (ExpressInfo info : expressInfoList) {
            if (deliveryName.contains(info.getDisplay().getFullname()) || deliveryName.contains(info.getDisplay().getShortname())) {
                deliveryCode = info.getSkey();
                break;
            }
        }
        redisClient.set(getDeliveryCodeKey(deliveryName), deliveryCode, 24 * 60 * 60);
        return deliveryCode;
    }
}
