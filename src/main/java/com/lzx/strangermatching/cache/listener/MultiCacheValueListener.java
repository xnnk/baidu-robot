package com.lzx.strangermatching.cache.listener;

/**
 * @InterfaceName: KeywordCacheValueListener
 * @Description: 多键值对缓存
 * @Author: LZX
 * @Date: 2024/6/22 16:08
 */
public interface MultiCacheValueListener<K, V> {
    void onChange(K key, V oldValue, V newValue);
}
