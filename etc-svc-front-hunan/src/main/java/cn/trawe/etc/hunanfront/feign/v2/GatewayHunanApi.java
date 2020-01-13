package cn.trawe.etc.hunanfront.feign.v2;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.trawe.etc.hunanfront.feign.entity.hunan.CarServiceReq;
import cn.trawe.etc.hunanfront.feign.entity.hunan.CarServiceResp;
import cn.trawe.etc.hunanfront.feign.entity.hunan.HunanGatewayBaseResp;
import cn.trawe.etc.hunanfront.feign.entity.hunan.OrderEquipmentReq;
import cn.trawe.etc.hunanfront.feign.entity.hunan.OrderExamineReq;
import cn.trawe.etc.hunanfront.feign.entity.hunan.OrderQueryReq;
import cn.trawe.etc.hunanfront.feign.entity.hunan.OrderSubmitReq;
import cn.trawe.etc.hunanfront.feign.entity.hunan.UserInfoModifyReq;
import io.swagger.annotations.ApiOperation;

public interface GatewayHunanApi {
	
	/**
	 * 3.6 拓展业务订单提交
	 * @param req
	 * @return
	 */
	
	@PostMapping(value = "/v2/orderSubmit")
	public HunanGatewayBaseResp orderSubmit(@RequestBody OrderSubmitReq req,@RequestParam String note1);

	
	
	/**
	 * 3.22   拓展业务订单审核接口
	 * @param req
	 * @return
	 */
	@PostMapping(value = "/v2/expandOrderExamine")
	public HunanGatewayBaseResp expandOrderExamine(@RequestBody OrderExamineReq req,@RequestParam String note1);

		


	
	/**
	 * 3.21 拓展业务设备登记接口
	 * @param req
	 * @return
	 */
	@PostMapping(value = "/v2/orderEquipmentReq")
	public HunanGatewayBaseResp orderEquipmentReq(@RequestBody OrderEquipmentReq req,@RequestParam String note1);

		
	
	
	/**
	 * 3.21  在线拓展业务订单查询接口
	 * @param req
	 * @return
	 */
	@PostMapping(value = "/v2/orderQuery")
	public HunanGatewayBaseResp orderQuery(@RequestBody OrderQueryReq req,@RequestParam String note1);

	
	
	/**
	 * 3.28个人用户信息变更接口
	 * @param req
	 * @return
	 */
	@PostMapping(value = "/v2/userModify")
	HunanGatewayBaseResp userModify(@RequestBody UserInfoModifyReq req,@RequestParam String note1);

	/**
	 * 湖南交警接口
	 * @param req
	 * @return
	 */
	@ApiOperation(value = "湖南交警接口")
	@PostMapping(value = "/issue/order/carServiceQuery")
	public CarServiceResp carServiceQuery(@RequestBody CarServiceReq req);

}
