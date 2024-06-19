package com.lzx.strangermatching.repository;

import com.lzx.strangermatching.entity.SysRoleResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SysRoleResourceRepository extends JpaRepository<SysRoleResource,Integer> {

    List<SysRoleResource> findByRoleId(Integer roleId);

    void deleteByRoleId(Integer id);
}
