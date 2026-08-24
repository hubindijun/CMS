package com.cms.admin.vo.system;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoleVO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
}
