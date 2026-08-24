package com.cms.admin.dto.system;

import com.cms.common.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LoginLogQueryDTO extends PageQuery {
    private String username;
    private Integer status;
    private String startTime;
    private String endTime;
}
