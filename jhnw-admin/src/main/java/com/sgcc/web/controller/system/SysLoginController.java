package com.sgcc.web.controller.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.framework.web.service.TokenService;
import com.sgcc.system.domain.SysUserOnline;
import com.sgcc.web.controller.monitor.SysUserOnlineController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.sgcc.common.constant.Constants;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.entity.SysMenu;
import com.sgcc.common.core.domain.entity.SysUser;
import com.sgcc.common.core.domain.model.LoginBody;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.framework.web.service.SysLoginService;
import com.sgcc.framework.web.service.SysPermissionService;
import com.sgcc.system.service.ISysMenuService;

/**
 * 登录验证
 * 
 * @author ruoyi
 */
@RestController
public class SysLoginController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    /**
     * 登录方法
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        String username = loginBody.getUsername();

        //同一用户，禁止同时多次登录!
        SysUserOnlineController sysUserOnlineController = new SysUserOnlineController();
        TableDataInfo list = sysUserOnlineController.queryOnlineUsers();
        List<SysUserOnline> rows = (List<SysUserOnline>) list.getRows();
        for (SysUserOnline sysUserOnline:rows){
            if (sysUserOnline.getUserName().equals(username)){
                return AjaxResult.error("同一用户，禁止同时多次登录!");
            }
        }

        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
