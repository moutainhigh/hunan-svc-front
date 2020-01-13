package cn.trawe.etc.hunanfront.expose.v2;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseResp {

	private String success;
    private String errorCode;
    private String errorMsg;

    public enum Success {
        SUCCEED {
            @Override
            public String toString() {
                return "1";
            }
        },
        FAILED {
            @Override
            public String toString() {
                return "0";
            }
        }
    }

    public enum ErrorCode {
        SUCCEED {
            @Override
            public String toString() {
                return "100";
            }
        },
        PARAMS_ERROR {
            @Override
            public String toString() {
                return "101";
            }
        },
        SYSTEM_ERROR {
            @Override
            public String toString() {
                return "102";
            }
        },
        OTHER_ERROR {
            @Override
            public String toString() {
                return "103";
            }
        },
        TOKEN_ERROR {
            @Override
            public String toString() {
                return "104";
            }
        }
    }
}
