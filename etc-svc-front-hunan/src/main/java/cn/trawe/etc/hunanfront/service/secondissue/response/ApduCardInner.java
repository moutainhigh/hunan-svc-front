package cn.trawe.etc.hunanfront.service.secondissue.response;

import lombok.Data;

@Data
public class ApduCardInner{
	
	private int cmdType =0;
	
	private String cmdValue;

	public ApduCardInner(int cmdType, String cmdValue) {
		super();
		this.cmdType = cmdType;
		this.cmdValue = cmdValue;
	}

	public ApduCardInner(String cmdValue) {
		super();
		this.cmdValue = cmdValue;
	}
	
	
	
	
}