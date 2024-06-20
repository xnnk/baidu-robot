package com.lzx.strangermatching.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @ClassName: ShopRobReq
 * @Description: 商品机器人所需参数
 * @Author: LZX
 * @Date: 2024/6/20 8:48
 */
@Data
public class ShopRobReq {

    /**
     * 商品名称
     */
    @NotEmpty(message = "商品名称不能为空")
    private String name;

    /**
     * 商品亮点
     */
    @NotEmpty(message = "商品亮点不能为空")
    private String highlight;
}
