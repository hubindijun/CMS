package com.cms.api.service;

import com.cms.api.dto.DictDataQueryDTO;
import com.cms.api.vo.DictDataVO;
import com.cms.common.base.PageResult;

public interface ApiDictDataService {

    PageResult<DictDataVO> page(DictDataQueryDTO dto);
}
