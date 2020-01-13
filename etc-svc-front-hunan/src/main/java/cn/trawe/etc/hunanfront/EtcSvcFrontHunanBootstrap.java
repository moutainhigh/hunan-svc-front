package cn.trawe.etc.hunanfront;


import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.ptc.board.log.proxy.BizDigestAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jiang Guangxing
 */
@SpringBootApplication(scanBasePackages = {
        "cn.trawe",
        "com.ptc.board.log"
}, scanBasePackageClasses = {BizDigestAspect.class})
@EnableEurekaClient
@EnableFeignClients(basePackages = "cn.trawe")
@EnableApolloConfig
@EnableScheduling
@EnableAutoConfiguration
public class EtcSvcFrontHunanBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(EtcSvcFrontHunanBootstrap.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
