package com.cms.admin.dto.system;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
/**
 * 角色修改请求参数
 */
public class RoleUpdateDTO {
    @NotNull(message = "ID不能为空")
    private Long id;
    private String name;
    private String description;
    private Integer status;
    private List<Long> permissionIds;
}
