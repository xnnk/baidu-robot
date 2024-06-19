package com.lzx.strangermatching.repository;

import com.lzx.strangermatching.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @InterfaceName: SysUserRepository
 * @Description:
 * @Author: LZX
 * @Date: 2023/12/18 14:50
 */
public interface SysUserRepository extends JpaRepository<SysUser,Integer> {

    /**
     * 根据账号查询用户
     * @param account
     * @return
     */
    SysUser findByAccount(String account);

    Page<SysUser> findAll(Specification<SysUser> sysRoleSpecification, Pageable pageable);

}
