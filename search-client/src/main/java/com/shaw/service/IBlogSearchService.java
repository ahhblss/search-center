package com.shaw.service;

import com.shaw.result.Result;
import com.shaw.vo.BlogVo;

import java.util.List;

/**
 * Created by shaw on 2016/9/14 0014.
 */
public interface IBlogSearchService {
    /**
     * 根据关键字搜索博客
     */
    Result<List<BlogVo>> searchBlog(String keyword,Integer searchNum);
}
