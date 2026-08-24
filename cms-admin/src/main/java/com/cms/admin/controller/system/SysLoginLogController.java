package com.cms.admin.controller.system;

import com.cms.admin.dto.system.LoginLogQueryDTO;
import com.cms.admin.service.SysLoginLogService;
import com.cms.admin.vo.system.LoginLogVO;
import com.cms.common.base.PageResult;
import com.cms.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/login-log")
@RequiredArgsConstructor
public class SysLoginLogController {

    private final SysLoginLogService loginLogService;

    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('system:loginLog:query')")
    public Result<PageResult<LoginLogVO>> page(LoginLogQueryDTO dto) {
        return Result.ok(loginLogService.page(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:loginLog:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        loginLogService.delete(id);
        return Result.ok();
    }

    @DeleteMapping("/clear")
    @PreAuthorize("@pms.hasPermission('system:loginLog:remove')")
    public Result<Void> clear() {
        loginLogService.clear();
        return Result.ok();
    }
}
