package com.cms.admin.service;

import com.cms.admin.dto.system.DictDataQueryDTO;
import com.cms.admin.entity.SysDictData;
import com.cms.admin.vo.system.DictDataVO;
import com.cms.common.base.PageResult;

import java.util.List;

public interface SysDictDataService {
    PageResult<DictDataVO> page(DictDataQueryDTO dto);
    DictDataVO getById(Long id);
    List<DictDataVO> listByType(String dictType);
    void add(SysDictData dictData);
    void update(SysDictData dictData);
    void delete(Long id);
}
