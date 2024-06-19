package com.lzx.strangermatching.repository;

import com.lzx.strangermatching.entity.SysUserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SysUserRoleRepository extends JpaRepository<SysUserRole,Integer> {

    List<SysUserRole> findByUserId(Integer id);

    void deleteByUserId(Integer id);
}
