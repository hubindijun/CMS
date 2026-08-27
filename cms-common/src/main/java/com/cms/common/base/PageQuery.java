package com.cms.common.base;

import lombok.Data;
import java.io.Serializable;

/**
 * 分页查询参数基类
 */
@Data
public class PageQuery implements Serializable {
    /** 页码，默认 1 */
    private Integer pageNum = 1;
    /** 每页大小，默认 10 */
    private Integer pageSize = 10;
}
