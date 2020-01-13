package cn.trawe.etc.hunanfront.feign;

import cn.trawe.pay.common.etcmsg.EtcResponse;
import cn.trawe.pay.expose.entity.EtcUserInvoice;
import cn.trawe.pay.expose.entity.IssueEtcCard;
import cn.trawe.pay.expose.request.*;
import cn.trawe.pay.expose.request.issue.*;
import cn.trawe.pay.expose.request.sign.ThirdSignSaveReq;
import cn.trawe.pay.expose.response.*;
import cn.trawe.pay.expose.response.issue.*;
import com.alibaba.fastjson.JSONObject;
import feign.hystrix.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * @author Jiang Guangxing
 */
@FeignClient(name = "etc-zuul-service-api", fallbackFactory = ApiClient_20190614.ApiFallbackFactory.class)
public interface ApiClient_20190614 {
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

    @PostMapping("/issue/order/query")
    IssueOrderQueryResp orderQuery(@RequestBody IssueOrderQueryReq req, @RequestHeader String token, @RequestHeader(required = false, name = "alipay_user_id") String alipayUserId);

    @PostMapping("/issue/order/query-history-order")
    IssueHistoryOrderResp queryHistoryOrder(@RequestBody IssueOrderQueryReq req, @RequestHeader String token);

    @PostMapping("/issue/card/query")
    GlobalResponse<List<IssueEtcCard>> cardQuery(@RequestBody JSONObject json, @RequestHeader String token);

    @PostMapping("/issue/order/submit")
    IssueOrderSubmitResp orderSubmit(@RequestBody IssueOrderSubmitReq req, @RequestHeader String token);

    @PostMapping("/issue/invoice")
    GlobalResponse<NullResp> saveInvoice(@RequestBody EtcUserInvoice model, @RequestHeader("alipay_user_id") String alipayUserId, @RequestHeader String token);

    @PostMapping("/issue/third/out-order/save")
    ThirdOutOrderSaveResp saveThirdOutOrder(@RequestBody ThirdOutOrderSaveReq req, @RequestHeader String token);

    @PostMapping("/issue/third/out-order/query")
    ThirdOutOrderQueryResp queryThirdOutOrder(@RequestBody ThirdOutOrderQueryReq req, @RequestHeader String token);

    @PostMapping("/issue/order/third-order-cancel")
    GlobalResponse<NullResp> thirdOrderCancel(@RequestBody IssueThirdOrderCancelReq req, @RequestHeader String token);

    @PostMapping("/issue/withhold/third-sign-save")
    BaseResponse thirdSignSave(@RequestBody ThirdSignSaveReq req, @RequestHeader String token);

    @PostMapping("/issue/card/query-by-alipay-user-id")
    IssueEtcCardResp<IssueEtcCard> queryCardByAlipayUserId(@RequestHeader("alipay_user_id") String alipayUserId, @RequestBody QueryByOwnerReq req, @RequestHeader String token);

    @PostMapping("users/images/saveOrUpdate")
    EtcResponse saveOrUpdateImages(SaveOrUpdateImagesReq req, @RequestHeader String token);

    @Component
    class ApiFallbackFactory implements FallbackFactory<ApiClient_20190614> {
        @Override
        public ApiClient_20190614 create(Throwable throwable) {
            return new ApiClient_20190614() {
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
                public IssueOrderQueryResp orderQuery(IssueOrderQueryReq req, String token, String alipayUserId) {
                    return null;
                }

                @Override
                public IssueHistoryOrderResp queryHistoryOrder(IssueOrderQueryReq req, String token) {
                    return null;
                }

                @Override
                public GlobalResponse<List<IssueEtcCard>> cardQuery(JSONObject json, String token) {
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
                public ThirdOutOrderQueryResp queryThirdOutOrder(ThirdOutOrderQueryReq req, String token) {
                    return null;
                }

                @Override
                public GlobalResponse<NullResp> thirdOrderCancel(IssueThirdOrderCancelReq req, String token) {
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
            };
        }
    }
}
