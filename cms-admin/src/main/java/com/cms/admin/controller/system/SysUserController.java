package com.cms.admin.controller.system;

import com.cms.admin.dto.system.UserAddDTO;
import com.cms.admin.dto.system.UserQueryDTO;
import com.cms.admin.dto.system.UserUpdateDTO;
import com.cms.admin.service.SysUserService;
import com.cms.admin.vo.system.UserVO;
import com.cms.common.base.PageResult;
import com.cms.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('system:user:query')")
    public Result<PageResult<UserVO>> page(UserQueryDTO dto) {
        return Result.ok(userService.page(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:user:query')")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @PostMapping
    @PreAuthorize("@pms.hasPermission('system:user:add')")
    public Result<Void> add(@RequestBody @Valid UserAddDTO dto) {
        userService.add(dto);
        return Result.ok();
    }

    @PutMapping
    @PreAuthorize("@pms.hasPermission('system:user:edit')")
    public Result<Void> update(@RequestBody @Valid UserUpdateDTO dto) {
        userService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:user:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("@pms.hasPermission('system:user:edit')")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.toggleStatus(id, status);
        return Result.ok();
    }

    @PutMapping("/{id}/reset-password")
    @PreAuthorize("@pms.hasPermission('system:user:resetPwd')")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String password) {
        userService.resetPassword(id, password);
        return Result.ok();
    }
}
