package cn.trawe.etc.hunanfront.service.v2;

import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.req.VehicleFrontImageReq;
import cn.trawe.etc.hunanfront.expose.v2.req.VehicleSaveReq;
import cn.trawe.etc.hunanfront.expose.v2.req.VehicleSubmitReq;
import cn.trawe.etc.hunanfront.expose.v2.resp.VehicleFrontImageResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.VehicleSaveResp;
import cn.trawe.etc.hunanfront.expose.v2.resp.VehicleSubmitResp;

public interface VehicleServiceI {
	
	VehicleSaveResp vehicleSave (VehicleSaveReq req,ThirdPartner partner);
	
	
	VehicleSubmitResp vehicleSubmit (VehicleSubmitReq req,ThirdPartner partner);
	
	VehicleFrontImageResp vehicleFrontPicUpload(VehicleFrontImageReq req,ThirdPartner partner);

}
