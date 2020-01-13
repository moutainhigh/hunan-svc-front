package cn.trawe.etc.hunanfront.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.ptc.board.flowlimit.FlowLimiterManager;
import com.ptc.board.flowlimit.FlowLimiterService;
import com.ptc.board.flowlimit.config.DefaultFlowLimiterConfig;
import com.ptc.board.flowlimit.router.DefaultLimiterFactoryImp;
import com.ptc.board.flowlimit.web.interceptor.UrlFlowLimiterInterceptor;
import com.ptc.board.flowlimit.web.response.LimitResponseHandler;
import com.ptc.board.flowlimit.web.response.impl.DefaultResponseHandlerFactory;

import cn.trawe.etc.hunanfront.common.JsonLimitResponseHandler;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jiang Guangxing
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
//	@Resource
//	DefaultFlowLimiterConfig flowLimiterConfig;
//
//	@Resource
//	FlowLimiterService limiterService;
	
	
//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//
//		try {
//			// 添加限流拦截器
//			LimitResponseHandler responseHandler = new DefaultResponseHandlerFactory()
//					.create(flowLimiterConfig.getLimitResponse());
//
//			HandlerInterceptor interceptor = new UrlFlowLimiterInterceptor(limiterService, responseHandler);
//
//			// 限流拦截器对所有请求url生效
//			registry.addInterceptor(interceptor).addPathPatterns("/**");
//		} catch (Exception e) {
//			log.error("crash on config interceptor", e);
//		}
//	}
    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        //驼峰转下划线
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCase);
        fastJsonConfig.setSerializeConfig(serializeConfig);
        //序列化格式
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteDateUseDateFormat);
        // 处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);

        fastConverter.setFastJsonConfig(fastJsonConfig);
        return fastConverter;
    }

    @Bean
    public Decoder feignDecoder() {
        return new ResponseEntityDecoder(new SpringDecoder(feignHttpMessageConverter()));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new UnderlineToCamelArgumentResolver());
    }

    private ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
        MappingJackson2XmlHttpMessageConverter xmlConverter = new MappingJackson2XmlHttpMessageConverter();
        xmlConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_XHTML_XML,
                MediaType.APPLICATION_FORM_URLENCODED, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML));
        HttpMessageConverters httpMessageConverters = new HttpMessageConverters(xmlConverter, this.fastJsonHttpMessageConverter());
        return () -> httpMessageConverters;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        FlowLimiterService service = new FlowLimiterManager().create(flowLimiterConfig(), new DefaultLimiterFactoryImp());
        HandlerInterceptor interceptor = new UrlFlowLimiterInterceptor(service, limitResponseHandler());
        registry.addInterceptor(interceptor).addPathPatterns("/**");
    }

    @Bean
    LimitResponseHandler limitResponseHandler() {
        return new JsonLimitResponseHandler();
    }

    @Bean
    @ConfigurationProperties(prefix = "flowlimit-test")
    DefaultFlowLimiterConfig flowLimiterConfig() {
        return new DefaultFlowLimiterConfig();
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
 
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
 
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
 
    }


}
