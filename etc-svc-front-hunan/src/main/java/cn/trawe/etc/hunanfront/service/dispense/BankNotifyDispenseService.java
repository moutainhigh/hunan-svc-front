//package cn.trawe.etc.hunanfront.service.dispense;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alipay.api.AlipayApiException;
//
//import cn.trawe.etc.hunanfront.entity.ThirdPartner;
//import cn.trawe.etc.hunanfront.expose.v2.BaseRequest;
//import cn.trawe.etc.hunanfront.utils.HttpUtil;
//import cn.trawe.etc.hunanfront.utils.SignUtil2;
//import cn.trawe.util.LogUtil;
//import lombok.extern.slf4j.Slf4j;
//
//@Service
//@Slf4j
//public class BankNotifyDispenseService {
//	
//	  public void doService(JSONObject bizContent,ThirdPartner partner) throws AlipayApiException {
//		  BaseRequest applySyncRequest = new BaseRequest();
//	        applySyncRequest.setBizContent(bizContent.toJSONString());
//	        applySyncRequest.setSignType("RSA2");
//	        applySyncRequest.setCharset("UTF-8");
//	        applySyncRequest.setSign(SignUtil2.signResponse(bizContent,partner.getTrawePrivateKey(),applySyncRequest.getCharset(),true));
//	        LogUtil.info(log, bizContent.getString("order_id"), "调用申请单状态同步接口请求", bizContent);
//	        //最多重试5次
//	        int retryTimes = 5;
//	        for (int i = 1; i <= retryTimes; i++) {
//	            try {
//	                Thread.sleep(5000 * (i - 1));
//	                //升级该接口支持不通渠道分发调用
//	                HttpHeaders headers = new HttpHeaders();
//	                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//	                Map<String, String> map= new HashMap<String,String>();
//	               
//	                map.put("bizContent", SignUtil2.getContent(bizContent));
//	                map.put("sign", SignUtil2.signResponse(bizContent,partner.getTrawePrivateKey(),applySyncRequest.getCharset(),true));
//	                String channelResp = HttpUtil.httpPostWithForm(partner.getNotifyUrl(), map);
//	                net.sf.json.JSONObject responsebody = net.sf.json.JSONObject.fromObject(channelResp);
//	                //验证签名
//	                net.sf.json.JSONObject check = responsebody.getJSONObject("response");
//	                boolean verifyResponse = SignUtil2.verifyResponse( responsebody.getString("response"), responsebody.getString("sign"), partner.getPublicKey(), "UTF-8");
//	                if(!verifyResponse) {
//	                	break;
//	                }
//	                LogUtil.info(log, bizContent.getString("order_id"), "第" + i + "次调用申请单状态同步接口响应", responsebody.toString());
//	                if (check != null && "10000".equals(check.getString("code")))
//	                    break;
//	            } catch (Exception e) {
//	                LogUtil.error(log, bizContent.getString("order_id"), "第" + i + "次调用申请单状态同步接口失败", e);
//	            }
//	        }
//	  }
//
//}
