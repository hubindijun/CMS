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

/**
 * 系统角色管理接口
 * <p>
 * 提供角色的分页查询、列表查询、新增、修改、删除、状态切换与权限分配等功能。
 * </p>
 */
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService roleService;

    /**
     * 分页查询角色列表
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('system:role:query')")
    public Result<PageResult<RoleVO>> page(RoleQueryDTO dto) {
        return Result.ok(roleService.page(dto));
    }

    /**
     * 获取全部角色列表
     *
     * @return 角色列表
     */
    @GetMapping("/list")
    @PreAuthorize("@pms.hasPermission('system:role:query')")
    public Result<List<RoleVO>> list() {
        return Result.ok(roleService.listAll());
    }

    /**
     * 新增角色
     *
     * @param dto 角色信息
     * @return 无
     */
    @PostMapping
    @PreAuthorize("@pms.hasPermission('system:role:add')")
    public Result<Void> add(@RequestBody @Valid RoleAddDTO dto) {
        roleService.add(dto);
        return Result.ok();
    }

    /**
     * 修改角色
     *
     * @param dto 角色信息
     * @return 无
     */
    @PutMapping
    @PreAuthorize("@pms.hasPermission('system:role:edit')")
    public Result<Void> update(@RequestBody @Valid RoleUpdateDTO dto) {
        roleService.update(dto);
        return Result.ok();
    }

    /**
     * 删除角色
     *
     * @param id 角色 ID
     * @return 无
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:role:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.ok();
    }

    /**
     * 切换角色状态（启用/禁用）
     *
     * @param id     角色 ID
     * @param status 状态值
     * @return 无
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("@pms.hasPermission('system:role:edit')")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        roleService.toggleStatus(id, status);
        return Result.ok();
    }

    /**
     * 获取角色已分配的权限 ID 列表
     *
     * @param id 角色 ID
     * @return 权限 ID 列表
     */
    @GetMapping("/{id}/permissions")
    @PreAuthorize("@pms.hasPermission('system:role:permission')")
    public Result<List<Long>> getPermissionIds(@PathVariable Long id) {
        return Result.ok(roleService.getPermissionIds(id));
    }

    /**
     * 为角色分配权限
     *
     * @param id            角色 ID
     * @param permissionIds 权限 ID 列表
     * @return 无
     */
    @PutMapping("/{id}/permissions")
    @PreAuthorize("@pms.hasPermission('system:role:permission')")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return Result.ok();
    }
}
