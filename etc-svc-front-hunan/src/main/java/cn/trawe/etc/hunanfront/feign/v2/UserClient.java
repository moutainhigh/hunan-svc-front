package cn.trawe.etc.hunanfront.feign.v2;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

import cn.trawe.pay.api.UserServiceApi;
import cn.trawe.pay.common.etcmsg.EtcResponse;
import cn.trawe.pay.expose.entity.EtcIssueOrder;
import cn.trawe.pay.expose.request.ActiveInfoAuditReq;
import cn.trawe.pay.expose.request.DrivingLicenseImageReq;
import cn.trawe.pay.expose.request.DrivingLicenseReq;
import cn.trawe.pay.expose.request.IdCardImageReq;
import cn.trawe.pay.expose.request.IdCardReq;
import cn.trawe.pay.expose.request.QueryAddressReq;
import cn.trawe.pay.expose.request.QueryImageReq;
import cn.trawe.pay.expose.request.QueryUserReq;
import cn.trawe.pay.expose.request.SaveOrUpdateDrivingLicenseReq;
import cn.trawe.pay.expose.request.SaveOrUpdateIdCardReq;
import cn.trawe.pay.expose.request.SaveOrUpdateImagesReq;
import cn.trawe.pay.expose.request.TransferImagesReq;
import cn.trawe.pay.expose.request.UpdateUserInfoCertNoReq;
import cn.trawe.pay.expose.request.UploadFileReq;
import cn.trawe.pay.expose.request.UserReq;
import cn.trawe.pay.expose.response.DrivingLicenseImageRes;
import cn.trawe.pay.expose.response.DrivingLicenseRes;
import cn.trawe.pay.expose.response.IdCardImageRes;
import cn.trawe.pay.expose.response.IdCardRes;
import cn.trawe.pay.expose.response.IdRes;
import cn.trawe.pay.expose.response.OwnerRes;
import cn.trawe.pay.expose.response.QueryUserRes;
import cn.trawe.pay.expose.response.UserAddressRes;
import cn.trawe.pay.expose.response.UserImageRes;
import cn.trawe.pay.expose.response.UserInfoRes;
import feign.hystrix.FallbackFactory;

/**
 * @author Jiang Guangxing
 */
@FeignClient(name = "etc-core-etc-user", path = "/users")
public interface UserClient extends UserServiceApi {
    
}
