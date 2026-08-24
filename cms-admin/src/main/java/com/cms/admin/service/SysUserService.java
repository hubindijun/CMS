package com.cms.admin.service;

import com.cms.admin.dto.system.UserAddDTO;
import com.cms.admin.dto.system.UserQueryDTO;
import com.cms.admin.dto.system.UserUpdateDTO;
import com.cms.admin.vo.system.UserVO;
import com.cms.common.base.PageResult;

public interface SysUserService {
    PageResult<UserVO> page(UserQueryDTO dto);
    void add(UserAddDTO dto);
    void update(UserUpdateDTO dto);
    void delete(Long id);
    void toggleStatus(Long id, Integer status);
    void resetPassword(Long id, String newPassword);
    UserVO getById(Long id);
}
