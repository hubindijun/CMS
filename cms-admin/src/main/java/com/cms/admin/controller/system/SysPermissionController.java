package com.cms.admin.controller.system;
import org.springframework.security.access.prepost.PreAuthorize;
import com.cms.admin.dto.system.PermissionAddDTO;
import com.cms.admin.dto.system.PermissionUpdateDTO;
import com.cms.admin.service.SysPermissionService;
import com.cms.admin.vo.system.PermissionVO;
import com.cms.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 系统权限管理接口
 * <p>
 * 提供权限的树形查询、新增、修改与删除功能。
 * </p>
 */
@RestController
@RequestMapping("/api/system/permission")
@RequiredArgsConstructor
@PreAuthorize("hasRole('admin')")
public class SysPermissionController {

    private final SysPermissionService permissionService;

    /**
     * 获取权限树
     *
     * @return 权限树结构列表
     */
    @GetMapping("/tree")
    public Result<List<PermissionVO>> tree() {
        return Result.ok(permissionService.tree());
    }

    /**
     * 新增权限
     *
     * @param dto 权限信息
     * @return 无
     */
    @PostMapping
    public Result<Void> add(@RequestBody @Valid PermissionAddDTO dto) {
        permissionService.add(dto);
        return Result.ok();
    }

    /**
     * 修改权限
     *
     * @param dto 权限信息
     * @return 无
     */
    @PutMapping
    public Result<Void> update(@RequestBody @Valid PermissionUpdateDTO dto) {
        permissionService.update(dto);
        return Result.ok();
    }

    /**
     * 删除权限
     *
     * @param id 权限 ID
     * @return 无
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.ok();
    }
}
