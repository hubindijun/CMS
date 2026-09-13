package com.cms.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.dto.system.RoleQueryDTO;
import com.cms.admin.entity.SysRole;
import com.cms.admin.vo.system.RoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * 系统角色 Mapper 接口
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 分页查询角色列表
     */
    List<RoleVO> selectPageList(@Param("dto") RoleQueryDTO dto);

    /**
     * 查询所有启用的角色
     */
    List<RoleVO> selectAllEnabled();

    /**
     * 根据角色编码查询
     */
    SysRole selectByCode(@Param("code") String code);

    /**
     * 根据用户ID查询角色列表
     */
    List<SysRole> selectByUserId(@Param("userId") Long userId);
}
