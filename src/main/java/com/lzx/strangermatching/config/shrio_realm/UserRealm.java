package com.lzx.strangermatching.config.shrio_realm;

import com.lzx.strangermatching.entity.SysUser;
import com.lzx.strangermatching.enums.ForbiddenEnum;
import com.lzx.strangermatching.enums.REnum;
import com.lzx.strangermatching.service.SysResourceService;
import com.lzx.strangermatching.service.SysUserService;
import com.lzx.strangermatching.util.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: UserRealm
 * @Description: 用户认证
 * @Author: LZX
 * @Date: 2023/12/12 16:03
 */
@Slf4j
@Component
public class UserRealm extends AuthorizingRealm {

    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysResourceService sysResourceService;

    /**
     * 用户授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        // 查找该角色所拥有的资源并授权
        simpleAuthorizationInfo.setStringPermissions(sysResourceService.selectUserPerms(ShiroUtil.getUserId()));
        return simpleAuthorizationInfo;
    }

    /**
     * 用户认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        UsernamePasswordToken token = (UsernamePasswordToken)authenticationToken;

        SysUser sysUser = sysUserService.findByAccount(token.getUsername());
        if(sysUser == null){
            log.error(REnum.UNkNOWN_ACCOUNT.getMessage()+"SysUser = null");
            throw new UnknownAccountException();
        }
        if(ForbiddenEnum.DISABLE.getCode().toString().equals(sysUser.getForbidden())){
            log.error(REnum.UNkNOWN_ACCOUNT.getMessage()+"SysUser = {}",sysUser);
            throw new DisabledAccountException();
        }
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(sysUser,sysUser.getPassword(),getName());
        simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(sysUser.getSalt()));
        return simpleAuthenticationInfo;
    }

    /**
     * 密码验证
     * @param credentialsMatcher
     */
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        HashedCredentialsMatcher md5HashedCredentialsMatcher = new HashedCredentialsMatcher();
        md5HashedCredentialsMatcher.setHashAlgorithmName(ShiroUtil.hashAlgorithmName);
        md5HashedCredentialsMatcher.setHashIterations(ShiroUtil.hashIterations);
        md5HashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        super.setCredentialsMatcher(md5HashedCredentialsMatcher);
    }
}
