package com.cms.admin.dto.system;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
/**
 * 角色新增请求参数
 */
public class RoleAddDTO {
    @NotBlank(message = "角色名称不能为空")
    private String name;
    @NotBlank(message = "角色编码不能为空")
    private String code;
    private String description;
    private Integer status;
    private List<Long> permissionIds;
}
