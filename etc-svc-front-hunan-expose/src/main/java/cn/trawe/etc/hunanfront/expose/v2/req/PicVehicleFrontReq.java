package cn.trawe.etc.hunanfront.expose.v2.req;

import cn.trawe.etc.hunanfront.expose.v2.BaseReq;
import lombok.Data;


@Data
public class PicVehicleFrontReq extends BaseReq {
	
	private String imageType;
	
	private String image;

	@Override
	public String toString() {
		return "PicVehicleFrontReq [imageType=" + imageType + ", getChannelNo()=" + getChannelNo() + ", getOrderId()="
				+ getOrderId() + ", getAccountNo()=" + getAccountNo() + ", getPassword()=" + getPassword() + "]";
	}
	
	

}
