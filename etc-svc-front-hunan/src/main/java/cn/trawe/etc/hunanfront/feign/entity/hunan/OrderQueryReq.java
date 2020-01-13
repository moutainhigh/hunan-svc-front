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
 * 3.21 订单查询请求
 */
public class OrderQueryReq {

    @XmlElement(name = "ListNo")
    @ApiModelProperty(value = "外部流水号 ")
    private String listNo  ;

    @XmlElement(name = "Channel")
    @ApiModelProperty(value = "渠道 ")
    private String channel    ;

    @XmlElement(name = "VehiclePlate")
    @ApiModelProperty(value = "车牌号码 ")
    private String vehiclePlate     ;

    @XmlElement(name = "VehiclePlateColor")
    @ApiModelProperty(value = "车牌颜色  ")
    private Integer vehiclePlateColor      ;





}
