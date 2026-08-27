package com.cms.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 系统用户 Mapper 接口
 */
public interface SysUserMapper extends BaseMapper<SysUser> {
}
