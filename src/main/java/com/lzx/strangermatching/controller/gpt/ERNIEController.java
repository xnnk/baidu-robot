package com.lzx.strangermatching.controller.gpt;

import com.alibaba.fastjson.JSON;
import com.lzx.strangermatching.cache.manager.KeywordCacheManager;
import com.lzx.strangermatching.request.ShopRobReq;
import com.lzx.strangermatching.response.R;
import com.lzx.strangermatching.service.ERNIEService;
import com.lzx.strangermatching.util.RUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ERNIEController
 * @Description:
 * @Author: LZX
 * @Date: 2024/6/20 10:14
 */

@RestController
@RequestMapping("/ERNIE")
public class ERNIEController {

    @Autowired
    private ERNIEService ernieService;

    @PostMapping("/info/shopping")
    public R createShoppingInfo(@RequestBody ShopRobReq requestBody) {
        String name = requestBody.getName();
        String highlight = requestBody.getHighlight();
        String body = String.format("""
                {
                   "messages": [
                    {"role":"user","content":"name: %s, highlights: %s"}
                   ],
                   "system": "现在你是一位商品介绍人, 你需要根据我给出的商品信息写出一句低于40字的针对商品亮点的商品说明, 不要只单一的重复商品亮点. 接下来是这款商品的信息: 商品名称：{name} 商品亮点：{highlights}"
                }
                """, name, highlight);
        String result = ernieService.invokeERNIETiny_Shopping(body);
        return RUtil.success(result);
    }

    @PostMapping("/info/shopping/cache")
    public R createShoppingInfoWithCache(@RequestBody ShopRobReq requestBody) {
        String keyword = JSON.toJSONString(requestBody);
        String result = ernieService.getIfPresent(keyword);
        return RUtil.success(result);
    }
}
