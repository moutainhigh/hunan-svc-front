package cn.trawe.etc.hunanfront.utils;

import cn.trawe.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Jiang Guangxing
 */
public class DateUtil {
    public static String tenYearsLaterDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, 10);
        return DateUtils.format(calendar.getTime(), DateUtils.YYYY_MM_DD_HH_MM_SS);
    }

    private DateUtil() {
    }
}
