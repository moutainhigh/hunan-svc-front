package cn.trawe.etc.hunanfront.service.secondissue.analysis;

import org.springframework.stereotype.Service;

import cn.trawe.etc.hunanfront.exception.AnalySisException;
import cn.trawe.etc.hunanfront.request.secondissue.SecondIssueReq;
import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BaseBussinessService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Sw1Sw2AnalysisService  extends BaseBussinessService{
	
	
	
	public boolean analysis(SecondIssueReq req,String apdu) {
		


		if(apdu.equals("9000")) {
			return true;
		}
		
		
//		if(apdu.endsWith("6988")) {
//			//上报特殊状态
//			//上报6988错误
//			cardAction(req, "6988",0);
//		}
//	    		
	   return false;
		
	}
    public static void main(String[] args) {
    	System.out.println("6700".endsWith("9000"));
		
	}
}
