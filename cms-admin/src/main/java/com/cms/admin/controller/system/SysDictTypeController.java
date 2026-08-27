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

/**
 * 字典类型管理接口
 * <p>
 * 提供字典类型的分页查询、列表查询、详情、新增、修改与删除功能。
 * </p>
 */
@RestController
@RequestMapping("/api/system/dict/type")
@RequiredArgsConstructor
public class SysDictTypeController {

    private final SysDictTypeService dictTypeService;

    /**
     * 分页查询字典类型
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('system:dict:query')")
    public Result<PageResult<DictTypeVO>> page(DictTypeQueryDTO dto) {
        return Result.ok(dictTypeService.page(dto));
    }

    /**
     * 获取全部字典类型列表
     *
     * @return 字典类型列表
     */
    @GetMapping("/list")
    @PreAuthorize("@pms.hasPermission('system:dict:query')")
    public Result<List<DictTypeVO>> list() {
        return Result.ok(dictTypeService.listAll());
    }

    /**
     * 根据 ID 获取字典类型详情
     *
     * @param id 字典类型 ID
     * @return 字典类型信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:dict:query')")
    public Result<DictTypeVO> getById(@PathVariable Long id) {
        return Result.ok(dictTypeService.getById(id));
    }

    /**
     * 新增字典类型
     *
     * @param dictType 字典类型信息
     * @return 无
     */
    @PostMapping
    @PreAuthorize("@pms.hasPermission('system:dict:add')")
    public Result<Void> add(@RequestBody @Valid SysDictType dictType) {
        dictTypeService.add(dictType);
        return Result.ok();
    }

    /**
     * 修改字典类型
     *
     * @param dictType 字典类型信息
     * @return 无
     */
    @PutMapping
    @PreAuthorize("@pms.hasPermission('system:dict:edit')")
    public Result<Void> update(@RequestBody @Valid SysDictType dictType) {
        dictTypeService.update(dictType);
        return Result.ok();
    }

    /**
     * 删除字典类型
     *
     * @param id 字典类型 ID
     * @return 无
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:dict:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        dictTypeService.delete(id);
        return Result.ok();
    }
}
