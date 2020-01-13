package cn.trawe.etc.hunanfront.api.v2;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import cn.trawe.etc.hunanfront.anno.ParamModel;
import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.expose.v2.req.OpenAccountReq;
import cn.trawe.etc.hunanfront.expose.v2.req.QualificationCheckReq;
import cn.trawe.etc.hunanfront.expose.v2.resp.OpenAccountResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.QualificationCheckResp;

public interface HunanIssueOrderApiV2Test {
	
	/**
	 * 开户
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/test/v2/etc/open_account",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public OpenAccountResp openAccount(@RequestBody OpenAccountReq req);
	/**
	 * 办理资格校验
	 * @param /v2/etc/precheck
	 * @return
	 */
	@PostMapping(value = "/test/v2/etc/precheck",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public QualificationCheckResp precheck(@RequestBody QualificationCheckReq req);
	
	/**
	 * 车辆信息保存
	 * @param vehicle_info_save
	 * @return
	 */
	@PostMapping(value = "/test/v2/etc/vehicle_info_save",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseResponse vehicle_info_save(@ParamModel BaseRequest req);
	/**
	 * 车辆信息提交
	 * @param vehicle_info_submit
	 * @return
	 */
	@PostMapping(value = "/test/v2/etc/vehicle_info_submit",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseResponse vehicle_info_submit(@ParamModel BaseRequest req);
	
	/**
	 * 发行订单提交
	 * @param vehicle_info_submit
	 * @return
	 */
	@PostMapping(value = "/test/v2/etc/issue_order_submit",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseResponse issue_order_submit(@ParamModel BaseRequest req);
	
	/**
	 * 发行订单查询
	 * @param issue_order_query
	 * @return
	 */
	@PostMapping(value = "/test/v2/etc/issue_order_query",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseResponse issue_order_query(@ParamModel BaseRequest req);
	
	/**
	 * 发行订单取消
	 * @param issue_order_cancel
	 * @return
	 */
	@PostMapping(value = "/test/v2/etc/issue_order_cancel",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseResponse issue_order_cancel(@ParamModel BaseRequest req);
	
	
	/**
	 * 修改手机号
	 * @param issue_order_modify_by_phone
	 * @return
	 */
	@PostMapping(value = "/test/v2/etc/modify_phone",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseResponse issue_order_modify_by_phone(@ParamModel BaseRequest req);
	
	/**
	 * 修改订单库存用户名和密码
	 * @param issue_order_modify_by_account
	 * @return
	 */
	@PostMapping(value = "/test/v2/etc/modify_issue_order_account",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseResponse issue_order_modify_by_account(@ParamModel BaseRequest req);
	
	
	/**
	 * 订单取消
	 * @param vehicleFrontImageUpload
	 * @return
	 */
	@PostMapping(value = "/test/v2/etc/image/vehicle_front",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseResponse vehicleFrontImageUpload(@ParamModel BaseRequest req);
	
	/**
	 * 卡状态查询
	 * @param queryCardStatus
	 * @return
	 */
	@PostMapping(value = "/test/v2/etc/query_card_status",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseResponse queryCardStatus(@ParamModel BaseRequest req);
	
	
	
	
	
	

}
