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
 * 3.23 发行业务 (3.24 拓展业务)  订单审核请求
 */
public class OrderExamineReq {

    @XmlElement(name = "ListNo")
    @ApiModelProperty(value = "外部流水号 ")
    private String listNo  ;

    @XmlElement(name = "Result")
    @ApiModelProperty(value = "审核结果  ")
    private Integer result    ;

    @XmlElement(name = "Reason")
    @ApiModelProperty(value = "审核原因")
    private String reason    ;






}
