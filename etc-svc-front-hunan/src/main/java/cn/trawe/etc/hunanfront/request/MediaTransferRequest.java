package cn.trawe.etc.hunanfront.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Kevis
 * @date 2019/5/8
 */
@Data
public class MediaTransferRequest {
    //@NotBlank(message = "媒体标识不能为空")
    private String mediaId;
    @NotBlank(message = "申请单编号不能为空")
    private String orderId;
    @NotBlank(message = "业务标识不能为空")
    private String bizType;
    @NotBlank(message = "媒体类型不能为空")
    private String mediaType;
    @NotBlank(message = "媒体内容不能为空")
    private String mediaContent;
    @NotBlank(message = "⽤户user_id不能为空")
    private String userId;
	@Override
	public String toString() {
		return "MediaTransferRequest [mediaId=" + mediaId + ", orderId=" + orderId + ", bizType=" + bizType
				+ ", mediaType=" + mediaType + ", userId=" + userId + "]";
	}
	
    
    
    
}
