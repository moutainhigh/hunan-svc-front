package cn.trawe.etc.hunanfront.service.secondissue.bussiness;

import java.util.Map;

import org.springframework.stereotype.Service;

import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.ApduFlagResponse;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.CardInfoApdu;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.ObuInfoApdu;
import cn.trawe.etc.hunanfront.service.secondissue.request.ApduReq;

@Service
public class BaseBussinessApduFlagImpl extends BaseBussinessService {
	
	public ApduFlagResponse doService(SecondIssueReq req) {
		
				ApduFlagResponse  resp = new ApduFlagResponse();
				Map<String, ApduReq> result_info = req.getResultInfo(); 
				//解析卡号ObuInfoApdu
				ObuInfoApdu obuInfo = ObuAnalysisService.analysis(result_info.get("0").getInner().getCmdValue());
				//解析OBU号
				CardInfoApdu cardInfo = CardAnalysisService.analysis(result_info.get("1").getInner().getCmdValue());
				
				resp.setCardInfo(cardInfo);
				resp.setObuInfo(obuInfo);
				return resp;
				
				
		
		
		
		
	}

}
