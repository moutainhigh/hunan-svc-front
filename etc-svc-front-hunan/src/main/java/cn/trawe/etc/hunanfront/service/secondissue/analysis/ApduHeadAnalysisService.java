package cn.trawe.etc.hunanfront.service.secondissue.analysis;

import java.util.Map;

import org.springframework.stereotype.Service;

import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.ApduFlagResponse;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.CardInfoApdu;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.ObuInfoApdu;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BaseBussinessService;
import cn.trawe.etc.hunanfront.service.secondissue.request.ApduReq;

@Service
public class ApduHeadAnalysisService extends BaseBussinessService{
	
	
	
	public ApduFlagResponse doService(SecondIssueReq req) {
		
		ApduFlagResponse  resp = new ApduFlagResponse();
		Map<String, ApduReq> result_info = req.getResultInfo(); 
		Sw1Sw2AnalysisService.analysis(req,result_info.get("0").getInner().getCmdValue());
		Sw1Sw2AnalysisService.analysis(req,result_info.get("2").getInner().getCmdValue());
		Sw1Sw2AnalysisService.analysis(req,result_info.get("3").getInner().getCmdValue());
		//解析卡号ObuInfoApdu
		ObuInfoApdu obuInfo = ObuAnalysisService.analysis(result_info.get("1").getInner().getCmdValue());
		//解析OBU号
		CardInfoApdu cardInfo = CardAnalysisService.analysis(result_info.get("4").getInner().getCmdValue());
		resp.setCardInfo(cardInfo);
		resp.setObuInfo(obuInfo);
		return resp;
		
		




} 

}
