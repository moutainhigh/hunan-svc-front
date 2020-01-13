package cn.trawe.etc.hunanfront.dao;

import cn.trawe.easyorm.BaseDAO;
import cn.trawe.etc.hunanfront.entity.ThirdOrderSycnRecord;

import org.springframework.stereotype.Repository;

/**
 * @author Jiang Guangxing
 */
@Repository
public class ThirdOrderSycnRecordDao extends BaseDAO<ThirdOrderSycnRecord> {
    public void addRetryTimes(long id) {
        String sql = "update third_order_sycn_record set retry_times=retry_times+1, update_time=NOW() where id=" + id;
        this.getMasterJdbcTemplate().execute(sql);
    }
}
