package cn.trawe.etc.hunanfront.service.v2;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseReq;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderCancelReq;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderModifyByAccountReq;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderModifyByPhoneReq;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderQueryReq;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderSubmitReq;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderCancelResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderModifyByAccountResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderModifyByPhoneResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderQueryResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderSubmitResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.SecondIssueQueryResp;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueResp;

public interface IssueServiceI {
	
	IssueOrderSubmitResp  issueOrderSubmit (IssueOrderSubmitReq req,ThirdPartner partner);
	
	
	IssueOrderCancelResp issueOrderCancel (IssueOrderCancelReq req,ThirdPartner partner);
	
	IssueOrderQueryResp  issueOrderQuery(IssueOrderQueryReq req ,ThirdPartner partner);
	
	IssueOrderModifyByPhoneResp issueOrderModifyByPhone(IssueOrderModifyByPhoneReq req,ThirdPartner partner);
	
	
	IssueOrderModifyByAccountResp issueOrderModifyByAccount(IssueOrderModifyByAccountReq req,ThirdPartner partner);
	
	SecondIssueQueryResp secondIssueQuery( BaseReq req,ThirdPartner partner);
	
	SecondIssueResp secondIssueReplace( SecondIssueReq req,ThirdPartner partner);
	
	SecondIssueResp secondIssueReplaceSumbitOrder( SecondIssueReq req,ThirdPartner partner);

}
