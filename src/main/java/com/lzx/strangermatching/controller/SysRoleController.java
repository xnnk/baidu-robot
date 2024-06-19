package com.lzx.strangermatching.controller;

import com.lzx.strangermatching.enums.REnum;
import com.lzx.strangermatching.exception.SystemException;
import com.lzx.strangermatching.request.SysRoleReq;
import com.lzx.strangermatching.response.R;
import com.lzx.strangermatching.service.SysRoleService;
import com.lzx.strangermatching.util.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;


@RestController
@RequestMapping("/sys")
@Slf4j
public class SysRoleController {

    @Autowired
    SysRoleService sysRoleService;

    /**
     * 新增角色
     * @param sysRoleReq
     * @param bindingResult
     * @return
     */
    @RequiresPermissions("sys:role:insert")
    @PostMapping("/saveRole")
    public R saveRole(@Valid @RequestBody SysRoleReq sysRoleReq,
                      BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.error("【新增角色】参数不正确:sysRoleFrom={}", sysRoleReq);
            throw new SystemException(REnum.PARAM_ERROR.getCode(), Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }

        return sysRoleService.saveRole(sysRoleReq);
    }

    /**
     * 查询角色列表
     * @param page
     * @param size
     * @param name
     * @return
     */
    @RequiresPermissions("sys:role:list")
    @GetMapping("/selectRoleList")
    public R selectRoleList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                            @RequestParam(value = "size", defaultValue = "10") Integer size,
                            @RequestParam(value = "name",defaultValue = "") String name){

        PageRequest pageRequest = PageRequest.of(page,size);
        return sysRoleService.selectRoleList(name,pageRequest);
    }

    /**
     * 查询角色详情
     * @param id
     * @return
     */
    @RequiresPermissions("sys:role:detail")
    @GetMapping("/selectRoleDetail")
    public R selectRoleDetail(@RequestParam(value = "id",required = false) Integer id) throws javax.transaction.SystemException {

        Assert.isNull(id,"id不能为空");
        return sysRoleService.selectRoleDetail(id);

    }

    /**
     * 更新角色
     * @param sysRoleReq
     * @param bindingResult
     * @return
     */
    @RequiresPermissions("sys:role:update")
    @PutMapping("/updateRole")
    public R updateRole(@Valid @RequestBody SysRoleReq sysRoleReq,
                        BindingResult bindingResult) throws javax.transaction.SystemException {

        Assert.isNull(sysRoleReq.getId(),"id不能为空");

        if(bindingResult.hasErrors()){
            log.error("【更新角色】参数不正确:sysRoleFrom={}", sysRoleReq);
            throw new SystemException(REnum.PARAM_ERROR.getCode(), Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }

        return sysRoleService.updateRole(sysRoleReq);
    }

    /**
     * 删除角色
     * @param id
     * @return
     */
    @RequiresPermissions("sys:role:delete")
    @DeleteMapping("/deleteRole/{id}")
    public R deleteRole(@PathVariable Integer id){
        return sysRoleService.deleteRole(id);
    }
}
