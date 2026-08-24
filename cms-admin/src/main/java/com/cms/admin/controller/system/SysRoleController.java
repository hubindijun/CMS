package com.cms.admin.controller.system;

import com.cms.admin.dto.system.RoleAddDTO;
import com.cms.admin.dto.system.RoleQueryDTO;
import com.cms.admin.dto.system.RoleUpdateDTO;
import com.cms.admin.service.SysRoleService;
import com.cms.admin.vo.system.RoleVO;
import com.cms.common.base.PageResult;
import com.cms.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService roleService;

    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('system:role:query')")
    public Result<PageResult<RoleVO>> page(RoleQueryDTO dto) {
        return Result.ok(roleService.page(dto));
    }

    @GetMapping("/list")
    @PreAuthorize("@pms.hasPermission('system:role:query')")
    public Result<List<RoleVO>> list() {
        return Result.ok(roleService.listAll());
    }

    @PostMapping
    @PreAuthorize("@pms.hasPermission('system:role:add')")
    public Result<Void> add(@RequestBody @Valid RoleAddDTO dto) {
        roleService.add(dto);
        return Result.ok();
    }

    @PutMapping
    @PreAuthorize("@pms.hasPermission('system:role:edit')")
    public Result<Void> update(@RequestBody @Valid RoleUpdateDTO dto) {
        roleService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:role:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("@pms.hasPermission('system:role:edit')")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        roleService.toggleStatus(id, status);
        return Result.ok();
    }

    @GetMapping("/{id}/permissions")
    @PreAuthorize("@pms.hasPermission('system:role:permission')")
    public Result<List<Long>> getPermissionIds(@PathVariable Long id) {
        return Result.ok(roleService.getPermissionIds(id));
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("@pms.hasPermission('system:role:permission')")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return Result.ok();
    }
}
