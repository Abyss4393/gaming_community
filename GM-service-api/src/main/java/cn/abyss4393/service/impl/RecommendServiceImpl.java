package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.ArticleMapper;
import cn.abyss4393.mapper.RecommendMapper;
import cn.abyss4393.mapper.UserMapper;
import cn.abyss4393.po.Article;
import cn.abyss4393.po.EntryRecommend;
import cn.abyss4393.po.User;
import cn.abyss4393.service.IRecommendService;
import cn.abyss4393.utils.recommend.Recommend;
import cn.abyss4393.vo.ArticleVo;
import cn.abyss4393.vo.RecommendVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className RecommendServiceImpl
 * @description TODO
 * @date 2024/3/12
 * @completion false
 */

@Service
public class RecommendServiceImpl implements IRecommendService {

    @Resource
    private Recommend recommend;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RecommendMapper recommendMapper;

    @Override
    public ResultFul<?> forUser(Integer id) {
        recommend.init();
        List<ArticleVo> articles = new ArrayList<>();
        if (0 != id) {
            Set<Integer> recommendIds = recommend.toUser(20, 5, id);
            for (Integer recommend : recommendIds) {
                ArticleVo articleVo = new ArticleVo();
                Article article = articleMapper.selectById(recommend);
                if (null == article) continue;
                int uid = article.getPosterId();
                User simpleUserInfo = userMapper.getSimpleUserInfo(uid);
                articleVo.setArticle(article);
                articleVo.setAvatar(simpleUserInfo.getAvatar());
                articleVo.setNickname(simpleUserInfo.getNickname());
                articles.add(articleVo);
            }
        }
        LambdaQueryWrapper<Article> sortArticleWrapper = Wrappers.lambdaQuery();
        sortArticleWrapper.orderByDesc(Article::getArticleLike);
        List<Article> sortArticles = articleMapper.selectList(sortArticleWrapper);
        sortArticles = sortArticles.subList(0, 5);
        sortArticles.forEach(article -> {
            ArticleVo articleVo = new ArticleVo();
            User simpleUserInfo = userMapper.getSimpleUserInfo(article.getPosterId());
            articleVo.setArticle(article);
            articleVo.setAvatar(simpleUserInfo.getAvatar());
            articleVo.setNickname(simpleUserInfo.getNickname());
            articles.add(articleVo);
        });
        return ResultFul.success(BaseCode.SUCCESS, articles);
    }

    @Override
    public ResultFul<?> getRecommends() {
        List<RecommendVo> recommendVos = new ArrayList<>();
        List<EntryRecommend> entryRecommends = recommendMapper.selectList(null);
        entryRecommends.forEach(item -> {
            RecommendVo recommendVo = new RecommendVo();
            Integer aId = item.getAId();
            Article article = articleMapper.selectById(aId);
            recommendVo.setRecommend(item);
            recommendVo.setArticle(article);
            recommendVos.add(recommendVo);
        });
        return ResultFul.success(BaseCode.SUCCESS, recommendVos);
    }

    @Override
    public ResultFul<?> setRecommend(EntryRecommend recommend) {
        int row = Math.toIntExact(recommendMapper.selectCount(null) + 1);
        recommend.setId(row);
        int insert = recommendMapper.insert(recommend);
        return 0 != insert ? ResultFul.success(BaseCode.SUCCESS) :
                ResultFul.fail(BaseCode.ERROR);
    }

    @Override
    public ResultFul<?> cancelRecommend(Integer aid) {
        LambdaQueryWrapper<EntryRecommend> entryRecommendLambdaQueryWrapper = Wrappers.lambdaQuery();
        entryRecommendLambdaQueryWrapper.eq(EntryRecommend::getAId, aid);
        int delete = recommendMapper.delete(entryRecommendLambdaQueryWrapper);
        recommendMapper.sort();
        return 0 != delete ? ResultFul.success(BaseCode.DELETE) :
                ResultFul.fail(BaseCode.DELETE_ERROR);
    }
}
