package com.lzx.strangermatching.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName: DateUtil
 * @Description:
 * @Author: LZX
 * @Date: 2024/6/20 9:24
 */
public class DateUtil {
    public static String createNowDate() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        return now.format(formatter);
    }
}
