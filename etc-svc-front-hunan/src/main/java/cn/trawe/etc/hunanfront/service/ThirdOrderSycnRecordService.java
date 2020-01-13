package cn.trawe.etc.hunanfront.service;

import cn.trawe.etc.hunanfront.dao.ThirdOrderSycnRecordCompletedDao;
import cn.trawe.etc.hunanfront.dao.ThirdOrderSycnRecordDao;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Jiang Guangxing
 */
@Slf4j
@Service
public class ThirdOrderSycnRecordService {
    public void addRetryTimes(long id) {
        recordDao.addRetryTimes(id);
    }

    @Transactional
    public void valid(long id) {
        recordCompletedDao.valid(id);
        recordDao.delete(id);
    }

    @Autowired
    public ThirdOrderSycnRecordService(ThirdOrderSycnRecordDao recordDao, ThirdOrderSycnRecordCompletedDao recordCompletedDao) {
        this.recordDao = recordDao;
        this.recordCompletedDao = recordCompletedDao;
    }

    private ThirdOrderSycnRecordDao recordDao;
    private ThirdOrderSycnRecordCompletedDao recordCompletedDao;
}
