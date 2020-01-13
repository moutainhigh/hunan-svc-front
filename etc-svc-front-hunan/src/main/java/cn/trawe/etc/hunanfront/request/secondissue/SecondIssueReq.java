package cn.trawe.etc.hunanfront.request.secondissue;

import java.util.Map;

import cn.trawe.etc.hunanfront.service.secondissue.request.ApduReq;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class SecondIssueReq {
	
	private String channelNo;
	
	private String orderId;
	
	private String outBizNo;
	
	private String cardNo;
	
	private String obuNo;
	
	private String type;
	
	private String sellerId;
	
	private Integer curStep;
	
	private String apduFlag;
	
	private String originValue;
	
	private String uploadImageFlag;
	
	private Map<String,ApduReq> resultInfo;
	
	private Integer repairType;
	
	private String oldCardNo;
	
	private String oldObuNo;
	
	private String picData;
	
	

}
