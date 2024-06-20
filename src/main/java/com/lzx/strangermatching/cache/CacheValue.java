package com.lzx.strangermatching.cache;

import com.lzx.strangermatching.cache.listener.CacheValueListener;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @ClassName: CacheValue
 * @Description: 缓存模板
 * @Author: LZX
 * @Date: 2024/6/19 18:07
 */
@Slf4j
public class CacheValue<T> {

    private Date updateTime;

    private final long deprecatedTime;

    private final Supplier<T> supplier;

    private T result;

    private T oldValue;

    @Setter
    private CacheValueListener<T> cacheValueListener;


    public CacheValue(long deprecatedTime, Supplier<T> supplier) {
        this.deprecatedTime = deprecatedTime;
        this.supplier = supplier;
        result = supplier.get();
        updateTime = new Date();
    }

    public T get() {
        if (expire()) {
            T newValue = supplier.get();
            if(!Objects.equals(oldValue, newValue) && cacheValueListener != null) {
                try {
                    cacheValueListener.onChange(oldValue, newValue);
                } catch (Throwable throwable) {
                    log.error("cache listener execute error", throwable);
                }
            }

            oldValue = result;
            result = newValue;
            updateTime = new Date();
        }
        return result;
    }

    private boolean expire() {
        return System.currentTimeMillis() - updateTime.getTime() > deprecatedTime;
    }
}
