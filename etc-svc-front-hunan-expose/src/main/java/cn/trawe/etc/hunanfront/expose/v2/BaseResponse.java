package cn.trawe.etc.hunanfront.expose.v2;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Kevis
 * @date 2019/5/7
 */
@Data
@Accessors(chain = true)
public class BaseResponse<T> {
    private T response;
    private String sign;
}
