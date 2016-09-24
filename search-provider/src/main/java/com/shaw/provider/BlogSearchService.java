package com.shaw.provider;

import com.shaw.lucene.BlogIndex;
import com.shaw.result.Result;
import com.shaw.result.ResultSupport;
import com.shaw.service.IBlogSearchService;
import com.shaw.vo.BlogVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by shaw on 2016/9/14 0014.
 */
@Service
public class BlogSearchService implements IBlogSearchService {
    @Resource
    private BlogIndex blogIndex;

    @Override
    public Result<List<BlogVo>> searchBlog(String keyword,Integer searchNum) {
        ResultSupport<List<BlogVo>> resultSupport = new ResultSupport<List<BlogVo>>();
        try {
            List<BlogVo> voList = blogIndex.searchBlog(keyword,searchNum);
            resultSupport.setSuccess(true);
            resultSupport.setModel(voList);
            return resultSupport;
        } catch (Exception e) {
            resultSupport.setMessage(e.getMessage());
            resultSupport.setSuccess(false);
            resultSupport.setModel(null);
            return resultSupport;
        }
    }
}
