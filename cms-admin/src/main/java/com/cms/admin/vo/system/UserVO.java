package com.cms.admin.vo.system;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private List<Long> roleIds;
    private List<String> roleNames;
    private LocalDateTime createTime;
}
