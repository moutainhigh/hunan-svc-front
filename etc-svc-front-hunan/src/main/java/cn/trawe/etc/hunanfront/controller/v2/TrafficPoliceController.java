package cn.trawe.etc.hunanfront.controller.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.api.AlipayApiException;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;

import cn.trawe.etc.hunanfront.common.AuthCheckHandler;
import cn.trawe.etc.hunanfront.config.ParamModel;
import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResp;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.expose.v2.req.OpenAccountReq;
import cn.trawe.etc.hunanfront.expose.v2.resp.OpenAccountResp;
import cn.trawe.etc.hunanfront.feign.entity.hunan.CarServiceReq;
import cn.trawe.etc.hunanfront.feign.entity.hunan.CarServiceResp;
import cn.trawe.etc.hunanfront.feign.v2.GatewayHunanApi;
import cn.trawe.etc.hunanfront.service.BaseService;
import cn.trawe.etc.hunanfront.utils.SignUtil2;
import cn.trawe.etc.hunanfront.utils.ValidUtils;
import cn.trawe.util.LogUtil;
import cn.trawe.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TrafficPoliceController extends BaseService{
	
	
	@Autowired
	GatewayHunanApi  GatewayHunanApi;
	
	@Autowired 
	private AuthCheckHandler AuthCheckHandler;
	
	
	public boolean LOG_FLAG =true;
	
	@PostMapping(value = "/v2/trafficPolice/carQuery")
	public BaseResponse carServiceQuery(@ParamModel BaseRequest req){
		if(LOG_FLAG) {
			LogUtil.info(log, "trafficPolice", "交警信息请求报文详细日志:"+JSON.toJSONString(req));
		}
		
		ThirdPartner partner = AuthCheckHandler.check(req);
		//ThirdPartner partner =new ThirdPartner();
		CarServiceReq  reqBody = JSON.parseObject(req.getBizContent(),CarServiceReq.class);
		//校验
		
		String err = ValidUtils.validateBean(reqBody);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);
        if(reqBody.getViVehicleType()!=2) {
        	 return paramsError("客货类型必须为货车", req.getCharset(),partner);
        }
        LogUtil.info(log, reqBody.getVehPlate(), "交警请求:"+JSON.toJSONString(req));
		CarServiceResp carServiceQuery = GatewayHunanApi.carServiceQuery(reqBody);
		LogUtil.info(log, reqBody.getVehPlate(), "交警响应:"+JSON.toJSONString(carServiceQuery));
		BaseResponse<CarServiceResp> resp = new BaseResponse<>();
        resp.setResponse(carServiceQuery);
        try{
            resp.setSign(SignUtil2.signResponse(carServiceQuery,partner.getTrawePrivateKey(),"UTF-8",true));
        }catch (AlipayApiException e){
            log.error(e.getMessage(),e.fillInStackTrace());
            throw new RuntimeException("响应加密失败原因:"+e.getLocalizedMessage());
        }
        
		if(LOG_FLAG) {
			LogUtil.info(log, "trafficPolice", "交警响应报文详细日志:"+JSON.toJSONString(resp));
		}
		return resp;
		
	}
	

}
