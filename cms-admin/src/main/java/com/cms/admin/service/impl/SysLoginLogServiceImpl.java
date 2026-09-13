package com.cms.admin.service.impl;

import com.cms.admin.dto.system.LoginLogQueryDTO;
import com.cms.admin.mapper.SysLoginLogMapper;
import com.cms.admin.service.SysLoginLogService;
import com.cms.admin.vo.system.LoginLogVO;
import com.cms.common.base.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 登录日志服务实现
 */
@Service
@RequiredArgsConstructor
public class SysLoginLogServiceImpl implements SysLoginLogService {

    private final SysLoginLogMapper loginLogMapper;

    @Override
    public PageResult<LoginLogVO> page(LoginLogQueryDTO dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<LoginLogVO> list = loginLogMapper.selectPageList(dto);
        PageInfo<LoginLogVO> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getList(), pageInfo.getTotal(), dto.getPageNum(), dto.getPageSize());
    }

    @Override
    public void delete(Long id) {
        loginLogMapper.deleteById(id);
    }

    @Override
    public void clear() {
        loginLogMapper.clearAll();
    }
}
