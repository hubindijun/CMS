package com.cms.admin.service.impl;

import com.cms.admin.dto.system.UserAddDTO;
import com.cms.admin.dto.system.UserQueryDTO;
import com.cms.admin.dto.system.UserUpdateDTO;
import com.cms.admin.entity.SysUser;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.admin.mapper.SysUserRoleMapper;
import com.cms.admin.service.SysUserService;
import com.cms.admin.vo.system.UserVO;
import com.cms.common.base.PageResult;
import com.cms.common.constant.CommonConstant;
import com.cms.common.exception.BusinessException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统用户服务实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<UserVO> page(UserQueryDTO dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<UserVO> list = userMapper.selectPageList(dto);
        PageInfo<UserVO> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getList(), pageInfo.getTotal(), dto.getPageNum(), dto.getPageSize());
    }

    @Override
    @Transactional
    public void add(UserAddDTO dto) {
        SysUser existing = userMapper.selectByUsername(dto.getUsername());
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }
        if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            SysUser phoneExist = userMapper.selectByPhone(dto.getPhone());
            if (phoneExist != null) {
                throw new BusinessException("手机号已存在");
            }
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
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
        if (CommonConstant.ADMIN_ROLE_CODE.equals(user.getUsername())) {
            throw new BusinessException("admin用户不可修改");
        }
        if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            SysUser phoneExist = userMapper.selectByPhoneExcludeId(dto.getPhone(), dto.getId());
            if (phoneExist != null) {
                throw new BusinessException("手机号已存在");
            }
        }

        SysUser update = new SysUser();
        BeanUtils.copyProperties(dto, update);
        userMapper.updateById(update);

        if (dto.getRoleIds() != null) {
            userRoleMapper.deleteByUserId(dto.getId());
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
        if (CommonConstant.ADMIN_ROLE_CODE.equals(user.getUsername())) {
            throw new BusinessException("admin用户不可删除");
        }
        userMapper.deleteById(id);
        userRoleMapper.deleteByUserId(id);
    }

    @Override
    public void toggleStatus(Long id, Integer status) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (CommonConstant.ADMIN_ROLE_CODE.equals(user.getUsername())) {
            throw new BusinessException("admin用户不可禁用");
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
        if (CommonConstant.ADMIN_ROLE_CODE.equals(user.getUsername())) {
            throw new BusinessException("admin用户密码不可重置");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    @Override
    public UserVO getById(Long id) {
        UserVO vo = userMapper.selectUserVoById(id);
        if (vo == null) {
            throw new BusinessException("用户不存在");
        }
        return vo;
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        userRoleMapper.batchInsert(userId, roleIds);
    }
}
