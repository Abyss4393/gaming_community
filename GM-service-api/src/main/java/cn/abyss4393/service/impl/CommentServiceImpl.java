package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.CommentMapper;
import cn.abyss4393.mapper.UserMapper;
import cn.abyss4393.po.Comment;
import cn.abyss4393.po.ManagerNotification;
import cn.abyss4393.po.User;
import cn.abyss4393.po.UserNotification;
import cn.abyss4393.service.ICommentService;
import cn.abyss4393.utils.common.PageUtils;
import cn.abyss4393.utils.file.FileUtils;
import cn.abyss4393.utils.rabbitmq.RabbitMQConstantUtils;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import cn.abyss4393.vo.CommentVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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
    private RabbitTemplate rabbitTemplate;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommentMapper commentMapper;

    @Transactional
    @Override
    public ResultFul<?> postComment(Comment comment) {
        if (StringUtils.checkValNull(comment))
            return ResultFul.fail(BaseCode.MISS_PARAMS);
        String content = comment.getContent();
        if (!"".equals(content) && content.contains("<img")) {
            List<String> replacePaths = FileUtils.handlerBase64Content(content);
            assert replacePaths != null;
            String replace = FileUtils.replace(content, replacePaths);
            comment.setContent(replace);
        }
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Comment::getAId, comment.getAId());
        lambdaQueryWrapper.eq(Comment::getUId, comment.getUId());
        final boolean exists = commentMapper.exists(lambdaQueryWrapper);
        if (!exists) {
            comment.setId(Math.toIntExact(commentMapper.selectCount(null)) + 1);
            comment.setCommentTime(TimeStampUtil.getTimestamp());
            Comment.init(comment);
            commentMapper.insert(comment);
            String msg = "有新的评论发布，请及时处理";
            Map<String, Object> messageBodyForManager = RabbitMQConstantUtils.createMessageBodyForManager(msg,
                    String.valueOf(ManagerNotification.NOTIFICATION_TYPE.COMMENT));
            rabbitTemplate.convertAndSend(RabbitMQConstantUtils.DIRECT_EXCHANGE, RabbitMQConstantUtils.MANAGE_QUEUE, messageBodyForManager);
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
        return ResultFul.success(BaseCode.SUCCESS, comments);
    }

    @Override
    public ResultFul<?> getBatchComments(Integer currentPage, Integer pageSize) {
        if (null == currentPage || null == pageSize) return ResultFul.fail(BaseCode.ERROR);
        Page<Comment> page = new Page<>(currentPage, pageSize);
        Page<Comment> commentPage = commentMapper.selectPage(page, null);
        List<Comment> records = commentPage.getRecords();
        records.forEach(item -> {
            int uid = item.getUId();
            User simpleUserInfo = userMapper.getSimpleUserInfo(uid);
            item.setNickname(simpleUserInfo.getNickname());
            item.setAvatar(simpleUserInfo.getAvatar());
        });
        commentPage.setRecords(records);
        // 返回结果
        Map<String, Object> commentResult = PageUtils.createPageResultMap(commentPage);
        return ResultFul.success(BaseCode.SUCCESS, commentResult);
    }

    @Transactional
    @Override
    public ResultFul<?> delCommentByIds(Serializable aid, Serializable uid) {
        if (StringUtils.checkValNull(aid) || StringUtils.checkValNull(uid))
            return ResultFul.fail(BaseCode.MISS_PARAMS);
        LambdaQueryWrapper<Comment> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(Comment::getAId, aid);
        existWrapper.eq(Comment::getUId, uid);
        final boolean exists = commentMapper.exists(existWrapper);
        if (exists) {
            LambdaQueryWrapper<Comment> delWrapper = new LambdaQueryWrapper<>();
            delWrapper.eq(Comment::getUId, uid);
            delWrapper.orderByDesc(Comment::getUId);
            final int delete = commentMapper.delete(delWrapper);
            final boolean sort = commentMapper.sort();
            return delete != 0 && sort ? ResultFul.success(BaseCode.DELETE) :
                    ResultFul.fail(BaseCode.DELETE_ERROR);
        }
        return ResultFul.fail(BaseCode.DELETE_ERROR);


    }

    @Override
    public ResultFul<?> searchComment(String keyword, Integer currentPage, Integer pageSize) {
        Map<String, Object> searchResult = new HashMap<>();
        Page<Comment> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<User> userLike = userLambdaQueryWrapper.like(User::getNickname, keyword);
        List<User> users = userMapper.selectList(userLike);
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.or().like(Comment::getId, keyword);
        lambdaQueryWrapper.or().like(Comment::getContent, keyword);
        if (0 != users.size()) {
            Set<Integer> ids = users.stream().map(User::getId).collect(Collectors.toSet());
            for (int id : ids) {
                lambdaQueryWrapper.or().like(Comment::getUId, id);
            }
        }
        IPage<Comment> commentIPage = commentMapper.selectPage(page, lambdaQueryWrapper);
        List<Comment> records = commentIPage.getRecords();
        records.forEach(item -> {
            int uid = item.getUId();
            User simpleUserInfo = userMapper.getSimpleUserInfo(uid);
            item.setNickname(simpleUserInfo.getNickname());
            item.setAvatar(simpleUserInfo.getAvatar());
        });
        commentIPage.setRecords(records);
        searchResult.put("data", commentIPage.getRecords());
        searchResult.put("currentPage", commentIPage.getCurrent());
        searchResult.put("pageSize", commentIPage.getSize());
        searchResult.put("total", commentIPage.getTotal());
        return ResultFul.success(BaseCode.SUCCESS, searchResult);
    }

    @Override
    public ResultFul<?> sortBy(Integer aid, Integer sortTypeDesc) {
        List<CommentVo> commentVos = new ArrayList<>();
        LambdaQueryWrapper<Comment> sort = Wrappers.lambdaQuery();
        sort.eq(Comment::getAId, aid);
        if (Comment.OrderBy.HOT.getOrderType() == sortTypeDesc)
            sort.orderByAsc(Comment::getCommentLike);
        else if (Comment.OrderBy.NEW.getOrderType() == sortTypeDesc) {
            sort.orderByDesc(Comment::getCommentTime);
        } else if (Comment.OrderBy.LATER.getOrderType() == sortTypeDesc)
            sort.orderByAsc(Comment::getCommentTime);
        List<Comment> comments = commentMapper.selectList(sort);
        comments.forEach(comment -> {
            CommentVo commentVo = new CommentVo();
            User simpleUserInfo = userMapper.getSimpleUserInfo(comment.getUId());
            simpleUserInfo.setId(comment.getUId());
            commentVo.setComment(comment);
            commentVo.setUser(simpleUserInfo);
            commentVos.add(commentVo);
        });
        return ResultFul.success(BaseCode.SUCCESS, commentVos);
    }

    @Override
    public ResultFul<?> like(Integer id, Integer from, Integer to) {
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = Wrappers.lambdaQuery();
        commentLambdaQueryWrapper.eq(Comment::getId, id);
        Comment comment = commentMapper.selectOne(commentLambdaQueryWrapper);
        int like = comment.getCommentLike();
        like += 1;
        comment.setCommentLike(like);
        commentMapper.updateById(comment);
        User user = userMapper.getSimpleUserInfo(from);
        Map<String, User> userMap = Map.of("user", user);
        Map<String, Object> messageBodyForUser = RabbitMQConstantUtils.createMessageBodyForUser(to,
                String.valueOf(UserNotification.NOTIFICATION_TYPE.COMMENT), userMap.toString());
        rabbitTemplate.convertAndSend(RabbitMQConstantUtils.DIRECT_EXCHANGE, RabbitMQConstantUtils.USER_QUEUE, messageBodyForUser);
        return ResultFul.success(BaseCode.UPVOTE_FAIL);
    }

    @Override
    public ResultFul<?> dislike(Integer id) {
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = Wrappers.lambdaQuery();
        commentLambdaQueryWrapper.eq(Comment::getId, id);
        Comment comment = commentMapper.selectOne(commentLambdaQueryWrapper);
        int dislike = comment.getCommentDislike();
        dislike += 1;
        comment.setCommentDislike(dislike);
        commentMapper.updateById(comment);
        return ResultFul.success(BaseCode.DISLIKE);

    }
}
