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
 * 3.12 订单设备登记请求
 */
public class OrderEquipmentReq /* extends BaseReq */{

    @XmlElement(name = "ListNo")
    @ApiModelProperty(value = "外部流水号 ")
    private String listNo  ;

    @XmlElement(name = "RepairType")
    @ApiModelProperty(value = "补换类型  ")
    private Integer repairType   ;

    @XmlElement(name = "FaceCardNum")
    @ApiModelProperty(value = "新卡片表面号   ")
    private String faceCardNum    ;

    @XmlElement(name = "PhyCardNum")
    @ApiModelProperty(value = "物理卡号")
    private String phyCardNum     ;

    @XmlElement(name = "CardType")
    @ApiModelProperty(value = "卡片类型")
    private Integer cardType      ;

    @XmlElement(name = "Version")
    @ApiModelProperty(value = "卡片版本")
    private Integer version       ;

    @XmlElement(name = "SerialNumber")
    @ApiModelProperty(value = "新合同序列号 ")
    private String serialNumber        ;

    @XmlElement(name = "ObuId")
    @ApiModelProperty(value = "物理号 ")
    private String obuId         ;

    @XmlElement(name = "Supplier")
    @ApiModelProperty(value = "服务提供商/发 行方标识 ")
    private String supplier          ;


    @XmlElement(name = "ContractType")
    @ApiModelProperty(value = "合同类型 ")
    private Integer contractType           ;


    @XmlElement(name = "ContractVersion")
    @ApiModelProperty(value = "合同版本  ")
    private Integer contractVersion            ;








}
