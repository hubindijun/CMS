package com.cms.admin.vo.system;

import lombok.Data;
import java.time.LocalDateTime;

@Data
/**
 * 字典类型视图对象
 */
public class DictTypeVO {
    private Long id;
    private String dictName;
    private String dictType;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
}
