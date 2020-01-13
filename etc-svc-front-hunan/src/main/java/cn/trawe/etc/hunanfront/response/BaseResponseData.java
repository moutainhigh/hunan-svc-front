//package cn.trawe.etc.hunanfront.response;
//
//import lombok.Data;
//import lombok.experimental.Accessors;
//import lombok.extern.slf4j.Slf4j;
//
//
///**
// * @author Jiang Guangxing
// */
//@Data
//@Slf4j
//@Accessors(chain = true)
//public class BaseResponseData {
//    private String success;
//    private String errorCode;
//    private String errorMsg;
//
//    public enum Success {
//        SUCCEED {
//            @Override
//            public String toString() {
//                return "1";
//            }
//        },
//        FAILED {
//            @Override
//            public String toString() {
//                return "0";
//            }
//        }
//    }
//
//    public enum ErrorCode {
//        SUCCEED {
//            @Override
//            public String toString() {
//                return "100";
//            }
//        },
//        PARAMS_ERROR {
//            @Override
//            public String toString() {
//                return "101";
//            }
//        },
//        SYSTEM_ERROR {
//            @Override
//            public String toString() {
//                return "102";
//            }
//        },
//        OTHER_ERROR {
//            @Override
//            public String toString() {
//                return "103";
//            }
//        }
//    }
//}
