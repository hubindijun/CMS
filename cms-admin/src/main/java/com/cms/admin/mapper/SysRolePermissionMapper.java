package com.cms.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 角色权限关联 Mapper 接口
 */
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {
}
