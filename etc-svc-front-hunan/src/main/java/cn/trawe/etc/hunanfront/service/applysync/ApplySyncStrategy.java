package cn.trawe.etc.hunanfront.service.applysync;

import com.alipay.api.AlipayApiException;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.request.ApplyOrderSyncReq;
import cn.trawe.etc.hunanfront.response.ApplyOrderSync;
import cn.trawe.etc.hunanfront.service.BaseService;
import cn.trawe.etc.hunanfront.utils.SignUtil2;
import lombok.extern.slf4j.Slf4j;

/**
 * 订单同步抽象策略
 *
 * @author Jiang Guangxing
 */
@Slf4j
public abstract class ApplySyncStrategy extends BaseService {
    public abstract BaseResponse sync(String charset, ApplyOrderSyncReq req,ThirdPartner ThirdPartner);

    BaseResponse succeedResponse(String charset, ApplyOrderSyncReq applyOrderSyncReq,ThirdPartner ThirdPartner) {
        ApplyOrderSync applyOrderSync = new ApplyOrderSync().setOrderId(applyOrderSyncReq.getOrderId()).setOutBizNo(applyOrderSyncReq.getOutBizNo());
        applyOrderSync.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString()).setSuccess(BaseResponseData.Success.SUCCEED.toString());
        try {
			return new BaseResponse<ApplyOrderSync>().setResponse(applyOrderSync).setSign(SignUtil2.signResponse(applyOrderSync,ThirdPartner.getTrawePrivateKey(),charset,true));
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e.fillInStackTrace());
		}
		return null;
    }

    BaseResponse succeedResponseCheck(String charset, ApplyOrderSyncReq applyOrderSyncReq,ThirdPartner ThirdPartner) {
        ApplyOrderSync applyOrderSync = new ApplyOrderSync().setOrderId(applyOrderSyncReq.getOrderId()).setOutBizNo(applyOrderSyncReq.getOutBizNo());
        applyOrderSync.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString()).setSuccess(BaseResponseData.Success.SUCCEED.toString());

        BaseResponse<ApplyOrderSync> resp = new BaseResponse<ApplyOrderSync>();
        resp.setResponse(applyOrderSync);
        try{
            resp.setSign(SignUtil2.signResponse(applyOrderSync,ThirdPartner.getTrawePrivateKey(),charset,true));
        }catch (AlipayApiException e){
            log.error(e.getMessage(),e.fillInStackTrace());
        }
        return resp;
    }
    

    BaseResponse succeedResponseError(String charset, ApplyOrderSyncReq applyOrderSyncReq,ThirdPartner ThirdPartner,String message) {
        ApplyOrderSync applyOrderSync = new ApplyOrderSync().setOrderId(applyOrderSyncReq.getOrderId()).setOutBizNo(applyOrderSyncReq.getOutBizNo());
        applyOrderSync.setErrorCode(BaseResponseData.ErrorCode.OTHER_ERROR.toString()).setSuccess(BaseResponseData.Success.FAILED.toString());

        BaseResponse<ApplyOrderSync> resp = new BaseResponse<ApplyOrderSync>();
        applyOrderSync.setMessage(message);
        resp.setResponse(applyOrderSync);
        try{
            resp.setSign(SignUtil2.signResponse(applyOrderSync,ThirdPartner.getTrawePrivateKey(),charset,true));
        }catch (AlipayApiException e){
            log.error(e.getMessage(),e.fillInStackTrace());
        }
        return resp;
    }
    
    
}
