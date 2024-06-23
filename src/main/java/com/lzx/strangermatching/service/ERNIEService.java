package com.lzx.strangermatching.service;

/**
 * @InterfaceName: ERNIEService
 * @Description:
 * @Author: LZX
 * @Date: 2024/6/20 9:01
 */
public interface ERNIEService {
    public String invokeERNIETiny_Shopping(String body);

    String getIfPresent(String keyword);
}
