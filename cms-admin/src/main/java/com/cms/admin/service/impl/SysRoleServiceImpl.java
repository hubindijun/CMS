package com.cms.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.admin.dto.system.RoleAddDTO;
import com.cms.admin.dto.system.RoleQueryDTO;
import com.cms.admin.dto.system.RoleUpdateDTO;
import com.cms.admin.entity.SysRole;
import com.cms.admin.entity.SysRolePermission;
import com.cms.admin.mapper.SysRoleMapper;
import com.cms.admin.mapper.SysRolePermissionMapper;
import com.cms.admin.service.SysRoleService;
import com.cms.admin.vo.system.RoleVO;
import com.cms.common.base.PageResult;
import com.cms.common.constant.CommonConstant;
import com.cms.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统角色服务实现
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    public PageResult<RoleVO> page(RoleQueryDTO dto) {
        Page<SysRole> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getName())) {
            wrapper.like(SysRole::getName, dto.getName());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(SysRole::getId);
        Page<SysRole> result = roleMapper.selectPage(page, wrapper);

        List<RoleVO> voList = result.getRecords().stream().map(role -> {
            RoleVO vo = new RoleVO();
            BeanUtils.copyProperties(role, vo);
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), dto.getPageNum(), dto.getPageSize());
    }

    @Override
    public List<RoleVO> listAll() {
        return roleMapper.selectList(new LambdaQueryWrapper<SysRole>().eq(SysRole::getStatus, 1))
                .stream().map(role -> {
                    RoleVO vo = new RoleVO();
                    BeanUtils.copyProperties(role, vo);
                    return vo;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void add(RoleAddDTO dto) {
        SysRole existing = roleMapper.selectOne(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getCode, dto.getCode())
        );
        if (existing != null) {
            throw new BusinessException("角色编码已存在");
        }
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        if (role.getStatus() == null) role.setStatus(1);
        roleMapper.insert(role);

        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            saveRolePermissions(role.getId(), dto.getPermissionIds());
        }
    }

    @Override
    @Transactional
    public void update(RoleUpdateDTO dto) {
        SysRole role = roleMapper.selectById(dto.getId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        SysRole update = new SysRole();
        BeanUtils.copyProperties(dto, update);
        roleMapper.updateById(update);

        if (dto.getPermissionIds() != null) {
            rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                    .eq(SysRolePermission::getRoleId, dto.getId()));
            saveRolePermissions(dto.getId(), dto.getPermissionIds());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) return;
        if (CommonConstant.ADMIN_ROLE_CODE.equals(role.getCode())) {
            throw new BusinessException("admin角色不可删除");
        }
        roleMapper.deleteById(id);
        rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, id));
    }

    @Override
    @Transactional
    public void toggleStatus(Long id, Integer status) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if (CommonConstant.ADMIN_ROLE_CODE.equals(role.getCode())) {
            throw new BusinessException("admin角色不可禁用");
        }
        role.setStatus(status);
        roleMapper.updateById(role);
    }

    @Override
    public List<Long> getPermissionIds(Long id) {
        return rolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, id)
        ).stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignPermissions(Long id, List<Long> permissionIds) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if (CommonConstant.ADMIN_ROLE_CODE.equals(role.getCode())) {
            throw new BusinessException("admin角色拥有所有权限，无需分配");
        }
        rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, id));
        if (permissionIds != null && !permissionIds.isEmpty()) {
            saveRolePermissions(id, permissionIds);
        }
    }

    private void saveRolePermissions(Long roleId, List<Long> permissionIds) {
        for (Long permId : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permId);
            rolePermissionMapper.insert(rp);
        }
    }
}
