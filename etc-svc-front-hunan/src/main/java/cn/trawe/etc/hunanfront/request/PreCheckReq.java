package cn.trawe.etc.hunanfront.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Jiang Guangxing
 */
@Data
public class PreCheckReq {
    @NotBlank(message = "⻋牌号不能为空")
    private String viNumber;
    @NotBlank(message = "⻋牌颜色不能为空")
    private String viPlateColor;
    //@NotBlank(message = "支付宝用户uid不能为空")
    private String userId;
    
	
	private String channelNo;
	
	private String opType;
	
	private String account;
	
	private String password;
	
	private String accountResult;
	
	private String message;
	
}
