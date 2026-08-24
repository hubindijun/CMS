package com.cms.admin.service;

import com.cms.admin.dto.system.LoginLogQueryDTO;
import com.cms.admin.vo.system.LoginLogVO;
import com.cms.common.base.PageResult;

public interface SysLoginLogService {
    PageResult<LoginLogVO> page(LoginLogQueryDTO dto);
    void delete(Long id);
    void clear();
}
