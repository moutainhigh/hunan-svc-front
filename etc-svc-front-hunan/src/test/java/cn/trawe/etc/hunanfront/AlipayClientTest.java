package cn.trawe.etc.hunanfront;

import cn.trawe.etc.hunanfront.config.AlipayClientConifg;
import cn.trawe.util.LogUtil;
import cn.trawe.utils.DateUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayCommerceTransportEtcApplySyncRequest;
import com.alipay.api.request.AlipayCommerceTransportEtcMediaGetRequest;
import com.alipay.api.response.AlipayCommerceTransportEtcApplySyncResponse;
import com.alipay.api.response.AlipayCommerceTransportEtcMediaGetResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author Jiang Guangxing
 */
@Slf4j
public class AlipayClientTest {
//    private static final String appId = "2018100861636478";
//    private static final String privateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCCa29Z14yvPotfgl6+L6q7VguR5BeFcPe6QlBpeLu6ek8zWFe0kwWg28NwYyV0w0s2QpJFvh2NfzYHJ6tHyffQsqSvJQHaOvvgrlSKLLDXSVSOzuhejhBRazHpUHOmHqgAXanSKdt0edFotJzFzmc7rEQeqA9JehejnyrKVsPHs9J8PeBLDtXNtqnLbw12fwWAu9GhgWdhgYIWyBL4rpg5+nuI8RLltaYOew8TLmkb+D2Var90Q5RHvq7piUAG81xAaHHeMxY6GgJsv1cXZWdKSnXtAgJHZ5N5Mc69Zm7+3/RstmphhKBmfbqHswtFs9mj6r4pfg2XMJpb596tGkSLAgMBAAECggEAMZ11ixQ5bSG6G+eY63lu1j3xwNqpKBbF55H6zWUk5dhJ8RqWoxebeIqiW0scoML7BfVleCrPS1kt0KMCwhjPshGEHS1S+xadY+huOPKvg/ddt8Z3GCGri43gOwZkOLpI53q6dD2AQ1tkkxTC322aUFVExPTie+16uMCAouFHMd1nO/CUGGaL0dkZLZ+NZ6RRaKXZW85viW/WJZS6m6OLHhyMvATXLIsNMgVW7ohBwPhfYUi2PJATJ0yNMLuUhWfuIqE6mYWLZUGSfwrpgMEK78eaZT93QLeS8rtnk17WmVJ6C715Ig0I5wuIIdV/IPyxaf3yzZOAgZ7wPpZkoL2eIQKBgQDMW5JW3HMKmXONlYa8xCepimmGR6hSVMI8dlnWOKqNnv7VkX9t15LLFN1yOhnUWcxaYXei5j8MIxf/JVEVtYcHL3Xut3KxFV8w/TgZ8PUOj5U7LoHRPxzAYhj4/A0+VAC/6zVDRIxnqgMbwLbPZJOLN6Okn34us2gSTqWAPfrFFwKBgQCjYJ7i6tLJWhRr8aOHsy+h0LUXn+selGnY2q6NvV13okGQcGzOe3y+VknbkNf00Agmyjro6h4p2VQgnk1i8iHVOYhe0NP6FPLCpL4lagRKQ33iL04+QW9hv+suPH9oW+vHj5jXmmvj7B4imnzm1d/XFQHDNhD550e2HlvsbYoMrQKBgQCUq3HVG1/eMDDuqf9xyMqJ/xYyCGOeMed1ESzeOi9qBGeke8tW3+P9Nlq8RSSRZhrTDeF5c0Qe0lEc38sTnhJz2cVUO8WGquOrwixUQwYk57RLb9QlpGKIW1WLneMMmxRIixcervnuWLMWN6dHTgZA2+ODWjz0QLTPzMN+fpi3VQKBgQCI+Fg8peJSLNMyLEONYQxt/SvVwpy75nFW/P+alUfnJIETNy4BbkYGzRDeo3BqLn6nBqSc4KqpKyr8rbrzWUxEFxfdZfZRk35y8Or5Mwilui+vkTlnuDRJHz1sZgvq0iW6CQl6uFv7mdkp6ZsNbAFdsJ7pqkpROTVF2e33KV6zgQKBgQCxztr5lQ10DNSGJfqjV3/W+5dVQZ0XohGtsF6q3qHJNQ9x3PMwhczM4d9jWyvcByx5njqHX3oWzzgNoTOez/CMyESubUtvLaLEa6SwgBEvDvWJYoSRTRMBzdp09vHHrAXNh0+IYzIEvyONvv5FzHVTrxrV5n/P1wKzaHbo1A2FEQ==";
//    private static final String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA128psApoAauvQYJPW5ayTowKxwKH1QTwmwMUPFn62IR1NQKf9sfx0KK1uSNSNCGEbavNqz/QpMAirGIBFVXN+ptQHLhi/sOS1+XyqbSp1LZ0CSl3TSZEwYCaKld5hutN15lrZ1BE3Vi+NEjZ8Ce283/jh50dxejDfJ3NLCALCkHRnAvPovXZGbLZiUywRchg/IarbTbzVmf0yIz4zNGAt2uRvS3lAX0gq2hSGdDXbB13CQlr6+UN8kuZraIGLBgTDl4m6/AyH6yLSZLpTIhf49evyaOH4WrWTsYSDb2leHTwiaG4Cm/mpoGOTo2SzzInLEMSTsSpRbeoOH/r1tU1PQIDAQAB";

    private static final String appId = "2021000114692216";
    private static final String privateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDOB+7d4u08HvfvzynZGZ1FMfKi/4kravQbBd+6O38Yn/b8AfOy+vHHm6CRFOwpAGT503SliRJgSBs6vCwrdAXZctYRzKkU7l4gCFMG8rL0kX1KWzYi3sQlsBylPay7cXVcjZiBNrxI752753L1C+JFP6yfbOSV2xn3xB4KlMRtLDcDYWHa2ffpS9nuFzY9YF5zt+63yxUhhQxe9ISeS1K+1YGrQCemjZG8908e5kuq/ywcL/lhHB4BJ8xEp0u9OuyUTCnJy84Y/ifTsxog8BXuDRPyzEChplssNUjH1RgHjpeCOPImypGuvbXq47KhEEPUW2qho5zIX0+UcdsZJ0XRAgMBAAECggEAXZLCzSnUf1q9VsArDHwSrquZvKf8X6jKxz8qtoVxGvkEDr7ANQi+KN8o1NvAynpwYfrE3q3bl7kIDOwLz4x5X6JFUX43SNdeDoRZWS1/U46EbfHxK3MreMZ8rBvPyK4mFGwG2KDIcQPLCt16m4rTMIpT13B4fQsuxxXeYwXgFIiQpSbVdWadnoZvZRwDeJF3p2kMOx9THnaMNnVakBuSKgpRd18tq4MnVRLreNk5rdtEvUtRtdINUgIAjCkFISw3XgeodRYCYG04EJtO7CEQNAzirZTUXaRjfFa1WXu33xrYyYAJdtYP/Q0fxY60xL8hcAAc2Zhaee6IsJPnuYPcBQKBgQDq549qQ/DzZV7FoIv51VIhGoJOwAI1XJtTvKBtzfjX4oKDmmjOzU4tVgCryxxU53IPwd8oXmvNKJkXVH4q8h4ILwBLBiaSEPtuIjcVSbo7Z99xY2Vt2z/17h1pFLYdJSdP+yDs7iDQKZe7BmdduOys9bRr+OCrVwDerEP/oEziewKBgQDgiJJtsT60z7cM5Jyc7VvF6VnsRZuQQesicJ7AbJ8k7zCjCeCv4/LLr6VUsWs2yW0KzXG5A2nJ70uBNBe+W08vOD7jNFPvyEQEh4+39IfBWVDzq13lcW1X//OU3zUoftXiqgxxtABVB6Mly6skex4KUN1uDt4I5P8oNnvCiPc9IwKBgQDQAgaz8b++uAgI9lac/3H/kErNUydhe0SsDL7/HMH64T/zK1sdrR1J9fsYJP5MjLorC+EBDUNmY0nVJ+OlQcqoMn6O8L5c357VcoTWW/gGPL/W105szhZAPv9aGpX9DvZV06nfRCpYSkxqt4v2qRcjPVvrtHG2J4/EnkSEar1KWwKBgQCxeuKbuD3DuGiN1WsCFBC1uMUuoLrdZW2SVIj3uyR0kmjUhutGvRze6iD6eB8yODdsEYax4sPNLcx1/ZJDEnPd9EypVWR/pcI1/l2Y374rFAmMAkn/IhB3PcbxRxoCv3cbaqTZf5m/nIDWUE4gUP0m1FKjOzdAupoB1EcxNwiPFwKBgQCBYKck0H/Yl3Z7RHAjpxsoV5gZnljxU0WDa7/QW0xr/c2SBcFY1tr7sV3wiz3bEo35Zou7xd++PCetCmd+5TaQ0nNwye1L75q6x9spzb64WHMtuXfZdCB9nWcKtVx1iFNpaqY6EM8poJ2/5kPTyu+qGm7XTEJNg4gpXEtGjhLWOw==";
    private static final String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwIFLOUgY176uBA50E86KgJvoXwrUQ9KQiK2w4vS5jV6DJ6wX85dOy7i6bAY//KD5bk0fIvgWjSz4IOP306PekwaeI7nOVwFRMI0ZqY++vGQ7nrc2gNyGrHcN6T/aiyr9pWiVxmB9Mc84LiGeBzAd6rzBLCv+W40OAN3gppERV//Vl7Gj1m4iRIOgH9t6HkvJpSXmpr84Oi6fB7/jl3oIXqQFQ37wsXM3lh1TkqfqecEYgtreBp/ZOh0BD/w0B37NmTt8sVtfIIg7GM+AdAQJvLnuk61WegaX5fsTW0LpKOPy0KjpV5C3gqbkwMMyNXF52cDN6XZxfl8PJyeQ+k8Q2wIDAQAB";

    public static void main(String[] args) throws AlipayApiException {
        AlipayClientConifg.AlipayClientProperties properties = new AlipayClientConifg.AlipayClientProperties();
        properties.setServerUrl("http://openapi.dl.alipaydev.com/gateway.do");
        properties.setAlipayPublicKey(alipayPublicKey);
        properties.setPrivateKey(privateKey);
        properties.setAppId(appId);

        AlipayClient client = new DefaultAlipayClient(properties.getServerUrl(), properties.getAppId(), properties.getPrivateKey(),
                properties.getFormat(), properties.getCharset(), properties.getAlipayPublicKey(), properties.getSignType());
//        ordersync(client);
        getImages(client);
    }

    private static void getImages(AlipayClient client) throws AlipayApiException {
        AlipayCommerceTransportEtcMediaGetRequest request = new AlipayCommerceTransportEtcMediaGetRequest();
        request.putOtherTextParam("ws_service_url", "11.166.64.73:12200");
        request.setBizContent("{" +
                "\"order_id\":\"20190518152047123248924875\"," + "\"out_biz_no\":\"20190516152147123248924876\"," +
                "\"biz_type\":\"1\"" +
                " }");
        AlipayCommerceTransportEtcMediaGetResponse response = client.execute(request);
        log.info(JSON.toJSONString(response));
    }

    private static void ordersync(AlipayClient client) {
        JSONObject bizContent = new JSONObject();
        bizContent.put("order_id", "20190518152047123248924875");
        bizContent.put("order_status", 3);
        bizContent.put("order_update_time", DateUtils.format(new Date(), DateUtils.YYYY_MM_DD_HH_MM_SS));

        AlipayCommerceTransportEtcApplySyncRequest applySyncRequest = new AlipayCommerceTransportEtcApplySyncRequest();
        applySyncRequest.putOtherTextParam("ws_service_url", "11.166.64.73:12200");
        applySyncRequest.setBizContent(bizContent.toJSONString());
        LogUtil.info(log, bizContent.getString("order_id"), "调用申请单状态同步接口请求", bizContent);
        //最多重试5次
        int retryTimes = 5;
        for (int i = 1; i <= retryTimes; i++) {
            try {
                Thread.sleep(5000 * (i - 1));
                AlipayCommerceTransportEtcApplySyncResponse res = client.execute(applySyncRequest);
                LogUtil.info(log, bizContent.getString("order_id"), "第" + i + "次调用申请单状态同步接口响应", res);
                if (res != null && "10000".equals(res.getCode()))
                    break;
            } catch (Exception e) {
                LogUtil.error(log, bizContent.getString("order_id"), "第" + i + "次调用申请单状态同步接口失败", e);
            }
        }
    }
}
