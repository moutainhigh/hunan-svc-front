package cn.trawe.etc.hunanfront.expose.v2.req;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import cn.trawe.etc.hunanfront.expose.v2.BaseReq;
import lombok.Data;
@Data
public class VehicleSubmitReq extends BaseReq {
	
	@NotBlank(message = "用户ID不能为空")
	@Length(max =30,message ="长度不能大于30")
	private String userNo;
	@NotBlank(message = "车辆ID不能为空")
	@Length(max =50,message ="长度不能大于50")
	private String vehicleId;

}
