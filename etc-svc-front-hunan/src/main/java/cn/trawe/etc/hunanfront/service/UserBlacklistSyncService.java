package cn.trawe.etc.hunanfront.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.feign.EtcCoreWithholdClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kevis
 * @date 2019/5/10
 */
@Slf4j
@Service
public class UserBlacklistSyncService extends BaseService {
    //@Autowired
    private EtcCoreWithholdClient etcCoreWithholdClient;
    @Value("${api.token}")
    private String token;
    
    @Autowired
    private ThirdPartnerService thirdPartnerService;

    public BaseResponse userBlacklistSync(BaseRequest req) {
		return null;
//    	
//    	 ThirdPartner partner = thirdPartnerService.getPartner(req);
//         if(partner == null){
//             return paramsError("验签失败", req.getCharset(),partner);
//         }
//         boolean flag = thirdPartnerService.check(req, partner);
//         if(!flag){
//             return paramsError("验签失败", req.getCharset(),partner);
//         }
//
//        UserBlacklistSyncRequest userBlacklistSyncRequest = JSONObject.parseObject(req.getBizContent(), UserBlacklistSyncRequest.class);
//        String err = ValidUtils.validateBean(userBlacklistSyncRequest);
//        if (StringUtils.isNotEmpty(err)) {
//            return paramsError(err, req.getCharset(),partner);
//        }
//        LogUtil.info(log, null, "黑名单同步接口请求", userBlacklistSyncRequest);
//        UserBlacklistSyncResponse userBlacklistSyncResponse = etcCoreWithholdClient.userBlacklistSync(userBlacklistSyncRequest, token);
//        LogUtil.info(log, null, "黑名单同步接口响应", userBlacklistSyncResponse);
//        if (userBlacklistSyncResponse == null) {
//            return systemError(req.getCharset(),partner);
//        }
//        return new BaseResponse<UserBlacklistSyncResponse>().setResponse(userBlacklistSyncResponse).setSign(signUtil.sign(userBlacklistSyncResponse, req.getCharset()));
    }
}
