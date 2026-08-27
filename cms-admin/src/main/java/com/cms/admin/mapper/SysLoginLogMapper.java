package com.cms.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.entity.SysLoginLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 登录日志 Mapper 接口
 */
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {
}
