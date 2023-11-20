package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.CommentMapper;
import cn.abyss4393.po.Comment;
import cn.abyss4393.service.ICommentService;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className CommentServiceImpl
 * @description TODO
 * @date 19/11/2023
 * @completion false
 */
@Service
public class CommentServiceImpl implements ICommentService {

    @Resource
    private CommentMapper commentMapper;

    @Override
    public ResultFul<?> postComment(Comment comment) {
        System.out.println(comment);
        if (StringUtils.checkValNull(comment))
            return ResultFul.fail(BaseCode.MISS_PARAMS);
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Comment::getAId, comment.getAId());
        lambdaQueryWrapper.eq(Comment::getUId, comment.getUId());
        final boolean exists = commentMapper.exists(lambdaQueryWrapper);
        if (!exists) {
            comment.setId(Math.toIntExact(commentMapper.selectCount(null)) + 1);
            comment.setReplyTime(TimeStampUtil.getTimestamp());
            commentMapper.insert(comment);
            return ResultFul.success(BaseCode.COMMENT_SUCCESS);
        }
        return ResultFul.fail(BaseCode.COLLECT_FAIL);
    }
}
