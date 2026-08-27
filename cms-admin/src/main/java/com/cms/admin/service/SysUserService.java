package com.cms.admin.service;

import com.cms.admin.dto.system.UserAddDTO;
import com.cms.admin.dto.system.UserQueryDTO;
import com.cms.admin.dto.system.UserUpdateDTO;
import com.cms.admin.vo.system.UserVO;
import com.cms.common.base.PageResult;

/**
 * 系统用户服务
 */
public interface SysUserService {
    /**
     * 分页查询用户列表
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    PageResult<UserVO> page(UserQueryDTO dto);

    /**
     * 新增用户
     *
     * @param dto 用户信息
     */
    void add(UserAddDTO dto);

    /**
     * 修改用户
     *
     * @param dto 用户信息
     */
    void update(UserUpdateDTO dto);

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     */
    void delete(Long id);

    /**
     * 切换用户状态（启用/禁用）
     *
     * @param id 用户ID
     * @param status 目标状态 0启用 1禁用
     */
    void toggleStatus(Long id, Integer status);

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @param newPassword 新密码（明文）
     */
    void resetPassword(Long id, String newPassword);

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserVO getById(Long id);
}
