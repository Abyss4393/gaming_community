package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.ReplyMapper;
import cn.abyss4393.po.Reply;
import cn.abyss4393.service.IReplyService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ReplyServiceImpl
 * @description TODO
 * @date 26/11/2023
 * @completion false
 */
@Service
public class ReplyServiceImpl implements IReplyService {

    @Resource
    private ReplyMapper replyMapper;

    @Override
    public ResultFul<?> getRepliesByUId(Serializable uid) {
        if (StringUtils.checkValNull(uid))
            return ResultFul.fail(BaseCode.MISS_PARAMS);
        LambdaQueryWrapper<Reply> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Reply::getUserId,uid);
        lambdaQueryWrapper.orderByDesc(Reply::getUserId);
        final List<Reply> replies = replyMapper.selectList(lambdaQueryWrapper);
        return ResultFul.success(BaseCode.SUCCESS,replies);
    }
}
