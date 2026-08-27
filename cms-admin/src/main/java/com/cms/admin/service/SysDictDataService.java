package com.cms.admin.service;

import com.cms.admin.dto.system.DictDataQueryDTO;
import com.cms.admin.entity.SysDictData;
import com.cms.admin.vo.system.DictDataVO;
import com.cms.common.base.PageResult;

import java.util.List;

/**
 * 字典数据服务
 */
public interface SysDictDataService {
    /**
     * 分页查询字典数据
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    PageResult<DictDataVO> page(DictDataQueryDTO dto);

    /**
     * 根据ID查询字典数据
     *
     * @param id 字典数据ID
     * @return 字典数据
     */
    DictDataVO getById(Long id);

    /**
     * 根据字典类型编码查询字典数据列表
     *
     * @param dictType 字典类型编码
     * @return 字典数据列表
     */
    List<DictDataVO> listByType(String dictType);

    /**
     * 新增字典数据
     *
     * @param dictData 字典数据
     */
    void add(SysDictData dictData);

    /**
     * 修改字典数据
     *
     * @param dictData 字典数据
     */
    void update(SysDictData dictData);

    /**
     * 删除字典数据（逻辑删除）
     *
     * @param id 字典数据ID
     */
    void delete(Long id);
}
