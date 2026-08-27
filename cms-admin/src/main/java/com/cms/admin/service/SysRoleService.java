package com.cms.admin.service;

import com.cms.admin.dto.system.RoleAddDTO;
import com.cms.admin.dto.system.RoleQueryDTO;
import com.cms.admin.dto.system.RoleUpdateDTO;
import com.cms.admin.vo.system.RoleVO;
import com.cms.common.base.PageResult;
import java.util.List;

/**
 * 系统角色服务
 */
public interface SysRoleService {
    /**
     * 分页查询角色列表
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    PageResult<RoleVO> page(RoleQueryDTO dto);

    /**
     * 查询所有角色（用于分配角色下拉等场景）
     *
     * @return 角色列表
     */
    List<RoleVO> listAll();

    /**
     * 新增角色
     *
     * @param dto 角色信息
     */
    void add(RoleAddDTO dto);

    /**
     * 修改角色
     *
     * @param dto 角色信息
     */
    void update(RoleUpdateDTO dto);

    /**
     * 删除角色（逻辑删除）
     *
     * @param id 角色ID
     */
    void delete(Long id);

    /**
     * 切换角色状态（启用/禁用）
     *
     * @param id 角色ID
     * @param status 目标状态 0启用 1禁用
     */
    void toggleStatus(Long id, Integer status);

    /**
     * 查询角色拥有的权限ID列表
     *
     * @param id 角色ID
     * @return 权限ID列表
     */
    List<Long> getPermissionIds(Long id);

    /**
     * 给角色分配权限（全量覆盖）
     *
     * @param id 角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(Long id, List<Long> permissionIds);
}
