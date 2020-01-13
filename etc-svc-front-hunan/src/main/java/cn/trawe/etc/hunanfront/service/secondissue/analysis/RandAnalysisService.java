package cn.trawe.etc.hunanfront.service.secondissue.analysis;

import org.springframework.stereotype.Service;

import cn.trawe.etc.hunanfront.exception.AnalySisException;
import cn.trawe.etc.hunanfront.service.secondissue.apdu.RandomInfoApdu;

@Service
public class RandAnalysisService {
	
	public RandomInfoApdu analysis(String apdu1,String apdu2) {
//		if (res.data.length === 2 && res.data[1].length >= 12) {
//	        let random = res.data[1].substr(res.data[1].length - 12, 8);
//	        res.data = random;
//	        res.msg = '获取随机数成功';
//	        typeof callBack.success == 'function' && callBack.success(res);
//	      } else {
//	        res.code = traweStatusCode.STATUS_FAIL;
//	        res.msg = '获取随机数失败';
//	        typeof callBack.fail == 'function' && callBack.fail(res);
//	      }
		if(apdu1.endsWith("9000")&&apdu2.endsWith("9000")) {
			RandomInfoApdu resp = new RandomInfoApdu();
			if (apdu2.length() >= 12) {
				resp.setRandom(apdu2.substring(0, 8));
				 return resp;
		   }
		}
		
	   throw new AnalySisException("随机数解析失败，解析指令 apdu1:"+apdu1+"apdu2:"+apdu2);
	}

	public static void main(String[] args) {
		System.out.println("01234567890123".substring(0, 8));
	}
}
