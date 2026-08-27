package com.cms.admin.dto.system;

import com.cms.common.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
/**
 * 字典数据查询参数
 */
public class DictDataQueryDTO extends PageQuery {
    private String dictType;
    private String dictLabel;
    private Integer status;
}
