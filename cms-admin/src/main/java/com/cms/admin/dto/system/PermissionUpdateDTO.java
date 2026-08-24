package com.cms.admin.dto.system;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PermissionUpdateDTO {
    @NotNull(message = "ID不能为空")
    private Long id;
    private Long parentId;
    private String name;
    private Integer type;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sort;
    private Integer status;
}
