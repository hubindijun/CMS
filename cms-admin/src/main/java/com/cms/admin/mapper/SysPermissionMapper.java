package com.cms.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * 系统权限 Mapper 接口
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 查询用户拥有的所有权限
     */
    List<SysPermission> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询用户拥有的资源路径规则列表（去重，过滤空值和禁用的）
     */
    List<String> selectResourcePathsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询权限列表
     */
    List<SysPermission> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询所有权限（按sort排序）
     */
    List<SysPermission> selectAllOrdered();

    /**
     * 统计子级权限数量
     */
    Long countByParentId(@Param("parentId") Long parentId);
}
