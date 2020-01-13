package cn.trawe.etc.hunanfront.expose.v2.req;

import cn.trawe.etc.hunanfront.expose.v2.BaseReq;
import lombok.Data;

@Data
public class IssueOrderModifyByPhoneReq  extends BaseReq{
	
	private String oldPhone;
	
	private String newPhone;

}
