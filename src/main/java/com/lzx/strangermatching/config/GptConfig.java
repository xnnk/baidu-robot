package com.lzx.strangermatching.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @ClassName: GptConfig
 * @Description:
 * @Author: LZX
 * @Date: 2024/6/12 10:06
 */
@Component
public class GptConfig {

    @Value("${gpt.SK}")
    private String sk;

    @Value("${gpt.AK}")
    private String ak;

    public static String SK;
    public static String AK;

    @PostConstruct
    public void init() {
        GptConfig.SK = this.sk;
        GptConfig.AK = this.ak;
    }
}
