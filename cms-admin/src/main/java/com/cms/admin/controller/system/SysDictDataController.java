package com.cms.admin.controller.system;
import org.springframework.security.access.prepost.PreAuthorize;
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

/**
 * 字典数据管理接口
 * <p>
 * 提供字典数据的分页查询、详情查询、按类型查询、新增、修改与删除功能。
 * </p>
 */
@RestController
@RequestMapping("/api/system/dict/data")
@RequiredArgsConstructor
@PreAuthorize("hasRole('admin')")
public class SysDictDataController {

    private final SysDictDataService dictDataService;

    /**
     * 分页查询字典数据
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult<DictDataVO>> page(DictDataQueryDTO dto) {
        return Result.ok(dictDataService.page(dto));
    }

    /**
     * 根据 ID 获取字典数据详情
     *
     * @param id 字典数据 ID
     * @return 字典数据信息
     */
    @GetMapping("/{id}")
    public Result<DictDataVO> getById(@PathVariable Long id) {
        return Result.ok(dictDataService.getById(id));
    }

    /**
     * 根据字典类型查询字典数据列表
     *
     * @param dictType 字典类型编码
     * @return 字典数据列表
     */
    @GetMapping("/byType")
    public Result<List<DictDataVO>> listByType(@RequestParam String dictType) {
        return Result.ok(dictDataService.listByType(dictType));
    }

    /**
     * 新增字典数据
     *
     * @param dictData 字典数据信息
     * @return 无
     */
    @PostMapping
    public Result<Void> add(@RequestBody @Valid SysDictData dictData) {
        dictDataService.add(dictData);
        return Result.ok();
    }

    /**
     * 修改字典数据
     *
     * @param dictData 字典数据信息
     * @return 无
     */
    @PutMapping
    public Result<Void> update(@RequestBody @Valid SysDictData dictData) {
        dictDataService.update(dictData);
        return Result.ok();
    }

    /**
     * 删除字典数据
     *
     * @param id 字典数据 ID
     * @return 无
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dictDataService.delete(id);
        return Result.ok();
    }
}
