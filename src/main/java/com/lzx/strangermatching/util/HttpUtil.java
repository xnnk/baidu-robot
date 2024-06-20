package com.lzx.strangermatching.util;

import com.lzx.strangermatching.config.GptConfig;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName: HttpUtil
 * @Description:
 * @Author: LZX
 * @Date: 2024/6/20 9:14
 */
public class HttpUtil {

    AuthStringUtil authStringUtils = new AuthStringUtil();

    /**
     * 创建Request
     * @param url
     * @param body
     * @return
     */
    public Request createRequest(String url, String body) {
        String nowDate = DateUtil.createNowDate();

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .header("x-bce-date", nowDate)
                .post(RequestBody.create(body.getBytes(StandardCharsets.UTF_8)))
                .build();

        Request authRequest = null;

        try {
            authRequest = authStringUtils.createAuthorization(request, GptConfig.SK, GptConfig.AK, nowDate, "1800");
        } catch (MalformedURLException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Create authorization error: " + e);
        }
        return authRequest;
    }

}
