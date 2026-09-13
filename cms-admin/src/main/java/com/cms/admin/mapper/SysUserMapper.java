package com.cms.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.dto.system.UserQueryDTO;
import com.cms.admin.entity.SysUser;
import com.cms.admin.vo.system.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * 系统用户 Mapper 接口
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 分页查询用户列表（含角色信息）
     */
    List<UserVO> selectPageList(@Param("dto") UserQueryDTO dto);

    /**
     * 根据用户名查询用户
     */
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     */
    SysUser selectByPhone(@Param("phone") String phone);

    /**
     * 根据手机号查询用户（排除指定ID）
     */
    SysUser selectByPhoneExcludeId(@Param("phone") String phone, @Param("excludeId") Long excludeId);

    /**
     * 根据ID查询用户详情（含角色信息）
     */
    UserVO selectUserVoById(@Param("id") Long id);
}
