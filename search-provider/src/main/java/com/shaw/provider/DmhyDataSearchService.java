package com.shaw.provider;

import com.shaw.lucene.DmhyDataIndex;
import com.shaw.result.Result;
import com.shaw.result.ResultSupport;
import com.shaw.service.IDmhyDataSearchService;
import com.shaw.vo.DmhyDataVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by shaw on 2016/9/14 0014.
 */
@Service
public class DmhyDataSearchService implements IDmhyDataSearchService {
    @Resource
    private DmhyDataIndex dmhyDataIndex;

    @Override
    public Result<List<DmhyDataVo>> searchDmhyData(String keyword, Integer searchNum) {
        ResultSupport<List<DmhyDataVo>> resultSupport = new ResultSupport<List<DmhyDataVo>>();
        try {
            List<DmhyDataVo> voList = dmhyDataIndex.searchAnime(keyword, searchNum);
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
