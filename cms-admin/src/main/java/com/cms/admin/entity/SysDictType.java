package com.cms.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cms.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
/**
 * 字典类型实体
 */
public class SysDictType extends BaseEntity {
    private String dictName;
    private String dictType;
    private Integer status;
    private String remark;
}
