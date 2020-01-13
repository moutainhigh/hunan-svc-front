package cn.trawe.etc.hunanfront.controller.v2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import cn.trawe.etc.hunanfront.api.v2.HunanIssueOrderApiV2;
import cn.trawe.etc.hunanfront.common.AuthCheckHandler;
import cn.trawe.etc.hunanfront.config.ParamModel;
import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseReq;
import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderCancelReq;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderModifyByAccountReq;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderModifyByPhoneReq;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderQueryReq;
import cn.trawe.etc.hunanfront.expose.v2.req.IssueOrderSubmitReq;
import cn.trawe.etc.hunanfront.expose.v2.req.OpenAccountReq;
import cn.trawe.etc.hunanfront.expose.v2.req.QualificationCheckReq;
import cn.trawe.etc.hunanfront.expose.v2.req.QueryCardStatusReq;
import cn.trawe.etc.hunanfront.expose.v2.req.VehicleFrontImageReq;
import cn.trawe.etc.hunanfront.expose.v2.req.VehicleSaveReq;
import cn.trawe.etc.hunanfront.expose.v2.req.VehicleSubmitReq;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderCancelResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderModifyByAccountResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderModifyByPhoneResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderQueryResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.IssueOrderSubmitResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.OpenAccountResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.QualificationCheckResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.QueryCardStatusResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.SecondIssueQueryResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.VehicleFrontImageResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.VehicleSaveResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.VehicleSubmitResp;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueResp;
import cn.trawe.etc.hunanfront.service.BaseService;
import cn.trawe.etc.hunanfront.service.v2.AccountServiceI;
import cn.trawe.etc.hunanfront.service.v2.IssueServiceI;
import cn.trawe.etc.hunanfront.service.v2.VehicleServiceI;
import cn.trawe.etc.hunanfront.utils.ValidUtils;
import cn.trawe.util.LogUtil;
import cn.trawe.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class IssueController extends BaseService implements HunanIssueOrderApiV2 {
	
	
	@Autowired
	private AccountServiceI  accountService;
	
	@Autowired
	private VehicleServiceI  vehicleService;
	
	@Autowired
	private IssueServiceI  issueService;
	
	@Autowired 
	private AuthCheckHandler AuthCheckHandler;
	
	@Value("${secondissue.logFlag}")
	public boolean LOG_FLAG;
	
	@Override
	public String log(String req) {
		log.info(req);
		if("1".equals(req)) {
			LOG_FLAG =true;
			return "日志级别修改为true";
		}
		else {
			LOG_FLAG =false;
			return "日志级别修改为false";
		}
		
		
		
	}


	@Override
	public BaseResponse openAccount(@ParamModel BaseRequest req) {
		if(LOG_FLAG) {
			LogUtil.info(log, "openAccount", "开户信息请求报文详细日志:"+JSON.toJSONString(req));
		}
		
		ThirdPartner partner = AuthCheckHandler.check(req);
		
		OpenAccountReq oepnAccount = JSON.parseObject(req.getBizContent(),OpenAccountReq.class);
		//校验
		
		String err = ValidUtils.validateBean(oepnAccount);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);
		OpenAccountResp openAccount = accountService.openAccount(oepnAccount, partner);
		BaseResponse sign = AuthCheckHandler.sign(openAccount,partner);
		if(LOG_FLAG) {
			LogUtil.info(log, "openAccount", "开户信息响应报文详细日志:"+JSON.toJSONString(sign));
		}
		return sign;
	}

	@Override
	public BaseResponse precheck(@ParamModel BaseRequest req) {
		if(LOG_FLAG) {
		LogUtil.info(log, "precheck", "办理资格校验请求报文详细日志:"+JSON.toJSONString(req));
		}
		ThirdPartner partner = AuthCheckHandler.check(req);
		QualificationCheckReq reqContent = JSON.parseObject(req.getBizContent(),QualificationCheckReq.class);
		//校验
		String err = ValidUtils.validateBean(req);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);
      //校验
      	LogUtil.info(log, reqContent.getOrderId(), "车牌校验请求报文:"+JSON.toJSONString(reqContent));
        QualificationCheckResp respContent = accountService.qualificationCheck(reqContent, partner);
		BaseResponse sign = AuthCheckHandler.sign(respContent,partner);
		if(LOG_FLAG) {
			LogUtil.info(log, "precheck", "办理资格校验响应报文详细日志:"+JSON.toJSONString(sign));
			}
		return sign;
	}

	@Override
	public BaseResponse vehicle_info_save(@ParamModel BaseRequest req) {
		if(LOG_FLAG) {
			LogUtil.info(log, "vehicle_info_save", "车辆保存请求详细日志:"+JSON.toJSONString(req));
		}
		
		ThirdPartner partner = AuthCheckHandler.check(req);
		VehicleSaveReq reqContent = JSON.parseObject(req.getBizContent(),VehicleSaveReq.class);
		//校验
		LogUtil.info(log, reqContent.getOrderId(), "车辆信息请求报文校验前:"+reqContent.toString());
		//校验行驶证日期格式
		String err = ValidUtils.validateBean(reqContent);
		Pattern yyyy_MM_dd = Pattern.compile("^\\d{4}\\-\\d{2}\\-\\d{2}$");
		Matcher m1 = yyyy_MM_dd.matcher(reqContent.getViGrantTime());
		if(!m1.matches()) 
		{
				 err += "," + "行驶证发证日期不合法";
			    
		}
		Matcher m3 = yyyy_MM_dd.matcher(reqContent.getViStartTime());
		
		if(!m3.matches()) 
		{
			 err += "," + "行驶证注册日期不合法";
		    
		}
		
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);
        
		VehicleSaveResp respContent = vehicleService.vehicleSave(reqContent, partner);
		BaseResponse sign = AuthCheckHandler.sign(respContent,partner);
		if(LOG_FLAG) {
			LogUtil.info(log, "vehicle_info_save", "车辆保存响应详细日志:"+JSON.toJSONString(sign));
		}
		return sign;
	}

	@Override
	public BaseResponse vehicle_info_submit(@ParamModel BaseRequest req) {
		if(LOG_FLAG) {
			LogUtil.info(log, "vehicle_info_submit", "车辆信息提交请求报文详细日志:"+JSON.toJSONString(req));
		}
		
		ThirdPartner partner = AuthCheckHandler.check(req);
		VehicleSubmitReq reqContent = JSON.parseObject(req.getBizContent(),VehicleSubmitReq.class);
		//校验
		
		String err = ValidUtils.validateBean(reqContent);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);
        VehicleSubmitResp respContent = vehicleService.vehicleSubmit(reqContent, partner);
		BaseResponse sign = AuthCheckHandler.sign(respContent,partner);
		if(LOG_FLAG) {
			LogUtil.info(log, "vehicle_info_submit", "车辆信息提交响应报文详细日志:"+JSON.toJSONString(sign));
		}
		return sign;
	}

	@Override
	public BaseResponse issue_order_submit(@ParamModel BaseRequest req) {
		if(LOG_FLAG) {
			LogUtil.info(log, "issue_order_submit", "订单提交请求报文详细日志:"+JSON.toJSONString(req));
		}
		
		ThirdPartner partner = AuthCheckHandler.check(req);
		IssueOrderSubmitReq reqContent = JSON.parseObject(req.getBizContent(),IssueOrderSubmitReq.class);
		//校验
		LogUtil.info(log, reqContent.getOrderId(), "订单提交请求报文校验前:"+JSON.toJSONString(reqContent));
		String err = ValidUtils.validateBean(reqContent);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);
		IssueOrderSubmitResp respContent = issueService.issueOrderSubmit(reqContent, partner);
		BaseResponse sign = AuthCheckHandler.sign(respContent,partner);
		if(LOG_FLAG) {
			LogUtil.info(log, "issue_order_submit", "订单提交响应报文详细日志:"+JSON.toJSONString(sign));
		}
		return sign;
	}

	@Override
	public BaseResponse issue_order_query(@ParamModel BaseRequest req) {
		if(LOG_FLAG) {
			LogUtil.info(log, "issue_order_query", "订单查询请求报文详细日志:"+JSON.toJSONString(req));
		}
		
		ThirdPartner partner = AuthCheckHandler.check(req);
		IssueOrderQueryReq reqContent = JSON.parseObject(req.getBizContent(),IssueOrderQueryReq.class);
		//校验
		String err = ValidUtils.validateBean(reqContent);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);
        IssueOrderQueryResp respContent = issueService.issueOrderQuery(reqContent, partner);
		BaseResponse sign = AuthCheckHandler.sign(respContent,partner);
		if(LOG_FLAG) {
			LogUtil.info(log, "issue_order_query", "订单查询响应报文详细日志:"+JSON.toJSONString(sign));
		}
		return sign;
	}

	@Override
	public BaseResponse issue_order_cancel(@ParamModel BaseRequest req) {
		if(LOG_FLAG) {
			LogUtil.info(log, "issue_order_cancel", "订单取消请求报文详细日志:"+JSON.toJSONString(req));
		}
		
		ThirdPartner partner = AuthCheckHandler.check(req);
		IssueOrderCancelReq reqContent = JSON.parseObject(req.getBizContent(),IssueOrderCancelReq.class);
		//校验
		String err = ValidUtils.validateBean(reqContent);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);
        IssueOrderCancelResp respContent = issueService.issueOrderCancel(reqContent, partner);
		BaseResponse sign = AuthCheckHandler.sign(respContent,partner);
		if(LOG_FLAG) {
			LogUtil.info(log, "issue_order_cancel", "订单取消返回详细日志:"+JSON.toJSONString(sign));
		}
		return sign;
	}

	@Override
	public BaseResponse issue_order_modify_by_phone(@ParamModel BaseRequest req) {
		if(LOG_FLAG) {
			LogUtil.info(log, "issue_order_modify_by_phone", "修改手机号请求报文详细日志:"+JSON.toJSONString(req));
		}
		ThirdPartner partner = AuthCheckHandler.check(req);
		IssueOrderModifyByPhoneReq reqContent = JSON.parseObject(req.getBizContent(),IssueOrderModifyByPhoneReq.class);
		//校验
		String err = ValidUtils.validateBean(reqContent);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);
        IssueOrderModifyByPhoneResp respContent = issueService.issueOrderModifyByPhone(reqContent, partner);
		BaseResponse sign = AuthCheckHandler.sign(respContent,partner);
		if(LOG_FLAG) {
			LogUtil.info(log, "issue_order_modify_by_phone", "修改手机号响应报文详细日志:"+JSON.toJSONString(sign));
		}
		return sign;
	}

	@Override
	public BaseResponse issue_order_modify_by_account(@ParamModel BaseRequest req) {
		if(LOG_FLAG) {
			LogUtil.info(log, "issue_order_modify_by_account", "修改库存用户密码请求报文详细日志:"+JSON.toJSONString(req));
		}
		ThirdPartner partner = AuthCheckHandler.check(req);
		IssueOrderModifyByAccountReq reqContent = JSON.parseObject(req.getBizContent(),IssueOrderModifyByAccountReq.class);
		//校验
		String err = ValidUtils.validateBean(reqContent);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);
        IssueOrderModifyByAccountResp respContent = issueService.issueOrderModifyByAccount(reqContent, partner);
		BaseResponse sign = AuthCheckHandler.sign(respContent,partner);
		if(LOG_FLAG) {
			LogUtil.info(log, "issue_order_modify_by_account", "修改库存用户密码响应报文详细日志:"+JSON.toJSONString(sign));
		}
		return sign;
	}

	@Override
	public BaseResponse vehicleFrontImageUpload(@ParamModel BaseRequest req) {
		if(LOG_FLAG) {
			LogUtil.info(log, "vehicleFrontImageUpload", "车头照请求报文详细日志:"+JSON.toJSONString(req));
		}
		ThirdPartner partner = AuthCheckHandler.check(req);
		VehicleFrontImageReq reqContent = JSON.parseObject(req.getBizContent(),VehicleFrontImageReq.class);
		//校验
		String err = ValidUtils.validateBean(reqContent);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);
        VehicleFrontImageResp respContent = vehicleService.vehicleFrontPicUpload(reqContent, partner);
		BaseResponse sign = AuthCheckHandler.sign(respContent,partner);
		if(LOG_FLAG) {
			LogUtil.info(log, "vehicleFrontImageUpload", "车头照响应报文详细日志:"+JSON.toJSONString(sign));
		}
		return sign;
	}

	@Override
	public BaseResponse queryCardStatus(@ParamModel BaseRequest req) {
		if(LOG_FLAG) {
			LogUtil.info(log, "queryCardStatus", "卡状态查询请求报文详细日志:"+JSON.toJSONString(req));
		}
		ThirdPartner partner = AuthCheckHandler.check(req);
		QueryCardStatusReq reqContent = JSON.parseObject(req.getBizContent(),QueryCardStatusReq.class);
		//校验
		String err = ValidUtils.validateBean(reqContent);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);
        QueryCardStatusResp respContent = accountService.queryCardStatus(reqContent, partner);
		BaseResponse sign = AuthCheckHandler.sign(respContent,partner);
		if(LOG_FLAG) {
			LogUtil.info(log, "queryCardStatus", "卡状态查询响应报文详细日志:"+JSON.toJSONString(sign));
		}
		return sign;
	}


	@Override
	public BaseResponse second_issue_query(@ParamModel BaseRequest req) {
		if(LOG_FLAG) {
			LogUtil.info(log, "second_issue_query", "激活进度查询请求报文详细日志:"+JSON.toJSONString(req));
		}
		ThirdPartner partner = AuthCheckHandler.check(req);
		BaseReq reqContent = JSON.parseObject(req.getBizContent(),BaseReq.class);
		//校验
		String err = ValidUtils.validateBean(reqContent);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);
        SecondIssueQueryResp secondIssueQuery = issueService.secondIssueQuery(reqContent, partner);
		BaseResponse sign = AuthCheckHandler.sign(secondIssueQuery,partner);
		if(LOG_FLAG) {
			LogUtil.info(log, "second_issue_query", "激活进度查询响应报文详细日志:"+JSON.toJSONString(sign));
		}
		return sign;
	}


	@Override
	public BaseResponse second_issue_replace(@ParamModel BaseRequest req) {
		if(LOG_FLAG) {
			LogUtil.info(log, "second_issue_query", "换卡换签发行请求详细日志:"+JSON.toJSONString(req));
		}
		ThirdPartner partner = AuthCheckHandler.check(req);
		SecondIssueReq reqContent = JSON.parseObject(req.getBizContent(),SecondIssueReq.class);
		//校验
		String err = ValidUtils.validateBean(reqContent);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);
        SecondIssueResp secondIssueQuery = issueService.secondIssueReplace(reqContent, partner);
		BaseResponse sign = AuthCheckHandler.sign(secondIssueQuery,partner);
		if(LOG_FLAG) {
			LogUtil.info(log, "second_issue_query", "换卡换签发行响应报文详细日志:"+JSON.toJSONString(sign));
		}
		return sign;
	}


	@Override
	public BaseResponse second_issue_replace_order_submit(@ParamModel BaseRequest req) {
		if(LOG_FLAG) {
			LogUtil.info(log, "second_issue_replace_order_submit", "换卡换签提交订单请求详细日志:"+JSON.toJSONString(req));
		}
		ThirdPartner partner = AuthCheckHandler.check(req);
//		ThirdPartner partner = new ThirdPartner();
//		partner.setAccountNo("jhapp");
//		partner.setPassword("afdd0b4ad2ec172c586e2150770fbf9e");
		//{"Account":"jhapp","Password":"afdd0b4ad2ec172c586e2150770fbf9e"}
		SecondIssueReq reqContent = JSON.parseObject(req.getBizContent(),SecondIssueReq.class);
		//校验
		String err = ValidUtils.validateBean(reqContent);
        if (ValidateUtil.isNotEmpty(err))
            return paramsError(err, req.getCharset(),partner);
        SecondIssueResp secondIssueQuery = issueService.secondIssueReplaceSumbitOrder(reqContent, partner);
		BaseResponse sign = AuthCheckHandler.sign(secondIssueQuery,partner);
		if(LOG_FLAG) {
			LogUtil.info(log, "second_issue_replace_order_submit", "换卡换签提交订单响应报文详细日志:"+JSON.toJSONString(sign));
		}
		return sign;
	}

	
}
