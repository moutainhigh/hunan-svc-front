package cn.trawe.etc.hunanfront.service.secondissue.apdu;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import cn.trawe.etc.hunanfront.service.secondissue.bussiness.BaseBussinessService;

@Service
public class CreateApduHeader extends BaseBussinessService{
	
	
	
	public Map<String,Object> createHeaderReadObuAndCard(){
		
		Map<String,Object> apduList = new HashMap<String,Object>();
		apduList.put("0", obuRespChoose3F00);
		apduList.put("1", obuRespReadSystem);
		apduList.put("2", cardRespChoose3F00);
		apduList.put("3", cardRespChoose1001);
		apduList.put("4", cardRespReadCard);
		return apduList;
		
	}
	
	

}
