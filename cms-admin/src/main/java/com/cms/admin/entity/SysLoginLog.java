package com.cms.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cms.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_login_log")
/**
 * 登录日志实体
 */
public class SysLoginLog extends BaseEntity {
    private String username;
    private String ip;
    private String location;
    private String browser;
    private String os;
    private Integer status;
    private String message;
    private LocalDateTime loginTime;
}
