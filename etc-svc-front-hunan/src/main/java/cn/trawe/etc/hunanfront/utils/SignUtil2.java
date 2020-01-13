package cn.trawe.etc.hunanfront.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.StringUtils;
import com.google.common.base.CaseFormat;

import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import cn.trawe.etc.hunanfront.expose.v2.BaseResp;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponse;
import cn.trawe.etc.hunanfront.feign.entity.hunan.CarServiceResp;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * @author Jiang Guangxing
 */
@Service
@Slf4j
public class SignUtil2 {
	

    /**
     * @param res        响应对象
     * @param privateKey 私钥
     * @param charset    字符编码
     * @return 签名
     * @throws AlipayApiException AlipayApiException
     */
    public static String signResponse(Object res, String privateKey, String charset,Boolean isToJson) throws AlipayApiException {
        if (res == null) {
            throw new IllegalArgumentException("res不能为空");
        }
        //String content ="";
        //if(isToJson) {
        String	 content = getContent(res);
        log.info("加签明文内容 : " +content);
       // }
        //content =res.toString();
        return AlipaySignature.rsa256Sign(content, privateKey, charset);
    }

    /**
     * @param res       响应对象
     * @param publicKey 公钥
     * 
     * 
     * 
     * 
     * @param charset   字符编码
     * @return 验签结果
     * @throws AlipayApiException AlipayApiException
     */
    public static boolean verifyResponse(Object res, String sign, String publicKey, String charset) throws AlipayApiException {
        if (res == null) {
            throw new IllegalArgumentException("res不能为空");
        }
        if (StringUtils.isEmpty(sign)) {
            throw new IllegalArgumentException("sign不能为空");
        }
        //String content = getContent(res);
        //log.info(res.toString());
        return AlipaySignature.rsaCheck(res.toString(), sign, publicKey, charset, AlipayConstants.SIGN_TYPE_RSA2);
    }

    /**
     * @param req        请求对象
     * @param privateKey 私钥
     * @param charset    字符编码
     * @return 签名
     * @throws AlipayApiException AlipayApiException
     */
    @Deprecated
    public static String signRequest(Object req, String privateKey, String charset) throws AlipayApiException {
        if (req == null) {
            throw new IllegalArgumentException("req不能为空");
        }
        Map<String, String> params = getStringStringMap(req);
        String content = AlipaySignature.getSignCheckContentV1(params);
        //log.info("content : " +content);
        //log.info("charset : "+charset);
        //log.info("privateKey : "+privateKey);
        return AlipaySignature.rsa256Sign(content, privateKey, charset);
    }

    /**
     * @param req       请求对象
     * @param publicKey 公钥
     * @param charset   字符编码
     * @return 验签结果
     * @throws AlipayApiException AlipayApiException
     */
    public static boolean verifyRequest(Object req, String publicKey,String charset) throws AlipayApiException {
        if (req == null) {
            throw new IllegalArgumentException("req不能为空");
        }
        return verifyRequest(getStringStringMap(req), publicKey, charset);
    }

    /**
     * @param params     请求参数map
     * @param privateKey 私钥
     * @param charset    字符编码
     * @return 签名
     * @throws AlipayApiException AlipayApiException
     */
    public static String signRequestForMap(Map<String, String> params, String privateKey, String charset) throws AlipayApiException {
        if (params == null || params.size() == 0) {
            throw new IllegalArgumentException("params不能为空");
        }
        String content = AlipaySignature.getSignCheckContentV1(params);
        //log.info(content);
        return AlipaySignature.rsa256Sign(content, privateKey, charset);
    }

    /**
     * @param params    请求参数map
     * @param publicKey 公钥
     * @param charset   字符编码
     * @return 验签结果
     * @throws AlipayApiException AlipayApiException
     */
    public static boolean verifyRequest(Map<String, String> params, String publicKey, String charset) throws AlipayApiException {
        if (params == null || params.size() == 0) {
            throw new IllegalArgumentException("params不能为空");
        }
        String sign = params.get("sign");
        if (StringUtils.isEmpty(sign))
            return false;
        String content = AlipaySignature.getSignCheckContentV1(params);
        //log.info("content : "+content);
        //log.info("sign : " +sign );
        //log.info("publicKey : " +publicKey);
        //log.info("charset : " +charset );
        return AlipaySignature.rsaCheck(content, sign, publicKey, charset, AlipayConstants.SIGN_TYPE_RSA2);
    }

    public static String getContent(Object res) {
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        return JSON.toJSONString(res, serializeConfig);
    }

    private static Map<String, String> getStringStringMap(Object object) {
        List<Field> fields = BeanHelper.getAllFieldList(object.getClass());
        Map<String, String> params = new HashMap<String, String>();
        for (Field field : fields) {
            String fieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
            Object fieldValue = BeanHelper.getFieldValue(field, object);
            if (fieldValue != null) {
                params.put(fieldName, String.valueOf(fieldValue));
            }
        }
        return params;
    }

    private SignUtil2() {
    }
    
    public static void main(String[] args) throws AlipayApiException {
    	
		String privateKey ="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCxi/WQQoDhzqTb4D61Lx6TsLqmPO2N9Y9gXB2JQe7OFXBOa45akgdD0BUqabWaO0x8A5Xc1+drK3w/ZdMUV7nBX+xLLl6mZoQ9xkK5SpzTLHaT0VQbG0yMo4VonbzZLfjjMcoMcJB25+7OqNInv9ClYnzPPu4TvClPfwZaPSQT3Vvb4kmXfSsE5cMOW+RyN2aPvERdVczcFYuwqwaQy6nvWbtoI+O8MVtHbSRrgFeACevWUwaqIE+yRUvc3pWjCc6vz1a5z3I4BA29RMLCye9Vam8VfHgHLmG6LRmMmliSYXwC8iuMZWL7YFGeicLXkBLxPSOf0yEDYTiX1WXGxSJrAgMBAAECggEAeDucjxXKK6CB2fiQ3qfLIB71NzBZeuIJNysKQXIp4tt301NY8pKjYtPeEXvGpGx1ziGrcgOvzs6C9HfjKoqB26c24G7FYBBai27grpQNgaBBqLaAK18gRrlR1dfEFJriShDkj+oGms9T1BVVQ9/MzsLUXJB0L18j5pbR2XhAH0aMomP71GkqRs4Z4mKiJxTk1RECFOCI/ybhuw1TX6qIdEiaMvZoZRaSvHNjDvGVJfJNB4CChd//W+NqQ+/EkkcFZXVLicSSpasoZdCMah9oJGv5jZ/eYn3DbjZJozwEkcvCd8kNuV9uNmleU7Ez7rwh1bNo+GPsoUS7xsOrdr5gSQKBgQDumD15n2IsuHs8jjFRL7j/QtIKef6cTFiLyCxpH0dse/G9Yrl6eHCjUgWfZo9bsGLO5Pa7t15iC7t84V7EcJyOBaD+Ok/m6tg/2eW/Ws2NCCEmlURcy74crIZvD+wRfJtwjZtKFPqs5mnXHY6Jx1R27/+Bb+ZI1qUtDrv5WTcHfQKBgQC+f6WtJODlwrbzVTgElA5rZWhk09OE2UW9+M0Sc7zlGwfhES9k8nkJql7XW+NUZwOxf8H/hUd6mRaNdAg4sLxzzCJ4C01hlY1SVqlBQsgjuqVCAhUveIjq+JBiVzlOBnrpeAHO0LhBipjJX19fhr8iJWaA3YJeVMr3m/P9/iMGBwKBgEsnMv3zQTY66b56P3iGIqxsvMJGCPfsaFunMq3uB05ZgByaUzgLZddbx5+c630DAyyOHFWmfZk+ftDoIDSaEAYAKeTKM07WHEWIegCbNnG85MxFaiIcnwZjJXkoN8COUlvSjt532znI3f+/NJ2bZrSMBhwcRhodWivmHzl8a4P1AoGATeFPsWaXTzd4piib2ztQXdW6hSYYnoJZQd46sKWCf0O6x+fHsYRzuWDenzrgyHvJBcMOY9FwNI3rq27ZgZu3n7vAB8XPH+sNOLFXRufvfLPspfzF/n9PsohmNt4j/mMl0tcOHig0fA8WgZUtH5euAl659ksY5knSJMKsUJWaafcCgYEA4LT10dQHuwrA76hfAn/dGqasraVGCYULqw2+lJr60lenh86nWVillyeFGFZzeDn+V0ZL+/ub4igghT602ZJkum/78gZynFyC4aB0CPjzzATBU1zkJQu3ghcG8tKT/AMXiFpwmxTGLbFmm+Js1H13qlPpVN1ldvIn75bMIMGnx28=";
		
		String publicKey ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsYv1kEKA4c6k2+A+tS8ek7C6pjztjfWPYFwdiUHuzhVwTmuOWpIHQ9AVKmm1mjtMfAOV3Nfnayt8P2XTFFe5wV/sSy5epmaEPcZCuUqc0yx2k9FUGxtMjKOFaJ282S344zHKDHCQdufuzqjSJ7/QpWJ8zz7uE7wpT38GWj0kE91b2+JJl30rBOXDDlvkcjdmj7xEXVXM3BWLsKsGkMup71m7aCPjvDFbR20ka4BXgAnr1lMGqiBPskVL3N6VownOr89Wuc9yOAQNvUTCwsnvVWpvFXx4By5hui0ZjJpYkmF8AvIrjGVi+2BRnonC15AS8T0jn9MhA2E4l9VlxsUiawIDAQAB";
	
//		Map<String,String> req = new HashMap<String,String>();
//		req.put("app_id", "13");
//		String signRequest = signRequestForMap(req,privateKey,"UTF-8");
//		req.put("sign", signRequest);
//		System.out.println(signRequest);
//		boolean verifyRequest = verifyRequest(req,publicKey,"UTF-8");
//		//verifyResponse("{\"account_result\":\"0\",\"check_result\":\"0\",\"error_code\":\"100\",\"success\":\"1\",\"vi_number\":\"湘AA66S6\",\"vi_plate_color\":\"3\"}", "\"dbR1B+1p3JSv6r9lqYoyNLxUbX4iCQNQvwBATlwqrUXx0Och6vWsyJrempJKxv00fHEWBoh5KnBzl7mGgixyjKY0oT7IuaFFhaWghrEPjPV5PQotZNE5jab9b1PturBlOIQhfMAscON6MXo9bFIb33EZNn5no81H48hpXEuQB6uhpEuV+Bs0IwzmKUjJG1RrDmwJRTCC6WSlGKCW+TOkB8zH7e+qbjfIZm0nW8OJhufHPZGcb2szTFnutCM+xoVrzZIJPISPLibqt4F2nMkvi9VvO9gjiD6ys4PYINjD6PYQNMI3gpvXM393QuynuSv6nq4easixUOXSK82+92hQSA==\"", publicKey, "UTF-8");
//		System.out.println(verifyRequest);
//    	String privateKey1 = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDYflWFz79PuqmG\n" + 
//    			"cFgXvDWiwv5sKy/ARohvmnLebPvQY4vIgrr1QUus0gZIDrH6ilEzWU8sfd3JeDdK\n" + 
//    			"loYQg+c7MOANkL8cehQDketjdQKjqGuZlZ1cLts2ywcMjKInui9FpWsYgRb7oOYu\n" + 
//    			"LAJO6AnvxT8oMkD4RAOZtLIExIahcIt4erngNijKFPAsZ+z39qWTR64tvYAsOBZy\n" + 
//    			"zwaL6XmctjQWyfIA3xcpXw9vzgwCLJyU6y3g1KEhjoer6HOV79Cg/RGWWraH2+Dj\n" + 
//    			"alppUwPDVNEJX48TPRCu9SBSMGeubxxfFQXWAM0F1vV/sk2RezeDgkOkF2YcsAj4\n" + 
//    			"etl3yd0RAgMBAAECggEBALjS8hRxDbT1ePELTEnHJks0CZ0zKWsPTD9rLhHyHPEF\n" + 
//    			"azrdUNs8yWxbSX2O/icUmsopvswPEJ+FgD6vTvkegvQnf8BVCLM5DYkWtEyOgphR\n" + 
//    			"mrrd3h/q6L9YiUYPcghUjTL6S/n5izN7Y8ebTsUqmAEY9hhM/uZ0ulYoy6oCVaXQ\n" + 
//    			"KrM9VTQUgQslRya6cGc+jKVyPaNfluZvpAy/Z/2muRCMVgr1iv1Xe+p8bJ/KRPLK\n" + 
//    			"PU1ua2DK0djWkSQUQ7RD2PqNSeGe8JJKxqxrlshRK6Hq+goRAjOirkIFicPd4hU4\n" + 
//    			"wksfBRcOkxk081SnRHiyzroJsaRM7e5546Ztc3cHeHkCgYEA+d0PTpuNixmxWmZj\n" + 
//    			"GqeRyo9EteXwSkeBOuAarPaIG9wfgs02jIz0gXLh9fFIJeM5lJKtAV7uQ3Qw+crc\n" + 
//    			"N1fZrtaBvof1GsoE7lUulQHTl4S64RnTUDfOLfVUyE2GVduJY2Ky8LYuyrwk3pxb\n" + 
//    			"r6GuhDBzV7TWEDjJ7S2X8ELzktcCgYEA3c94crOWuHdueTHf7baytoV57aFkyXHI\n" + 
//    			"t+HdBdsd9zM3l6Pqps4uDwmNDcxZM7B2qxvpq2iIRaqeRTeLyIjVh3mvo429JW5y\n" + 
//    			"xEqrlP7rhi7GwlmFZjsoc19HeFL2TpvqBENFJ7gFkwMrIKnEOQmi4gcFPcWyoiFS\n" + 
//    			"Rewi29yr+lcCgYEAnlrCu8AMGc9jvnZLcvYmiOj51ApxmrHsriKf5WStaHK+qchj\n" + 
//    			"PPqMb4oeuR986T40HPxElyzR9oAqFfXCUXTk6lDak6VY6uyD6YSk7BznPA0L32Pd\n" + 
//    			"r+ZeTnRAkl3HuJxVW16PJ81epHBPMaKI3SusDe0i/MAINum22BFgTQ4xjqsCgYBv\n" + 
//    			"qWIZXA8kDfuvR0xTfwW4nLkkx3rVblJ9P1bk+8m6CC6USBB4qJLHPyejKpw9STh0\n" + 
//    			"KzkW8Sio6ZuDfNMBwOC9sqSY0WHG1jMwXebCuxIWGrY0YJynOmDCs9Rm3lzgddz5\n" + 
//    			"CGRn+lvUBmRPrSb7D77Pynj4/+1NcTjZumZSCGULQQKBgQCrAkdHsvZAsAc5D4Ab\n" + 
//    			"Lc45mQ54RoFm8QlZ4f4+4eSOkH04VtJE8xymVhcSVtYvZBRP7JE3Gzdj0XcNxf9V\n" + 
//    			"btW4MzixXkAmF400H4zLLulvNpJ7OzU5uxD95igbh4m64jKf/+HlyEot6KEow2bp\n" + 
//    			"KTsi5I01kd2RsJHSbC8mvJx0mA==";
//    	String publicKey1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2H5Vhc+/T7qphnBYF7w1osL+bCsvwEaIb5py3mz70GOLyIK69UFLrNIGSA6x+opRM1lPLH3dyXg3SpaGEIPnOzDgDZC/HHoUA5HrY3UCo6hrmZWdXC7bNssHDIyiJ7ovRaVrGIEW+6DmLiwCTugJ78U/KDJA+EQDmbSyBMSGoXCLeHq54DYoyhTwLGfs9/alk0euLb2ALDgWcs8Gi+l5nLY0FsnyAN8XKV8Pb84MAiyclOst4NShIY6Hq+hzle/QoP0Rllq2h9vg42paaVMDw1TRCV+PEz0QrvUgUjBnrm8cXxUF1gDNBdb1f7JNkXs3g4JDpBdmHLAI+HrZd8ndEQIDAQAB";
//    	String content ="app_id=221&biz_content={\"vi_owner_cert_address\":\"贵州省贵阳市白云区绿地新都会\",\"vi_owner_cert_pic_back\":\"www.baidu.com\",\"vi_owner_cert_pic_front\":\"www.baidu.com\",\"vi_owner_name\":\"闫昭忻\",\"vi_owner_cert_no\":\"130828199312250050\",\"channel_no\":\"221\",\"vi_owner_cert_type\":\"0\",\"vi_owner_cert_number\":\"17844628923\",\"order_id\":\"221777777777777777777\"}&charset=UTF-8&pid=2088234111&service=trawe.etc.publish&utc_timestamp=1574146047601&version=1.0";
//    	String rsa256Sign = AlipaySignature.rsa256Sign(content, privateKey1, "UTF-8");
//    	System.out.println(rsa256Sign);
//    	boolean rsaCheck = AlipaySignature.rsaCheck(content, "1mkmxgJSza/A7T3EzOXxk5FJlXz7UtRmtKWSxQb8LvIMvPWDbYvzDJ6jUp23eJmB0YRTAlvzepQIbayWBtGA4x2PomgotouJ9DUkHjN1UtNedgd+zSEx9JN2T+OlpwyT7mXInsufNMGaExt/91oS5pWPywN3xijL1lovMiK+eE22befTI7q11shHOD+puBpnIFC0/5t3FGmhPNtS4QomUIVWPXAIPO8wkvBEd+fgH1yP7bRbbcuC5juXAJtoVmECwFn+dvyArZKpIHaqp1nTzgSHWM4Zy8xTCRGvUoRrT18GxzdGcxNEi71WM2rBmPAAEpC16Tm6oHmdUFUOHeIaZw==\n" + 
//    			"", publicKey1, "UTF-8", AlipayConstants.SIGN_TYPE_RSA2);
//    	System.out.println(rsaCheck);
//    	
//    	Map<String,String> req2 = new HashMap<String,String>();
//		req2.put("app_id", "221");
//		req2.put("biz_content", "{\"vi_owner_cert_address\":\"贵州省贵阳市白云区绿地新都会\",\"vi_owner_cert_pic_back\":\"www.baidu.com\",\"vi_owner_cert_pic_front\":\"www.baidu.com\",\"vi_owner_name\":\"闫昭忻\",\"vi_owner_cert_no\":\"130828199312250050\",\"channel_no\":\"221\",\"vi_owner_cert_type\":\"0\",\"vi_owner_cert_number\":\"17844628923\",\"order_id\":\"221777777777777777777\"}");
//		
//		req.put("charset", "UTF-8");
//		req.put("pid", "2088234111");
//		req.put("service", "trawe.etc.publish");
//		req.put("utc_Timestamp", "1574148636510");
//		req.put("version", "1.0");
//		String signRequest2 = signRequestForMap(req2,privateKey1,"UTF-8");
//		req.put("sign", signRequest2);
//		System.out.println(signRequest2);
		
		CarServiceResp carServiceQuery = new CarServiceResp();
		carServiceQuery.setErrorCode("NOT_FOUND");
		carServiceQuery.setSuccess("0");
		BaseResponse<CarServiceResp> resp = new BaseResponse<>();
        resp.setResponse(carServiceQuery);
        try{
            resp.setSign(SignUtil2.signResponse(resp,privateKey,"UTF-8",true));
        }catch (AlipayApiException e){
            log.error(e.getMessage(),e.fillInStackTrace());
            throw new RuntimeException("响应加密失败原因:"+e.getLocalizedMessage());
        }
        System.out.println(resp.toString());
        
        boolean rsaCheck = AlipaySignature.rsaCheck("{\"response\":{\"error_code\":\"NOT_FOUND\",\"success\":\"0\"}}", "pcloGWukShpnX++Na56ng9yKImKRvC0EIgI6lgUieMf7vnsfn1iw7xdgrTotWhQUB8qSBtxAVVa7gFqmSdRbIjxm2kGeaq1BJB+bftrPMUOBzhWWRvDN4ri4p/wqrjFSwuXtIoqUFI06LgCFKYRSgdGGZyLNJQqMHQeUTYdfVeCx0Pio0cRbEgEUZivUMwFzuq30e0xJN1qgTZ1xpPc0h1i17MDwd36D16ftAWyYdFgg5aQLouXI6fl867RmeDTQQVBORZNqyGQbkMwDZebq2Rm2AXZPt5boAh8amr/3m1UheeX8hdEmt0tlYl/Glso9MA5jX/u2cnGxmreZ2lc6Xw==", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsYv1kEKA4c6k2+A+tS8ek7C6pjztjfWPYFwdiUHuzhVwTmuOWpIHQ9AVKmm1mjtMfAOV3Nfnayt8P2XTFFe5wV/sSy5epmaEPcZCuUqc0yx2k9FUGxtMjKOFaJ282S344zHKDHCQdufuzqjSJ7/QpWJ8zz7uE7wpT38GWj0kE91b2+JJl30rBOXDDlvkcjdmj7xEXVXM3BWLsKsGkMup71m7aCPjvDFbR20ka4BXgAnr1lMGqiBPskVL3N6VownOr89Wuc9yOAQNvUTCwsnvVWpvFXx4By5hui0ZjJpYkmF8AvIrjGVi+2BRnonC15AS8T0jn9MhA2E4l9VlxsUiawIDAQAB\n" + 
        		"", "UTF-8", AlipayConstants.SIGN_TYPE_RSA2);
    	System.out.println(rsaCheck);
    }
}
