package com.cms.admin.controller;

import com.cms.admin.entity.SysRole;
import com.cms.admin.entity.SysUser;
import com.cms.admin.mapper.SysRoleMapper;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.admin.mapper.SysUserRoleMapper;
import com.cms.admin.entity.SysUserRole;
import com.cms.admin.security.LoginAttemptService;
import com.cms.admin.security.TokenService;
import com.cms.admin.util.CaptchaService;
import com.cms.admin.vo.UserInfoVO;
import com.cms.common.base.Result;
import com.cms.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证相关接口
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final CaptchaService captchaService;
    private final LoginAttemptService loginAttemptService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @param captcha 验证码
     * @param captchaKey 验证码key
     * @return token
     */
    @PostMapping("/login")
    public Result<String> login(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String captcha,
                                @RequestParam String captchaKey) {
        if (loginAttemptService.isLocked(username)) {
            throw new BusinessException("账号已锁定，请10分钟后再试");
        }
        if (!captchaService.verify(captchaKey, captcha)) {
            loginAttemptService.incrementFail(username);
            throw new BusinessException("验证码错误");
        }

        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
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
        String token = tokenService.createToken(user);
        return Result.ok(token);
    }

    /**
     * 登出
     *
     * @param authHeader Authorization header
     * @return 结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = tokenService.resolveToken(authHeader);
        tokenService.removeToken(token);
        return Result.ok();
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息（基本信息 + 角色列表）
     */
    @GetMapping("/user-info")
    public Result<UserInfoVO> userInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );

        List<Long> roleIds = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId())
        ).stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

        List<String> roleCodes = List.of();
        if (!roleIds.isEmpty()) {
            roleCodes = roleMapper.selectBatchIds(roleIds).stream()
                    .filter(r -> r.getStatus() == 1)
                    .map(SysRole::getCode)
                    .collect(Collectors.toList());
        }

        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRoles(roleCodes);

        return Result.ok(vo);
    }
}
