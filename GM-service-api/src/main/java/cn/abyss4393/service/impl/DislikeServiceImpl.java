package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.DisLikeMapper;
import cn.abyss4393.po.DisLike;
import cn.abyss4393.service.IDislikeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className DislikeServiceImpl
 * @description TODO
 * @date 2024/3/11
 * @completion false
 */

@Service
public class DislikeServiceImpl implements IDislikeService {
    @Resource
    private DisLikeMapper disLikeMapper;

    @Override
    public ResultFul<?> isDislike(Serializable uid, Serializable aid) {
        if (StringUtils.checkValNull(uid) || StringUtils.checkValNull(aid))
            return ResultFul.fail(BaseCode.MISS_PARAMS);
        LambdaQueryWrapper<DisLike> disLikeLambdaQueryWrapper = Wrappers.lambdaQuery();
        disLikeLambdaQueryWrapper.eq(DisLike::getUId, uid);
        disLikeLambdaQueryWrapper.eq(DisLike::getAId, aid);
        boolean exists = disLikeMapper.exists(disLikeLambdaQueryWrapper);
        return ResultFul.success(BaseCode.SUCCESS, new HashMap<>() {{
            this.put("isDislike", exists);
        }});
    }

}
