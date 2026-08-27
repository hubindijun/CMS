package com.cms.admin.dto.system;

import com.cms.common.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
/**
 * 登录日志查询参数
 */
public class LoginLogQueryDTO extends PageQuery {
    private String username;
    private Integer status;
    private String startTime;
    private String endTime;
}
