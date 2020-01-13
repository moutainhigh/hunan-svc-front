package cn.trawe.etc.hunanfront.service.v2;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.req.OpenAccountReq;
import cn.trawe.etc.hunanfront.expose.v2.req.QualificationCheckReq;
import cn.trawe.etc.hunanfront.expose.v2.req.QueryCardStatusReq;
import cn.trawe.etc.hunanfront.expose.v2.resp.OpenAccountResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.QualificationCheckResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.QueryCardStatusResp;

/**
 * 
 * @author jianjun.chai
 *
 */
public interface AccountServiceI {
	
	 
	  // 开户
	  OpenAccountResp openAccount(OpenAccountReq req,ThirdPartner partner);  
	  
	  
	  //资格校验
	  QualificationCheckResp qualificationCheck(QualificationCheckReq	 req,ThirdPartner partner);
	  
	  //卡状态查询
	  QueryCardStatusResp queryCardStatus(QueryCardStatusReq req,ThirdPartner partner);

}
