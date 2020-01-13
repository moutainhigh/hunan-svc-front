package cn.trawe.etc.hunanfront.feign.entity.hunan;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CarServiceReq {

    //车牌号码
	@NotBlank(message = "车牌号不能为空")
    private String vehPlate;

    //车牌颜色
	@NotNull(message = "车牌颜色不能为空")
    private Integer vehPlateColor;
    
	@NotNull(message = "客货类型不能为空")
    private int  viVehicleType;

}
