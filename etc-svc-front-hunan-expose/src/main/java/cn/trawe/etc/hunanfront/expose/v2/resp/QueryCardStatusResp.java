package cn.trawe.etc.hunanfront.expose.v2.resp;

import cn.trawe.etc.hunanfront.expose.v2.BaseResp;
import lombok.Data;

@Data
public class QueryCardStatusResp extends BaseResp{
	
	private String vehiclePlate;
	
	private String vehiclePlateColor;
	
	private String state;
	
	

}
