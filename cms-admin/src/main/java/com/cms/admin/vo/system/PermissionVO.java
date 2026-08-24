package com.cms.admin.vo.system;

import lombok.Data;
import java.util.List;

@Data
public class PermissionVO {
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
    private List<PermissionVO> children;
}
