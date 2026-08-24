package com.cms.admin.controller.system;

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

@RestController
@RequestMapping("/api/system/permission")
@RequiredArgsConstructor
public class SysPermissionController {

    private final SysPermissionService permissionService;

    @GetMapping("/tree")
    @PreAuthorize("@pms.hasPermission('system:permission:query')")
    public Result<List<PermissionVO>> tree() {
        return Result.ok(permissionService.tree());
    }

    @PostMapping
    @PreAuthorize("@pms.hasPermission('system:permission:add')")
    public Result<Void> add(@RequestBody @Valid PermissionAddDTO dto) {
        permissionService.add(dto);
        return Result.ok();
    }

    @PutMapping
    @PreAuthorize("@pms.hasPermission('system:permission:edit')")
    public Result<Void> update(@RequestBody @Valid PermissionUpdateDTO dto) {
        permissionService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:permission:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.ok();
    }
}
