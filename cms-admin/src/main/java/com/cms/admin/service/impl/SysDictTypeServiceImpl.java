package com.cms.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.admin.dto.system.DictTypeQueryDTO;
import com.cms.admin.entity.SysDictType;
import com.cms.admin.mapper.SysDictTypeMapper;
import com.cms.admin.service.SysDictTypeService;
import com.cms.admin.vo.system.DictTypeVO;
import com.cms.common.base.PageResult;
import com.cms.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典类型服务实现
 */
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl implements SysDictTypeService {

    private final SysDictTypeMapper dictTypeMapper;

    @Override
    public PageResult<DictTypeVO> page(DictTypeQueryDTO dto) {
        Page<SysDictType> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysDictType> wrapper = buildQueryWrapper(dto);
        wrapper.orderByDesc(SysDictType::getId);
        Page<SysDictType> result = dictTypeMapper.selectPage(page, wrapper);

        List<DictTypeVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), dto.getPageNum(), dto.getPageSize());
    }

    @Override
    public List<DictTypeVO> listAll() {
        List<SysDictType> list = dictTypeMapper.selectList(
                new LambdaQueryWrapper<SysDictType>().eq(SysDictType::getStatus, 1).orderByAsc(SysDictType::getDictName)
        );
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public DictTypeVO getById(Long id) {
        SysDictType dictType = dictTypeMapper.selectById(id);
        if (dictType == null) {
            throw new BusinessException("字典类型不存在");
        }
        return toVO(dictType);
    }

    @Override
    public void add(SysDictType dictType) {
        SysDictType existing = dictTypeMapper.selectOne(
                new LambdaQueryWrapper<SysDictType>().eq(SysDictType::getDictType, dictType.getDictType())
        );
        if (existing != null) {
            throw new BusinessException("字典类型已存在");
        }
        if (dictType.getStatus() == null) dictType.setStatus(1);
        dictTypeMapper.insert(dictType);
    }

    @Override
    public void update(SysDictType dictType) {
        SysDictType existing = dictTypeMapper.selectById(dictType.getId());
        if (existing == null) {
            throw new BusinessException("字典类型不存在");
        }
        SysDictType sameType = dictTypeMapper.selectOne(
                new LambdaQueryWrapper<SysDictType>()
                        .eq(SysDictType::getDictType, dictType.getDictType())
                        .ne(SysDictType::getId, dictType.getId())
        );
        if (sameType != null) {
            throw new BusinessException("字典类型已存在");
        }
        dictTypeMapper.updateById(dictType);
    }

    @Override
    public void delete(Long id) {
        dictTypeMapper.deleteById(id);
    }

    private LambdaQueryWrapper<SysDictType> buildQueryWrapper(DictTypeQueryDTO dto) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getDictName())) {
            wrapper.like(SysDictType::getDictName, dto.getDictName());
        }
        if (StringUtils.hasText(dto.getDictType())) {
            wrapper.like(SysDictType::getDictType, dto.getDictType());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(SysDictType::getStatus, dto.getStatus());
        }
        return wrapper;
    }

    private DictTypeVO toVO(SysDictType entity) {
        DictTypeVO vo = new DictTypeVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
