package cn.trawe.etc.hunanfront.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Kevis
 * @date 2019/5/9
 */
@Data
public class ApplyOrderQueryRequest {
    @NotBlank(message = "orderId不能为空")
    private String orderId;
    private String outBizNo;
}
