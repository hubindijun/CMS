package com.cms.admin.vo.system;

import lombok.Data;
import java.time.LocalDateTime;

@Data
/**
 * 字典数据视图对象
 */
public class DictDataVO {
    private Long id;
    private Integer dictSort;
    private String dictLabel;
    private String dictValue;
    private String dictType;
    private String cssClass;
    private String listClass;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
}
