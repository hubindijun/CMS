package com.cms.admin.service;

import com.cms.admin.dto.system.DictTypeQueryDTO;
import com.cms.admin.entity.SysDictType;
import com.cms.admin.vo.system.DictTypeVO;
import com.cms.common.base.PageResult;

import java.util.List;

public interface SysDictTypeService {
    PageResult<DictTypeVO> page(DictTypeQueryDTO dto);
    List<DictTypeVO> listAll();
    DictTypeVO getById(Long id);
    void add(SysDictType dictType);
    void update(SysDictType dictType);
    void delete(Long id);
}
