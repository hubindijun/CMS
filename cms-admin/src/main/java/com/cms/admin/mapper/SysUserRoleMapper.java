package com.cms.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * 用户角色关联 Mapper 接口
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户ID查询关联列表
     */
    List<SysUserRole> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID删除关联
     */
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 批量插入用户角色关联
     */
    void batchInsert(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
}
