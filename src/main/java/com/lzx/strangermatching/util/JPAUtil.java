package com.lzx.strangermatching.util;

public class JPAUtil {

    public static String like(String column){
        StringBuilder sb = new StringBuilder("%"+column+"%");
        return sb.toString();
    }
}