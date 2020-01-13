package cn.trawe.etc.hunanfront.service.secondissue.request;

import lombok.Data;

@Data
public class ApduInner {

	private int cmdType;
	
	private String cmdValue;
}
