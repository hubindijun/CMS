package com.cms.admin.vo;

import lombok.Data;
import java.util.List;

@Data
/**
 * 用户信息视图对象（含权限列表）
 */
public class UserInfoVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private List<String> roles;
    private List<String> permissions;
}
