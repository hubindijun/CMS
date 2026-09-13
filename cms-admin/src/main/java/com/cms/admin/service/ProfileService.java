package com.cms.admin.service;

import com.cms.admin.dto.system.ChangePasswordDTO;
import com.cms.admin.dto.system.ProfileUpdateDTO;
import com.cms.admin.vo.system.UserVO;

/**
 * 个人中心服务
 */
public interface ProfileService {

    /**
     * 获取当前登录用户的个人资料
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserVO getProfile(String username);

    /**
     * 修改当前登录用户的个人资料
     *
     * @param username 用户名
     * @param dto      资料信息
     */
    void updateProfile(String username, ProfileUpdateDTO dto);

    /**
     * 修改当前登录用户的密码
     *
     * @param username 用户名
     * @param dto      新旧密码
     */
    void changePassword(String username, ChangePasswordDTO dto);
}
