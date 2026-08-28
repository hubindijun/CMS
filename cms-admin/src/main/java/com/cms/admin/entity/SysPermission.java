package com.cms.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cms.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
/**
 * 系统权限（菜单）实体
 */
public class SysPermission extends BaseEntity {
    private Long parentId;
    private String name;
    private Integer type;
    private String path;
    private String component;
    private String resourcePath;
    private String icon;
    private Integer sort;
    private Integer status;
}
