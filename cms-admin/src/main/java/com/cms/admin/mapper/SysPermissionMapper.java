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
    List<SysPermission> selectByUserId(Long userId);
    List<SysPermission> selectByRoleId(Long roleId);
}
