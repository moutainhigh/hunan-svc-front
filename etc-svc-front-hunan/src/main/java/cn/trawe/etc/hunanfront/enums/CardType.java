package cn.trawe.etc.hunanfront.enums;

import cn.trawe.pay.expose.enums.EtcApplyCardType;

/**
 * @author Jiang Guangxing
 */
public enum CardType {
    OTHER,
    ALIPAY_CREDIT,
    STORED_VALUE_CARD;

    public static Integer warp(String cardTypeStr) {
        CardType[] cardTypes = CardType.values();
        int cardTypeIntValue = Integer.valueOf(cardTypeStr);
        if (cardTypeIntValue < 0 || cardTypeIntValue >= cardTypes.length)
            return null;
        CardType cardType = cardTypes[cardTypeIntValue];
        switch (cardType) {
            case ALIPAY_CREDIT:
                return EtcApplyCardType.ALIPAY_CREDIT.ordinal();
            case STORED_VALUE_CARD:
                return EtcApplyCardType.STORED_VALUE_CARD.ordinal();
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(warp("0"));
        System.out.println(warp("1"));
        System.out.println(warp("2"));
        System.out.println(warp("3"));
    }
}
