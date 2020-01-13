package cn.trawe.etc.hunanfront.service.secondissue.apdu;

import lombok.Data;

@Data
public class ApduFlagResponse {
	
	private CardInfoApdu cardInfo;
	
	private ObuInfoApdu obuInfo;

}
