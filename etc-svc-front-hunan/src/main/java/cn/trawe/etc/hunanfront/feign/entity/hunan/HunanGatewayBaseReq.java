package cn.trawe.etc.hunanfront.feign.entity.hunan;

import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;

@Data
public class HunanGatewayBaseReq {
	
	@XmlTransient
	private String note1;
	
	@XmlTransient
	private String orderNo;
	
	

}
