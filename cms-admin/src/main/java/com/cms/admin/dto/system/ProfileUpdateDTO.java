package com.cms.admin.dto.system;

import lombok.Data;

@Data
public class ProfileUpdateDTO {
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
}
