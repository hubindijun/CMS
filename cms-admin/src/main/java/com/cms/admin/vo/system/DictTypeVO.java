package com.cms.admin.vo.system;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DictTypeVO {
    private Long id;
    private String dictName;
    private String dictType;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
}
