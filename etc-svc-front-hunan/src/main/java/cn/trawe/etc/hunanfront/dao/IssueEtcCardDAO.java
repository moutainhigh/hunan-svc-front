package cn.trawe.etc.hunanfront.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

import cn.trawe.easyorm.BaseDAO;
import cn.trawe.easyorm.DAO;
import cn.trawe.pay.expose.entity.IssueEtcCard;
import cn.trawe.utils.DateUtils;
/**
 * 
 * @author daifei
 *
 */
@Repository
public class IssueEtcCardDAO extends BaseDAO<IssueEtcCard>{
	
	
	
	public List<IssueEtcCard> findByJson(JSONObject json){
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" 1=1 ");
		List<Object> params = new ArrayList<>();
		int pageNo=json.getIntValue("page_no");
		if (pageNo < 1) {
			pageNo = 1;
		}
		int pageSize=json.getIntValue("page_size");
		if (pageSize < 1) {
			pageSize = 1;
		}
		int begin = (pageNo - 1) * pageSize;
		for(String str:json.keySet()){
			if("page_no".equals(str)){
				continue;
			}
			if("page_size".equals(str)){
				continue;
			}
			Object value = json.get(str);
			if(null!=value && !StringUtils.isEmpty(value.toString())){
				if("pretreatment_effective_time".equals(str)){
					sqlBuffer.append(" and "+str +" <= ?");
					params.add(DateUtils.parse(value.toString()));
				}else{
					sqlBuffer.append(" and "+str +" = ?");
					params.add(value);
				}
				
			}
		}
		 return this.find(sqlBuffer.toString(), params, begin, pageSize);
	}
	
	
	public IssueEtcCard  getByPlateNo(String plateNo,int plateColor,int ownerCode){
		IssueEtcCard issueEtcCard=new IssueEtcCard();
		String sql=" where plate_no =? and plate_color = ?  and  owner_code = ? ";
		List<Object> params = new ArrayList<>();
		params.add(plateNo);
		params.add(plateColor);
		params.add(ownerCode);
		issueEtcCard=  findOne(sql,params);
		if(null==issueEtcCard){
			sql=" where plate_no =?  and  owner_code = ? ";
			params = new ArrayList<>();
			params.add(plateNo);
			params.add(ownerCode);
			issueEtcCard=  findOne(sql,params);
		}
		
		return issueEtcCard;
	}
	
	/**查询用户存在的有效的ETC卡*/
    public IssueEtcCard queryCardByalipayUserId(String alipayUserId,int ownerCode) {
        String sql = " where alipay_user_id = ? AND status in(1,2)  and  owner_code = ? ";
        List<Object> params = new ArrayList<>();
        params.add(alipayUserId);
        params.add(ownerCode);
        return findOne(sql,params);
    }
    
    
    /**根据订单号查询卡信息*/
    public IssueEtcCard queryCardByCardNo(String cardNo,int ownerCode) {
        String sql = " where card_no = ? AND owner_code = ?  ";
        List<Object> params = new ArrayList<>();
        params.add(cardNo);
        params.add(ownerCode);
        return findOne(sql,params);
    }
    
    public IssueEtcCard queryCardByOrderNo(String orderNo,int ownerCode) {
        String sql = " where order_no = ? AND owner_code = ?  ";
        List<Object> params = new ArrayList<>();
        params.add(orderNo);
        params.add(ownerCode);
        return findOne(sql,params);
    }

	/**
	 * 根据卡号和ownerCode查找，卡订单数据
	 *
	 * @param cardNo
	 * @return
	 */
	public IssueEtcCard findByOrderNoAndOwnerCode(String orderNo, Integer ownerCode) {
		StringBuffer sql = new StringBuffer(" order_no = ?   and  owner_code = ?  ");
		ArrayList<Object> params = new ArrayList<>();
		params.add(orderNo);
		params.add(ownerCode);
		return findOne(sql.toString(), params);
	}
	
	
	@Override
	public int update(IssueEtcCard issueEtcCard) {
        List<Object> param = new ArrayList<>();
        String where = " where id=? and owner_code = ?  ";
        param.add(issueEtcCard.getId());
        param.add(issueEtcCard.getOwnerCode());

        return DAO.use(getMasterJdbcTemplate().getDataSource())
                .query()
                .update(issueEtcCard, where, param);
    }

}
