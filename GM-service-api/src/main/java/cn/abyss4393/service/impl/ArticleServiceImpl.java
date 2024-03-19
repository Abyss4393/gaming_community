package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.*;
import cn.abyss4393.po.Collection;
import cn.abyss4393.po.*;
import cn.abyss4393.service.IArticleService;
import cn.abyss4393.utils.file.FileUtils;
import cn.abyss4393.utils.rabbitmq.RabbitMQConstantUtils;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import cn.abyss4393.vo.CommentVo;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ArticleServiceImpl
 * @description TODO
 * @date 21/9/2023
 */
@Service
public class ArticleServiceImpl implements IArticleService {


    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UpvoteMapper upvoteMapper;

    @Resource
    private DisLikeMapper disLikeMapper;

    @Resource
    private CollectionMapper collectionMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private ReplyMapper replyMapper;

    @Override
    public ResultFul<?> getArticleById(Serializable aid) throws Exception {
        Article article = articleMapper.selectById(aid);
        if (StringUtils.checkValNull(article))
            return ResultFul.fail(BaseCode.ARGS_ERROR);
        JSONObject temp = JSONUtil.parseObj(article);
        User tempUserInfo = userMapper.getSimpleUserInfo(Objects.requireNonNull((Integer) temp.get("posterId")));
        tempUserInfo.setId(temp.getInt("posterId"));
        List<CommentVo> commentVos = new ArrayList<>();
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Comment::getAId, aid);
        final List<Comment> list = commentMapper.selectList(lambdaQueryWrapper);
        list.forEach(item -> {
            CommentVo commentvo = new CommentVo();
            commentvo.setComment(item);
            User user = userMapper.getSimpleUserInfo(item.getUId());
            user.setId(item.getUId());
            commentvo.setUser(user);
            commentVos.add(commentvo);
        });
        JSONObject tempContent = JSONUtil.parseObj(temp.getStr("content"));
        temp.set("posterData", tempUserInfo);
        temp.replace("content", tempContent);
        temp.set("comments", commentVos);
        return ResultFul.success(BaseCode.SUCCESS, temp);
    }

    @Override
    public ResultFul<?> getArticleListByPid(Serializable pid) throws Exception {
        return ResultFul.success(BaseCode.SUCCESS,articleMapper.selectList(new LambdaQueryWrapper<>(){{
            this.eq(Article::getPosterId,pid);
        }}));
    }

    @Override
    public ResultFul<?> getArticleList() throws Exception {
        JSONArray ja = JSONUtil.parseArray(Objects.requireNonNull(articleMapper.selectList(null)));
        JSONArray handlerArray = new JSONArray();
        ja.forEach(item -> {
            JSONObject temp = JSONUtil.parseObj(item);
            User tempUser = userMapper.getSimpleUserInfo(Objects.requireNonNull((Integer) temp.get("posterId")));
            JSONObject tempContent = JSONUtil.parseObj(temp.getStr("content"));
            temp.replace("content", tempContent);
            temp.set("posterData", tempUser);
            handlerArray.add(temp);
        });

        return StringUtils.checkValNotNull(handlerArray) ?
                ResultFul.success(BaseCode.SUCCESS, handlerArray) :
                ResultFul.fail(BaseCode.ERROR, null);


    }

    @Transactional
    @Override
    public ResultFul<?> postArticle(Article article) {
        if (StringUtils.checkValNull(article))
            return ResultFul.fail(BaseCode.ARGS_ERROR);
        JSONObject frontObjData = JSONUtil.parseObj(article.getContent());
        JSONArray frontJSONData = frontObjData.getJSONArray("contentList");
        List<String> replaces = new ArrayList<>();
        frontJSONData.forEach(item -> {
            JSONObject temp = JSONUtil.parseObj(item);
            String textContent = temp.getStr("text");
            if (!"".equals(textContent) && textContent.contains("<img")) {
                List<String> replacePaths = FileUtils.handlerBase64Content(textContent);
                assert replacePaths != null;
                String replace = FileUtils.replace(textContent, replacePaths);
                replaces.add(replace);
            }
        });
        for (int i = 0; i < replaces.size(); i++) {
            JSONObject tempObj = JSONUtil.parseObj(frontJSONData.get(i));
            tempObj.replace("text", replaces.get(i));
            frontJSONData.set(i, tempObj);
        }
        article.setContent(JSONUtil.toJsonStr(frontObjData));
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Article::getPosterId, article.getPosterId());
        lambdaQueryWrapper.eq(Article::getTitle, article.getTitle());
        boolean exists = articleMapper.exists(lambdaQueryWrapper);
        if (!exists) {
            article.setId(Math.toIntExact(articleMapper.selectCount(null)) + 1);
            article.setPostTime(TimeStampUtil.getTimestamp());
            Article.initDefaultAttr(article);
            articleMapper.insert(article);
            String msg = "有新的帖子发布，请及时处理";
            Map<String, Object> messageBodyForManager = RabbitMQConstantUtils.createMessageBodyForManager(msg,
                    String.valueOf(ManagerNotification.NOTIFICATION_TYPE.ARTICLE));
            rabbitTemplate.convertAndSend(RabbitMQConstantUtils.DIRECT_EXCHANGE,
                    RabbitMQConstantUtils.MANAGE_QUEUE, messageBodyForManager);
            return ResultFul.success(BaseCode.POST);
        }
        return ResultFul.fail(BaseCode.POST_FAIL);
    }

    @Transactional
    @Override
    public ResultFul<?> like(Integer uid, Integer aid) {
        if (StringUtils.checkValNull(uid) || StringUtils.checkValNull(aid))
            return ResultFul.fail(BaseCode.ARGS_ERROR);
        LambdaQueryWrapper<Upvote> existQuery = Wrappers.lambdaQuery();
        existQuery.eq(Upvote::getUId, uid).eq(Upvote::getAId, aid);
        boolean exist = upvoteMapper.exists(existQuery);
        if (exist) {
            return ResultFul.fail(BaseCode.UPVOTE_FAIL);
        }

        Upvote upvote = new Upvote() {{
            this.setId(Math.toIntExact((upvoteMapper.selectCount(null) + 1)));
            this.setUId(uid);
            this.setAId(aid);
        }};
        int insert = upvoteMapper.insert(upvote);
        Article article = new Article() {{
            this.setId(aid);
            this.setArticleLike(upvoteMapper.countLike(aid));
        }};
        int update = articleMapper.update(article, new LambdaQueryWrapper<>() {{
            this.eq(Article::getId, aid);
        }});
        return 0 != insert && 0 != update ? ResultFul.success(BaseCode.UPVOTE) :
                ResultFul.fail(BaseCode.UPVOTE_FAIL);
    }

    @Transactional
    @Override
    public ResultFul<?> addCollect(Integer aid, Integer uid) {
        if (StringUtils.checkValNull(aid) || StringUtils.checkValNull(uid))
            return ResultFul.fail(BaseCode.ARGS_ERROR);
        LambdaQueryWrapper<Collection> existQuery = Wrappers.lambdaQuery();
        existQuery.eq(Collection::getArticleId, aid).eq(Collection::getUserId, uid);
        boolean exist = collectionMapper.exists(existQuery);
        if (exist) {
            return ResultFul.fail(BaseCode.COLLECT_FAIL);
        }

        Collection collection = new Collection() {{
            this.setId(Math.toIntExact(collectionMapper.selectCount(null)) + 1);
            this.setUserId(uid);
            this.setArticleId(aid);
        }};
        int insert = collectionMapper.insert(collection);

        int countCollect = collectionMapper.countCollect(aid);

        Article article = new Article() {{
            this.setPosterId(uid);
            this.setCollectCount(countCollect);
        }};
        int update = articleMapper.update(article, new LambdaQueryWrapper<>() {{
            this.eq(Article::getId, aid);
        }});
        return 0 != update && 0 != insert ? ResultFul.success(BaseCode.COLLECT) :
                ResultFul.fail(BaseCode.COLLECT_FAIL);
    }


    @Transactional
    @Override
    public ResultFul<?> dislike(Integer uid, Integer aid) {
        if (StringUtils.checkValNull(aid))
            return ResultFul.fail(BaseCode.ARGS_ERROR);
        LambdaQueryWrapper<DisLike> existQuery = Wrappers.lambdaQuery();
        existQuery.eq(DisLike::getUId, uid).eq(DisLike::getAId, aid);
        boolean exist = disLikeMapper.exists(existQuery);
        if (exist) {
            return ResultFul.fail(BaseCode.DELETE_ERROR);
        }

        DisLike disLike = new DisLike() {{
            this.setId(Math.toIntExact(collectionMapper.selectCount(null)) + 1);
            this.setUId(uid);
            this.setAId(aid);
        }};

        int insert = disLikeMapper.insert(disLike);
        int count = disLikeMapper.countDislike(aid);

        int update = articleMapper.update(new Article() {{
            this.setId(aid);
            this.setArticleDislike(count);
        }}, new LambdaQueryWrapper<>() {{
            this.eq(Article::getId, aid);
        }});
        return 0 != update && 0 != insert ? ResultFul.success(BaseCode.DISLIKE) :
                ResultFul.fail(BaseCode.DISLIKE_FAIL);
    }

    @Transactional
    @Override
    public ResultFul<?> cancelLike(Integer uid, Integer aid) {

        if (StringUtils.checkValNull(aid) || StringUtils.checkValNull(aid))
            return ResultFul.fail(BaseCode.ERROR);
        LambdaQueryWrapper<Upvote> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(Upvote::getUId, uid).eq(Upvote::getAId, aid);
        int delete = upvoteMapper.delete(lambdaQueryWrapper);
        upvoteMapper.sort();
        int count = upvoteMapper.countLike(aid);
        Article article = new Article() {{
            this.setId(aid);
            this.setArticleLike(count);
        }};
        int update = articleMapper.update(article, new LambdaQueryWrapper<>() {{
            this.eq(Article::getId, aid);
        }});
        return 0 != delete && 0 != update ? ResultFul.success(BaseCode.SUCCESS) :
                ResultFul.fail(BaseCode.ERROR);
    }

    @Override
    public ResultFul<?> cancelDislike(Integer uid, Integer aid) {
        if (StringUtils.checkValNull(aid) || StringUtils.checkValNull(aid))
            return ResultFul.fail(BaseCode.ERROR);
        LambdaQueryWrapper<DisLike> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(DisLike::getUId, uid).eq(DisLike::getAId, aid);
        int delete = disLikeMapper.delete(lambdaQueryWrapper);
        disLikeMapper.sort();
        int count = disLikeMapper.countDislike(aid);
        Article article = new Article() {{
            this.setId(aid);
            this.setArticleDislike(count);
        }};
        int update = articleMapper.update(article, new LambdaQueryWrapper<>() {{
            this.eq(Article::getId, aid);
        }});
        return 0 != delete && 0 != update ? ResultFul.success(BaseCode.SUCCESS) :
                ResultFul.fail(BaseCode.ERROR);
    }

    @Transactional
    @Override
    public ResultFul<?> cancelCollect(Integer aid, Integer uid) {
        if (StringUtils.checkValNull(aid) || StringUtils.checkValNull(aid))
            return ResultFul.fail(BaseCode.ERROR);
        // 删除收藏记录
        LambdaQueryWrapper<Collection> collectionLambdaQueryWrapper = Wrappers.<Collection>lambdaQuery();
        collectionLambdaQueryWrapper.eq(Collection::getArticleId, aid).eq(Collection::getUserId, uid);
        int delete = collectionMapper.delete(collectionLambdaQueryWrapper);
        boolean sort = collectionMapper.sort();
        int countCollect = collectionMapper.countCollect(aid);
        Article article = new Article() {{
            this.setId(aid);
            this.setCollectCount(countCollect);
        }};
        int update = articleMapper.update(article, new LambdaQueryWrapper<>() {{
            this.eq(Article::getId, aid);
        }});
        return 0 != delete && sort && 0 != update ? ResultFul.success(BaseCode.SUCCESS) :
                ResultFul.fail(BaseCode.ERROR);
    }


    @Override
    public ResultFul<?> searchArticles(String keyword, Integer currentPage, Integer pageSize) {
        Page<Article> pageArticle = new Page<>(currentPage, pageSize);
        Map<String, Object> articlePageResult = new HashMap<>();
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Article::getTitle, keyword).
                or().like(Article::getType, keyword).
                or().like(Article::getPosterName, keyword).
                or().like(Article::getContentDes, keyword);
        Page<Article> articlePage = articleMapper.selectPage(pageArticle, queryWrapper);
        articlePageResult.put("currentPage", currentPage);
        articlePageResult.put("pageSize", pageSize);
        articlePageResult.put("total", articlePage.getTotal());
        articlePageResult.put("data", articlePage.getRecords());
        return ResultFul.success(BaseCode.SUCCESS, articlePageResult);
    }

    @Override
    public ResultFul<?> findUnapprovedArticles(Integer currentPage, Integer pageSize) {
        Page<Article> unapprovedPage = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Article> lq = new LambdaQueryWrapper<>();
        lq.eq(Article::getApproved, 0);
        Page<Article> unapprovedList = articleMapper.selectPage(unapprovedPage, lq);
        Map<String, Object> result = new HashMap<>();
        result.put("currentPage", currentPage);
        result.put("pageSize", pageSize);
        result.put("total", unapprovedList.getTotal());
        result.put("data", unapprovedList.getRecords());
        return ResultFul.success(BaseCode.SUCCESS, result);
    }

    @Transactional
    @Override
    public ResultFul<?> deleteArticleById(Serializable id) throws Exception {
        if (Objects.isNull(id)) return ResultFul.fail(BaseCode.DELETE_ERROR);
        int deleteUpvote = upvoteMapper.delete(new LambdaQueryWrapper<>() {{
            this.eq(Upvote::getAId, id);
        }});
        int deleteCollect = collectionMapper.delete(new LambdaQueryWrapper<>() {{
            this.eq(Collection::getArticleId, id);
        }});
        int deleteComment = commentMapper.delete(new LambdaQueryWrapper<>() {{
            this.eq(Comment::getAId, id);
        }});

        int delete = replyMapper.delete(new LambdaQueryWrapper<>() {{
            this.eq(Reply::getArticleId, id);
        }});

        int deleteArticle = articleMapper.deleteById(id);
        boolean sort = articleMapper.sort();
        return 0 != deleteArticle && sort ? ResultFul.success(BaseCode.DELETE) : ResultFul.fail(BaseCode.DELETE_ERROR);
    }
}
