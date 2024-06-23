package com.lzx.strangermatching.cache.pojo;

import lombok.Data;

/**
 * @ClassName: keywordPojo
 * @Description: 关键词使用次数pojo
 * @Author: LZX
 * @Date: 2024/6/23 0:01
 */
@Data
public class KeywordPojo {

    /**
     * 相关关键词输出的文字
     */
    private String keywordOutput;

    /**
     * 共使用多少次
     */
    private Integer times;

}
