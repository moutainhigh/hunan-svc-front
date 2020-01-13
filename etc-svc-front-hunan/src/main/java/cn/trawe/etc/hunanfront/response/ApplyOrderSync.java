package cn.trawe.etc.hunanfront.response;

import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Jiang Guangxing
 */
@Getter
@Setter
@Accessors(chain = true)
public class ApplyOrderSync extends BaseResponseData {
    private String orderId;
    private String outBizNo;
    private String message;
    private String orderStatus;
}
