package cn.trawe.etc.hunanfront.service.v2;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.req.PicVehicleFrontReq;
import cn.trawe.etc.hunanfront.expose.v2.resp.PicVehicleFrontResp;

public interface PicServiceI {
	
	
	PicVehicleFrontResp vehicleFrontUpload(PicVehicleFrontReq req,ThirdPartner partner);
	
	

}
