package com.hqizcc.ltcs.hqbsltcs.config;

import com.hqizcc.ltcs.hqbsltcs.model.SysPermission;
import com.hqizcc.ltcs.hqbsltcs.model.SysRole;
import com.hqizcc.ltcs.hqbsltcs.model.UserInfo;
import com.hqizcc.ltcs.hqbsltcs.service.UserInfoService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

public class MyShiroRealm extends AuthorizingRealm {

    @Autowired
    private UserInfoService userInfoService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        System.out.println("权限配置 --> MyShiroRealm.doGetAuthorizationInfo()");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        UserInfo userInfo = (UserInfo) principalCollection.getPrimaryPrincipal();

        for (SysRole role : userInfo.getRoleList()){
            authorizationInfo.addRole(role.getRole());
            for (SysPermission p : role.getPermissions()){
                authorizationInfo.addStringPermission(p.getPermission());
            }
        }

        return authorizationInfo;
    }

    /**
     * 登录认证
     * 用来进行身份验证，也就是说验证用户输入的账号和密码是否正确
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        System.out.println("shiro 登录认证");

        // 获取用户输入的账号
        String username = (String) token.getPrincipal();
        System.out.println(token.getCredentials());
        // 通过username从数据库中查找User对象，如果找到，没找到
        // 实际项目中，这里根据实际情况做缓存，如果不做，shiro也是有时间间隔机制的，2分钟不会重复执行该方法
        UserInfo userInfo = userInfoService.findByUsername(username);
        System.out.println("-----> userInfo = " + userInfo);
        if (userInfo == null){
            return null;
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userInfo,
                userInfo.getPassword(),
                ByteSource.Util.bytes(userInfo.getCredentialsSalt()),
                getName()
        );

        return authenticationInfo;

    }
}
