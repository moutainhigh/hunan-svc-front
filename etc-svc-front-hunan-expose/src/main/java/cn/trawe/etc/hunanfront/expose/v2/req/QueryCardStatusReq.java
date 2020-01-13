package cn.trawe.etc.hunanfront.expose.v2.req;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class QueryCardStatusReq {
	
	private String channelNo;
	
	@NotBlank(message = "卡号不能为空")
	private String cardNo;

}
