package cn.trawe.etc.hunanfront.request;



import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Jiang Guangxing
 */
@Data
@Accessors(chain = true)
public class ApplyOrderSyncReq {
	
	@NotBlank(message = "渠道编号不能为空")
	private String channelNo;
    @NotBlank(message = "申请单同步操作不能为空")
    @Min(value = 0, message = "申请单同步操作不能小于0")
    private String opType;
    
    private String phoneNumber;
    
    private String accountNo;
    
    private String password;
    /**
     * 申请单编号
     */
    @NotBlank(message = "申请单编号不能为空")
    private String orderId;
    /**
     * 申请单外部编号
     */
    private String outBizNo;
    /**
     * 申请时间 2019-14-17 22:27:34
     */
    private String orderCreateTime;
    /**
     * 同步给业主的申请单状态
     * 0:未提交;(支付宝etc服务第一次同 步给业主就以这个状态) 1:审核中(业主同步给支付宝); 2:驳回(业主同步给支付宝); 3:不通过(业主同步给支付宝); 4:通过(业主同步给支付宝); 5:撤销(支付宝同步给业主);
     */
    private String orderStatus;
    /**
     * 审核意⻅
     */
    private String censorInfo;
    /**
     * 最近更新时间
     */
    private String orderUpdateTime;
    /**
     * 发行方编号
     */
    private String sellerId;
    /**
     * 发行方名称
     */
    private String sellerName;
    /**
     * ⻋主信息。jsonString，字段说明⻅ 《⻋主信息》
     */
    @NotNull(message = "⻋主信息不能为空")
    private ViOwnerInfo viOwnerInfo;
    /**
     * ⻋辆信息。jsonString， 字段说明⻅《⻋辆信息》
     */
    @NotNull(message = "车辆信息不能为空")
    private ViInfo viInfo;
    /**
     * 收货信息。jsonString， 字段说明⻅《收货信息》
     */
    @NotNull(message = "收货信息不能为空")
    private DeliveryInfo deliveryInfo;
    /**
     * 发票信息。jsonString, 字段说⻅《发票信息》
     */
    private InvoiceInfo invoiceInfo;
    /**
     * 卡类型 1:记账卡; 2:储值卡
     */
    //@NotBlank(message = "卡类型不能为空")
    //@Min(value = 0, message = "卡类型不能小于0")
    private String cardType;
    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 设备类型 暂时无法确定有哪些类型
     */
    private String deviceType;
    /**
     * 设备状态
     * 0:未发货 1:已发货(由业主同步给支付宝) 2:运输中(由业主同步给支付宝) 3:已签收(待定) 4:已二发(由业主同步给支付宝) 5:已激活(由业主同步给支付宝) 6:退货中(由业主同步给支付宝) 7:已退货(由业主同步给支付宝) 8:换货中(由业主同步给支付宝)
     */
    private String deviceStatus;
    /**
     * 设备编号
     */
    private String deviceNo;
    /**
     * 权益列表;jsonArrayString。 包含(权益编号，外部权益编号，权 益名称)
     * [{"interest_no":"324", "interest_out_no":"f007", "interest_name":"免服务费"}]
     */
    private String interests;
    /**
     * 设备费用
     */
    //@NotBlank(message = "设备费用不能为空")
    //@Min(value = 0, message = "设备费用不能小于0")
    private String deviceAmount;
    /**
     * 服务费
     */
    @NotBlank(message = "服务费不能为空")
    //@Min(value = 0, message = "服务费不能小于0")
    private String serviceAmount;
    /**
     * 物流费用
     */
    //@NotBlank(message = "物流费用不能为空")
    //@Min(value = 0, message = "服务费不能小于0")
    private String deliveryAmount;
    /**
     * 订单总金额
     */
    //@NotBlank(message = "订单总金额不能为空")
    //@Min(value = 0, message = "订单总金额不能小于0")
    private String totalAmount;
    /**
     * 支付交易流水号
     */
    private String tradeNo;
    /**
     * 支付状态
     */
    private String payStatus;
    /**
     * 扣款协议编号
     */
    //@NotBlank(message = "扣款协议编号不能为空")
    private String agreementNo;
    /**
     * 代扣商户支付宝账号
     */
    private String merchantAccount;
    /**
     * 代扣商户支付宝账号pid
     */
    private String merchantPid;
    /**
     * 扣款支付宝账号
     */
    private String buyerAccount;
    /**
     * 扣款支付宝账号uid
     */
    @NotBlank(message = "userId不能为空")
    private String buyerUid;
    /**
     * 是否同意发行协议
     */
    private String sellerAgreement;
    /**
     * 发行协议版本
     */
    private String sellerAgreementVersion;
    /**
     * 扩展参数
     */
    private String extend;
    /**
     * 三方订单同步记录主键
     */
    private long recordId;
    
    private String installStatus;

    /**
     * ⻋主信息
     */
    @Data
    public static class ViOwnerInfo {
        private String viOwnerType;
        @NotBlank(message = "申请人姓名不能为空")
        private String viOwnerName;
        private String viOwnerCertType;
        @NotBlank(message = "申请人证件号码不能为空")
        private String viOwnerCertNo;
        private String ownerCertImgFront;
        private String ownerCertImgBack;
    }

    /**
     * 车辆信息
     */
    @Data
    public static class ViInfo {
        @NotBlank(message = "车牌号不能为空")
        private String viNumber;
        @NotBlank(message = "车牌颜色不能为空")
        @Min(value = 0, message = "车牌颜色不能小于0")
        private String viPlateColor;
        @NotBlank(message = "车辆类型不能为空")
        private String viType;
        @NotBlank(message = "⻋辆所有人不能为空")
        private String viOwnerName;
        
        private String viOwnerAddress;
        @NotBlank(message = "⻋辆使用性质不能为空")
        private String viUseType;
        @NotBlank(message = "⻋辆品牌型号不能为空")
        private String viModelName;
        @NotBlank(message = "⻋辆识别代号不能为空")
        private String viVin;
        @NotBlank(message = "⻋辆发动机号码不能为空")
        private String engineNo;
        @NotBlank(message = "行驶证注册日期不能为空")
        //@Pattern(regexp = "^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$")
        private String viStartTime;
        @NotBlank(message = "行驶证发证日期不能为空")
        //@Pattern(regexp = "^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$")
        private String viGrantTime;
        private String viLicenseNo;
        @NotBlank(message = "核定载人数不能为空")
        @Min(value = 0, message = "核定载人数不能小于0")
        private String viAc;
        private String viTotalMass;
        private String viReadinessMass;
        private String viLength;
        private String viHeight;
        private String viWidth;
        private String viTractionMass;
        private String viInspectionRecord;
        private String viLicenseImgFront;
        private String viLicenseImgBack;
        private String viImgAngle;
    }

    /**
     * 收货信息
     */
    @Data
    public static class DeliveryInfo {
        private String provinceCode;
        private String provinceName;
        private String cityCode;
        private String cityName;
        private String districtCode;
        private String districtName;
        @NotBlank(message = "收货信息详细地址不能为空")
        private String address;
        @NotBlank(message = "收货人不能为空")
        private String contactName;
        @NotBlank(message = "收货人联系方式不能为空")
        private String contactTel;
        private String deliveryName;
        private String deliveryCode;
    }

    /**
     * 发票信息
     */
    @Data
    public static class InvoiceInfo {
        //@Min(value = 0, message = "是否开具发票字段不能小于0")
        private String needInvoice;
        private String invoiceType;
       // @NotBlank(message = "发票抬头类型不能为空")
        //@Min(value = 1, message = "发票抬头类型不能小于1")
        private String invoiceTitleType;
        private String invoiceTitle;
        private String invoiceContent;
        private String phone;
        private String address;
        private String bankName;
        private String bankAccount;
        //@NotBlank(message = "发票信息邮箱不能为空")
        private String email;
        //税号
        private String dutyNo;
    }

   
}
