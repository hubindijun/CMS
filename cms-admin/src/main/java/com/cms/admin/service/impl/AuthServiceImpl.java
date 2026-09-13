package com.cms.admin.service.impl;

import com.cms.admin.entity.SysRole;
import com.cms.admin.entity.SysUser;
import com.cms.admin.mapper.SysRoleMapper;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.admin.security.LoginAttemptService;
import com.cms.admin.security.TokenService;
import com.cms.admin.service.AuthService;
import com.cms.admin.util.CaptchaService;
import com.cms.admin.vo.UserInfoVO;
import com.cms.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证服务实现
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final CaptchaService captchaService;
    private final LoginAttemptService loginAttemptService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public String login(String username, String password, String captcha, String captchaKey) {
        if (loginAttemptService.isLocked(username)) {
            throw new BusinessException("账号已锁定，请10分钟后再试");
        }
        if (!captchaService.verify(captchaKey, captcha)) {
            loginAttemptService.incrementFail(username);
            throw new BusinessException("验证码错误");
        }

        SysUser user = userMapper.selectByUsername(username);
        if (user == null) {
            loginAttemptService.incrementFail(username);
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已禁用");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            loginAttemptService.incrementFail(username);
            throw new BusinessException("用户名或密码错误");
        }

        loginAttemptService.clearFail(username);
        return tokenService.createToken(user);
    }

    @Override
    public void logout(String authHeader) {
        String token = tokenService.resolveToken(authHeader);
        tokenService.removeToken(token);
    }

    @Override
    public UserInfoVO getUserInfo(String username) {
        SysUser user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        List<SysRole> roles = roleMapper.selectByUserId(user.getId());
        List<String> roleCodes = roles.stream()
                .filter(r -> r.getStatus() != null && r.getStatus() == 1)
                .map(SysRole::getCode)
                .collect(Collectors.toList());

        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRoles(roleCodes);
        return vo;
    }
}
