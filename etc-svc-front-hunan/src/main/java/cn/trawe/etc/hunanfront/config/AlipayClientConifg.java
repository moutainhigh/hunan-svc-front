package cn.trawe.etc.hunanfront.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jiang Guangxing
 */
//@Configuration
public class AlipayClientConifg {
    @Bean
    public AlipayClient alipayClient(AlipayClientProperties properties) {
        return new DefaultAlipayClient(properties.getServerUrl(), properties.getAppId(), properties.getPrivateKey(),
                properties.getFormat(), properties.getCharset(), properties.getAlipayPublicKey(), properties.getSignType());
    }

    @Data
    public static class AlipayClientProperties {
        private String appId;
        private String privateKey;
        private String alipayPublicKey;
        private String serverUrl = "https://openapi.alipay.com/gateway.do";
        private String charset = "UTF-8";
        private String format = "json";
        private String signType = "RSA2";
    }

    @Bean
    @ConfigurationProperties(prefix = "alipay.client")
    AlipayClientProperties alipayClientProperties() {
        return new AlipayClientProperties();
    }
}
