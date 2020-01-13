/*
package cn.trawe.etc.hunanfront.config;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;

import cn.trawe.etc.hunanfront.rocketmq.listener.ApplyOrderSyncListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Properties;

*/
/**
 * @author Jiang Guangxing
 *//*

@Slf4j
@Configuration
public class RocketMqConfig {
    @Value("${rocketmq.group_id}")
    private String groupId;
    @Value("${rocketmq.access_key}")
    private String accessKey;
    @Value("${rocketmq.secret_key}")
    private String secretKey;
    @Value("${rocketmq.consume_thread_nums}")
    private String consumeThreadNums;
    @Value("${rocketmq.namesrv_addr}")
    private String namesrvAddr;
    @Value("${rocketmq.send_msg_timeout_millis}")
    private String sendMsgTimeoutMillis;
    @Value("${rocketmq.topic}")
    private String topic;
    @Value("${rocketmq.tag}")
    private String tag;
    @Value("${rocketmq.max_reconsume_times}")
    private String maxReconsumeTimes;

    @Autowired
    private ApplyOrderSyncListener applyOrderSyncListener;

    @Bean
    public Producer producer(Properties rocketMqProperties) {
        log.info("启动生产者开始");
        // 在发送消息前，初始化调用start方法来启动Producer，只需调用一次即可，当项目关闭时，自动shutdown
        Producer producer = ONSFactory.createProducer(rocketMqProperties);
        producer.start();
        log.info("启动生产者成功");
        return producer;
    }

    @PostConstruct
    public void consumer() {
        log.info("启动消费者开始");
        Consumer consumer = ONSFactory.createConsumer(rocketMqProperties());
        consumer.subscribe(topic, tag, applyOrderSyncListener);//监听第一个topic，new对应的监听器
        // 在发送消息前，必须调用start方法来启动consumer，只需调用一次即可，当项目关闭时，自动shutdown
        consumer.start();
        log.info("启动消费者成功");
    }

    @Bean
    public Properties rocketMqProperties() {
        Properties properties = new Properties();
        //您在控制台创建的consumer ID
        properties.setProperty(PropertyKeyConst.GROUP_ID, groupId);
        // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.setProperty(PropertyKeyConst.AccessKey, accessKey);
        // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.setProperty(PropertyKeyConst.SecretKey, secretKey);
        //设置发送超时时间，单位毫秒
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, sendMsgTimeoutMillis);
        properties.setProperty(PropertyKeyConst.ConsumeThreadNums, consumeThreadNums);
        properties.setProperty(PropertyKeyConst.MaxReconsumeTimes, maxReconsumeTimes);
        // 设置 TCP 接入域名(此处以公共云生产环境为例)，设置 TCP 接入域名，进入 MQ 控制台的消费者管理页面，在左侧操作栏单击获取接入点获取
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, namesrvAddr);

        return properties;
    }
}
*/
