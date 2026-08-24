package com.cms.admin.service;

import com.cms.admin.dto.system.PermissionAddDTO;
import com.cms.admin.dto.system.PermissionUpdateDTO;
import com.cms.admin.vo.system.PermissionVO;
import java.util.List;

public interface SysPermissionService {
    List<PermissionVO> tree();
    List<PermissionVO> menuTreeByUserId(Long userId);
    void add(PermissionAddDTO dto);
    void update(PermissionUpdateDTO dto);
    void delete(Long id);
}
