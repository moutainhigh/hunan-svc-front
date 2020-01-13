package cn.trawe.etc.hunanfront.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author Jiang Guangxing
 */
@Data
@Accessors(chain = true)
public class ThirdOrderSycnRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String reqJson = "";
    private int retryTimes;
    private Date createTime = new Date();
    private Date updateTime = new Date();
}
