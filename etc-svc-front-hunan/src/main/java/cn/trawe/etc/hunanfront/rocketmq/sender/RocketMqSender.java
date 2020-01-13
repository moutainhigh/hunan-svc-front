package cn.trawe.etc.hunanfront.rocketmq.sender;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jiang Guangxing
 */
@Slf4j
@Component
public class RocketMqSender {
	
	
	
    public static String sendStringMsg(String msg) {
//    	 return RocketmqManager.produce("alipay", "hunan-netpub-ordersync", msg);
    	return "";
    }

    private RocketMqSender() {
    }
}
