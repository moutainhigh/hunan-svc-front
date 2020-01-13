package cn.trawe.etc.hunanfront.expose.v2.resp;

import cn.trawe.etc.hunanfront.expose.v2.BaseResp;
import lombok.Data;

@Data
public class SecondIssueQueryResp extends BaseResp {
	
	private String cardNo;
	
	private String obuNo;
	
	private String cardStatus;
	
	private String obuStatus;

}
