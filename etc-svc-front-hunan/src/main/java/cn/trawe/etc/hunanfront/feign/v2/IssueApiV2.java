package cn.trawe.etc.hunanfront.feign.v2;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.alibaba.fastjson.JSONObject;

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

public interface IssueApiV2 {
	
	    @PostMapping("/issue/etc/user-info-upload")
	    UserInfoUploadResp userInfoUpload(@RequestBody UserInfoUploadReq req);

	    @PostMapping("/issue/etc/car-info-save")
	    BaseResponse carInfoSave(@RequestBody CarInfoSaveReq req);

	    @PostMapping("/issue/etc/car-info-submit")
	    BizResp carInfoSubmit(@RequestBody CarInfoSubmitReq req);

	    @PostMapping("/issue/etc/submit-obu-order")
	    SubmitObuOrderResp submitObuOrder(@RequestBody SubmitObuOrderReq req);

	    @PostMapping("/issue/etc/check-vehicle-license")
	    CheckVehicleLicenseResp checkVehicleLicense(@RequestBody CheckVehicleLicenseReq req);

	    @PostMapping("/issue/etc/image-upload")
	    BizResp imageUpload(@RequestBody ImageUploadReq req);
	    
	    @PostMapping("/issue/order/third-order-cancel")
	    GlobalResponse<NullResp> thirdOrderCancel(@RequestBody IssueThirdOrderCancelReq req);
	    
	    @PostMapping("/issue/order/query")
	    IssueOrderQueryResp orderQuery(@RequestBody IssueOrderQueryReq req, @RequestHeader(required = false, name = "alipay_user_id") String alipayUserId);
	    
	    @PostMapping(value = "/second-issue/image/vehicle")
		public ImageVehicleResp imageVehicle(@RequestBody ImageVehicleReq req);
		
		
		@PostMapping(value = "/second-issue/card/write")
		public CardWriteResp cardWrite(@RequestBody CardWriteReq req);
		
		
		@PostMapping(value = "/second-issue/card/action")
		public CardActionResp cardAction(@RequestBody CardActionReq req);
		
		
		@PostMapping(value = "/second-issue/tag/write")
		public TagWriteResp tagWrite(@RequestBody TagWriteReq req);
		
		
		@PostMapping(value = "/second-issue/tag/action")
		public TagActionResp tagAction(@RequestBody TagActionReq req);
		
		
		@PostMapping(value = "/second-issue/activation/query")
		public ActivationQueryResp activationQuery(@RequestBody ActivationQueryReq req);
		
		
		@PostMapping(value = "/second-issue/customer/info-upload")
		public BaseResponse customerInfoUpload(@RequestBody CustomerInfoUploadReq req);
		
		
		@PostMapping(value = "/second-issue/activation/check")
		public ActivationCheckResp activationCheck(@RequestBody ActivationCheckReq req);

		@PostMapping(value = "/second-issue/activation/action")
		public SecondActiveUploadResponse activeAction(@RequestBody SecondActiveUploadRequest req);

		
		@PostMapping(value = "/second-issue/recharge/init")
		public RechargeInitResponse rechargeInit(@RequestBody RechargeInitRequest req);


		@PostMapping(value = "/second-issue/recharge/action")
		public RechargeActionResponse rechargeAction(@RequestBody RechargeActionRequest req);
		
		
		@PostMapping(value = "/second-issue/order/query")
		public SecondIssueOrderResponse orderQuery(@RequestBody SecondIssueOrderRequest req);
		
		@PostMapping(value = "/issue/card/query-by-order-no")
		public IssueEtcCardResp<IssueEtcCard> queryByOrderNo(@RequestBody QueryByOrderReq req);
	    
	    @PostMapping(value = "/issue/card/query-by-card-no")
	 	public IssueEtcCardResp<IssueEtcCard> queryByCardNo(@RequestBody QueryByCardNoReq req);
	    
	    @PostMapping(value = "/issue/card/query")
		public GlobalResponse<List<IssueEtcCard>> queryCardByJson(@RequestBody JSONObject json);
	    
	    @PostMapping(value = "/issue/card/save-or-update")
	   	public GlobalResponse<NullResp> saveOrUpdateEtcCard(@RequestBody JSONObject json);
	    
	    @PostMapping("/issue/third/out-order/save")
	    ThirdOutOrderSaveResp saveThirdOutOrder(@RequestBody ThirdOutOrderSaveReq req);

//	    @PostMapping("/issue/third/out-order/query")
//	    ThirdOutOrderQueryResp queryThirdOutOrder(@RequestBody ThirdOutOrderQueryReq req);
	    
	    @PostMapping(value = "/issue/order/edit")
	    public IssueSaveResp edit(@RequestBody JSONObject json);
	    
	    @PostMapping("/issue/order/getOrder")
	    EtcObjectResponse<EtcIssueOrder> getOrder(@RequestBody GetOrderReq req);
	    
	    @PostMapping("/issue/etc/card-query")
	    CardQueryResponse cardQuery(@RequestBody CardQueryStatusRequest req);
	    
	    
        //旧版车牌校验
	    @PostMapping("/issue/check-vehicle-license")
	    CheckPlateNoResp checkVehicleLicense(
	            @RequestBody CheckPlateNoReq req);
	    
	    

}
