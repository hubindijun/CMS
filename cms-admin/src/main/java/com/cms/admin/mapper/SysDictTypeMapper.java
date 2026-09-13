package com.cms.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.dto.system.DictTypeQueryDTO;
import com.cms.admin.entity.SysDictType;
import com.cms.admin.vo.system.DictTypeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * 字典类型 Mapper 接口
 */
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    /**
     * 分页查询字典类型
     */
    List<DictTypeVO> selectPageList(@Param("dto") DictTypeQueryDTO dto);

    /**
     * 查询所有启用的字典类型
     */
    List<DictTypeVO> selectAllEnabled();

    /**
     * 根据字典类型查询
     */
    SysDictType selectByDictType(@Param("dictType") String dictType);

    /**
     * 根据字典类型查询（排除指定ID）
     */
    SysDictType selectByDictTypeExcludeId(@Param("dictType") String dictType, @Param("excludeId") Long excludeId);
}
