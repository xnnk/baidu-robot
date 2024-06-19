package com.lzx.strangermatching.entity;

import lombok.Data;

import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

@Data
@Entity
public class SysUserRole {

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    /**
     * 用户Id
     */
    private Integer userId;


    /**
     * 角色Id
     */
    private Integer roleId;
}