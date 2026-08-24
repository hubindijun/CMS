package com.cms.common.base;

import lombok.Data;
import java.io.Serializable;

@Data
public class PageQuery implements Serializable {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
