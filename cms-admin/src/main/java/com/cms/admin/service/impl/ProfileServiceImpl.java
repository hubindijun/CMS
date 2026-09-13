package com.cms.admin.service.impl;

import com.cms.admin.dto.system.ChangePasswordDTO;
import com.cms.admin.dto.system.ProfileUpdateDTO;
import com.cms.admin.entity.SysUser;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.admin.service.ProfileService;
import com.cms.admin.vo.system.UserVO;
import com.cms.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 个人中心服务实现
 */
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserVO getProfile(String username) {
        SysUser user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public void updateProfile(String username, ProfileUpdateDTO dto) {
        SysUser user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        SysUser update = new SysUser();
        update.setId(user.getId());
        if (dto.getNickname() != null) {
            update.setNickname(dto.getNickname());
        }
        if (dto.getEmail() != null) {
            update.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            SysUser phoneExist = userMapper.selectByPhoneExcludeId(dto.getPhone(), user.getId());
            if (phoneExist != null) {
                throw new BusinessException("手机号已存在");
            }
            update.setPhone(dto.getPhone());
        }
        if (dto.getAvatar() != null) {
            update.setAvatar(dto.getAvatar());
        }

        userMapper.updateById(update);
    }

    @Override
    public void changePassword(String username, ChangePasswordDTO dto) {
        SysUser user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        SysUser update = new SysUser();
        update.setId(user.getId());
        update.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.updateById(update);
    }
}
