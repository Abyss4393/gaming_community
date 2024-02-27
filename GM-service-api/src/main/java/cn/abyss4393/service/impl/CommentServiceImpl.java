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

import java.io.Serializable;
import java.util.List;

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
        if (StringUtils.checkValNull(comment))
            return ResultFul.fail(BaseCode.MISS_PARAMS);
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Comment::getAId, comment.getAId());
        lambdaQueryWrapper.eq(Comment::getUId, comment.getUId());
        final boolean exists = commentMapper.exists(lambdaQueryWrapper);
        if (!exists) {
            comment.setId(Math.toIntExact(commentMapper.selectCount(null)) + 1);
            comment.setCommentTime(TimeStampUtil.getTimestamp());
            commentMapper.insert(comment);
            return ResultFul.success(BaseCode.COMMENT_SUCCESS);
        }
        return ResultFul.fail(BaseCode.COMMENT_ERROR);
    }

    @Override
    public ResultFul<?> getCommentByUid(Serializable uid) {
        if (StringUtils.checkValNull(uid))
            return ResultFul.fail(BaseCode.MISS_PARAMS);
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Comment::getUId, uid);
        lambdaQueryWrapper.orderByDesc(Comment::getId);
        final List<Comment> comments = commentMapper.selectList(lambdaQueryWrapper);
        return ResultFul.success(BaseCode.SUCCESS,comments);
    }

    @Override
    public ResultFul<?> delCommentByIds(Serializable aid,Serializable uid) {
        if (StringUtils.checkValNull(aid) || StringUtils.checkValNull(uid))
            return ResultFul.fail(BaseCode.MISS_PARAMS);
        LambdaQueryWrapper<Comment> exitWrapper = new LambdaQueryWrapper<>();
        exitWrapper.eq(Comment::getAId, aid);
        exitWrapper.eq(Comment::getUId,uid);
        final boolean exists = commentMapper.exists(exitWrapper);
        if(exists){
            LambdaQueryWrapper<Comment> delWrapper = new LambdaQueryWrapper<>();
            delWrapper.eq(Comment::getUId,uid);
            delWrapper.orderByDesc(Comment::getUId);
            final int delete = commentMapper.delete(delWrapper);
            final boolean sort = commentMapper.sort();
            return delete != 0 && sort ? ResultFul.success(BaseCode.DELETE) :
                    ResultFul.fail(BaseCode.DELETE_ERROR);
        }
        return ResultFul.fail(BaseCode.DELETE_ERROR);
    }
}
