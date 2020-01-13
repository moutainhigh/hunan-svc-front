package cn.trawe.etc.hunanfront.expose.v2.req;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import cn.trawe.etc.hunanfront.expose.v2.BaseReq;
import lombok.Data;

@Data
public class VehicleSaveReq extends BaseReq {
	
	@NotBlank(message = "用户ID不能为空")
	@Length(max =30,message ="长度不能大于30")
	private String userNo;
	@NotBlank(message = "车辆ID不能为空")
	@Length(max =50,message ="长度不能大于50")
	private String vehicleId;
	@NotBlank(message = "客户类型不能为空")
	private String userProperties;
	
	@NotBlank(message = "车牌号不能为空")
    private String viNumber;
    @NotBlank(message = "车牌颜色不能为空")
    @Min(value = 0, message = "车牌颜色不能小于0")
    private String viPlateColor;
    @NotBlank(message = "车辆类型不能为空")
    private String viType;
    @NotBlank(message = "⻋辆所有人不能为空")
    //@Length(max =20,message ="⻋辆所有人长度不能大于25")
    private String viOwnerName;
    //@NotBlank(message = "⻋辆所有人地址不能为空")
    //@Length(max =100,message ="⻋辆所有人地址长度不能大于100")
    private String viOwnerAddress;
    
    @NotBlank(message = "⻋辆使用性质不能为空")
    @Length(max =1,message ="⻋辆使用性质长度必须为1")
    private String viUseType;
    
    @NotBlank(message = "⻋辆品牌型号不能为空")
    @Length(max =30,message ="⻋辆品牌型号长度不能大于30")
    private String viModelName;
    
    @NotBlank(message = "⻋辆识别代号不能为空")
    @Length(max =25,message ="⻋辆识别代号长度不能大于25")
    private String viVin;
    
    @NotBlank(message = "⻋辆发动机号码不能为空")
    @Length(max =16,message ="⻋辆发动机号码长度不能大于16")
    private String engineNo;
    @NotBlank(message = "行驶证注册日期不能为空")
    //@Pattern(regexp = "^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$")
    private String viStartTime;
    @NotBlank(message = "行驶证发证日期不能为空")
    //@Pattern(regexp = "^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$")
    private String viGrantTime;
    
    private String viLicenseNo;
    
    //@Min(value = 0, message = "核定载人数不能小于0")
    private String viAc;
    
    private String viTotalMass;
    private String viReadinessMass;
    private String viLength;
    private String viHeight;
    private String viWidth;
    private String viTractionMass;
    @Length(max =100,message ="检验记录长度不能大于100")
    private String viInspectionRecord;
    @NotBlank(message = "行驶证副页不能为空")
    private String viVehiclePicBack;
    @NotBlank(message = "行驶证主页不能为空")
    private String viVehiclePicFront;

    private int viVehicleWheels;
 
    private int viVehicleAxles;
    @NotBlank(message = "客货类型不能为空")
    private String viVehicleType;

	@Override
	public String toString() {
		return "VehicleSaveReq [userNo=" + userNo + ", vehicleId=" + vehicleId + ", userProperties=" + userProperties
				+ ", viNumber=" + viNumber + ", viPlateColor=" + viPlateColor + ", viType=" + viType + ", viOwnerName="
				+ viOwnerName + ", viOwnerAddress=" + viOwnerAddress + ", viUseType=" + viUseType + ", viModelName="
				+ viModelName + ", viVin=" + viVin + ", engineNo=" + engineNo + ", viStartTime=" + viStartTime
				+ ", viGrantTime=" + viGrantTime + ", viLicenseNo=" + viLicenseNo + ", viAc=" + viAc + ", viTotalMass="
				+ viTotalMass + ", viReadinessMass=" + viReadinessMass + ", viLength=" + viLength + ", viHeight="
				+ viHeight + ", viWidth=" + viWidth + ", viTractionMass=" + viTractionMass + ", viInspectionRecord="
				+ viInspectionRecord + ", viVehicleWheels=" + viVehicleWheels + ", viVehicleAxles=" + viVehicleAxles
				+ ", getChannelNo()=" + getChannelNo() + ", getOrderId()=" + getOrderId() + ", getAccountNo()="
				+ getAccountNo() + ", getPassword()=" + getPassword() + "]";
	}
    
    


}
