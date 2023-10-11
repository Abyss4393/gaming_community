package cn.abyss4393.utils.timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className TimeStampUtil
 * @description TODO
 * @date 5/9/2023
 */
public class TimeStampUtil {

    /**
     * 返回一个完整的纪年日期时间戳(年月日星期毫分秒 )
     *
     * @return 时间戳
     */
    public static String getIntactTimestamp() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd EEEE HH:mm:ss");
        return dateFormat.format(date);
    }


    /**
     * 返回一个标准的纪年日期时间戳(年月日毫分秒 )
     *
     * @return 时间戳
     */
    public static String getTimestamp() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    /**
     * 返回一个简易的纪年日期时间戳(年月日)
     *
     * @return 时间戳
     */
    public static String getSingleTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd")
                .format(new Date(System.currentTimeMillis()));
    }

    public static long getTimeMillis() {
        return System.currentTimeMillis();
    }
}
