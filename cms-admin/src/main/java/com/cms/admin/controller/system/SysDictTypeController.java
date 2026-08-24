package com.cms.admin.controller.system;

import com.cms.admin.dto.system.DictTypeQueryDTO;
import com.cms.admin.entity.SysDictType;
import com.cms.admin.service.SysDictTypeService;
import com.cms.admin.vo.system.DictTypeVO;
import com.cms.common.base.PageResult;
import com.cms.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/dict/type")
@RequiredArgsConstructor
public class SysDictTypeController {

    private final SysDictTypeService dictTypeService;

    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('system:dict:query')")
    public Result<PageResult<DictTypeVO>> page(DictTypeQueryDTO dto) {
        return Result.ok(dictTypeService.page(dto));
    }

    @GetMapping("/list")
    @PreAuthorize("@pms.hasPermission('system:dict:query')")
    public Result<List<DictTypeVO>> list() {
        return Result.ok(dictTypeService.listAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:dict:query')")
    public Result<DictTypeVO> getById(@PathVariable Long id) {
        return Result.ok(dictTypeService.getById(id));
    }

    @PostMapping
    @PreAuthorize("@pms.hasPermission('system:dict:add')")
    public Result<Void> add(@RequestBody @Valid SysDictType dictType) {
        dictTypeService.add(dictType);
        return Result.ok();
    }

    @PutMapping
    @PreAuthorize("@pms.hasPermission('system:dict:edit')")
    public Result<Void> update(@RequestBody @Valid SysDictType dictType) {
        dictTypeService.update(dictType);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:dict:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        dictTypeService.delete(id);
        return Result.ok();
    }
}
