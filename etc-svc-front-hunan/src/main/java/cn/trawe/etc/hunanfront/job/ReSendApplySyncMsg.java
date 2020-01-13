//package cn.trawe.etc.hunanfront.job;
//
//import cn.trawe.etc.hunanfront.dao.ThirdOrderSycnRecordDao;
//import cn.trawe.etc.hunanfront.entity.ThirdOrderSycnRecord;
//import cn.trawe.etc.hunanfront.request.ApplyOrderSyncReq;
//import cn.trawe.etc.hunanfront.rocketmq.sender.RocketMqSender;
//import cn.trawe.pay.common.client.RedisClient;
//import com.alibaba.fastjson.JSON;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//import java.util.List;
//
///**
// * @author Jiang Guangxing
// */
//@Slf4j
//@Component
//public class ReSendApplySyncMsg {
//    private static final String SYNC_LOCK = "etc:front:alipay:lock";
//    private static final int SYNC_LOCK_EXPIRE_SECOND = 60;
//
//    //@Value("${rocketmq.max_reconsume_times}")
//    //private String maxReconsumeTimes;
//
//    @Scheduled(fixedDelay = 60 * 60 * 1000)
//    public void reSend() {
//        if (redisClient.lock(SYNC_LOCK, SYNC_LOCK_EXPIRE_SECOND)) {
//            try {
//                log.info("失败消息重发任务开始执行");
//                //todo 最大重试次数需配置
//                List<ThirdOrderSycnRecord> records = recordDao.find("retry_times > ? and retry_times < 20"
//                        , Collections.singletonList(16), 0, 100);
//                records.parallelStream().forEach(record -> {
//                    ApplyOrderSyncReq applyOrderSyncReq = JSON.parseObject(record.getReqJson(), ApplyOrderSyncReq.class);
//                    applyOrderSyncReq.setRecordId(record.getId());
//                    RocketMqSender.sendStringMsg(JSON.toJSONString(applyOrderSyncReq));
//                    log.info("【id={}】申请单同步消息重发完成", record.getId());
//                });
//            } finally {
//                redisClient.unlock(SYNC_LOCK);
//            }
//        } else {
//            log.info("not get lock");
//        }
//    }
//
//    private RedisClient redisClient;
//    private ThirdOrderSycnRecordDao recordDao;
//
//    @Autowired
//    public ReSendApplySyncMsg(RedisClient redisClient, ThirdOrderSycnRecordDao recordDao) {
//        this.redisClient = redisClient;
//        this.recordDao = recordDao;
//    }
//}
