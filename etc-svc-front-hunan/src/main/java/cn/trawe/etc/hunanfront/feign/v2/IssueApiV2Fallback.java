package cn.trawe.etc.hunanfront.feign.v2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import cn.trawe.etc.hunanfront.service.QueryMonitorServiceImpl;
import cn.trawe.etc.route.expose.request.CardQueryStatusRequest;
import cn.trawe.etc.route.expose.request.issuesecond.RechargeActionRequest;
import cn.trawe.etc.route.expose.request.issuesecond.RechargeInitRequest;
import cn.trawe.etc.route.expose.request.issuesecond.SecondActiveUploadRequest;
import cn.trawe.etc.route.expose.request.issuesecond.SecondIssueOrderRequest;
import cn.trawe.etc.route.expose.response.CardQueryResponse;
import cn.trawe.etc.route.expose.response.issuesecond.RechargeActionResponse;
import cn.trawe.etc.route.expose.response.issuesecond.RechargeInitResponse;
import cn.trawe.etc.route.expose.response.issuesecond.SecondActiveUploadResponse;
import cn.trawe.etc.route.expose.response.issuesecond.SecondIssueOrderResponse;
import cn.trawe.pay.common.etcmsg.EtcObjectResponse;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.entity.IssueEtcCard;
import cn.trawe.pay.expose.request.issue.CarInfoSaveReq;
import cn.trawe.pay.expose.request.issue.CarInfoSubmitReq;
import cn.trawe.pay.expose.request.issue.CheckPlateNoReq;
import cn.trawe.pay.expose.request.issue.CheckVehicleLicenseReq;
import cn.trawe.pay.expose.request.issue.GetOrderReq;
import cn.trawe.pay.expose.request.issue.ImageUploadReq;
import cn.trawe.pay.expose.request.issue.IssueOrderQueryReq;
import cn.trawe.pay.expose.request.issue.IssueThirdOrderCancelReq;
import cn.trawe.pay.expose.request.issue.QueryByCardNoReq;
import cn.trawe.pay.expose.request.issue.QueryByOrderReq;
import cn.trawe.pay.expose.request.issue.SubmitObuOrderReq;
import cn.trawe.pay.expose.request.issue.ThirdOutOrderQueryReq;
import cn.trawe.pay.expose.request.issue.ThirdOutOrderSaveReq;
import cn.trawe.pay.expose.request.issue.UserInfoUploadReq;
import cn.trawe.pay.expose.request.secondissue.ActivationCheckReq;
import cn.trawe.pay.expose.request.secondissue.ActivationQueryReq;
import cn.trawe.pay.expose.request.secondissue.CardActionReq;
import cn.trawe.pay.expose.request.secondissue.CardWriteReq;
import cn.trawe.pay.expose.request.secondissue.CustomerInfoUploadReq;
import cn.trawe.pay.expose.request.secondissue.ImageVehicleReq;
import cn.trawe.pay.expose.request.secondissue.TagActionReq;
import cn.trawe.pay.expose.request.secondissue.TagWriteReq;
import cn.trawe.pay.expose.response.BaseResponse;
import cn.trawe.pay.expose.response.GlobalResponse;
import cn.trawe.pay.expose.response.issue.BizResp;
import cn.trawe.pay.expose.response.issue.CheckPlateNoResp;
import cn.trawe.pay.expose.response.issue.CheckVehicleLicenseResp;
import cn.trawe.pay.expose.response.issue.IssueEtcCardResp;
import cn.trawe.pay.expose.response.issue.IssueOrderQueryResp;
import cn.trawe.pay.expose.response.issue.IssueSaveResp;
import cn.trawe.pay.expose.response.issue.NullResp;
import cn.trawe.pay.expose.response.issue.SubmitObuOrderResp;
import cn.trawe.pay.expose.response.issue.ThirdOutOrderQueryResp;
import cn.trawe.pay.expose.response.issue.ThirdOutOrderSaveResp;
import cn.trawe.pay.expose.response.issue.UserInfoUploadResp;
import cn.trawe.pay.expose.response.secondissue.ActivationCheckResp;
import cn.trawe.pay.expose.response.secondissue.ActivationQueryResp;
import cn.trawe.pay.expose.response.secondissue.CardActionResp;
import cn.trawe.pay.expose.response.secondissue.CardWriteResp;
import cn.trawe.pay.expose.response.secondissue.ImageVehicleResp;
import cn.trawe.pay.expose.response.secondissue.TagActionResp;
import cn.trawe.pay.expose.response.secondissue.TagWriteResp;

@Component
public class IssueApiV2Fallback   implements IssueCenterApi{
	
	 @Autowired
	 QueryMonitorServiceImpl queryMonitorServiceImpl;
	 
	 
	@Value("${spring.application.active}")
    protected  String active;
	
	
	private String sys_err_msg ="网络异常,请稍后重试";
	
	@Override
	public UserInfoUploadResp userInfoUpload(UserInfoUploadReq req) {
		UserInfoUploadResp resp = new UserInfoUploadResp();
		resp.setCode(10);
		resp.setMsg(sys_err_msg);
		
		return resp;
	}

	@Override
	public BaseResponse carInfoSave(CarInfoSaveReq req) {
		BaseResponse resp = new BaseResponse();
		resp.setCode(10);
		resp.setMsg(sys_err_msg);
		
		return resp;
	}

	@Override
	public BizResp carInfoSubmit(CarInfoSubmitReq req) {
		BizResp resp = new BizResp();
		resp.setCode(10);
		resp.setMsg(sys_err_msg);
		
		return resp;
	}

	@Override
	public SubmitObuOrderResp submitObuOrder(SubmitObuOrderReq req) {
		SubmitObuOrderResp resp = new SubmitObuOrderResp();
		resp.setCode(10);
		resp.setMsg(sys_err_msg);
		
		return resp;
	}

	@Override
	public CheckVehicleLicenseResp checkVehicleLicense(CheckVehicleLicenseReq req) {
		CheckVehicleLicenseResp resp = new CheckVehicleLicenseResp();
		resp.setCode(10);
		resp.setMsg(sys_err_msg);
		
		return resp;
	}

	@Override
	public BizResp imageUpload(ImageUploadReq req) {
		BizResp resp = new BizResp();
		resp.setCode(10);
		resp.setMsg(sys_err_msg);
		
		return resp;
	}

	@Override
	public GlobalResponse<NullResp> thirdOrderCancel(IssueThirdOrderCancelReq req) {
		GlobalResponse<NullResp> resp = new GlobalResponse<NullResp>();
		resp.setCode(10);
		resp.setMsg(sys_err_msg);
		
		return resp;
	}

	@Override
	public IssueOrderQueryResp orderQuery(IssueOrderQueryReq req, String alipayUserId) {
		IssueOrderQueryResp resp = new IssueOrderQueryResp();
		resp.setCode(10);
		resp.setMsg(sys_err_msg);
		
		return resp;
	}

	@Override
	public ImageVehicleResp imageVehicle(ImageVehicleReq req) {
		ImageVehicleResp resp = new ImageVehicleResp();
		resp.setCode(10);
		resp.setMsg(sys_err_msg);
		
		return resp;
	}

	@Override
	public CardWriteResp cardWrite(CardWriteReq req) {
		CardWriteResp resp = new CardWriteResp();
		resp.setCode(10);
		resp.setMsg(sys_err_msg);
		
		return resp;
	}

	@Override
	public CardActionResp cardAction(CardActionReq req) {
		CardActionResp resp = new CardActionResp();
		resp.setCode(10);
		resp.setMsg(sys_err_msg);
		
		return resp;
	}

	@Override
	public TagWriteResp tagWrite(TagWriteReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TagActionResp tagAction(TagActionReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActivationQueryResp activationQuery(ActivationQueryReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResponse customerInfoUpload(CustomerInfoUploadReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActivationCheckResp activationCheck(ActivationCheckReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SecondActiveUploadResponse activeAction(SecondActiveUploadRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RechargeInitResponse rechargeInit(RechargeInitRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RechargeActionResponse rechargeAction(RechargeActionRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SecondIssueOrderResponse orderQuery(SecondIssueOrderRequest req) {
		SecondIssueOrderResponse resp = new SecondIssueOrderResponse();
		resp.setCode(10);
		resp.setMsg(sys_err_msg);
		queryMonitorServiceImpl.send("orderQuery", "orderQuery-服务降级");
		return resp;
	}

	@Override
	public IssueEtcCardResp<IssueEtcCard> queryByOrderNo(QueryByOrderReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueEtcCardResp<IssueEtcCard> queryByCardNo(QueryByCardNoReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GlobalResponse<List<IssueEtcCard>> queryCardByJson(JSONObject json) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GlobalResponse<NullResp> saveOrUpdateEtcCard(JSONObject json) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ThirdOutOrderSaveResp saveThirdOutOrder(ThirdOutOrderSaveReq req) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public ThirdOutOrderQueryResp queryThirdOutOrder(ThirdOutOrderQueryReq req) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public IssueSaveResp edit(JSONObject json) {
		IssueSaveResp resp = new IssueSaveResp();
		resp.setCode(10);
		resp.setMsg(sys_err_msg);
		queryMonitorServiceImpl.send("edit", "edit-服务降级");
		return resp;
	}

	@Override
	public EtcObjectResponse<EtcIssueOrder> getOrder(GetOrderReq req) {
		EtcObjectResponse<EtcIssueOrder> resp = new EtcObjectResponse<EtcIssueOrder>();
		resp.setCode(10);
		resp.setMsg(sys_err_msg);
		queryMonitorServiceImpl.send("getOrder", "getOrder-服务降级");
		return resp;
	}

	@Override
	public CardQueryResponse cardQuery(CardQueryStatusRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CheckPlateNoResp checkVehicleLicense(CheckPlateNoReq req) {
		CheckPlateNoResp resp = new CheckPlateNoResp();
		resp.setCode(2);
		resp.setMsg(sys_err_msg);
		
		return resp;
	}

}
