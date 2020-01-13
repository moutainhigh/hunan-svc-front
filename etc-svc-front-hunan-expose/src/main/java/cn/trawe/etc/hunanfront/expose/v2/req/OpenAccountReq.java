package cn.trawe.etc.hunanfront.expose.v2.req;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import cn.trawe.etc.hunanfront.expose.v2.BaseReq;
import lombok.Data;

@Data
public class OpenAccountReq  extends BaseReq{
	
	private String viOwnerType;
    @NotBlank(message = "申请人姓名不能为空")
    @Length(max =25)
    private String viOwnerName;
    @NotBlank(message = "申请人证件类型不能为空")
    private String viOwnerCertType;
    @NotBlank(message = "申请人证件号码不能为空")
    @Length(max =30)
    private String viOwnerCertNo;
    @NotBlank(message = "申请人证件地址不能为空")
    @Length(max =100)
    private String viOwnerCertAddress;
    @NotBlank(message = "申请人手机号不能为空")
    @Length(max =15)
    private String viPhoneNumber;
    @NotBlank(message = "申请人身份证头像页不能为空")
    private String viOwnerCertPicFront;
    @NotBlank(message = "申请人身份证国徽页不能为空")
    private String viOwnerCertPicBack;
	@Override
	public String toString() {
		return "OpenAccountReq [viOwnerType=" + viOwnerType + ", viOwnerName=" + viOwnerName + ", viOwnerCertType="
				+ viOwnerCertType + ", viOwnerCertNo=" + viOwnerCertNo + ", viOwnerCertAddress=" + viOwnerCertAddress
				+ ", viPhoneNumber=" + viPhoneNumber + "]";
	}
    
    


}
