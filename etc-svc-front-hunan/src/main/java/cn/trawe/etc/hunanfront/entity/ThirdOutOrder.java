package cn.trawe.etc.hunanfront.entity;


import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 * @author Jiang Guangxing
 */
@Data
@Table(name="third_out_order")
public class ThirdOutOrder {
	
	@Id
    private long id;
    private String orderNo ;
    private String outOrderId;
    private String outType;
    private String thirdId;
    private String bankCode;

}
