package com.cms.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
/**
 * 系统权限 Mapper 接口
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    /**
     * 查询用户拥有的所有权限
     */
    List<SysPermission> selectByUserId(Long userId);

    /**
     * 查询用户拥有的资源路径规则列表（去重，过滤空值和禁用的）
     */
    List<String> selectResourcePathsByUserId(Long userId);

    List<SysPermission> selectByRoleId(Long roleId);
}
