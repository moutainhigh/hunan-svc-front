package cn.trawe.etc.hunanfront.dao;

import cn.trawe.easyorm.BaseDAO;
import cn.trawe.etc.hunanfront.entity.ThirdOrderSycnRecordCompleted;

import org.springframework.stereotype.Repository;

/**
 * @author Jiang Guangxing
 */
@Repository
public class ThirdOrderSycnRecordCompletedDao extends BaseDAO<ThirdOrderSycnRecordCompleted> {
    public void valid(long id) {
        String sql = "INSERT INTO third_order_sycn_record_completed ( req_json, retry_times ) SELECT" +
                " req_json, retry_times FROM third_order_sycn_record WHERE id =" + id;
        masterJdbcTemplate.execute(sql);
    }
}
