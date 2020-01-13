//package cn.trawe.etc.hunanfront.feign.v2;
//
//import java.util.List;
//
//import org.springframework.stereotype.Component;
//
//import com.alibaba.fastjson.JSONObject;
//
//import cn.trawe.etc.route.expose.request.NoticeActiveInfoAuditRequest;
//import cn.trawe.etc.route.expose.request.NoticeActiveStatusRequest;
//import cn.trawe.etc.route.expose.request.NoticeAuditRequest;
//import cn.trawe.etc.route.expose.request.NoticeCardInfoRequest;
//import cn.trawe.etc.route.expose.request.NoticeCardStatusRequest;
//import cn.trawe.etc.route.expose.response.NoticeActiveInfoAuditResponse;
//import cn.trawe.etc.route.expose.response.NoticeActiveStatusResponse;
//import cn.trawe.etc.route.expose.response.NoticeAuditResponse;
//import cn.trawe.etc.route.expose.response.NoticeCardInfoResponse;
//import cn.trawe.etc.route.expose.response.NoticeCardStatusResponse;
//import cn.trawe.pay.expose.entity.EtcUserInvoice;
//import cn.trawe.pay.expose.request.issue.CarInfoSaveReq;
//import cn.trawe.pay.expose.request.issue.CarInfoSubmitReq;
//import cn.trawe.pay.expose.request.issue.CheckPlateNoReq;
//import cn.trawe.pay.expose.request.issue.CheckVehicleLicenseReq;
//import cn.trawe.pay.expose.request.issue.ImageUploadReq;
//import cn.trawe.pay.expose.request.issue.IssueOrderCancelReq;
//import cn.trawe.pay.expose.request.issue.IssueOrderFinishReq;
//import cn.trawe.pay.expose.request.issue.IssueOrderNoReq;
//import cn.trawe.pay.expose.request.issue.IssueOrderPayNotifyReq;
//import cn.trawe.pay.expose.request.issue.IssueOrderPayReq;
//import cn.trawe.pay.expose.request.issue.IssueOrderQueryReq;
//import cn.trawe.pay.expose.request.issue.IssueOrderSubmitReq;
//import cn.trawe.pay.expose.request.issue.IssueThirdOrderCancelReq;
//import cn.trawe.pay.expose.request.issue.OwnerResouceReq;
//import cn.trawe.pay.expose.request.issue.SmsValidateCodeReq;
//import cn.trawe.pay.expose.request.issue.SubmitObuOrderReq;
//import cn.trawe.pay.expose.request.issue.UserInfoUploadReq;
//import cn.trawe.pay.expose.response.BaseResponse;
//import cn.trawe.pay.expose.response.GlobalResponse;
//import cn.trawe.pay.expose.response.issue.BizResp;
//import cn.trawe.pay.expose.response.issue.CheckPlateNoResp;
//import cn.trawe.pay.expose.response.issue.CheckVehicleLicenseResp;
//import cn.trawe.pay.expose.response.issue.IssueCheckResp;
//import cn.trawe.pay.expose.response.issue.IssueHistoryOrderResp;
//import cn.trawe.pay.expose.response.issue.IssueOrderPayNotifyResp;
//import cn.trawe.pay.expose.response.issue.IssueOrderPayResp;
//import cn.trawe.pay.expose.response.issue.IssueOrderQueryResp;
//import cn.trawe.pay.expose.response.issue.IssueOrderSubmitResp;
//import cn.trawe.pay.expose.response.issue.IssueSaveResp;
//import cn.trawe.pay.expose.response.issue.NullResp;
//import cn.trawe.pay.expose.response.issue.OrderRemoveResp;
//import cn.trawe.pay.expose.response.issue.OwnerResourceResp;
//import cn.trawe.pay.expose.response.issue.PhoneListResp;
//import cn.trawe.pay.expose.response.issue.UserInfoUploadResp;
//import cn.trawe.pay.expose.response.sign.SignCheckResp;
//
//@Component
//public class IssueCenterApiFallback implements IssueCenterApi {
//
//	@Override
//	public IssueCheckResp issueCheck(String alipayUserId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IssueCheckResp issueCheckByOrderNo(IssueOrderNoReq req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IssueOrderQueryResp orderQuery(IssueOrderQueryReq req, String alipayUserId) {
//		IssueOrderQueryResp resp = new IssueOrderQueryResp();
//		resp.setCode(10);
//		resp.setMsg("服务降级");
//		return resp;
//	}
//
//	@Override
//	public IssueHistoryOrderResp queryHistoryOrder(IssueOrderQueryReq req, String alipayUserId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IssueSaveResp save(JSONObject json, String alipayUserId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IssueSaveResp edit(JSONObject json) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IssueOrderPayResp orderPay(IssueOrderPayReq req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IssueOrderPayNotifyResp orderPayNotify(IssueOrderPayNotifyReq req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public GlobalResponse<NullResp> orderCancel(IssueOrderCancelReq req, String alipayUserId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public GlobalResponse<NullResp> thirdOrderCancel(IssueThirdOrderCancelReq req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public GlobalResponse<NullResp> orderFinish(IssueOrderFinishReq req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IssueOrderSubmitResp orderSubmit(IssueOrderSubmitReq req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public GlobalResponse<NullResp> saveInvoice(EtcUserInvoice model, String alipayUserId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public GlobalResponse<NullResp> invoiceSubmit(IssueOrderNoReq req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public GlobalResponse<EtcUserInvoice> queryInvoiceByOrderNo(IssueOrderNoReq req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public GlobalResponse<EtcUserInvoice> queryInvoiceByAlipayUserid(String alipayUserId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public CheckPlateNoResp checkVehicleLicense(CheckPlateNoReq req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public GlobalResponse<NullResp> sendValidateCodeSms(SmsValidateCodeReq req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public GlobalResponse<NullResp> checkValidateCode(SmsValidateCodeReq req, String alipayUserId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public GlobalResponse<List<OwnerResourceResp>> queryOwnerResouce(OwnerResouceReq req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public NoticeActiveStatusResponse noticeActiveStatus(NoticeActiveStatusRequest req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public NoticeAuditResponse noticeAudit(NoticeAuditRequest req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public NoticeCardInfoResponse noticeCardInfo(NoticeCardInfoRequest req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public NoticeCardStatusResponse noticeCardStatus(NoticeCardStatusRequest req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public NoticeActiveInfoAuditResponse noticeRegister(NoticeActiveInfoAuditRequest req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public OrderRemoveResp orderRemove(JSONObject json) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public PhoneListResp getAlipayPhone(String alipayUserId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public SignCheckResp signCheckByOrderNo(IssueOrderNoReq req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public UserInfoUploadResp userInfoUpload(UserInfoUploadReq req) {
//		UserInfoUploadResp resp = new UserInfoUploadResp();
//		resp.setCode(10);
//		resp.setMsg("服务降级");
//		return resp;
//	}
//
//	@Override
//	public BaseResponse carInfoSave(CarInfoSaveReq req) {
//		BaseResponse resp = new BaseResponse();
//		resp.setCode(10);
//		resp.setMsg("服务降级");
//		return resp;
//	}
//
//	@Override
//	public BizResp carInfoSubmit(CarInfoSubmitReq req) {
//		BizResp resp = new BizResp();
//		resp.setCode(10);
//		resp.setMsg("服务降级");
//		return resp;
//	}
//
//	@Override
//	public BizResp submitObuOrder(SubmitObuOrderReq req) {
//		BizResp resp = new BizResp();
//		resp.setCode(10);
//		resp.setMsg("服务降级");
//		return resp;
//	}
//
//	@Override
//	public CheckVehicleLicenseResp checkVehicleLicense(CheckVehicleLicenseReq req) {
//		CheckVehicleLicenseResp resp = new CheckVehicleLicenseResp();
//		resp.setCode(10);
//		resp.setMsg("服务降级");
//		return resp;
//	}
//
//	@Override
//	public BizResp imageUpload(ImageUploadReq req) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}
