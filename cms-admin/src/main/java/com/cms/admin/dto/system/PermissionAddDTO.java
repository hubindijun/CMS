package com.cms.admin.dto.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
/**
 * 权限新增请求参数
 */
public class PermissionAddDTO {
    private Long parentId;
    @NotBlank(message = "权限名称不能为空")
    private String name;
    @NotNull(message = "权限类型不能为空")
    private Integer type;
    private String path;
    private String component;
    private String resourcePath;
    private String icon;
    private Integer sort;
    private Integer status;
}
