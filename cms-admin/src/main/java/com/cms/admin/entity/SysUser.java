package com.cms.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cms.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
/**
 * 系统用户实体
 */
public class SysUser extends BaseEntity {
    private String username;

    @JsonIgnore
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
}
