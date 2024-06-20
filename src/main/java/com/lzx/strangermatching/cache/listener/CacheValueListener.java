package com.lzx.strangermatching.cache.listener;

/**
 * @ClassName: CacheValueListener
 * @Description:
 * @Author: LZX
 * @Date: 2024/6/19 18:11
 */
public interface CacheValueListener<T> {
    void onChange(T oldValue, T newValue);
}
