package com.lzx.strangermatching.repository.elastic;

import com.lzx.strangermatching.entity.UserInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @InterfaceName: UserInfoRepository
 * @Description:
 * @Author: LZX
 * @Date: 2023/12/19 16:39
 */
public interface UserInfoRepository extends ElasticsearchRepository<UserInfo, String> {

    /**
     * 根据userid查找用户匹配信息
     * @param userId
     * @return
     */
    UserInfo findUserInfoByUserId(Integer userId);

    /**
     * 根据userid删除用户匹配信息
     * @param userId
     * @return
     */
    void deleteUserInfoByUserId(Integer userId);

}
