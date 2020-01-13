package cn.trawe.etc.hunanfront.feign.v2;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import cn.trawe.etc.hunanfront.expose.v2.BaseResp;
import cn.trawe.etc.hunanfront.feign.entity.hunan.OrderEquipmentReq;
import cn.trawe.etc.hunanfront.feign.entity.hunan.OrderExamineReq;
import cn.trawe.etc.hunanfront.feign.entity.hunan.OrderQueryReq;
import cn.trawe.etc.hunanfront.feign.entity.hunan.OrderSubmitReq;
import cn.trawe.etc.hunanfront.feign.entity.hunan.UserInfoModifyReq;

@FeignClient(name = "etc-gw-hunan" )
public interface GatewayHunanApiImpl extends  GatewayHunanApi{
	

}
