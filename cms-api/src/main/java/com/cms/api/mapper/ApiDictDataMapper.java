package com.cms.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.api.dto.DictDataQueryDTO;
import com.cms.api.entity.ApiDictData;
import com.cms.api.vo.DictDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApiDictDataMapper extends BaseMapper<ApiDictData> {

    List<DictDataVO> selectPageList(@Param("dto") DictDataQueryDTO dto);
}
