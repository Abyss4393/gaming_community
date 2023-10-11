package cn.abyss4393.mapper;

import cn.abyss4393.po.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("select positiveness_count from tb_article where id = #{id}")
    int getPositivenessCount(@Param("id") Integer id);

    @Select("select passiveness_count from tb_article where id = #{id}")
    int getPassivenessCount(@Param("id") Integer id);

    @Select("select collect_count from tb_article where id = #{id}")
    int getCollectCount(@Param("id") Integer id);
}
