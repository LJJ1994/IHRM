package com.ihrm.system.shiro.realm;

import com.ihrm.common.shiro.realm.IhrmRealm;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.ProfileResult;
import com.ihrm.system.service.PermissionService;
import com.ihrm.system.service.UserService;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRealm extends IhrmRealm {
    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;

    /**
     * 用户授权方法
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        String username = usernamePasswordToken.getUsername();
        String password = new String(usernamePasswordToken.getPassword());

        User user = userService.findByMobilAndPassword(username, password);
        if (user != null) {
            ProfileResult profileResult = null;
            if ("user".equals(user.getLevel())) {
                profileResult = new ProfileResult(user);
            }else{
                Map<String, Object> map = new HashMap<>();
                if ("coAdmin".equals(user.getLevel())){
                    map.put("enVisible", "1");
                }
                List<Permission>  permissionList = permissionService.findAll(map);
                // 构造返回的安全数据: 用户基本数据, 权限数据
                profileResult = new ProfileResult(user, permissionList);
            }
            //构造方法：安全数据，密码，realm域名
           return new SimpleAuthenticationInfo(profileResult, user.getPassword(), this.getName());

        }
        // 用户异常
        return null;
    }
}
