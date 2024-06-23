package com.lzx.strangermatching.cache.manager;

import com.lzx.strangermatching.cache.MultiCacheValue;
import com.lzx.strangermatching.cache.pojo.KeywordPojo;
import com.lzx.strangermatching.cache.supplier.KeywordSupplier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @ClassName: KeywordCacheManager
 * @Description:
 * @Author: LZX
 * @Date: 2024/6/22 19:56
 */
@Service
public class KeywordCacheManager {

    private MultiCacheValue<String, KeywordPojo> keywordCache;

    private final ConcurrentMap<String, KeywordPojo> internalCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        keywordCache = new MultiCacheValue<>(600000, new KeywordSupplier(internalCache));

        keywordCache.setCacheValueListener(((key, oldValue, newValue) -> {
            return;
        }));
    }

    public KeywordPojo getTemplateByKeyword(String keyword) {
        KeywordPojo keywordPojo = keywordCache.getAndUpdate(keyword);
        if (keywordCache == null) {
            throw new RuntimeException("Failed to get keywordPojo");
        }
        System.out.println("---------------------" + keywordPojo + "------------------------");
        System.out.println("---------------------" + internalCache.toString() + "------------------------");
        return keywordPojo;
    }

}
