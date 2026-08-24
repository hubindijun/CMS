package com.cms.admin.dto.system;

import com.cms.common.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DictTypeQueryDTO extends PageQuery {
    private String dictName;
    private String dictType;
    private Integer status;
}
