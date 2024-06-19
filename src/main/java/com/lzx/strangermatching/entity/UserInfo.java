package com.lzx.strangermatching.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @ClassName: UserInfo
 * @Description: 匹配角色信息
 * @Author: LZX
 * @Date: 2023/12/19 15:55
 */
@Data
@Accessors(chain = true)
@Document(indexName = "userinfo")
public class UserInfo {

    /**
     * 主键id
     */
    @Id
    private String id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 性别(匹配可用参数) 1:男 2:女
     */
    private Integer sex;

    /**
     * 年龄(匹配可用参数)
     */
    private Integer age;

    /**
     * 身高(匹配可用参数)
     */
    private Integer height;

    /**
     * 体重(匹配可用参数)
     */
    private Integer weight;

    /**
     * 每月可支配的金钱(匹配可用参数)
     */
    private Integer cash;

}
