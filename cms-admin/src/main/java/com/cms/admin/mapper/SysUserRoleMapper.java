package com.cms.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 用户角色关联 Mapper 接口
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}
