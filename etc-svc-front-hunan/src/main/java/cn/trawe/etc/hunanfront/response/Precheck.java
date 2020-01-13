package cn.trawe.etc.hunanfront.response;

import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Jiang Guangxing
 */
@Getter
@Setter
@Accessors(chain = true)
public class Precheck extends BaseResponseData {
    private String checkResult;
    private String viNumber;
    private String viPlateColor;
    private String accountResult;

    public enum checkResult {
        /**
         * 检测通过
         */
        PASS,
        /**
         * 检查不通过
         */
        UNPASS,
        /**
         * 结果未知
         */
        UNKNOWN
    }
}
