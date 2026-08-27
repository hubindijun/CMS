package com.cms.admin.dto.system;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
/**
 * 用户修改请求参数
 */
public class UserUpdateDTO {
    @NotNull(message = "ID不能为空")
    private Long id;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
    private List<Long> roleIds;
}
