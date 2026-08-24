package com.cms.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.admin.dto.system.DictDataQueryDTO;
import com.cms.admin.entity.SysDictData;
import com.cms.admin.mapper.SysDictDataMapper;
import com.cms.admin.service.SysDictDataService;
import com.cms.admin.vo.system.DictDataVO;
import com.cms.common.base.PageResult;
import com.cms.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl implements SysDictDataService {

    private final SysDictDataMapper dictDataMapper;

    @Override
    public PageResult<DictDataVO> page(DictDataQueryDTO dto) {
        Page<SysDictData> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysDictData> wrapper = buildQueryWrapper(dto);
        wrapper.orderByAsc(SysDictData::getDictSort).orderByDesc(SysDictData::getId);
        Page<SysDictData> result = dictDataMapper.selectPage(page, wrapper);

        List<DictDataVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), dto.getPageNum(), dto.getPageSize());
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
        List<SysDictData> list = dictDataMapper.selectList(
                new LambdaQueryWrapper<SysDictData>()
                        .eq(SysDictData::getDictType, dictType)
                        .eq(SysDictData::getStatus, 1)
                        .orderByAsc(SysDictData::getDictSort)
        );
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void add(SysDictData dictData) {
        if (dictData.getDictSort() == null) dictData.setDictSort(0);
        if (dictData.getStatus() == null) dictData.setStatus(1);
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

    private LambdaQueryWrapper<SysDictData> buildQueryWrapper(DictDataQueryDTO dto) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getDictType())) {
            wrapper.eq(SysDictData::getDictType, dto.getDictType());
        }
        if (StringUtils.hasText(dto.getDictLabel())) {
            wrapper.like(SysDictData::getDictLabel, dto.getDictLabel());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(SysDictData::getStatus, dto.getStatus());
        }
        return wrapper;
    }

    private DictDataVO toVO(SysDictData entity) {
        DictDataVO vo = new DictDataVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
