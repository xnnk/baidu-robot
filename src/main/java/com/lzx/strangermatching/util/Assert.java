package com.lzx.strangermatching.util;

import org.apache.commons.lang3.StringUtils;

import javax.transaction.SystemException;

/**
 * 数据校验工具类
 */
public class Assert {

    public static void isBlank(String str, String message) throws SystemException {
        if (StringUtils.isBlank(str)) {
            throw new SystemException(message);
        }
    }

    public static void isNull(Object object, String message) throws SystemException {
        if (object == null) {
            throw new SystemException(message);
        }
    }
}
