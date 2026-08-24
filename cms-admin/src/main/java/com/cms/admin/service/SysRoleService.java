package com.cms.admin.service;

import com.cms.admin.dto.system.RoleAddDTO;
import com.cms.admin.dto.system.RoleQueryDTO;
import com.cms.admin.dto.system.RoleUpdateDTO;
import com.cms.admin.vo.system.RoleVO;
import com.cms.common.base.PageResult;
import java.util.List;

public interface SysRoleService {
    PageResult<RoleVO> page(RoleQueryDTO dto);
    List<RoleVO> listAll();
    void add(RoleAddDTO dto);
    void update(RoleUpdateDTO dto);
    void delete(Long id);
    void toggleStatus(Long id, Integer status);
    List<Long> getPermissionIds(Long id);
    void assignPermissions(Long id, List<Long> permissionIds);
}
