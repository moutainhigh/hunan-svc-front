package cn.trawe.etc.hunanfront.service.applysync;

import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Component;

/**
 * 订单同步策略工厂
 *
 * @author Jiang Guangxing
 */
@Slf4j
@Component
public class ApplySyncFactory extends ApplicationObjectSupport {
    private static final String BEAN_PREFIX = "applySyncStrategy";

    public ApplySyncStrategy getApplyCancelStrategy(String opType) {
        ApplicationContext applicationContext = super.getApplicationContext();
        if (applicationContext == null) {
            LogUtil.error(log, opType, "获取申请单同步操作失败");
            throw new RuntimeException("获取申请单同步操作失败");
        }
        try {
            return (ApplySyncStrategy) applicationContext.getBean(BEAN_PREFIX + opType);
        } catch (BeansException e) {
            //ignore error
            return (ApplySyncStrategy) applicationContext.getBean(BEAN_PREFIX);
        }
    }
}
