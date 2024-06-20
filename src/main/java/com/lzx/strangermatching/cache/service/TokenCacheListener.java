package com.lzx.strangermatching.cache.service;

import com.lzx.strangermatching.cache.CacheValue;
import com.lzx.strangermatching.cache.supplier.AccessTokenSupplier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @ClassName: TokenCacheListener
 * @Description:
 * @Author: LZX
 * @Date: 2024/6/19 18:26
 */
@Service
public class TokenCacheListener {
    private static final String clientId = "xxxxxxxxx";
    private static final String clientSecret = "xxxxxxxxx";

    private CacheValue<String> accessTokenCache;

    @PostConstruct
    public void init() {
        accessTokenCache = new CacheValue<>(2592000, new AccessTokenSupplier(clientId, clientSecret));

        accessTokenCache.setCacheValueListener(((oldValue, newValue) -> {
            if (oldValue == null) {
                return;
            }
            // logging

            //accessTokenCache.get(); 严重！会进入死循环

            // ...
        }));
    }

    public String getAccessToken() {
        String accessToken = accessTokenCache.get();
        if (accessToken == null) {
            throw new RuntimeException("Failed to get access token");
        }
        return accessToken;
    }
}
