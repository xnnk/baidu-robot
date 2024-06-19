package com.lzx.strangermatching.service.impl;

import com.lzx.strangermatching.entity.SysResource;
import com.lzx.strangermatching.entity.SysRole;
import com.lzx.strangermatching.entity.SysRoleResource;
import com.lzx.strangermatching.repository.SysResourceRepository;
import com.lzx.strangermatching.repository.SysRoleRepository;
import com.lzx.strangermatching.repository.SysRoleResourceRepository;
import com.lzx.strangermatching.request.SysRoleReq;
import com.lzx.strangermatching.response.R;
import com.lzx.strangermatching.response.SysRoleVo;
import com.lzx.strangermatching.service.SysRoleService;
import com.lzx.strangermatching.util.JPAUtil;
import com.lzx.strangermatching.util.RUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.lzx.strangermatching.enums.REnum.UNkNOWN_ACCOUNT;


@Service
@Transactional
@Slf4j
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    SysRoleRepository sysRoleRepository;

    @Autowired
    SysRoleResourceRepository sysRoleResourceRepository;

    @Autowired
    SysResourceRepository sysResourceRepository;
    /**
     * 新增角色
     * @param sysRoleReq
     * @return
     */
    @Override
    public R saveRole(SysRoleReq sysRoleReq) {

        /*分离对象并保存*/
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(sysRoleReq,sysRole);
        SysRole sysRoleSave = sysRoleRepository.save(sysRole);
        log.info("角色基本信息保存：sysRoleSave = {}",sysRoleSave);

        /*构建该角色的资源并保存*/
        List<SysRoleResource> sysRoleResources = new ArrayList<>();
        sysRoleReq.getSysResources().forEach(o->{
            SysRoleResource sysRoleResource = new SysRoleResource();
            sysRoleResource.setRoleId(sysRoleSave.getId());
            sysRoleResource.setResourceId(o.getId());
            sysRoleResources.add(sysRoleResource);
        });
        List<SysRoleResource> sysRoleResourcesSave = sysRoleResourceRepository.saveAll(sysRoleResources);
        log.info("角色资源保存：sysRoleResourcesSave = {}",sysRoleResourcesSave);

        return RUtil.success();
    }

    /**
     * 查询角色列表
     * @param pageable
     * @return
     */
    public R selectRoleList(String name,Pageable pageable){
        Specification<SysRole> specification = new Specification<SysRole>() {
            @Override
            public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicate = new ArrayList<>();
                if(StringUtils.isNoneBlank(name)){
                    predicate.add(criteriaBuilder.like(root.get("name").as(String.class), JPAUtil.like(name)));
                }
                Predicate[] pre = new Predicate[predicate.size()];
                return criteriaQuery.where(predicate.toArray(pre)).getRestriction();
            }
        };
        return RUtil.success(sysRoleRepository.findAll(specification,pageable));
    }

    /**
     * 查询角色详情
     * @param id
     * @return
     */
    @Override
    public R selectRoleDetail(Integer id) {

        /*查询角色基本信息*/
        SysRoleVo sysRoleVo = new SysRoleVo();
        Optional<SysRole> sysRole = sysRoleRepository.findById(id);
        if (sysRole.isEmpty()) {
            return RUtil.error(UNkNOWN_ACCOUNT.getCode(), UNkNOWN_ACCOUNT.getMessage());
        }
        BeanUtils.copyProperties(sysRole.get(),sysRoleVo);

        /*取出resourceId*/
        List<Integer> sysResourceIds = new ArrayList<>();
        List<SysRoleResource> sysRoleResources = sysRoleResourceRepository.findByRoleId(id);
        sysRoleResources.forEach(o->{
            sysResourceIds.add(o.getResourceId());
        });

        /*查询该角色拥有资源*/
        List<SysResource> sysResources = sysResourceRepository.findAllById(sysResourceIds);
        sysRoleVo.setSysResources(sysResources);

        log.info("角色详情：sysRoleVo = {}",sysRoleVo);
        return RUtil.success(sysRoleVo);
    }

    /**
     * 更新角色
     * @param sysRoleReq
     * @return
     */
    @Override
    public R updateRole(SysRoleReq sysRoleReq){

        /*分离角色与拥有资源*/
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(sysRoleReq,sysRole);

        /*初始化角色资源*/
        sysRoleResourceRepository.deleteByRoleId(sysRoleReq.getId());

        /*角色基本信息更新*/
        SysRole sysRoleSave = sysRoleRepository.save(sysRole);
        log.info("角色基本信息更新：sysRoleSave = {}",sysRoleSave);

        /*更新角色资源*/
        List<SysRoleResource> sysRoleResources = new ArrayList<>();
        sysRoleReq.getSysResources().forEach(o->{
            SysRoleResource sysRoleResource = new SysRoleResource();
            sysRoleResource.setResourceId(o.getId());
            sysRoleResource.setRoleId(sysRoleReq.getId());
            sysRoleResources.add(sysRoleResource);
        });
        List<SysRoleResource> sysRoleResourcesSave = sysRoleResourceRepository.saveAll(sysRoleResources);
        log.info("资源更新：sysRoleResourcesSave = {}",sysRoleResourcesSave);

        return RUtil.success();
    }

    /**
     * 删除角色接口
     * @param id
     * @return
     */
    @Override
    public R deleteRole(Integer id){
        sysRoleRepository.deleteById(id);

        /*资源删除*/
        sysRoleResourceRepository.deleteByRoleId(id);
        return RUtil.success();
    }
}
