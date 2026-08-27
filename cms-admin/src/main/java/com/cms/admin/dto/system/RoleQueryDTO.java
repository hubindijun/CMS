package com.cms.admin.dto.system;

import com.cms.common.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
/**
 * 角色查询参数
 */
public class RoleQueryDTO extends PageQuery {
    private String name;
    private Integer status;
}
