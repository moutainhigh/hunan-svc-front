package cn.trawe.etc.hunanfront.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import cn.trawe.etc.hunanfront.feign.ApiClient;
import cn.trawe.etc.hunanfront.request.PreCheckReq;
import cn.trawe.etc.hunanfront.response.Precheck;
import cn.trawe.etc.hunanfront.utils.SignUtil2;
import cn.trawe.etc.hunanfront.utils.ValidUtils;
import cn.trawe.pay.expose.request.issue.CheckPlateNoReq;
import cn.trawe.pay.expose.response.issue.CheckPlateNoResp;
import cn.trawe.util.LogUtil;
import cn.trawe.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jiang Guangxing
 */
@Slf4j
@Service
public class PrecheckService extends BaseService {
	
	@Autowired
    private ThirdPartnerService thirdPartnerService;

    public BaseResponse precheck(BaseRequest req) {
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

        PreCheckReq preCheckReq = JSON.parseObject(req.getBizContent(), PreCheckReq.class);
        String err = ValidUtils.validateBean(preCheckReq);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);

        //工行特殊处理
//        if(StringUtils.isNotBlank(preCheckReq.getOpType())&&"1".equals(preCheckReq.getOpType())) {
//        	CheckPlateNoReq checkPlateNoReq = new CheckPlateNoReq();
//            checkPlateNoReq.setOwnerCode(4301);
//            checkPlateNoReq.setVehicleLicensePlate(preCheckReq.getViNumber());
//            checkPlateNoReq.setVehicleLicenseColor(preCheckReq.getViPlateColor());
//            //根据新需求处理
//            String accountNo = preCheckReq.getAccount();
//          	String password = preCheckReq.getPassword();
//        	JSONObject object = new JSONObject();
//          	object.put("Account", accountNo);
//          	object.put("Password", password);
//          	checkPlateNoReq.setNote1(object.toJSONString());
//          	checkPlateNoReq.setNote2(preCheckReq.getOpType());
//            LogUtil.info(log, preCheckReq.getViNumber(), "发行服务车牌校验请求", checkPlateNoReq);
//            CheckPlateNoResp resp = apiClient.checkVehicleLicense(checkPlateNoReq, token);
//            LogUtil.info(log, preCheckReq.getViNumber(), "发行服务车牌校验响应", resp);
//            if (resp == null)
//                return systemError(req.getCharset(),partner);
//            if (1 == resp.getCode())
//                return response(req.getCharset(), preCheckReq, "1",partner.getTrawePrivateKey());
//            else if (2 == resp.getCode())
//                return response(req.getCharset(), preCheckReq, "2",partner.getTrawePrivateKey());
//            
//            preCheckReq.setAccountResult("0");
//            return response(req.getCharset(), preCheckReq, "0",partner.getTrawePrivateKey());
//        }
        CheckPlateNoReq checkPlateNoReq = new CheckPlateNoReq();
        checkPlateNoReq.setOwnerCode(4301);
        checkPlateNoReq.setVehicleLicensePlate(preCheckReq.getViNumber());
        checkPlateNoReq.setVehicleLicenseColor(preCheckReq.getViPlateColor());
        //根据新需求处理
      //根据新需求处理
        if(StringUtils.isNotBlank(preCheckReq.getAccount())) {
        	String accountNo = preCheckReq.getAccount();
        	String password = preCheckReq.getPassword();
      	    JSONObject object = new JSONObject();
        	object.put("Account", accountNo);
        	object.put("Password", password);
        	checkPlateNoReq.setNote1(object.toJSONString());	
        }
        else {
        	String accountNo = partner.getAccountNo();
          	String password = partner.getPassword();
        	JSONObject object = new JSONObject();
          	object.put("Account", accountNo);
          	object.put("Password", password);
          	checkPlateNoReq.setNote1(object.toJSONString());
        }
        
        
      	//checkPlateNoReq.setNote2(preCheckReq.getOpType());
        LogUtil.info(log, preCheckReq.getViNumber(), "发行服务车牌校验请求", checkPlateNoReq);
        CheckPlateNoResp resp = IssueCenterApi.checkVehicleLicense(checkPlateNoReq);
        LogUtil.info(log, preCheckReq.getViNumber(), "发行服务车牌校验响应", resp);
        
        if (resp == null)
            return systemError(req.getCharset(),partner);
        if (1 == resp.getCode())
        {
        	//错误信息填充
        	preCheckReq.setMessage(resp.getMsg());
        	return response(req.getCharset(), preCheckReq, "1",partner.getTrawePrivateKey());
        }
        else if (2 == resp.getCode()) {
        	preCheckReq.setMessage("调用车牌校验无响应");
            return response(req.getCharset(), preCheckReq, "2",partner.getTrawePrivateKey());
        }
        	
//        LogUtil.info(log, preCheckReq.getUserId(), "根据uid查询订单请求");
//        IssueOrderQueryResp queryResp = apiClient.orderQuery(new IssueOrderQueryReq(), token, preCheckReq.getUserId());
//        LogUtil.info(log, preCheckReq.getUserId(), "根据uid查询订单响应", queryResp);
//        if (queryResp == null)
//            return systemError(req.getCharset());
//        List<EtcIssueOrder> result = queryResp.getResult();
//        Precheck.checkResult checkResult = Precheck.checkResult.PASS;
//        if (ValidateUtil.isNotEmpty(result))
//            checkResult = Precheck.checkResult.UNPASS;
        //只要不返回特定错误都算验证成功
        //preCheckReq.setAccountResult("0");
        return response(req.getCharset(), preCheckReq, "0",partner.getTrawePrivateKey());
    }

    private BaseResponse response(String charset, PreCheckReq preCheckReq, String checkResult,String privateKey) {
        BaseResponse baseResponse = new BaseResponse<Precheck>();
        try{
            Precheck precheck = new Precheck().setViNumber(preCheckReq.getViNumber()).setViPlateColor(preCheckReq.getViPlateColor());
            precheck.setErrorCode(BaseResponseData.ErrorCode.SUCCEED.toString()).setSuccess(BaseResponseData.Success.SUCCEED.toString());
            precheck.setCheckResult(checkResult);
//            if(StringUtils.isNotBlank(preCheckReq.getAccountResult())) {
//            	 precheck.setAccountResult(preCheckReq.getAccountResult());
//            }
            precheck.setErrorMsg(preCheckReq.getMessage());
            baseResponse.setResponse(precheck);
            baseResponse.setSign(SignUtil2.signResponse(precheck,privateKey,charset,true));
        }catch (AlipayApiException e){
            log.error(e.getMessage(),e.fillInStackTrace());
        }
        return baseResponse;

    }

    @Autowired
    private ApiClient apiClient;
}
