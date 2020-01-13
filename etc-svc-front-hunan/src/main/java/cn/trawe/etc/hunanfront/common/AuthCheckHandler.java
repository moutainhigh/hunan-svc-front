package cn.trawe.etc.hunanfront.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayApiException;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseReq;
import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResp;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.response.MediaTransferResponse;
import cn.trawe.etc.hunanfront.service.ThirdPartnerService;
import cn.trawe.etc.hunanfront.utils.SignUtil2;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class AuthCheckHandler {
	
	@Autowired
    private ThirdPartnerService thirdPartnerService;
	
	public ThirdPartner check(BaseRequest req){
		
		
		ThirdPartner partner = thirdPartnerService.getPartner(req);
        if(partner == null){
        	 throw new RuntimeException("验证签名失败:渠道信息不存在");
            //return paramsError("验签失败", req.getCharset(),partner);
        }
        boolean flag = thirdPartnerService.check(req, partner);
        if(!flag){
        	throw new RuntimeException("验证签名失败");
            //return paramsError("验签失败", req.getCharset(),partner);
        }
        return partner;
		
	}
	
	public <T> BaseResponse sign(BaseResp req,ThirdPartner partner) {
		 BaseResponse<BaseResp> resp = new BaseResponse<>();
	     resp.setResponse(req);
	        try{
	            resp.setSign(SignUtil2.signResponse(req,partner.getTrawePrivateKey(),"UTF-8",true));
	        }catch (AlipayApiException e){
	            log.error(e.getMessage(),e.fillInStackTrace());
	            throw new RuntimeException("响应加密失败原因:"+e.getLocalizedMessage());
	        }
	        return resp;
		
	}

}
