package com.lzx.strangermatching.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzx.strangermatching.cache.service.TokenCacheListener;
import com.lzx.strangermatching.service.ERNIEService;
import com.lzx.strangermatching.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;

/**
 * @ClassName: ERNIEServiceImpl
 * @Description:
 * @Author: LZX
 * @Date: 2024/6/20 9:02
 */
@Service
@Slf4j
@Transactional
public class ERNIEServiceImpl implements ERNIEService {

    @Autowired
    private TokenCacheListener tokenCacheListener;

    HttpUtil httpUtil = new HttpUtil();

    @Override
    public String invokeERNIETiny_Shopping(String body) {
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie-tiny-8k?access_token=";
        String accessToken = tokenCacheListener.getAccessToken();
        url += accessToken;

        Request request = httpUtil.createRequest(url, body);

        OkHttpClient client = new OkHttpClient.Builder().build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.body().string());
                System.out.println(jsonNode.asText());
                String asText = jsonNode.get("result").asText();
                return asText;
            }
            else {
                throw new RuntimeException("Failed to get access_token: " + response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private String removeNonEssentialCharacters(String body) {
//        String replaceString = body.replaceAll("\\n", "").replaceAll("\\\"", "");
//
//    }
}
