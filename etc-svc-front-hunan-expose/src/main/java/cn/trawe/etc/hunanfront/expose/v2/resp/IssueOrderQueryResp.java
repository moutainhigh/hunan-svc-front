package cn.trawe.etc.hunanfront.expose.v2.resp;

import cn.trawe.etc.hunanfront.expose.v2.BaseResp;
import lombok.Data;

@Data
public class IssueOrderQueryResp extends BaseResp {
	
	    private String orderStatus;
	    private String censorInfo;
	    private String cardNo;
	    private String obuNo;
	    private String deviceStatus;
	    private String deliveryName;
	    private String deliveryNo;

	

}
