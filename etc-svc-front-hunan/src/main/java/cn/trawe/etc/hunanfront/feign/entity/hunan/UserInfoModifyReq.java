package cn.trawe.etc.hunanfront.feign.entity.hunan;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
@Setter
@Getter
@ToString
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Body")
public class UserInfoModifyReq /* extends BaseReq */{   
	
//	@XmlElement(name = "Note1")
//    @ApiModelProperty(value = "用户编号")
//	private String note1;
//	@XmlElement(name = "OrderNo")
//	@ApiModelProperty(value = "用户编号")
//	private String orderNo;
	 
	@XmlElement(name = "UserNo")
    @ApiModelProperty(value = "用户编号")
	private String userNo;
	
	@XmlElement(name = "IdType")
    @ApiModelProperty(value = "证件类型   0-身份证")
    private Integer idType;

    @XmlElement(name = "IdNum")
    @ApiModelProperty(value = "证件号码")
    private String idNum;

    @XmlElement(name = "Name")
    @ApiModelProperty(value = "客户姓名")
    private String name;

    @XmlElement(name = "Tel")
    @ApiModelProperty(value = "手机号码 ")
    private String tel;

    @XmlElement(name = "Address")
    @ApiModelProperty(value = "地址  ")
    private String address;

 


}
