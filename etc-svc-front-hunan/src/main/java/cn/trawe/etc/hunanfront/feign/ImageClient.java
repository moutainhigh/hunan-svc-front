package cn.trawe.etc.hunanfront.feign;

import cn.trawe.pay.common.etcmsg.EtcObjectResponse;
import feign.hystrix.FallbackFactory;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Jiang Guangxing
 */
//@FeignClient(url = "${image.url}", name = "imageClient", fallbackFactory = ImageClient.ImageFallbackFactory.class)
public interface ImageClient {
    @PostMapping("/image/upload")
    EtcObjectResponse<UploadImages> uploadImages(@RequestBody UploadImagesReq req);

    @Component
    class ImageFallbackFactory implements FallbackFactory<ImageClient> {
        @Override
        public ImageClient create(Throwable throwable) {
            return null;
        }
    }

    @Data
    class UploadImagesReq {
        private String orderId;
    }

    @Data
    class UploadImages {
        private List<Image> images;

        @Data
        public static class Image {
            private String savePath;
            private int imageSize;
            private int bizType;
            private String mediaType;
        }
    }
}
