package cn.trawe.etc.hunanfront.feign.entity.hunan;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CarServiceResp {

    private String success;

    private String errorCode;

    private CarServiceModule module;
    
}
