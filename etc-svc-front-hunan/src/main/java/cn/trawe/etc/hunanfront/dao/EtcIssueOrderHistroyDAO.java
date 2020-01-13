/**
 * EtcIssueOrderDAO.java
 * 2017年5月13日
 * ©2015-2016 北京特微智能科技有限公司. All rights reserved.
 */
package cn.trawe.etc.hunanfront.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.trawe.easyorm.BaseDAO;
import cn.trawe.pay.expose.entity.EtcIssueOrderHistroy;

/**
 * 
 * @author zhaoyouqiang
 * 
 */
@Repository
public class EtcIssueOrderHistroyDAO extends BaseDAO<EtcIssueOrderHistroy> {

    public List<EtcIssueOrderHistroy> findOrders(String sql, List<Object> params,int begin,int pageSize) {
        return this.find(sql, params,begin,pageSize);
    }
    
    public List<EtcIssueOrderHistroy> findPhoneByalipayUserId(String alipayUserId) {
    	 List<Object> params = new ArrayList<>();
         params.add(alipayUserId);
    	String sql=" alipay_user_id = ? ";
        return this.find(sql, params);
    }
}
