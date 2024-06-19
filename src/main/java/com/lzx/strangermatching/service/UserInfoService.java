package com.lzx.strangermatching.service;

import com.lzx.strangermatching.entity.UserInfo;
import com.lzx.strangermatching.request.SearchReq;
import com.lzx.strangermatching.response.R;

import java.util.List;

/**
 * @InterfaceName: UserInfoService
 * @Description:
 * @Author: LZX
 * @Date: 2023/12/19 16:47
 */
public interface UserInfoService {

    R saveUserInfo(UserInfo userInfo);

    R deleteUserInfo(Integer userId);

    List<UserInfo> search(SearchReq searchReq);

    List<UserInfo> scoreByBMI(List<UserInfo> userInfos, SearchReq searchReq);
}
