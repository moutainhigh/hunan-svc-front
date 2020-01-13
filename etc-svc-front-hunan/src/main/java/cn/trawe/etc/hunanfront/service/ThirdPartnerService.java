package cn.trawe.etc.hunanfront.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;

import cn.trawe.etc.hunanfront.dao.ThirdPartnerDao;
import cn.trawe.etc.hunanfront.entity.ThirdPartner;
import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.utils.SignUtil2;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jiang Guangxing
 */
@Slf4j
@Service
public class ThirdPartnerService {


  

	@Autowired
    private ThirdPartnerDao thirdPartnerDao;
    
    public ThirdPartner getPartner(BaseRequest req){

        boolean flag = false;
        if(req.getBizContent()==null) {
        	throw new RuntimeException("bizContent为空");
        }
        String bizContent = req.getBizContent();
        Map map = (Map) JSONObject.parse(bizContent);
        String channelNo = (String)map.get("channel_no");
        if(StringUtils.isBlank(channelNo)) {
        	throw new RuntimeException("渠道号为空");
        }
        
        
        ArrayList<Object> list = new ArrayList<>();
        list.add(channelNo);
        ThirdPartner one = thirdPartnerDao.findOne(" app_id = ? and status = 0 ", list);

        return one;


    }
    
    public ThirdPartner getPartnerByChannelNo(String channelNo){

       

        ArrayList<Object> list = new ArrayList<>();
        list.add(channelNo);
        ThirdPartner one = thirdPartnerDao.findOne(" app_id = ? and status = 0 ", list);

        return one;


    }

    public boolean check(BaseRequest req,ThirdPartner one){

        boolean flag = false;
        String publicKey = one.getPublicKey();
        try{
            flag = SignUtil2.verifyRequest(req, publicKey, req.getCharset());
        }catch (AlipayApiException e){
            log.error(e.getMessage(),e.fillInStackTrace());
        }
        return flag;
    	
    	
    	//TODO
//        String privateKey ="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCxi/WQQoDhzqTb4D61Lx6TsLqmPO2N9Y9gXB2JQe7OFXBOa45akgdD0BUqabWaO0x8A5Xc1+drK3w/ZdMUV7nBX+xLLl6mZoQ9xkK5SpzTLHaT0VQbG0yMo4VonbzZLfjjMcoMcJB25+7OqNInv9ClYnzPPu4TvClPfwZaPSQT3Vvb4kmXfSsE5cMOW+RyN2aPvERdVczcFYuwqwaQy6nvWbtoI+O8MVtHbSRrgFeACevWUwaqIE+yRUvc3pWjCc6vz1a5z3I4BA29RMLCye9Vam8VfHgHLmG6LRmMmliSYXwC8iuMZWL7YFGeicLXkBLxPSOf0yEDYTiX1WXGxSJrAgMBAAECggEAeDucjxXKK6CB2fiQ3qfLIB71NzBZeuIJNysKQXIp4tt301NY8pKjYtPeEXvGpGx1ziGrcgOvzs6C9HfjKoqB26c24G7FYBBai27grpQNgaBBqLaAK18gRrlR1dfEFJriShDkj+oGms9T1BVVQ9/MzsLUXJB0L18j5pbR2XhAH0aMomP71GkqRs4Z4mKiJxTk1RECFOCI/ybhuw1TX6qIdEiaMvZoZRaSvHNjDvGVJfJNB4CChd//W+NqQ+/EkkcFZXVLicSSpasoZdCMah9oJGv5jZ/eYn3DbjZJozwEkcvCd8kNuV9uNmleU7Ez7rwh1bNo+GPsoUS7xsOrdr5gSQKBgQDumD15n2IsuHs8jjFRL7j/QtIKef6cTFiLyCxpH0dse/G9Yrl6eHCjUgWfZo9bsGLO5Pa7t15iC7t84V7EcJyOBaD+Ok/m6tg/2eW/Ws2NCCEmlURcy74crIZvD+wRfJtwjZtKFPqs5mnXHY6Jx1R27/+Bb+ZI1qUtDrv5WTcHfQKBgQC+f6WtJODlwrbzVTgElA5rZWhk09OE2UW9+M0Sc7zlGwfhES9k8nkJql7XW+NUZwOxf8H/hUd6mRaNdAg4sLxzzCJ4C01hlY1SVqlBQsgjuqVCAhUveIjq+JBiVzlOBnrpeAHO0LhBipjJX19fhr8iJWaA3YJeVMr3m/P9/iMGBwKBgEsnMv3zQTY66b56P3iGIqxsvMJGCPfsaFunMq3uB05ZgByaUzgLZddbx5+c630DAyyOHFWmfZk+ftDoIDSaEAYAKeTKM07WHEWIegCbNnG85MxFaiIcnwZjJXkoN8COUlvSjt532znI3f+/NJ2bZrSMBhwcRhodWivmHzl8a4P1AoGATeFPsWaXTzd4piib2ztQXdW6hSYYnoJZQd46sKWCf0O6x+fHsYRzuWDenzrgyHvJBcMOY9FwNI3rq27ZgZu3n7vAB8XPH+sNOLFXRufvfLPspfzF/n9PsohmNt4j/mMl0tcOHig0fA8WgZUtH5euAl659ksY5knSJMKsUJWaafcCgYEA4LT10dQHuwrA76hfAn/dGqasraVGCYULqw2+lJr60lenh86nWVillyeFGFZzeDn+V0ZL+/ub4igghT602ZJkum/78gZynFyC4aB0CPjzzATBU1zkJQu3ghcG8tKT/AMXiFpwmxTGLbFmm+Js1H13qlPpVN1ldvIn75bMIMGnx28=";
//		String publicKey ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsYv1kEKA4c6k2+A+tS8ek7C6pjztjfWPYFwdiUHuzhVwTmuOWpIHQ9AVKmm1mjtMfAOV3Nfnayt8P2XTFFe5wV/sSy5epmaEPcZCuUqc0yx2k9FUGxtMjKOFaJ282S344zHKDHCQdufuzqjSJ7/QpWJ8zz7uE7wpT38GWj0kE91b2+JJl30rBOXDDlvkcjdmj7xEXVXM3BWLsKsGkMup71m7aCPjvDFbR20ka4BXgAnr1lMGqiBPskVL3N6VownOr89Wuc9yOAQNvUTCwsnvVWpvFXx4By5hui0ZjJpYkmF8AvIrjGVi+2BRnonC15AS8T0jn9MhA2E4l9VlxsUiawIDAQAB";
//	
//		Map<String,String> reqmap = new HashMap<String,String>();
//		reqmap.put("app_id", "13");
//		String signRequest;
//		try {
//			signRequest = SignUtil2.signRequestForMap(reqmap,privateKey,"UTF-8");
//			reqmap.put("sign", signRequest);
//			System.out.println(signRequest);
//			boolean verifyRequest = SignUtil2.verifyRequest(reqmap,publicKey,"UTF-8");
//			return true;
//		} catch (AlipayApiException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return true;
		//TODO
    }


}
