package com.cms.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.admin.dto.system.LoginLogQueryDTO;
import com.cms.admin.entity.SysLoginLog;
import com.cms.admin.mapper.SysLoginLogMapper;
import com.cms.admin.service.SysLoginLogService;
import com.cms.admin.vo.system.LoginLogVO;
import com.cms.common.base.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysLoginLogServiceImpl implements SysLoginLogService {

    private final SysLoginLogMapper loginLogMapper;

    @Override
    public PageResult<LoginLogVO> page(LoginLogQueryDTO dto) {
        Page<SysLoginLog> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getUsername())) {
            wrapper.like(SysLoginLog::getUsername, dto.getUsername());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(SysLoginLog::getStatus, dto.getStatus());
        }
        if (StringUtils.hasText(dto.getStartTime())) {
            wrapper.ge(SysLoginLog::getLoginTime, LocalDateTime.parse(dto.getStartTime() + "T00:00:00"));
        }
        if (StringUtils.hasText(dto.getEndTime())) {
            wrapper.le(SysLoginLog::getLoginTime, LocalDateTime.parse(dto.getEndTime() + "T23:59:59"));
        }
        wrapper.orderByDesc(SysLoginLog::getLoginTime);
        Page<SysLoginLog> result = loginLogMapper.selectPage(page, wrapper);

        List<LoginLogVO> voList = result.getRecords().stream().map(log -> {
            LoginLogVO vo = new LoginLogVO();
            BeanUtils.copyProperties(log, vo);
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), dto.getPageNum(), dto.getPageSize());
    }

    @Override
    public void delete(Long id) {
        loginLogMapper.deleteById(id);
    }

    @Override
    public void clear() {
        loginLogMapper.delete(null);
    }
}
