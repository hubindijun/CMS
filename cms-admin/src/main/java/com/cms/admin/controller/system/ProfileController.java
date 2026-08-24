package com.cms.admin.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cms.admin.dto.system.ChangePasswordDTO;
import com.cms.admin.dto.system.ProfileUpdateDTO;
import com.cms.admin.entity.SysUser;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.admin.vo.system.UserVO;
import com.cms.common.base.Result;
import com.cms.common.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public Result<UserVO> getProfile() {
        String username = getCurrentUsername();
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return Result.ok(vo);
    }

    @PutMapping
    public Result<Void> updateProfile(@RequestBody ProfileUpdateDTO dto) {
        String username = getCurrentUsername();
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        SysUser update = new SysUser();
        update.setId(user.getId());
        if (dto.getNickname() != null) update.setNickname(dto.getNickname());
        if (dto.getEmail() != null) update.setEmail(dto.getEmail());
        if (dto.getPhone() != null) {
            SysUser phoneExist = userMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getPhone, dto.getPhone())
                            .ne(SysUser::getId, user.getId())
            );
            if (phoneExist != null) {
                throw new BusinessException("手机号已存在");
            }
            update.setPhone(dto.getPhone());
        }
        if (dto.getAvatar() != null) update.setAvatar(dto.getAvatar());

        userMapper.updateById(update);
        return Result.ok();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody @Valid ChangePasswordDTO dto) {
        String username = getCurrentUsername();
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
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
        return Result.ok();
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
