package com.cms.admin.service;

import com.cms.admin.dto.system.DictTypeQueryDTO;
import com.cms.admin.entity.SysDictType;
import com.cms.admin.vo.system.DictTypeVO;
import com.cms.common.base.PageResult;

import java.util.List;

/**
 * 字典类型服务
 */
public interface SysDictTypeService {
    /**
     * 分页查询字典类型
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    PageResult<DictTypeVO> page(DictTypeQueryDTO dto);

    /**
     * 查询所有字典类型
     *
     * @return 字典类型列表
     */
    List<DictTypeVO> listAll();

    /**
     * 根据ID查询字典类型
     *
     * @param id 字典类型ID
     * @return 字典类型
     */
    DictTypeVO getById(Long id);

    /**
     * 新增字典类型
     *
     * @param dictType 字典类型
     */
    void add(SysDictType dictType);

    /**
     * 修改字典类型
     *
     * @param dictType 字典类型
     */
    void update(SysDictType dictType);

    /**
     * 删除字典类型（逻辑删除）
     *
     * @param id 字典类型ID
     */
    void delete(Long id);
}
