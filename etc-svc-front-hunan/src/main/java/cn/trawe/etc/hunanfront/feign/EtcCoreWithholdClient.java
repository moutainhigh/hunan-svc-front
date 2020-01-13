package cn.trawe.etc.hunanfront.feign;

import feign.hystrix.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import cn.trawe.etc.hunanfront.request.UserBlacklistSyncRequest;
import cn.trawe.etc.hunanfront.response.UserBlacklistSyncResponse;

/**
 * @author Jiang Guangxing
 */
//@FeignClient(name = "etc-core-withhold", fallbackFactory = EtcCoreWithholdClient.ApiFallbackFactory.class)
public interface EtcCoreWithholdClient {

    @PostMapping("/etc/user_blacklist_sync")
    UserBlacklistSyncResponse userBlacklistSync(@RequestBody UserBlacklistSyncRequest req, @RequestHeader String token);

    @Component
    class ApiFallbackFactory implements FallbackFactory<EtcCoreWithholdClient> {
        @Override
        public EtcCoreWithholdClient create(Throwable throwable) {
            return (req, token) -> null;
        }
    }
}
