package com.lzx.strangermatching.request;

import lombok.Data;

/**
 * @ClassName: SearchReq
 * @Description: 匹配参数
 * @Author: LZX
 * @Date: 2023/12/19 20:06
 */
@Data
public class SearchReq {

    /**
     * 匹配性别倾向 0:双性 1:男 2:女
     */
    private Integer sexualOrientation;

    /**
     * 匹配年龄倾向 0:无所谓 1:大 2:小
     */
    private Integer ageTendency;

    /**
     * 匹配身高倾向 0:无所谓 其他:具体数值
     */
    private Integer heightTendency;

    /**
     * 匹配体重倾向 0:无所谓 1:瘦 2:正常 3:偏胖 4:胖
     */
    private Integer weightTendency;

    /**
     * 匹配收入倾向 0:无所谓 1:多 2:少
     */
    private Integer incomePropensity;

}
