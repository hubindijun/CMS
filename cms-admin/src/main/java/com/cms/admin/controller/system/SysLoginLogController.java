package com.cms.admin.controller.system;
import org.springframework.security.access.prepost.PreAuthorize;
import com.cms.admin.dto.system.LoginLogQueryDTO;
import com.cms.admin.service.SysLoginLogService;
import com.cms.admin.vo.system.LoginLogVO;
import com.cms.common.base.PageResult;
import com.cms.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 登录日志管理接口
 * <p>
 * 提供登录日志的分页查询、单条删除与清空功能。
 * </p>
 */
@RestController
@RequestMapping("/api/system/login-log")
@RequiredArgsConstructor
@PreAuthorize("hasRole('admin')")
public class SysLoginLogController {

    private final SysLoginLogService loginLogService;

    /**
     * 分页查询登录日志
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult<LoginLogVO>> page(LoginLogQueryDTO dto) {
        return Result.ok(loginLogService.page(dto));
    }

    /**
     * 删除单条登录日志
     *
     * @param id 日志 ID
     * @return 无
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        loginLogService.delete(id);
        return Result.ok();
    }

    /**
     * 清空全部登录日志
     *
     * @return 无
     */
    @DeleteMapping("/clear")
    public Result<Void> clear() {
        loginLogService.clear();
        return Result.ok();
    }
}
