package cn.trawe.etc.hunanfront.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.trawe.easyorm.BaseDAO;
import cn.trawe.pay.expose.entity.EtcIssueOrder;

/**
 * @author zhaoyouqiang
 */
@Repository
public class EtcIssueOrderDAO extends BaseDAO<EtcIssueOrder> {

    public List<EtcIssueOrder> findOrders(String sql, List<Object> params, int begin, int pageSize) {
        return this.find(sql, params, begin, pageSize);
    }

    public List<EtcIssueOrder> findByPlate(String plateNo, String plateColor, String outId) {
        return this.find("plate_no=? and plate_color=? and out_order_id!=?", asList(plateNo, plateColor, outId));
    }
}
