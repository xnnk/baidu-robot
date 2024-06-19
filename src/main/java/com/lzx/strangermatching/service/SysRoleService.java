package com.lzx.strangermatching.service;

import com.lzx.strangermatching.request.SysRoleReq;
import com.lzx.strangermatching.response.R;
import org.springframework.data.domain.Pageable;


public interface SysRoleService {

    R saveRole(SysRoleReq sysRoleReq);

    R selectRoleList(String name,Pageable pageable);

    R selectRoleDetail(Integer id);

    R updateRole(SysRoleReq sysRoleReq);

    R deleteRole(Integer id);
}
