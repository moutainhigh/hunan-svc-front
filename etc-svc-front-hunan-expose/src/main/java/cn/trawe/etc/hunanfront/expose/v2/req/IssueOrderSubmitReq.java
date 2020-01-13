package cn.trawe.etc.hunanfront.expose.v2.req;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import cn.trawe.etc.hunanfront.expose.v2.BaseReq;
import lombok.Data;


@Data
public class IssueOrderSubmitReq  extends BaseReq{
	
	@NotBlank(message = "安装类型不能为空")
	private String installStatus;
	
	@NotBlank(message = "用户ID不能为空")
	@Length(max =30,message ="长度不能大于30")
	private String userNo;
	@NotBlank(message = "车辆ID不能为空")
	@Length(max =50,message ="长度不能大于50")
	private String vehicleId;
	@NotBlank(message = "省份信息不能为空")
    private String provinceName;
	@NotBlank(message = "城市信息不能为空")
    private String cityName;
	@NotBlank(message = "区信息不能为空")
    private String districtName;
    @NotBlank(message = "详细地址不能为空")
    @Length(max =60,message ="长度不能大于60")
    private String address;
    @NotBlank(message = "收货人不能为空")
    @Length(max =25,message ="长度不能大于25")
    private String contactName;
    @NotBlank(message = "收货人联系方式不能为空")
    @Length(max =15,message ="长度不能大于15")
    private String contactTel;
    @NotBlank(message = "绑定支付渠道类型不能为空")
    @Length(max =10,message ="长度不能大于10")
    private String signChannel;
    @Length(max =30,message ="长度不能大于30")
    private String signAccount;
    
    private int signCardType;

}
