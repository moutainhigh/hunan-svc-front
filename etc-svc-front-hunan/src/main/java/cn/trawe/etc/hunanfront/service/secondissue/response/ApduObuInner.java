package cn.trawe.etc.hunanfront.service.secondissue.response;

import lombok.Data;

@Data
public class ApduObuInner{
	
	private int cmdType =1;
	
	private String cmdValue;

	public ApduObuInner(String cmdValue) {
		super();
		this.cmdValue = cmdValue;
	}
	
	
	
}