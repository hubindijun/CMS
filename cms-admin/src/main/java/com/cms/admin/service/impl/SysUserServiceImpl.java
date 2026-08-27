package com.cms.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.admin.dto.system.UserAddDTO;
import com.cms.admin.dto.system.UserQueryDTO;
import com.cms.admin.dto.system.UserUpdateDTO;
import com.cms.admin.entity.SysRole;
import com.cms.admin.entity.SysUser;
import com.cms.admin.entity.SysUserRole;
import com.cms.admin.mapper.SysRoleMapper;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.admin.mapper.SysUserRoleMapper;
import com.cms.admin.service.SysUserService;
import com.cms.admin.vo.system.UserVO;
import com.cms.common.base.PageResult;
import com.cms.common.constant.CommonConstant;
import com.cms.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统用户服务实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<UserVO> page(UserQueryDTO dto) {
        Page<SysUser> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getUsername())) {
            wrapper.like(SysUser::getUsername, dto.getUsername());
        }
        if (StringUtils.hasText(dto.getPhone())) {
            wrapper.like(SysUser::getPhone, dto.getPhone());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(SysUser::getId);
        Page<SysUser> result = userMapper.selectPage(page, wrapper);

        List<UserVO> voList = result.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            List<Long> roleIds = getRoleIds(user.getId());
            vo.setRoleIds(roleIds);
            vo.setRoleNames(getRoleNames(roleIds));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), dto.getPageNum(), dto.getPageSize());
    }

    @Override
    @Transactional
    public void add(UserAddDTO dto) {
        SysUser existing = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername())
        );
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }
        if (StringUtils.hasText(dto.getPhone())) {
            SysUser phoneExist = userMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, dto.getPhone())
            );
            if (phoneExist != null) {
                throw new BusinessException("手机号已存在");
            }
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (user.getStatus() == null) user.setStatus(1);
        userMapper.insert(user);

        saveUserRoles(user.getId(), dto.getRoleIds());
    }

    @Override
    @Transactional
    public void update(UserUpdateDTO dto) {
        SysUser user = userMapper.selectById(dto.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (CommonConstant.ROOT_ROLE_CODE.equals(user.getUsername())) {
            throw new BusinessException("root用户不可修改");
        }
        if (StringUtils.hasText(dto.getPhone())) {
            SysUser phoneExist = userMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getPhone, dto.getPhone())
                            .ne(SysUser::getId, dto.getId())
            );
            if (phoneExist != null) {
                throw new BusinessException("手机号已存在");
            }
        }

        SysUser update = new SysUser();
        BeanUtils.copyProperties(dto, update);
        userMapper.updateById(update);

        if (dto.getRoleIds() != null) {
            userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, dto.getId()));
            saveUserRoles(dto.getId(), dto.getRoleIds());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (CommonConstant.ROOT_ROLE_CODE.equals(user.getUsername())) {
            throw new BusinessException("root用户不可删除");
        }
        userMapper.deleteById(id);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
    }

    @Override
    public void toggleStatus(Long id, Integer status) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (CommonConstant.ROOT_ROLE_CODE.equals(user.getUsername())) {
            throw new BusinessException("root用户不可禁用");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (CommonConstant.ROOT_ROLE_CODE.equals(user.getUsername())) {
            throw new BusinessException("root用户密码不可重置");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    @Override
    public UserVO getById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        List<Long> roleIds = getRoleIds(id);
        vo.setRoleIds(roleIds);
        vo.setRoleNames(getRoleNames(roleIds));
        return vo;
    }

    private List<Long> getRoleIds(Long userId) {
        return userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        ).stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }

    private List<String> getRoleNames(List<Long> roleIds) {
        if (roleIds.isEmpty()) return List.of();
        return roleMapper.selectBatchIds(roleIds).stream()
                .map(SysRole::getName).collect(Collectors.toList());
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return;
        for (Long roleId : roleIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            userRoleMapper.insert(ur);
        }
    }
}
