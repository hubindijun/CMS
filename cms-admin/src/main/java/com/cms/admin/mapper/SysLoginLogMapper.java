package com.cms.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.dto.system.LoginLogQueryDTO;
import com.cms.admin.entity.SysLoginLog;
import com.cms.admin.vo.system.LoginLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * 登录日志 Mapper 接口
 */
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {

    /**
     * 分页查询登录日志
     */
    List<LoginLogVO> selectPageList(@Param("dto") LoginLogQueryDTO dto);

    /**
     * 清空所有日志
     */
    void clearAll();
}
