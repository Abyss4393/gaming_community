package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.ArticleMapper;
import cn.abyss4393.mapper.CollectionMapper;
import cn.abyss4393.po.Article;
import cn.abyss4393.po.Collection;
import cn.abyss4393.service.ICollectionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className CollectionServiceImpl
 * @description TODO
 * @date 1/11/2023
 */
@Service
public class CollectionServiceImpl implements ICollectionService {

    @Resource
    private CollectionMapper collectionMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public ResultFul<?> getArticleByCollection(Serializable uid) {
        List<Collection> collections = collectionMapper.selectList(new LambdaQueryWrapper<>() {{
            this.eq(Collection::getUserId, uid);
        }});
        if (0 == collections.size()) return ResultFul.success(BaseCode.SUCCESS);
        List<Article> resultContainer = new LinkedList<>();
        collections.forEach(item -> {
            Article article = articleMapper.selectOne(new LambdaQueryWrapper<>() {{
                this.eq(Article::getId, item.getArticleId());
            }});
            resultContainer.add(article);
        });
        return ResultFul.success(BaseCode.SUCCESS,resultContainer);
    }
}
