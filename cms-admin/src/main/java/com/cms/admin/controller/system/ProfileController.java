package com.cms.admin.controller.system;

import com.cms.admin.dto.system.ChangePasswordDTO;
import com.cms.admin.dto.system.ProfileUpdateDTO;
import com.cms.admin.service.ProfileService;
import com.cms.admin.vo.system.UserVO;
import com.cms.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 个人中心接口
 */
@RestController
@RequestMapping("/api/system/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 获取当前登录用户的个人资料
     */
    @GetMapping
    public Result<UserVO> getProfile() {
        String username = getCurrentUsername();
        return Result.ok(profileService.getProfile(username));
    }

    /**
     * 修改当前登录用户的个人资料
     */
    @PutMapping
    public Result<Void> updateProfile(@RequestBody ProfileUpdateDTO dto) {
        String username = getCurrentUsername();
        profileService.updateProfile(username, dto);
        return Result.ok();
    }

    /**
     * 修改当前登录用户的密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody @Valid ChangePasswordDTO dto) {
        String username = getCurrentUsername();
        profileService.changePassword(username, dto);
        return Result.ok();
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
