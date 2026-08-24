package com.cms.admin.controller.system;

import com.cms.admin.dto.system.DictDataQueryDTO;
import com.cms.admin.entity.SysDictData;
import com.cms.admin.service.SysDictDataService;
import com.cms.admin.vo.system.DictDataVO;
import com.cms.common.base.PageResult;
import com.cms.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/dict/data")
@RequiredArgsConstructor
public class SysDictDataController {

    private final SysDictDataService dictDataService;

    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('system:dict:query')")
    public Result<PageResult<DictDataVO>> page(DictDataQueryDTO dto) {
        return Result.ok(dictDataService.page(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:dict:query')")
    public Result<DictDataVO> getById(@PathVariable Long id) {
        return Result.ok(dictDataService.getById(id));
    }

    @GetMapping("/byType")
    @PreAuthorize("@pms.hasPermission('system:dict:query')")
    public Result<List<DictDataVO>> listByType(@RequestParam String dictType) {
        return Result.ok(dictDataService.listByType(dictType));
    }

    @PostMapping
    @PreAuthorize("@pms.hasPermission('system:dict:add')")
    public Result<Void> add(@RequestBody @Valid SysDictData dictData) {
        dictDataService.add(dictData);
        return Result.ok();
    }

    @PutMapping
    @PreAuthorize("@pms.hasPermission('system:dict:edit')")
    public Result<Void> update(@RequestBody @Valid SysDictData dictData) {
        dictDataService.update(dictData);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:dict:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        dictDataService.delete(id);
        return Result.ok();
    }
}
