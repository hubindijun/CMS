package com.cms.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.dto.system.DictDataQueryDTO;
import com.cms.admin.entity.SysDictData;
import com.cms.admin.vo.system.DictDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * 字典数据 Mapper 接口
 */
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 分页查询字典数据
     */
    List<DictDataVO> selectPageList(@Param("dto") DictDataQueryDTO dto);

    /**
     * 根据字典类型查询启用的数据列表
     */
    List<DictDataVO> selectByDictType(@Param("dictType") String dictType);
}
