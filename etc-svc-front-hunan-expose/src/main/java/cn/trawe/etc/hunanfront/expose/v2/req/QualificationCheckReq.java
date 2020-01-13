package cn.trawe.etc.hunanfront.expose.v2.req;

import javax.validation.constraints.NotBlank;

import cn.trawe.etc.hunanfront.expose.v2.BaseReq;
import lombok.Data;

@Data
public class QualificationCheckReq   extends BaseReq{
	
	@NotBlank(message = "车牌号不能为空")
    private String viNumber;
    @NotBlank(message = "车牌颜色不能为空")
    private String viPlateColor;
    @NotBlank(message = "客户类型不能为空")
    private String userProperties;
    @NotBlank(message = "用户ID不能为空")
    private String userNo;
    
    
    

}
