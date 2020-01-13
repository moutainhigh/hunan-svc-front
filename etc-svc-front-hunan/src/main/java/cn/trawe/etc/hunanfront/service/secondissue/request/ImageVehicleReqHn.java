

package cn.trawe.etc.hunanfront.service.secondissue.request;

import lombok.Data;

@Data
public class ImageVehicleReqHn {
	
	private String channelNo;
	
	private String orderId;
	
	private String outBizNo;
	
	private String imageType;
	
	private String kind;
	
	private String type;
	
	private String image;

	@Override
	public String toString() {
		return "ImageVehicleReqHn [channelNo=" + channelNo + ", orderId=" + orderId + ", outBizNo=" + outBizNo
				+ ", imageType=" + imageType + ", kind=" + kind + ", type=" + type + "]";
	}
	
	

}
