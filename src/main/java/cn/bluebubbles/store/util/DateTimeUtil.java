package cn.bluebubbles.store.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yibo
 * @date 2019-01-12 15:11
 * @description 时间日期和字符串之间互转
 */
public class DateTimeUtil {

    public static Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String dateTimeStr, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            logger.error("字符串转日期错误", e);
        }
        return date;
    }

    public static String dateToStr(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static Date strToDate(String dateTimeStr) {
        return strToDate(dateTimeStr, STANDARD_FORMAT);
    }

    public static String dateToStr(Date date) {
        return dateToStr(date, STANDARD_FORMAT);
    }
}
