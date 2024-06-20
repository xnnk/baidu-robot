package com.lzx.strangermatching.cache.supplier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * @ClassName: AccessTokenSupplier
 * @Description:
 * @Author: LZX
 * @Date: 2024/6/19 19:03
 */
public class AccessTokenSupplier implements Supplier<String> {

    private final String clientId;
    private final String clientSecret;

    public AccessTokenSupplier(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public String get() {
        String url = String.format(
                "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=%s&client_secret=%s",
                clientId, clientSecret);

        OkHttpClient httpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("".getBytes()))
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.body().string());
                System.out.println(jsonNode.get("access_token").asText());
                return jsonNode.get("access_token").asText();
            }
            else {
                throw new RuntimeException("Failed to get access_token: " + response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to get access_token", e);
        }
    }
}
