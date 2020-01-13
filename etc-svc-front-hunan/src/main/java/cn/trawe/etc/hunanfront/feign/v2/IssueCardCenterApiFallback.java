package cn.trawe.etc.hunanfront.feign.v2;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.trawe.etc.route.expose.request.NoticeCardChangeRequest;
import cn.trawe.etc.route.expose.response.NoticeCardChangeResponse;
import cn.trawe.pay.expose.entity.IssueEtcCard;
import cn.trawe.pay.expose.request.issue.QueryByCardNoReq;
import cn.trawe.pay.expose.request.issue.QueryByOrderReq;
import cn.trawe.pay.expose.request.issue.QueryByOwnerReq;
import cn.trawe.pay.expose.response.GlobalResponse;
import cn.trawe.pay.expose.response.issue.IssueEtcCardResp;
import cn.trawe.pay.expose.response.issue.NullResp;

public class IssueCardCenterApiFallback implements IssueCardCenterApi {

	@Override
	public IssueEtcCardResp<IssueEtcCard> queryByCardNo(QueryByCardNoReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueEtcCardResp<IssueEtcCard> queryByOrderNo(QueryByOrderReq req) {
		IssueEtcCardResp<IssueEtcCard> resp = new IssueEtcCardResp<IssueEtcCard>();
		resp.setCode(10);
		resp.setMsg("服务降级");
		return resp;
	}

	@Override
	public IssueEtcCardResp<IssueEtcCard> queryByAlipayUserId(String alipayUserId, QueryByOwnerReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GlobalResponse<List<IssueEtcCard>> query(JSONObject json) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GlobalResponse<NullResp> saveOrUpdate(JSONObject json) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GlobalResponse<NullResp> edit(JSONObject json) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NoticeCardChangeResponse cardChange(NoticeCardChangeRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GlobalResponse<Integer> postfeepublish(String address, String ownerCode) {
		// TODO Auto-generated method stub
		return null;
	}

}
