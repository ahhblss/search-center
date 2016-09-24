package com.shaw.service;

import com.shaw.result.Result;
import com.shaw.vo.DmhyDataVo;

import java.util.List;

/**
 * Created by shaw on 2016/9/14 0014.
 */
public interface IDmhyDataSearchService {
    /**
     * 根据关键字搜索 花园动画资源。
     **/
    Result<List<DmhyDataVo>> searchDmhyData(String keyword, Integer searchNum);
}
