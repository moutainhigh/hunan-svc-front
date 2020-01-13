package cn.trawe.etc.hunanfront.entity;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 快递公司信息
 *
 * @author Jiang Guangxing
 */
@Data
@XmlRootElement(name = "item")
public class ExpressInfo {
    @XmlElement
    private String skey;
    @XmlElement
    private Display display;

    @Data
    @XmlRootElement(name = "display")
    public static class Display {
        private String fullname;
        private String shortname;
    }
}

