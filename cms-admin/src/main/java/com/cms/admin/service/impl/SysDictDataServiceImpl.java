package com.cms.admin.service.impl;

import com.cms.admin.dto.system.DictDataQueryDTO;
import com.cms.admin.entity.SysDictData;
import com.cms.admin.mapper.SysDictDataMapper;
import com.cms.admin.service.SysDictDataService;
import com.cms.admin.vo.system.DictDataVO;
import com.cms.common.base.PageResult;
import com.cms.common.exception.BusinessException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典数据服务实现
 */
@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl implements SysDictDataService {

    private final SysDictDataMapper dictDataMapper;

    @Override
    public PageResult<DictDataVO> page(DictDataQueryDTO dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<DictDataVO> list = dictDataMapper.selectPageList(dto);
        PageInfo<DictDataVO> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getList(), pageInfo.getTotal(), dto.getPageNum(), dto.getPageSize());
    }

    @Override
    public DictDataVO getById(Long id) {
        SysDictData dictData = dictDataMapper.selectById(id);
        if (dictData == null) {
            throw new BusinessException("字典数据不存在");
        }
        return toVO(dictData);
    }

    @Override
    public List<DictDataVO> listByType(String dictType) {
        return dictDataMapper.selectByDictType(dictType);
    }

    @Override
    public void add(SysDictData dictData) {
        if (dictData.getDictSort() == null) {
            dictData.setDictSort(0);
        }
        if (dictData.getStatus() == null) {
            dictData.setStatus(1);
        }
        dictDataMapper.insert(dictData);
    }

    @Override
    public void update(SysDictData dictData) {
        SysDictData existing = dictDataMapper.selectById(dictData.getId());
        if (existing == null) {
            throw new BusinessException("字典数据不存在");
        }
        dictDataMapper.updateById(dictData);
    }

    @Override
    public void delete(Long id) {
        dictDataMapper.deleteById(id);
    }

    private DictDataVO toVO(SysDictData entity) {
        DictDataVO vo = new DictDataVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
