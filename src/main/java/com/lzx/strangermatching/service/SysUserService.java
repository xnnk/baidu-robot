package com.lzx.strangermatching.service;

import com.lzx.strangermatching.entity.SysUser;
import com.lzx.strangermatching.request.SysUserReq;
import com.lzx.strangermatching.response.R;
import org.springframework.data.domain.Pageable;

/**
 * @InterfaceName: SysUserService
 * @Description:
 * @Author: LZX
 * @Date: 2023/12/18 14:52
 */
public interface SysUserService {

    SysUser findByAccount(String account);

    R saveUser(SysUserReq sysUserReq);

    R selectUserList(String name, Pageable pageable);

    R selectUserDetail(Integer id);

    R updateUser(SysUserReq sysUserReq);

    R deleteUser(Integer id);
}
