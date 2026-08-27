package com.cms.admin.dto.system;

import com.cms.common.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
/**
 * 用户查询参数
 */
public class UserQueryDTO extends PageQuery {
    private String username;
    private String phone;
    private Integer status;
}
