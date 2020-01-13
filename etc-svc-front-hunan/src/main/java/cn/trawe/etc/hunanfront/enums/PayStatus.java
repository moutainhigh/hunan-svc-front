package cn.trawe.etc.hunanfront.enums;

/**
 * @author Jiang Guangxing
 */
public enum PayStatus {
    /**
     * 订单支付初始状态
     */
    WAITING,
    /**
     * 由支付消息触发？
     */
    PAYED,
    /**
     * 退款中
     */
    REFUNDING,
    /**
     * 退款消息触发？
     */
    REFUND
}
