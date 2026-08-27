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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统权限（菜单）服务实现
 */
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
        // 1. 查询全部权限（用于补全父级节点，数量有限，内存中处理性能没问题）
        List<SysPermission> allPerms = permissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getSort)
        );
        Map<Long, SysPermission> allPermMap = allPerms.stream()
                .collect(Collectors.toMap(SysPermission::getId, p -> p));

        // 2. 获取用户拥有的权限，过滤出目录和菜单（type <= 2）
        List<SysPermission> userPerms = permissionMapper.selectByUserId(userId);
        List<SysPermission> menus = userPerms.stream()
                .filter(p -> p.getType() != null && p.getType() <= 2)
                .collect(Collectors.toList());

        // 3. 递归补全所有缺失的父级节点
        List<SysPermission> result = fillParents(menus, allPermMap);
        return buildTree(result, 0L);
    }

    /**
     * 补全菜单节点缺失的父级节点（确保菜单树完整，从叶子一直补到根）
     *
     * @param menus 用户拥有的菜单节点
     * @param allPermMap 全部权限映射（id -> 权限对象）
     * @return 包含所有祖先节点的菜单列表
     */
    private List<SysPermission> fillParents(List<SysPermission> menus, Map<Long, SysPermission> allPermMap) {
        List<SysPermission> result = new ArrayList<>(menus);
        Set<Long> seen = result.stream()
                .map(SysPermission::getId)
                .collect(Collectors.toSet());
        boolean changed = true;
        while (changed) {
            changed = false;
            List<SysPermission> toAdd = new ArrayList<>();
            for (SysPermission p : result) {
                Long parentId = p.getParentId();
                if (parentId != null && parentId != 0L && !seen.contains(parentId)) {
                    SysPermission parent = allPermMap.get(parentId);
                    if (parent != null) {
                        toAdd.add(parent);
                        seen.add(parentId);
                        changed = true;
                    }
                }
            }
            result.addAll(toAdd);
        }
        return result;
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
