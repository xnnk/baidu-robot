package com.lzx.strangermatching.controller;

import com.lzx.strangermatching.entity.UserInfo;
import com.lzx.strangermatching.request.SearchReq;
import com.lzx.strangermatching.response.R;
import com.lzx.strangermatching.service.UserInfoService;
import com.lzx.strangermatching.util.RUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName: searchController
 * @Description:
 * @Author: LZX
 * @Date: 2023/12/19 22:36
 */
@RestController
@RequestMapping("/core")
@Slf4j
public class SearchController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 陌生人匹配
     * @param searchReq
     * @return
     */
    @PostMapping("/search")
    public R searchStranger(@RequestBody SearchReq searchReq) {

        List<UserInfo> fUserInfos = userInfoService.search(searchReq);

        // 匹配BMI
        List<UserInfo> lUserInfos = userInfoService.scoreByBMI(fUserInfos, searchReq);

        return RUtil.success(lUserInfos);
    }
}
