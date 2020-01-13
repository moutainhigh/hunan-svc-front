package cn.trawe.etc.hunanfront.controller.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
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
import cn.trawe.etc.hunanfront.expose.v2.resp.VehicleFrontImageResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.VehicleSaveResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.VehicleSubmitResp;
import cn.trawe.etc.hunanfront.service.v2.AccountServiceI;
import cn.trawe.etc.hunanfront.service.v2.IssueServiceI;
import cn.trawe.etc.hunanfront.service.v2.VehicleServiceI;
import cn.trawe.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import lombok.extern.slf4j.Slf4j;
@Api
@Slf4j
@RestController
@RequestMapping("/issue")
public class IssueControllerTest   {
	
	
	@Autowired
	private AccountServiceI  accountService;
	
	@Autowired
	private VehicleServiceI  vehicleService;
	
	@Autowired
	private IssueServiceI  issueService;
	
	
	


	@PostMapping(value = "/test/v2/etc/open_account",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody OpenAccountResp openAccount(@RequestBody  OpenAccountReq req,@RequestHeader String user,@RequestHeader String password) {
		
		LogUtil.info(log, req.getOrderId(), "开户信息请求报文校验前:"+req.toString());
		
		if(!checkAdmin(user, password)) {
			throw new RuntimeException("密码错误");
		}
        
		OpenAccountResp openAccount = accountService.openAccount(req, new ThirdPartner());
		
		return openAccount;
	}

	@PostMapping(value = "/test/v2/etc/precheck",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody QualificationCheckResp precheck(@RequestBody QualificationCheckReq req,@RequestHeader String user,@RequestHeader String password) {
		
		//校验
		
		LogUtil.info(log, req.getOrderId(), "办理资格请求报文校验前:"+JSON.toJSONString(req));
		if(!checkAdmin(user, password)) {
			throw new RuntimeException("XXXXXXXXXXX");
		}   
        QualificationCheckResp respContent = accountService.qualificationCheck(req, new ThirdPartner());
		
		return respContent;
	}

	@PostMapping(value = "/test/v2/etc/vehicle_info_save",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody VehicleSaveResp vehicle_info_save(@RequestBody VehicleSaveReq req,@RequestHeader String user,@RequestHeader String password) {
		//log.info("车辆信息请求报文:"+JSON.toJSONString(req));
		
		
		LogUtil.info(log, req.getOrderId(), "车辆信息请求报文校验前:"+req.toString());
		if(!checkAdmin(user, password)) {
			throw new RuntimeException("XXXXXXXXXXX");
		}
		
		VehicleSaveResp respContent = vehicleService.vehicleSave(req, new ThirdPartner());
		
		return respContent;
	}

	@PostMapping(value = "/test/v2/etc/vehicle_info_submit",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody VehicleSubmitResp vehicle_info_submit(@RequestBody VehicleSubmitReq req,@RequestHeader String user,@RequestHeader String password) {
		
		if(!checkAdmin(user, password)) {
			throw new RuntimeException("XXXXXXXXXXX");
		}
        VehicleSubmitResp respContent = vehicleService.vehicleSubmit(req, new ThirdPartner());
		
		return respContent;
	}

	@PostMapping(value = "/test/v2/etc/issue_order_submit",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody IssueOrderSubmitResp issue_order_submit(@RequestBody IssueOrderSubmitReq req,@RequestHeader String user,@RequestHeader String password) {
		
		if(!checkAdmin(user, password)) {
			throw new RuntimeException("XXXXXXXXXXX");
		}
		IssueOrderSubmitResp respContent = issueService.issueOrderSubmit(req, new ThirdPartner());
		
		return respContent;
	}

	@PostMapping(value = "/test/v2/etc/issue_order_query",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody IssueOrderQueryResp issue_order_query(@RequestBody IssueOrderQueryReq req,@RequestHeader String user,@RequestHeader String password) {
		if(!checkAdmin(user, password)) {
			throw new RuntimeException("XXXXXXXXXXX");
		}
        IssueOrderQueryResp respContent = issueService.issueOrderQuery(req, new ThirdPartner());
		
		return respContent;
	}

	@PostMapping(value = "/test/v2/etc/issue_order_cancel",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody IssueOrderCancelResp issue_order_cancel(@RequestBody IssueOrderCancelReq req,@RequestHeader String user,@RequestHeader String password) {
		if(!checkAdmin(user, password)) {
			throw new RuntimeException("XXXXXXXXXXX");
		}
        IssueOrderCancelResp respContent = issueService.issueOrderCancel(req, new ThirdPartner());
		
		return respContent;
	}

	@PostMapping(value = "/test/v2/etc/modify_phone",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody IssueOrderModifyByPhoneResp issue_order_modify_by_phone(@RequestBody IssueOrderModifyByPhoneReq req,@RequestHeader String user,@RequestHeader String password) {
		if(!checkAdmin(user, password)) {
			throw new RuntimeException("XXXXXXXXXXX");
		}
        IssueOrderModifyByPhoneResp respContent = issueService.issueOrderModifyByPhone(req,  new ThirdPartner());
		
		return respContent;
	}

	@PostMapping(value = "/test/v2/etc/modify_issue_order_account",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody IssueOrderModifyByAccountResp issue_order_modify_by_account(@RequestBody IssueOrderModifyByAccountReq req,@RequestHeader String user,@RequestHeader String password) {
		if(!checkAdmin(user, password)) {
			throw new RuntimeException("XXXXXXXXXXX");
		}
        IssueOrderModifyByAccountResp respContent = issueService.issueOrderModifyByAccount(req, new ThirdPartner());
		
		return respContent;
	}

	@PostMapping(value = "/test/v2/etc/image/vehicle_front",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody VehicleFrontImageResp vehicleFrontImageUpload(@RequestBody VehicleFrontImageReq req,@RequestHeader String user,@RequestHeader String password) {
		if(!checkAdmin(user, password)) {
			throw new RuntimeException("XXXXXXXXXXX");
		}
        VehicleFrontImageResp respContent = vehicleService.vehicleFrontPicUpload(req, new ThirdPartner());
		
		return respContent;
	}

	@PostMapping(value = "/test/v2/etc/query_card_status",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody QueryCardStatusResp queryCardStatus(@RequestBody QueryCardStatusReq req,@RequestHeader String user,@RequestHeader String password) {
		if(!checkAdmin(user, password)) {
			throw new RuntimeException("XXXXXXXXXXX");
		}
        QueryCardStatusResp respContent = accountService.queryCardStatus(req, new ThirdPartner());
		
		return respContent;
	}
	
	
	public boolean checkAdmin(String user,String password) {
		if(user.equals("trawe")&&password.equals("trawe901ccc")) {
			return true;
		}
		else {
			return false;
		}
	}

	

}
