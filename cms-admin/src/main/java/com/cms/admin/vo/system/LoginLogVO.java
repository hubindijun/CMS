package com.cms.admin.vo.system;

import lombok.Data;
import java.time.LocalDateTime;

@Data
/**
 * 登录日志视图对象
 */
public class LoginLogVO {
    private Long id;
    private String username;
    private String ip;
    private String location;
    private String browser;
    private String os;
    private Integer status;
    private String message;
    private LocalDateTime loginTime;
}
