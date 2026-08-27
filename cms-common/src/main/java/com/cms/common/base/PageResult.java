package com.cms.common.base;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 *
 * @param <T> 数据项类型
 */
@Data
public class PageResult<T> implements Serializable {
    /** 数据列表 */
    private List<T> list;
    /** 总条数 */
    private long total;
    /** 当前页码 */
    private long pageNum;
    /** 每页大小 */
    private long pageSize;

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> of(List<T> list, long total, long pageNum, long pageSize) {
        PageResult<T> r = new PageResult<>();
        r.setList(list);
        r.setTotal(total);
        r.setPageNum(pageNum);
        r.setPageSize(pageSize);
        return r;
    }
}
