package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.UpvoteMapper;
import cn.abyss4393.po.Upvote;
import cn.abyss4393.service.IUpvoteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className UpvoteServiceImpl
 * @description TODO
 * @date 20/11/2023
 * @completion false
 */
@Service
public class UpvoteServiceImpl implements IUpvoteService {

    @Resource
    private UpvoteMapper upvoteMapper;

    @Override
    public ResultFul<?> isUpvote(Serializable uid, Serializable aid) {
        if (StringUtils.checkValNull(uid) || StringUtils.checkValNull(aid))
            return ResultFul.fail(BaseCode.MISS_PARAMS);
        LambdaQueryWrapper<Upvote> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Upvote::getUId, uid);
        lambdaQueryWrapper.eq(Upvote::getAId, aid);
        final boolean exists = upvoteMapper.exists(lambdaQueryWrapper);
        return ResultFul.success(BaseCode.SUCCESS,new HashMap<>(){{
            this.put("isUpvote",exists);
        }});
    }
}
