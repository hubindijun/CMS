package com.cms.admin.controller.system;

import com.cms.admin.service.SysPermissionService;
import com.cms.admin.vo.system.PermissionVO;
import com.cms.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 菜单接口
 */
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysPermissionService permissionService;

    /**
     * 获取当前登录用户的菜单树
     */
    @GetMapping("/tree")
    public Result<List<PermissionVO>> menuTree() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return Result.ok(permissionService.menuTreeByUsername(username));
    }
}
