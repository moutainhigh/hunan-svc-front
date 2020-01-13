package cn.trawe.etc.hunanfront;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;

import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EtcSvcFrontHunanBootstrap.class)
@Slf4j
public class ApplyOrderQueryServiceTest {
   
	@Autowired
	private cn.trawe.etc.hunanfront.service.ApplyOrderQueryService ApplyOrderQueryService;
	
    final static String PRIVATE_KEY= "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCS3HvJGdvTbWyPKQnfvEurr+dpA+vj/p1UT+dU4zGMpvaDX39n3RLBoSXEdjo76Wmsmm+wVX4ahoh6TEHeiWc9f0YyB/CuzdvFsJERzLGrSjQE0vzYUolnzPkOEcU95MgLMlD2he5rH4xWGQlnWybc6Oq5d7tksdOc9sOm+Ce8ifVrSuMFdbSpwxNIXW3ia0LYZgfI3aIVgPpzMmAOxZOg7KCagO6MWeTdu5Fq4JKDDdnegxTDsUzWHauh4fh8fUBbUmqN9sQDGrZfo3BqddDOtLUV4vq21IeWQhlDt5jnGNcwfWDQepUgUXZbj4cT7BDEcB3RTd1x7f9JQNasH0MZAgMBAAECggEANduJwcwr47ODVlT0fApvrbzKnqaDgY9EX0EWUvkmmLA6Qx9od8yJLl1MnmnFHWeSC2xvfTk9D7k6n68LkPLO26yrL3nd+B0idbM04FQy6+BAh2xgsWxDW7+9rj0yGmqmOgTv0Bh5wOtG6eQwoKi67dS1D+Mq49ibBDsysS7cU7WuAzky8cCm/ygAvaFQvBJ5rCCgIRYrMAkcZv4tXx0RgQL4NUDJuuwiGJLuxyVt67CcIpr5jaP3sMqtZVyxYgLQMPmetxTmpg6U2foutXPz5rm1E2xhTi102aXUUaVbo0Vw/ijRT0gUM1abXR8mwna/TqkcSudl2QD4GcAlQBpiSQKBgQDEGrk6EXRyDbfyT5HjYW/stQdzJTmOt1/yH326N12FNodS+Hjmzq784qvw25qM0jGEJbF07MwGKdqfCiEYP+TirkNcmvaWFRzrPY70ipK7t43fDHBcWmYWoG4cMK6/T+V/dSX+IGlJdK9SCurDXm8cTiqMmadp8k7valGPmCmEQwKBgQC/t3i7xmYvwh9z/oD0bfzffpYdfmZbhPCw5dFcT3zXc97eC23DfRllELckCrkcn1gF1+5Ran0st7KiE5voMlMxQt1WEwCLYFrbd3ktNLWLCe02khz+U2l8676USiuqvQCNnJpY0sbjoc3Tv9cwuKVu8KzbKRUHIAZ/k7B3HSqzcwKBgDyrE9C+I6tBST4ilbyloU22t6ZMxuC7JGiwi2H3zOvwca8ehbDKEmLT8WOjXM3iHUWHr72/oGLrweHf7VsmMTZbuSVGH7FQuNCiC77lu/1tDOwLWhuIL/siCM6Z1QYEvrhO0sEq38fE3JRiIjzs0y0LlahHxP5G41q4JqFo7rcnAoGBAKduQRekjku5OiTgKJjPkZ9R+yHo6NWWhWnIW45FsR6sh//ddU/b6hi0f20PnFid5I695NjfqlAqYexgObGQec00uv5X/Aq87LxsW7yaIs45rhwRgRavnBnH/JmfKQGkrF4AXV+jPKVCAvKhwPpA1+HKePuxpjahxmsWfW+ZXlh3AoGAQ4sTYp/WtHWsHc2zGfDLQbUPJTrkuz+boF5XVVTHapdVHaWSot6WaLwlw1VxAkC9Uy19al17at7rY+uC0Y2VmyZgkskwkMXgJ28JMzLka2hxy6t8CxcDnGNkrKPaHZ78aC0RDO1xoqtlb+opn1/t/dnMUprH0C6CMfZQGzeh3cI=";

    final static String TRAWE_PUBLIC_KEY= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsYv1kEKA4c6k2+A+tS8ek7C6pjztjfWPYFwdiUHuzhVwTmuOWpIHQ9AVKmm1mjtMfAOV3Nfnayt8P2XTFFe5wV/sSy5epmaEPcZCuUqc0yx2k9FUGxtMjKOFaJ282S344zHKDHCQdufuzqjSJ7/QpWJ8zz7uE7wpT38GWj0kE91b2+JJl30rBOXDDlvkcjdmj7xEXVXM3BWLsKsGkMup71m7aCPjvDFbR20ka4BXgAnr1lMGqiBPskVL3N6VownOr89Wuc9yOAQNvUTCwsnvVWpvFXx4By5hui0ZjJpYkmF8AvIrjGVi+2BRnonC15AS8T0jn9MhA2E4l9VlxsUiawIDAQAB";

	@Test
	public void case0() throws AlipayApiException {
		BaseRequest req = new BaseRequest();
		JSONObject requestBody = new JSONObject();
		 requestBody.put("order", "");
		 String biz_content = requestBody.toJSONString();
	        System.out.println(biz_content);
	        req.setService("trawe.etc.publish");
	        req.setCharset("UTF-8");
	        req.setVersion("1.0");
	        req.setAppId("123");
	        req.setPid("2088XXX111");
	        req.setUtcTimestamp(String.valueOf(new Date().getTime() / 1000));
//	        request.setUtcTimestamp("123");
	        req.setSignType("RSA2");
	        req.setBizContent(biz_content);
	        String sign = "app_id="+req.getAppId()
	                +"&biz_content="+req.getBizContent()
	                +"&charset="+req.getCharset()
	                +"&pid="+req.getPid()
	                +"&service="+req.getService()
	                +"&utc_timestamp="+req.getUtcTimestamp()
	                +"&version="+ req.getVersion();
	        String str = "";
	        str = AlipaySignature.rsa256Sign(sign, PRIVATE_KEY, req.getCharset());
	        req.setSign(str);
		     ApplyOrderQueryService.applyOrderQuery(req);
	}
	
   
}
