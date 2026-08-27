package com.cms.admin.dto.system;

import lombok.Data;

@Data
/**
 * 个人信息修改请求参数
 */
public class ProfileUpdateDTO {
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
}
