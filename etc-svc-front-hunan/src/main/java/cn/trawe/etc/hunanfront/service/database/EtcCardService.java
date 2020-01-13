package cn.trawe.etc.hunanfront.service.database;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.trawe.etc.hunanfront.dao.EtcIssueOrderDAO;
import cn.trawe.etc.hunanfront.dao.EtcIssueOrderHistroyDAO;
import cn.trawe.etc.hunanfront.dao.IssueEtcCardDAO;
import cn.trawe.etc.hunanfront.dao.IssueEtcCardHistroyDAO;
import cn.trawe.etc.hunanfront.dao.SecondIssueDAO;
import cn.trawe.etc.hunanfront.dao.SecondIssueReactivationDAO;
import cn.trawe.etc.hunanfront.service.BaseService;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.entity.EtcIssueOrderHistroy;
import cn.trawe.pay.expose.entity.IssueEtcCard;
import cn.trawe.pay.expose.entity.IssueEtcCardHistroy;
import cn.trawe.pay.expose.entity.SecondIssueProcess;
import cn.trawe.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EtcCardService extends BaseService{
	
    @Autowired
    IssueEtcCardDAO issueEtcCardDAO;
    
    @Autowired
    EtcIssueOrderDAO  etcIssueOrderDAO;

    @Autowired
    IssueEtcCardHistroyDAO issueEtcCardHistroyDAO;
    
    @Autowired
    SecondIssueDAO secondIssueDAO;

    @Autowired
    SecondIssueReactivationDAO secondIssueReactivationDAO;

    
    @Autowired
    EtcIssueOrderHistroyDAO etcIssueOrderHistroyDAO;
	
   
	/**
	  *   更新卡号，保存到历史表 初始化二发进度表、
	 * @param oldCardNo
	 * @param newCardNo
	 * @return
	 */
    @Transactional(rollbackFor = Exception.class)
	public Boolean replaceCardNo(String orderNo,String oldCardNo,String newCardNo){
		
		 Boolean flag = false;
		 EtcIssueOrder order = getOrder(orderNo,"");
		 LogUtil.info(log, orderNo, "订单信息:"+JSON.toJSONString(order));
		 IssueEtcCard issueEtcCardCheck = findByAccountNoAndCardNo("",newCardNo,4301);
		 LogUtil.info(log, orderNo, "新卡信息:"+JSON.toJSONString(issueEtcCardCheck));
		 if(issueEtcCardCheck!=null) {
			 if(!issueEtcCardCheck.getOrderNo().equals(orderNo)){
				 
				 throw new RuntimeException("新卡号:"+newCardNo+"已被"+issueEtcCardCheck.getPlateNo()+"绑定");
			 }
			 else {
				 LogUtil.info(log, orderNo, "已经存在新卡并且订单号一致");
				 return true;
			 }
			 
		 }
		
	     IssueEtcCard issueEtcCard = findByAccountNoAndCardNo("",oldCardNo,4301);
	     LogUtil.info(log, orderNo, "旧卡信息:"+JSON.toJSONString(issueEtcCard));
        JSONObject json = (JSONObject) JSON.toJSON(issueEtcCard);
        IssueEtcCardHistroy issueEtcCardHistroy = JSONObject.toJavaObject(json, IssueEtcCardHistroy.class);
        // 保存旧数据，并更新
        issueEtcCardHistroy.setCardNo(oldCardNo);
        int save = issueEtcCardHistroyDAO.save(issueEtcCardHistroy);
        if(save<=0) {
        	throw new RuntimeException("卡号保存历史表失败");
        }
        LogUtil.info(log, orderNo, "卡表保存历史表成功");
        issueEtcCard.setCardNo(newCardNo);
        issueEtcCard.setCreateTime(new Date());
        issueEtcCard.setUpdateTime(new Date());
        issueEtcCard.setStatus(0);
        issueEtcCard.setActivateStatus(0);
        issueEtcCard.setOrderNo(order.getNote2());
        int update = issueEtcCardDAO.update(issueEtcCard);
        if(update<=0) {
        	throw new RuntimeException("更新卡表失败");
        }
        LogUtil.info(log, orderNo, "卡表更新成功");
        SecondIssueProcess sep = secondIssueDAO.findByOrderNoAndOwnerCode(orderNo,issueEtcCard.getOwnerCode());
    	sep.setCardNo(newCardNo);
    	sep.setOrderNo(order.getNote2());
    	sep.setCardStatus("0");
    	sep.setFinishStatus("0");
    	sep.setUpdateTime(new Date());
    	Integer updateById = secondIssueDAO.updateById(sep);
    	 if(updateById<=0) {
        	throw new RuntimeException("更新卡表失败");
        }
    	 LogUtil.info(log, orderNo, "二发进度表更新成功");
        
      //更新二发进度表订单号
        order.setTwOrderNo(orderNo);
        order.setOrderNo(order.getNote2());
        order.setOrderStatus(16);  //二发初始化完成、卡号替换完成
        int update2 = etcIssueOrderDAO.update(order);
        if(update2<=0) {
        	throw new RuntimeException("更新订单表失败");
        }
        LogUtil.info(log, orderNo, "更新订单表成功");
        flag = true;
	    return flag;
		
	}
    @Transactional(rollbackFor = Exception.class)
	public Boolean replaceObuNo(String orderNo,String oldCardNo,String oldObuNo,String newObuNo){
		 EtcIssueOrder order = getOrder(orderNo,"");
		 LogUtil.info(log, orderNo, "订单信息:"+JSON.toJSONString(order));
		 Boolean flag = false;
	       
	     IssueEtcCard issueEtcCard = findByObuNo(oldObuNo,4301);
	     LogUtil.info(log, orderNo, "旧卡信息:"+JSON.toJSONString(issueEtcCard));
	     
       if (null != issueEtcCard) {
       
    	   IssueEtcCard issueEtcObu = findByObuNo(oldObuNo,4301);
    	   LogUtil.info(log, orderNo, "旧OBU信息:"+JSON.toJSONString(issueEtcObu));
    	   if(issueEtcObu==null) {
    		   throw new RuntimeException("OBU号:"+oldObuNo+"不存在");
    	   }
    	   IssueEtcCard issueEtcObuCheck = findByObuNo(newObuNo,4301);
    	   LogUtil.info(log, orderNo, "新OBU信息:"+JSON.toJSONString(issueEtcObuCheck));
  		 	if(issueEtcObuCheck!=null) {
  			 if(!issueEtcObuCheck.getOrderNo().equals(orderNo)){
  				 throw new RuntimeException("新OBU号:"+newObuNo+"已被"+issueEtcObuCheck.getPlateNo()+"绑定");
  			 }
  			 else {
  				 return true; 
  			 }
  			
  		   }
           JSONObject json = (JSONObject) JSON.toJSON(issueEtcCard);
           IssueEtcCardHistroy issueEtcCardHistroy = JSONObject.toJavaObject(json, IssueEtcCardHistroy.class);
           // 保存旧数据，并更新
           //issueEtcCardHistroy.setCardNo(oldCardNo);
           int save = issueEtcCardHistroyDAO.save(issueEtcCardHistroy);
           if(save<=0) {
           	throw new RuntimeException("卡号保存历史表失败");
           }
           LogUtil.info(log, orderNo, "卡表保存历史表成功");
           issueEtcCard.setObuCode(newObuNo);
           issueEtcCard.setUpdateTime(new Date());
           issueEtcCard.setStatus(0);
           issueEtcCard.setActivateStatus(0);
           issueEtcCard.setOrderNo(order.getNote2());
           int update = issueEtcCardDAO.update(issueEtcCard);
           if(update<=0) {
           	throw new RuntimeException("更新卡表失败");
           }
           LogUtil.info(log, orderNo, "卡表更新成功");
           SecondIssueProcess sep = secondIssueDAO.findByOrderNoAndOwnerCode(orderNo,issueEtcCard.getOwnerCode());
            sep.setOrderNo(order.getNote2());
           	sep.setObuNo(newObuNo);
           	sep.setCardStatus("1");
           	sep.setVehicleStatus("0");
           	sep.setFinishStatus("0");
           	sep.setSystemStatus("0");
           	sep.setUpdateTime(new Date());
           	Integer updateById = secondIssueDAO.updateById(sep);
           	if(updateById<=0) {
            	throw new RuntimeException("更新二发进度表失败");
            }
           	LogUtil.info(log, orderNo, "二发进度表更新成功");
           	
           
           
         //更新二发进度表订单号
           order.setTwOrderNo(orderNo);
           order.setOrderNo(order.getNote2());
           order.setOrderStatus(16);  //二发初始化完成、卡号替换完成
           int update2 = etcIssueOrderDAO.update(order);
           if(update2<=0) {
           	throw new RuntimeException("更新订单表失败");
           }
           LogUtil.info(log, orderNo, "更新订单表成功");
       }
       else {
    	   throw new RuntimeException("OBU号:"+oldObuNo+"不存在");
       }
         flag = true;
	        
	    return flag;
		
	}
    @Transactional(rollbackFor = Exception.class)
	public Boolean replaceCardNoObuNo(String orderNo,String oldCardNo,String newCardNo,String oldObuNo,String newObuNo){
		
		 Boolean flag = false;
		 EtcIssueOrder order = getOrder(orderNo,"");
		 LogUtil.info(log, orderNo, "订单信息:"+JSON.toJSONString(order));
		 IssueEtcCard issueEtcCardCheck = findByAccountNoAndCardNo("",newCardNo,4301);
		 LogUtil.info(log, orderNo, "新卡信息:"+JSON.toJSONString(issueEtcCardCheck));
		 if(issueEtcCardCheck!=null) {
			 if(!issueEtcCardCheck.getOrderNo().equals(orderNo)){
				 throw new RuntimeException("新卡号:"+newCardNo+"已被"+issueEtcCardCheck.getPlateNo()+"绑定");
			 }
			 else {
				 return true;
			 }
			 
		 }
		 IssueEtcCard issueEtcObuCheck = findByObuNo(newObuNo,4301);
		 LogUtil.info(log, orderNo, "新OBU信息:"+JSON.toJSONString(issueEtcObuCheck));
		 	if(issueEtcObuCheck!=null) {
			 if(!issueEtcObuCheck.getOrderNo().equals(orderNo)){
				 throw new RuntimeException("新OBU号:"+newObuNo+"已被"+issueEtcObuCheck.getPlateNo()+"绑定");
			 }
			 else {
				 return true; 
			 }
			
		   }
		 
		 
		IssueEtcCard issueEtcCard = findByAccountNoAndCardNo("",oldCardNo,4301);
		LogUtil.info(log, orderNo, "旧卡信息:"+JSON.toJSONString(issueEtcCard));
             if (null != issueEtcCard) {
    
    	
		    JSONObject json = (JSONObject) JSON.toJSON(issueEtcCard);
		    IssueEtcCardHistroy issueEtcCardHistroy = JSONObject.toJavaObject(json, IssueEtcCardHistroy.class);
		    // 保存旧数据，并更新
		    
		    int save = issueEtcCardHistroyDAO.save(issueEtcCardHistroy);
		    if(save<=0) {
               	throw new RuntimeException("卡号保存历史表失败");
            }
		    LogUtil.info(log, orderNo, "新增卡表到历史成功");
		    issueEtcCard.setCardNo(newCardNo);
		    issueEtcCard.setObuCode(newObuNo);
		    issueEtcCard.setUpdateTime(new Date());
		    issueEtcCard.setStatus(0);
		    issueEtcCard.setActivateStatus(0);
		    issueEtcCard.setOrderNo(order.getNote2());
		    int update = issueEtcCardDAO.update(issueEtcCard);
            if(update<=0) {
            	throw new RuntimeException("更新卡表失败");
            }
            LogUtil.info(log, orderNo, "卡表更新成功");
		    SecondIssueProcess sep = secondIssueDAO.findByOrderNoAndOwnerCode(orderNo,issueEtcCard.getOwnerCode());
            
        	sep.setOrderNo(order.getNote2());
      	    sep.setCardNo(newCardNo);
        	sep.setObuNo(newObuNo);
        	sep.setCardStatus("0");
        	sep.setVehicleStatus("0");
        	sep.setFinishStatus("0");
        	sep.setSystemStatus("0");
        	sep.setUpdateTime(new Date());
        	Integer updateById = secondIssueDAO.updateById(sep);
        	if(updateById<=0) {
            	throw new RuntimeException("更新二发进度表失败");
            }
        	LogUtil.info(log, orderNo, "二发进度表更新成功");
            	
            
            //更新二发进度表订单号
            order.setTwOrderNo(orderNo);
            order.setOrderNo(order.getNote2());
            order.setOrderStatus(16);  //二发初始化完成、卡号替换完成
            int update2 = etcIssueOrderDAO.update(order);
            if(update2<=0) {
            	throw new RuntimeException("更新订单表失败");
            }
            LogUtil.info(log, orderNo, "订单表更新成功");
            flag = true;
            }
             else {
            	 throw new RuntimeException("旧卡号:"+oldCardNo+"不存在");
             }
              
          
	        
	        return flag;
		
	}
	
	
	
	
	
	 private IssueEtcCard findByAccountNo(String accountNo,int ownerCode) {
	        ArrayList<Object> params = new ArrayList<>();
	        params.add(accountNo);
	        params.add(ownerCode);
	        return issueEtcCardDAO.findOne(" user_account = ? and  owner_code = ?    ", params);
	    }

	    private IssueEtcCard findByAccountNoAndCardNo(String accountNo, String cardNo,int ownerCode ) {
	        StringBuffer sql = new StringBuffer();
	        ArrayList<Object> params = new ArrayList<>();
	        sql.append(" where owner_code = ?   ");
	        params.add(ownerCode);
	       
	        if (!StringUtils.isEmpty(accountNo)) {
	            sql.append(" and  user_account = ? ");
	            params.add(accountNo);
	        }

	        if (!StringUtils.isEmpty(cardNo)) {
	            sql.append(" and  card_no = ? ");
	            params.add(cardNo);
	        }

	        return issueEtcCardDAO.findOne(sql.toString(), params);
	    }
	    
	    private IssueEtcCard findByObuNo(String obuNo,int ownerCode ) {
	        StringBuffer sql = new StringBuffer();
	        ArrayList<Object> params = new ArrayList<>();
	        sql.append(" where owner_code = ?   ");
	        params.add(ownerCode);
	       
	        

	        if (!StringUtils.isEmpty(obuNo)) {
	            sql.append(" and  obu_code = ? ");
	            params.add(obuNo);
	        }

	        return issueEtcCardDAO.findOne(sql.toString(), params);
	    }

	
	

}
