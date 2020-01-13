package cn.trawe.etc.hunanfront.feign.entity.hunan;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Setter
@Getter
@ToString
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Body")
/**
 * 3.7 订单提交请求
 */
public class OrderSubmitReq  {

    @XmlElement(name = "ListNo")
    @ApiModelProperty(value = "外部流水号   ")
    private String listNo  ;

    @XmlElement(name = "BussType")
    @ApiModelProperty(value = "业务类型   ")
    private Integer bussType   ;

    @XmlElement(name = "CancelReason")
    @ApiModelProperty(value = "注销原因   ")
    private Integer cancelReason    ;

    @XmlElement(name = "CancelRemark")
    @ApiModelProperty(value = "备注 ")
    private String cancelRemark     ;

    @XmlElement(name = "FaceCardNum")
    @ApiModelProperty(value = "卡片表面号  ")
    private String faceCardNum      ;

    @XmlElement(name = "SerialNumber")
    @ApiModelProperty(value = "OBU 序列号   ")
    private String serialNumber  ;


    @XmlElement(name = "ReceiveName")
    @ApiModelProperty(value = "收货人 ")
    private String receiveName   ;


    @XmlElement(name = "ReceiveAddress")
    @ApiModelProperty(value = "收货地址  ")
    private String receiveAddress    ;

    @XmlElement(name = "ReceiveTel")
    @ApiModelProperty(value = "收货手机号码 ")
    private String receiveTel     ;

    @XmlElement(name = "PicData")
    @ApiModelProperty(value = "手持证件图 片")
    private String picData      ;







}
