//package cn.trawe.etc.hunanfront.config;
//
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.ptc.board.flowlimit.FlowLimiterManager;
//import com.ptc.board.flowlimit.FlowLimiterService;
//import com.ptc.board.flowlimit.config.DefaultFlowLimiterConfig;
//import com.ptc.board.flowlimit.router.DefaultLimiterFactoryImp;
//
//@Configuration
//@EnableAutoConfiguration
//public class FlowLimitConfig {
//
//	/**
//	 * 加载限流配置
//	 */
//	@Bean
//	@ConfigurationProperties(prefix = "flowlimit-test")
//	DefaultFlowLimiterConfig singleFlowLimiterConfig() {
//		// 默认加载yml配置文件中的配置,如果需要动态修改配置,请自身实现从数据库加载,并赋值到此bean
//		return new DefaultFlowLimiterConfig();
//	}
//
//	@Bean
//	FlowLimiterService singleLimitService() {
//		return new FlowLimiterManager().create(singleFlowLimiterConfig(), new DefaultLimiterFactoryImp());
//	}
//
//}
