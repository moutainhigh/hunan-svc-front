package cn.trawe.etc.hunanfront.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.trawe.easyorm.BaseDAO;
import cn.trawe.easyorm.DAO;
import cn.trawe.pay.expose.entity.SecondIssueProcess;

/**
 * 
 * @author daifei
 *
 */
@Repository
public class SecondIssueDAO extends BaseDAO<SecondIssueProcess>{

    /**
     * 根据卡号和ownerCode查找，订单数据
     *
     * @param orderNo
     * @return
     */
    public SecondIssueProcess findByOrderNoAndOwnerCode(String orderNo, Integer ownerCode) {
        ArrayList<Object> params = new ArrayList<>();
        params.add(orderNo);
        params.add(ownerCode);
        return findOne(" order_no = ?   and  owner_code = ?  ", params);
    }

    public Integer updateById(SecondIssueProcess sep) {
        List<Object> params = new ArrayList<>();
        params.add(sep.getId());
        params.add(sep.getOwnerCode());
        return DAO.use(getMasterJdbcTemplate().getDataSource())
                .query()
                .update(sep, "id =? and owner_code = ? ", params);
    }
}