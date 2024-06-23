package com.lzx.strangermatching.cache.supplier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzx.strangermatching.cache.pojo.KeywordPojo;
import com.lzx.strangermatching.service.ERNIEService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @ClassName: KeywordSupplier
 * @Description: 关键词保留
 * @Author: LZX
 * @Date: 2024/6/22 19:57
 */
public class KeywordSupplier implements Function<String, KeywordPojo> {

    private final ConcurrentMap<String, KeywordPojo> internalCache;

    public KeywordSupplier(ConcurrentMap<String, KeywordPojo> internalCache) {
        this.internalCache = internalCache;
    }

    @Override
    public KeywordPojo apply(String key) {
        // 获取当前值并递增
        KeywordPojo keywordPojo = internalCache.get(key);

        // value为空说明还没使用此关键词进行文生文
        if (keywordPojo == null) {
            return getNewTemplate(key);
        }
        else {
            // 使用三次关键词后需要提醒重新进行生成文字
            if (keywordPojo.getTimes() >= 3) {
                return getNewTemplate(key);
            }
            else {
                keywordPojo.setTimes(keywordPojo.getTimes() + 1);
            }
            internalCache.put(key, keywordPojo);    // 更新缓存
        }

        return keywordPojo;
    }

    private KeywordPojo getNewTemplate(String key) {
        String url = "http://localhost:15597/ERNIE/info/shopping";

        OkHttpClient httpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(key.getBytes()))
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.body().string());
                if (Integer.parseInt(jsonNode.get("code").asText()) == 0 || jsonNode.get("msg").asText().equals("ok")) {
                    // 生成文字后将value返回到上层
                    KeywordPojo newPojo = new KeywordPojo();
                    String data = jsonNode.get("data").asText();
                    newPojo.setKeywordOutput(data);
                    newPojo.setTimes(1);
                    internalCache.put(key, newPojo);
                    return newPojo;
                }
                else {
                    throw new RuntimeException("Failed to get generated text: " + response.body().string());
                }
            }
            else {
                throw new RuntimeException("Failed to get generated text: " + response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to get generated text", e);
        }
    }
}
