package com.cms.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * 角色权限关联 Mapper 接口
 */
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /**
     * 根据角色ID查询关联列表
     */
    List<SysRolePermission> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色ID删除关联
     */
    void deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色权限关联
     */
    void batchInsert(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);
}
