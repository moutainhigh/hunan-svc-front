package cn.trawe.etc.hunanfront.entity;

import lombok.Data;

/**
 * @author Kevis
 * @date 2019/5/10
 */
@Data
public class BlackUser {
    private String cardNo;
    private String deviceNo;
    private String viNumber;
    private String viPlateColor;
    private String viOwnerType;
    private String viOwnerCertType;
    private String viOwnerCertNo;
    private String opReason;
}