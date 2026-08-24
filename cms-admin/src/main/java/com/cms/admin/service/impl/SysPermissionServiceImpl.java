package com.cms.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cms.admin.dto.system.PermissionAddDTO;
import com.cms.admin.dto.system.PermissionUpdateDTO;
import com.cms.admin.entity.SysPermission;
import com.cms.admin.mapper.SysPermissionMapper;
import com.cms.admin.service.SysPermissionService;
import com.cms.admin.vo.system.PermissionVO;
import com.cms.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysPermissionMapper permissionMapper;

    @Override
    public List<PermissionVO> tree() {
        List<SysPermission> all = permissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getSort)
        );
        return buildTree(all, 0L);
    }

    @Override
    public List<PermissionVO> menuTreeByUserId(Long userId) {
        List<SysPermission> all = permissionMapper.selectByUserId(userId);
        List<SysPermission> menus = all.stream()
                .filter(p -> p.getType() != null && p.getType() <= 2)
                .collect(Collectors.toList());
        return buildTree(menus, 0L);
    }

    @Override
    @Transactional
    public void add(PermissionAddDTO dto) {
        if (dto.getParentId() == null) dto.setParentId(0L);
        SysPermission perm = new SysPermission();
        BeanUtils.copyProperties(dto, perm);
        if (perm.getSort() == null) perm.setSort(0);
        if (perm.getStatus() == null) perm.setStatus(1);
        permissionMapper.insert(perm);
    }

    @Override
    @Transactional
    public void update(PermissionUpdateDTO dto) {
        SysPermission perm = permissionMapper.selectById(dto.getId());
        if (perm == null) {
            throw new BusinessException("权限不存在");
        }
        SysPermission update = new SysPermission();
        BeanUtils.copyProperties(dto, update);
        permissionMapper.updateById(update);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long count = permissionMapper.selectCount(
                new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getParentId, id)
        );
        if (count > 0) {
            throw new BusinessException("存在子级权限，不可删除");
        }
        permissionMapper.deleteById(id);
    }

    private List<PermissionVO> buildTree(List<SysPermission> list, Long parentId) {
        Map<Long, List<PermissionVO>> childrenMap = list.stream()
                .map(this::toVO)
                .collect(Collectors.groupingBy(PermissionVO::getParentId));
        return buildTreeRecursive(childrenMap, parentId);
    }

    private List<PermissionVO> buildTreeRecursive(Map<Long, List<PermissionVO>> childrenMap, Long parentId) {
        List<PermissionVO> children = childrenMap.get(parentId);
        if (children == null || children.isEmpty()) return new ArrayList<>();
        for (PermissionVO vo : children) {
            vo.setChildren(buildTreeRecursive(childrenMap, vo.getId()));
        }
        return children;
    }

    private PermissionVO toVO(SysPermission perm) {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(perm, vo);
        return vo;
    }
}
