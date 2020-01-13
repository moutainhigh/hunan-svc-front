package cn.trawe.etc.hunanfront.service.secondissue.response;

import lombok.Data;

@Data
public class ApduObuResp {

	private ApduObuInner inner;

	public ApduObuResp(ApduObuInner inner) {
		super();
		this.inner = inner;
	}

	public ApduObuResp() {
		super();
	}
	
	
}

