package cn.trawe.etc.hunanfront.enums;

import lombok.Getter;

/**
 * @author Jiang Guangxing
 */
@Getter
public enum AuditType {
    BEIJING("1101", "1", "4"),
    JIANGSU("3201", "1", "4"),
    JIANGXI("3601", "1", "4"),
    QILU("3801", "1", "4"),
    XINLIAN("3701", "1", "4"),
    HEBEI("1301", "1", "4"),
    JILIN("2201", "1", "4"),
    QINGHAI("6301", "1", "4"),
    CHONGQING("5001", "1", "4"),
    HUNAN("4301", "1", "1"),
    GUIZHOU("5201", "3", "3"),
    TIANJIN("1201", "3", "3");

    AuditType(String ownerCode, String first, String notFirst) {
        this.ownerCode = ownerCode;
        this.first = first;
        this.notFirst = notFirst;
    }

    private String ownerCode;
    private String first;
    private String notFirst;

    public static String getAuditType(String sellerId, boolean firstSubmit) {
        for (AuditType auditType : AuditType.values()) {
            if (auditType.getOwnerCode().equals(sellerId)) {
                if (firstSubmit)
                    return auditType.getFirst();
                else
                    return auditType.getNotFirst();
            }
        }
        return null;
    }

    public static boolean canWarp(String sellerId) {
        for (AuditType auditType : AuditType.values()) {
            if (auditType.getOwnerCode().equals(sellerId)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(getAuditType("1101", true));
        System.out.println(getAuditType("1101", false));
        System.out.println(getAuditType("5201", false));
        System.out.println(getAuditType("5201", true));
    }
}
