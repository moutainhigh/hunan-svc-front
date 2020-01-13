package cn.trawe.etc.hunanfront.expose.v2.req;

import cn.trawe.etc.hunanfront.expose.v2.BaseReq;
import lombok.Data;

@Data
public class IssueOrderModifyByAccountReq extends BaseReq{
	
	private String accountNo;
	
	private String password;

}
