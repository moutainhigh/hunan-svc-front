package cn.trawe.etc.hunanfront.feign.entity.hunan;

import lombok.Data;

@Data
public class HunanGatewayBaseResp {

    private Integer code;
    private String msg;
    private String errorCode;
}
