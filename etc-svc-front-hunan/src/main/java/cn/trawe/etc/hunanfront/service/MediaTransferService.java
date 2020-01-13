package cn.trawe.etc.hunanfront.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.feign.ApiClient;
import cn.trawe.etc.hunanfront.request.MediaTransferRequest;
import cn.trawe.etc.hunanfront.response.MediaTransferResponse;
import cn.trawe.etc.hunanfront.utils.SignUtil2;
import cn.trawe.etc.hunanfront.utils.ValidUtils;
import cn.trawe.pay.common.etcmsg.EtcResponse;
import cn.trawe.pay.expose.request.UploadFileReq;
import cn.trawe.util.LogUtil;
import cn.trawe.utils.EnumUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kevis
 * @date 2019/5/8
 */
@Slf4j
@Service
public class MediaTransferService extends BaseService {
    @Autowired
    private ApiClient apiClient;
    @Value("${api.token}")
    private String token;
    @Autowired
    private ThirdPartnerService thirdPartnerService;


    public BaseResponse mediaTransfer(BaseRequest req) {

        ThirdPartner partner = thirdPartnerService.getPartner(req);
        if(partner == null){
            return paramsError("验签失败", req.getCharset(),partner);
        }
        boolean flag = thirdPartnerService.check(req, partner);
        if(!flag){
            return paramsError("验签失败", req.getCharset(),partner);
        }
    	
    	/* try {
 			if (!SignUtil2.verifyRequest(req,alipayPublicKey,req.getCharset()))
 			    return paramsError("验签失败", req.getCharset());
 		} catch (AlipayApiException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}*/

        MediaTransferRequest mediaTransferRequest = JSONObject.parseObject(req.getBizContent(), MediaTransferRequest.class);
        log.info(" 图片传输请求 :  " +mediaTransferRequest);
                
        String err = ValidUtils.validateBean(mediaTransferRequest);
        if (StringUtils.isNotEmpty(err)) {
            return paramsError(err,req.getCharset(),partner);
        }
        UploadFileReq uploadFileReq = new UploadFileReq();
        uploadFileReq.setBizType(EnumUtils.toEnum(Integer.valueOf(mediaTransferRequest.getBizType()), UploadFileReq.BizType.class));
        uploadFileReq.setMediaContent(mediaTransferRequest.getMediaContent());
        //BASE 64
        uploadFileReq.setMediaType(EnumUtils.toEnum(Integer.valueOf(mediaTransferRequest.getMediaType()), UploadFileReq.MediaType.class));
        uploadFileReq.setOrderId(mediaTransferRequest.getOrderId());
        uploadFileReq.setAlipayUserId(mediaTransferRequest.getUserId());
        EtcResponse etcResponse = apiClient.uploadFile(uploadFileReq, token);
        LogUtil.info(log, null, "上传文件响应", JSON.toJSONString(etcResponse));
        if (etcResponse == null) {
            return systemError(req.getCharset(),partner);
        }
        if (etcResponse.getCode() != 0) {
            return paramsError(etcResponse.getMsg(), req.getCharset(),partner);
        }
        MediaTransferResponse mediaTransferResponse = new MediaTransferResponse();
        mediaTransferResponse.setSuccess(BaseResponseData.Success.SUCCEED.toString());
        mediaTransferResponse.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString());
        BaseResponse<MediaTransferResponse> resp = new BaseResponse<>();
        resp.setResponse(mediaTransferResponse);
        try{
            resp.setSign(SignUtil2.signResponse(mediaTransferResponse,partner.getTrawePrivateKey(),req.getCharset(),true));
        }catch (AlipayApiException e){
            log.error(e.getMessage(),e.fillInStackTrace());
        }
        return resp;
    }
}
