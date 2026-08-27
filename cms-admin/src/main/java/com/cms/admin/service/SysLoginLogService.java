package com.cms.admin.service;

import com.cms.admin.dto.system.LoginLogQueryDTO;
import com.cms.admin.vo.system.LoginLogVO;
import com.cms.common.base.PageResult;

/**
 * 登录日志服务
 */
public interface SysLoginLogService {
    /**
     * 分页查询登录日志
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    PageResult<LoginLogVO> page(LoginLogQueryDTO dto);

    /**
     * 删除登录日志
     *
     * @param id 日志ID
     */
    void delete(Long id);

    /**
     * 清空登录日志
     */
    void clear();
}
