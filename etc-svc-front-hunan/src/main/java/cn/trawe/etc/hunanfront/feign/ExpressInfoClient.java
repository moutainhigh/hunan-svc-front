package cn.trawe.etc.hunanfront.feign;

import feign.hystrix.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import cn.trawe.etc.hunanfront.entity.ExpressInfo;

import java.util.List;

/**
 * @author Jiang Guangxing
 */
@FeignClient(url = "https://detail.i56.taobao.com/xml/cpcode_detail_list.xml", name = "expressInfoClient", fallbackFactory = ExpressInfoClient.ExpressInfoFallbackFactory.class)
public interface ExpressInfoClient {

    @GetMapping(produces = MediaType.APPLICATION_XHTML_XML_VALUE)
    List<ExpressInfo> expressInfos();

    @Component
    class ExpressInfoFallbackFactory implements FallbackFactory<ExpressInfoClient> {

        @Override
        public ExpressInfoClient create(Throwable throwable) {
            return () -> null;
        }
    }
}
