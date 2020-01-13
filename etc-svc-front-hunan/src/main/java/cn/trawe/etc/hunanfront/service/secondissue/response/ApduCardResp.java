package cn.trawe.etc.hunanfront.service.secondissue.response;

import lombok.Data;

@Data
public class ApduCardResp {
	
	 private ApduCardInner inner;

	public ApduCardResp(ApduCardInner inner) {
		super();
		this.inner = inner;
	}

	public ApduCardResp() {
		super();
	}

	
}




