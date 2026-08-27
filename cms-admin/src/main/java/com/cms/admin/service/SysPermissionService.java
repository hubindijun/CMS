package com.cms.admin.service;

import com.cms.admin.dto.system.PermissionAddDTO;
import com.cms.admin.dto.system.PermissionUpdateDTO;
import com.cms.admin.vo.system.PermissionVO;
import java.util.List;

/**
 * 系统权限（菜单）服务
 */
public interface SysPermissionService {
    /**
     * 查询全部权限树（目录+菜单+按钮）
     *
     * @return 权限树
     */
    List<PermissionVO> tree();

    /**
     * 根据用户ID查询菜单树（仅目录和菜单，不含按钮）
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    List<PermissionVO> menuTreeByUserId(Long userId);

    /**
     * 新增权限
     *
     * @param dto 权限信息
     */
    void add(PermissionAddDTO dto);

    /**
     * 修改权限
     *
     * @param dto 权限信息
     */
    void update(PermissionUpdateDTO dto);

    /**
     * 删除权限（逻辑删除）
     *
     * @param id 权限ID
     */
    void delete(Long id);
}
