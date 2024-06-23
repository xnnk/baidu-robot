package com.lzx.strangermatching.cache;

import com.lzx.strangermatching.cache.listener.MultiCacheValueListener;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @ClassName: MultiCacheValue
 * @Description: 多键值对缓存模板
 * @Author: LZX
 * @Date: 2024/6/22 20:11
 */
@Slf4j
public class MultiCacheValue<K, V> {

    private final long deprecatedTime;

    private final Function<K, V> supplier;

    private final ConcurrentMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();

    @Setter
    private MultiCacheValueListener<K, V> cacheValueListener;

    public MultiCacheValue(long deprecatedTime, Function<K, V> supplier) {
        this.deprecatedTime = deprecatedTime;
        this.supplier = supplier;
    }

//    public V getAndUpdate(K key) {
//        CacheEntry<V> entry = cache.get(key);
//        if (entry == null || entry.isExpired()) {
//            // apply(): 调用缓存次数+1, times++
//            V newValue = supplier.apply(key);
//            if (entry != null) {
//                V oldValue = entry.value;
//                if (!Objects.equals(oldValue, newValue) && cacheValueListener != null) {
//                    try {
//                        cacheValueListener.onChange(key, oldValue, newValue);
//                    } catch (Throwable throwable) {
//                        log.error("cache listener execute error", throwable);
//                    }
//                }
//            }
//            entry = new CacheEntry<>(newValue);
//            cache.put(key, entry);
//        }
//        return entry.value;
//    }

    public V getAndUpdate(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            // apply(): 调用缓存次数+1, times++
            V newValue = supplier.apply(key);

            V oldValue = null;
            if (entry != null) {
                oldValue = entry.value;
                if (!Objects.equals(oldValue, newValue) && cacheValueListener != null) {
                    try {
                        cacheValueListener.onChange(key, oldValue, newValue);
                    } catch (Throwable throwable) {
                        log.error("cache listener execute error", throwable);
                    }
                }
            }

            entry = new CacheEntry<>(newValue);
            cache.put(key, entry);
        }
        else {
            V newValue = supplier.apply(key);

            entry = new CacheEntry<>(newValue);
            cache.put(key, entry);
        }
        return entry.value;
    }

    public class CacheEntry<T> {
        private final T value;
        private final Date updateTime;

        CacheEntry(T value) {
            this.value = value;
            this.updateTime = new Date();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - updateTime.getTime() > deprecatedTime;
        }
    }

}
