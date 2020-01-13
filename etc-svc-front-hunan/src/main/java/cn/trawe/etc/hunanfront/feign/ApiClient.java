package cn.trawe.etc.hunanfront.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.alibaba.fastjson.JSONObject;

import cn.trawe.etc.route.expose.request.issuesecond.RechargeActionRequest;
import cn.trawe.etc.route.expose.request.issuesecond.RechargeInitRequest;
import cn.trawe.etc.route.expose.request.issuesecond.SecondActiveUploadRequest;
import cn.trawe.etc.route.expose.request.issuesecond.SecondIssueOrderRequest;
import cn.trawe.etc.route.expose.response.issuesecond.RechargeActionResponse;
import cn.trawe.etc.route.expose.response.issuesecond.RechargeInitResponse;
import cn.trawe.etc.route.expose.response.issuesecond.SecondActiveUploadResponse;
import cn.trawe.etc.route.expose.response.issuesecond.SecondIssueOrderResponse;
import cn.trawe.pay.common.etcmsg.EtcResponse;
import cn.trawe.pay.expose.entity.EtcUserInvoice;
import cn.trawe.pay.expose.entity.IssueEtcCard;
import cn.trawe.pay.expose.request.QueryImageReq;
import cn.trawe.pay.expose.request.SaveOrUpdateDrivingLicenseReq;
import cn.trawe.pay.expose.request.SaveOrUpdateIdCardReq;
import cn.trawe.pay.expose.request.SaveOrUpdateImagesReq;
import cn.trawe.pay.expose.request.TransferImagesReq;
import cn.trawe.pay.expose.request.UploadFileReq;
import cn.trawe.pay.expose.request.UserReq;
import cn.trawe.pay.expose.request.issue.CheckPlateNoReq;
import cn.trawe.pay.expose.request.issue.IssueOrderQueryReq;
import cn.trawe.pay.expose.request.issue.IssueOrderSubmitReq;
import cn.trawe.pay.expose.request.issue.IssueThirdOrderCancelReq;
import cn.trawe.pay.expose.request.issue.QueryByCardNoReq;
import cn.trawe.pay.expose.request.issue.QueryByOrderReq;
import cn.trawe.pay.expose.request.issue.QueryByOwnerReq;
import cn.trawe.pay.expose.request.issue.ThirdOutOrderQueryReq;
import cn.trawe.pay.expose.request.issue.ThirdOutOrderSaveReq;
import cn.trawe.pay.expose.request.secondissue.ActivationCheckReq;
import cn.trawe.pay.expose.request.secondissue.ActivationQueryReq;
import cn.trawe.pay.expose.request.secondissue.CardActionReq;
import cn.trawe.pay.expose.request.secondissue.CardWriteReq;
import cn.trawe.pay.expose.request.secondissue.CustomerInfoUploadReq;
import cn.trawe.pay.expose.request.secondissue.ImageUrlReq;
import cn.trawe.pay.expose.request.secondissue.ImageVehicleReq;
import cn.trawe.pay.expose.request.secondissue.TagActionReq;
import cn.trawe.pay.expose.request.secondissue.TagWriteReq;
import cn.trawe.pay.expose.request.sign.ThirdSignSaveReq;
import cn.trawe.pay.expose.response.BaseResponse;
import cn.trawe.pay.expose.response.GlobalResponse;
import cn.trawe.pay.expose.response.IdRes;
import cn.trawe.pay.expose.response.OwnerRes;
import cn.trawe.pay.expose.response.UserImageRes;
import cn.trawe.pay.expose.response.issue.CheckPlateNoResp;
import cn.trawe.pay.expose.response.issue.IssueEtcCardResp;
import cn.trawe.pay.expose.response.issue.IssueHistoryOrderResp;
import cn.trawe.pay.expose.response.issue.IssueOrderQueryResp;
import cn.trawe.pay.expose.response.issue.IssueOrderSubmitResp;
import cn.trawe.pay.expose.response.issue.IssueSaveResp;
import cn.trawe.pay.expose.response.issue.NullResp;
import cn.trawe.pay.expose.response.issue.ThirdOutOrderQueryResp;
import cn.trawe.pay.expose.response.issue.ThirdOutOrderSaveResp;
import cn.trawe.pay.expose.response.secondissue.ActivationCheckResp;
import cn.trawe.pay.expose.response.secondissue.ActivationQueryResp;
import cn.trawe.pay.expose.response.secondissue.CardActionResp;
import cn.trawe.pay.expose.response.secondissue.CardWriteResp;
import cn.trawe.pay.expose.response.secondissue.ImageUrlResp;
import cn.trawe.pay.expose.response.secondissue.ImageVehicleResp;
import cn.trawe.pay.expose.response.secondissue.TagActionResp;
import cn.trawe.pay.expose.response.secondissue.TagWriteResp;
import feign.hystrix.FallbackFactory;

/**
 * @author Jiang Guangxing
 url ="http://obu.trawe.net:38110"
 **/
@FeignClient(name = "etc-zuul-service-api", fallbackFactory = ApiClient.ApiFallbackFactory.class)
public interface ApiClient  {
    @PostMapping("/issue/check-vehicle-license")
    CheckPlateNoResp checkVehicleLicense(
            @RequestBody CheckPlateNoReq req, @RequestHeader String token);

    @PostMapping("/users/third/uploadFile")
    EtcResponse uploadFile(
            @RequestBody UploadFileReq req, @RequestHeader String token);

    @PostMapping("/users/images/query")
    UserImageRes getUserImage(
            @RequestBody QueryImageReq req, @RequestHeader String token);

    @PostMapping("/issue/order/save")
    IssueSaveResp saveOrder(@RequestBody JSONObject json, @RequestHeader("alipay_user_id") String alipayUserId, @RequestHeader String token);

    @PostMapping("/users/user-info")
    IdRes saveOrUpdateUser(UserReq req, @RequestHeader("alipay_user_id") String alipayUserId, @RequestHeader String token);

    @PostMapping("/users/id-card/saveOrUpdate")
    IdRes saveOrUpdateIdCard(SaveOrUpdateIdCardReq req, @RequestHeader String token);

    @PostMapping("/users/driving-license/saveOrUpdate")
    IdRes saveOrUpdateDrivingLicense(SaveOrUpdateDrivingLicenseReq req, @RequestHeader String token);

    @PostMapping("/users/owner/{ownerCode}")
    OwnerRes owner(@PathVariable("ownerCode") int ownerCode, @RequestHeader String token);

    

    @PostMapping("/issue/order/query-history-order")
    IssueHistoryOrderResp queryHistoryOrder(@RequestBody IssueOrderQueryReq req, @RequestHeader String token);

//    @PostMapping("/issue/card/query")
//    GlobalResponse<List<IssueEtcCard>> cardQuery(@RequestBody JSONObject json, @RequestHeader String token);

    @PostMapping("/issue/order/submit")
    IssueOrderSubmitResp orderSubmit(@RequestBody IssueOrderSubmitReq req, @RequestHeader String token);
    
    @PostMapping("/issue/order/submitImmediately")
    IssueOrderSubmitResp submitImmediately(@RequestBody IssueOrderSubmitReq req, @RequestHeader String token);

    @PostMapping("/issue/invoice")
    GlobalResponse<NullResp> saveInvoice(@RequestBody EtcUserInvoice model, @RequestHeader("alipay_user_id") String alipayUserId, @RequestHeader String token);

    @PostMapping("/issue/third/out-order/save")
    ThirdOutOrderSaveResp saveThirdOutOrder(@RequestBody ThirdOutOrderSaveReq req, @RequestHeader String token);

    
    

    @PostMapping("/issue/withhold/third-sign-save")
    BaseResponse thirdSignSave(@RequestBody ThirdSignSaveReq req, @RequestHeader String token);

    @PostMapping("/issue/card/query-by-alipay-user-id")
    IssueEtcCardResp<IssueEtcCard> queryCardByAlipayUserId(@RequestHeader("alipay_user_id") String alipayUserId, @RequestBody QueryByOwnerReq req, @RequestHeader String token);

    
    
//    @PostMapping(value = "/issue/second-issue/image/url")
//	public ImageUrlResp imageUrl(@RequestBody ImageUrlReq req, @RequestHeader String token);
//    
    @PostMapping("users/images/saveOrUpdate")
    EtcResponse saveOrUpdateImages(SaveOrUpdateImagesReq req, @RequestHeader String token);
    
	  @PostMapping("/users/images/transfer")
	  EtcResponse transferImages(TransferImagesReq req, @RequestHeader String token);
	  
		
		

    @Component
    class ApiFallbackFactory implements FallbackFactory<ApiClient> {
        @Override
        public ApiClient create(Throwable throwable) {
            return new ApiClient() {
                @Override
                public CheckPlateNoResp checkVehicleLicense(CheckPlateNoReq req, String token) {
                    return null;
                }

                @Override
                public EtcResponse uploadFile(UploadFileReq var1, String token) {
                    return null;
                }

                @Override
                public UserImageRes getUserImage(QueryImageReq req, String token) {
                    return null;
                }

                @Override
                public IssueSaveResp saveOrder(JSONObject json, String alipayUserId, String token) {
                    return null;
                }

                @Override
                public IdRes saveOrUpdateUser(UserReq req, String alipayUserId, String token) {
                    return null;
                }

                @Override
                public IdRes saveOrUpdateIdCard(SaveOrUpdateIdCardReq req, String token) {
                    return null;
                }

                @Override
                public IdRes saveOrUpdateDrivingLicense(SaveOrUpdateDrivingLicenseReq req, String token) {
                    return null;
                }

                @Override
                public OwnerRes owner(int ownerCode, String token) {
                    return null;
                }

                

                @Override
                public IssueHistoryOrderResp queryHistoryOrder(IssueOrderQueryReq req, String token) {
                    return null;
                }

               

                @Override
                public IssueOrderSubmitResp orderSubmit(IssueOrderSubmitReq req, String token) {
                    return null;
                }

                @Override
                public GlobalResponse<NullResp> saveInvoice(EtcUserInvoice model, String alipayUserId, String token) {
                    return null;
                }

                @Override
                public ThirdOutOrderSaveResp saveThirdOutOrder(ThirdOutOrderSaveReq req, String tokem) {
                    return null;
                }

             

               

                @Override
                public IssueEtcCardResp<IssueEtcCard> queryCardByAlipayUserId(String alipayUserId, QueryByOwnerReq req, String token) {
                    return null;
                }

                @Override
                public EtcResponse saveOrUpdateImages(SaveOrUpdateImagesReq req, String token) {
                    return null;
                }

                @Override
                public BaseResponse thirdSignSave(ThirdSignSaveReq req, String token) {
                    return null;
                }

				@Override
				public EtcResponse transferImages(TransferImagesReq req, String token) {
					// TODO Auto-generated method stub
					return null;
				}

				
				


				@Override
				public IssueOrderSubmitResp submitImmediately(IssueOrderSubmitReq req, String token) {
					// TODO Auto-generated method stub
					return null;
				}

				
				
            };
        }
    }
}
