package cn.trawe.etc.hunanfront.response;

import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import lombok.Data;

/**
 * @author Kevis
 * @date 2019/5/9
 */
@Data
public class ApplyOrderQueryResponse extends BaseResponseData {
    private String orderId;
    private String outBizNo;
    private String orderStatus;
    private String censorInfo;
    private String orderUpdateTime;
    private String cardNo;
    private String deviceNo;
    private String deviceStatus;
    private String deliveryName;
    private String deliveryNo;
    private String deviceType;
    private String cardExpiryDate;
    private String deviceExpiryDate;
    private String accountNo;
    private String auditPerson;
}
