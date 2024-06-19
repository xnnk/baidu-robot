package com.lzx.strangermatching.controller;

import com.lzx.strangermatching.enums.REnum;
import com.lzx.strangermatching.exception.SystemException;
import com.lzx.strangermatching.request.SysUserReq;
import com.lzx.strangermatching.response.R;
import com.lzx.strangermatching.service.SysUserService;
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
public class SysUserController {

    @Autowired
    SysUserService sysUserService;

    /**
     * 新增用户
     * @param sysUserReq
     * @param bindingResult
     * @return
     */
    @RequiresPermissions("sys:user:insert")
    @PostMapping("/saveUser")
    public R saveUser(@Valid @RequestBody SysUserReq sysUserReq,
                      BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.error("【新增用户】参数不正确:sysUserFrom={}", sysUserReq);
            throw new SystemException(REnum.PARAM_ERROR.getCode(), Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }

        return sysUserService.saveUser(sysUserReq);
    }


    /**
     * 查询用户列表
     * @param page
     * @param size
     * @param name
     * @return
     */
    @RequiresPermissions("sys:user:list")
    @GetMapping("/selectUserList")
    public R selectUserList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                            @RequestParam(value = "size", defaultValue = "10") Integer size,
                            @RequestParam(value = "name",defaultValue = "") String name){

        PageRequest pageRequest = PageRequest.of(page,size);
        return sysUserService.selectUserList(name,pageRequest);
    }

    /**
     * 查询用户详情
     * @param id
     * @return
     */
    @RequiresPermissions("sys:user:detail")
    @GetMapping("/selectUserDetail")
    public R selectUserDetail(@RequestParam(value = "id",required = false) Integer id) throws javax.transaction.SystemException {

        Assert.isNull(id,"id不能为空");
        return sysUserService.selectUserDetail(id);
    }

    /**
     * 更新用户
     * @param sysUserReq
     * @param bindingResult
     * @return
     */
    @RequiresPermissions("sys:user:update")
    @PutMapping("/updateUser")
    public R updateUser(@Valid @RequestBody SysUserReq sysUserReq,
                        BindingResult bindingResult) throws javax.transaction.SystemException {

        Assert.isNull(sysUserReq.getId(),"id不能为空");

        if(bindingResult.hasErrors()){
            log.error("【更新用户】参数不正确:sysRoleFrom={}", sysUserReq);
            throw new SystemException(REnum.PARAM_ERROR.getCode(), Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }

        return sysUserService.updateUser(sysUserReq);
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @RequiresPermissions("sys:user:delete")
    @DeleteMapping("/deleteUser/{id}")
    public R deleteUser(@PathVariable Integer id){
        return sysUserService.deleteUser(id);
    }
}
