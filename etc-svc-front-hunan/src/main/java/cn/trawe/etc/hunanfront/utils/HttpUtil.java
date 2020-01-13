package cn.trawe.etc.hunanfront.utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
/**
 * @Author 张子元 【15373911114@163.com】
 * @Date 2018/6/28 上午11:54
 */
public class HttpUtil {
    /**
     * httpClient发送post请求
     * @author 张子元
     * @date 2018/6/28 上午11:32
     * @param url, body
     * @return java.lang.String
     */
    public static String sendHttpPost(String url, String body) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        org.apache.http.client.methods.HttpPost httpPost = new org.apache.http.client.methods.HttpPost(url);
//        httpPost.addHeader("Content-Encoding","UTF-8");
        httpPost.addHeader("Content-Type", "application/json");
        if (body!=null){
            StringEntity entity1 = new StringEntity(body,"UTF-8");
            entity1.setContentEncoding("UTF-8");
            httpPost.setEntity(entity1);
        }
        CloseableHttpResponse response = httpClient.execute(httpPost);
//        System.out.println(response.getStatusLine().getStatusCode() + "\n");
        org.apache.http.HttpEntity entity = response.getEntity();
        String responseContent = EntityUtils.toString(entity, "UTF-8");
//        System.out.println(responseContent);

        response.close();
        httpClient.close();
        return responseContent;
    }
    /**
     * httpClient发送post请求
     * @author 张子元
     * @date 2018/6/28 上午11:32
     * @param url, body
     * @return java.lang.String
     */
    public static String sendHttpPost(String url, String body ,String token) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        org.apache.http.client.methods.HttpPost httpPost = new org.apache.http.client.methods.HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("token", token);
        if (body!=null){
            StringEntity entity1 = new StringEntity(body,"UTF-8");
            entity1.setContentEncoding("UTF-8");
            httpPost.setEntity(entity1);
        }
        CloseableHttpResponse response = httpClient.execute(httpPost);
        org.apache.http.HttpEntity entity = response.getEntity();
        String responseContent = EntityUtils.toString(entity, "UTF-8");

        response.close();
        httpClient.close();
        return responseContent;
    }
    /**
     * 以form表单形式提交数据，发送post请求
     * @explain
     *   1.请求头：httppost.setHeader("Content-Type","application/x-www-form-urlencoded")
     *   2.提交的数据格式：key1=value1&key2=value2...
     * @param url 请求地址
     * @param paramsMap 具体数据
     * @return 服务器返回数据
     */
    public static String httpPostWithForm(String url,Map<String, String> paramsMap){
        // 用于接收返回的结果
        String resultData ="";
        try {
            HttpPost post = new HttpPost(url);
            List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
            // 迭代Map-->取出key,value放到BasicNameValuePair对象中-->添加到list中
            for (String key : paramsMap.keySet()) {
                pairList.add(new BasicNameValuePair(key, paramsMap.get(key)));
            }
            UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(pairList, "utf-8");
            post.setEntity(uefe);
            // 创建一个http客户端
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            // 发送post请求
            HttpResponse response = httpClient.execute(post);

            // 状态码为：200
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                // 返回数据：
                resultData = EntityUtils.toString(response.getEntity(),"UTF-8");
            }else{
                throw new RuntimeException("接口连接失败！");
            }
        } catch (Exception e) {
            throw new RuntimeException("接口连接失败！");
        }
        return resultData;
    }
}
