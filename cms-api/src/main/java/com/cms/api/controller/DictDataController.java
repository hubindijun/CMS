package com.cms.api.controller;

import com.cms.api.dto.DictDataQueryDTO;
import com.cms.api.service.ApiDictDataService;
import com.cms.api.vo.DictDataVO;
import com.cms.common.base.PageResult;
import com.cms.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open/dict/data")
@RequiredArgsConstructor
public class DictDataController {

    private final ApiDictDataService dictDataService;

    @GetMapping("/page")
    public Result<PageResult<DictDataVO>> page(DictDataQueryDTO dto) {
        return Result.ok(dictDataService.page(dto));
    }
}
