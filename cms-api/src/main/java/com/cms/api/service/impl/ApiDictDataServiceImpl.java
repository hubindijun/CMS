package com.cms.api.service.impl;

import com.cms.api.dto.DictDataQueryDTO;
import com.cms.api.mapper.ApiDictDataMapper;
import com.cms.api.service.ApiDictDataService;
import com.cms.api.vo.DictDataVO;
import com.cms.common.base.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiDictDataServiceImpl implements ApiDictDataService {

    private final ApiDictDataMapper dictDataMapper;

    @Override
    public PageResult<DictDataVO> page(DictDataQueryDTO dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<DictDataVO> list = dictDataMapper.selectPageList(dto);
        PageInfo<DictDataVO> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getList(), pageInfo.getTotal(), dto.getPageNum(), dto.getPageSize());
    }
}
