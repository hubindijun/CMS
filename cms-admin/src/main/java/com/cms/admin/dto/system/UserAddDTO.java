package com.cms.admin.dto.system;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
/**
 * 用户新增请求参数
 */
public class UserAddDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
    private List<Long> roleIds;
}
