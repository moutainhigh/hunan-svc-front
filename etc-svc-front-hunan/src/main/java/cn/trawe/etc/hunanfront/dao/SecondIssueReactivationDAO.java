package cn.trawe.etc.hunanfront.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.trawe.easyorm.BaseDAO;
import cn.trawe.pay.expose.entity.SecondIssueReactivationProcess;

/**
 * 
 * @author daifei
 *
 */
@Repository
public class SecondIssueReactivationDAO extends BaseDAO<SecondIssueReactivationProcess>{
	
	public SecondIssueReactivationProcess  findByOrderNo(String orderNo){
		SecondIssueReactivationProcess secondIssueReactivationProcess;
		String sql=" where order_no =? and finish_status in (0,1,2) ";
		List<Object> params = new ArrayList<>();
		params.add(orderNo);
		secondIssueReactivationProcess=  findOne(sql,params);
		return secondIssueReactivationProcess;
	}
}
