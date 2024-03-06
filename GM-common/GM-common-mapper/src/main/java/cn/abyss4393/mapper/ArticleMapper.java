package cn.abyss4393.mapper;

import cn.abyss4393.po.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("select positiveness_count from tb_article where id = #{id}")
    int getPositivenessCount(@Param("id") Serializable id);

    @Select("select passiveness_count from tb_article where id = #{id}")
    int getPassivenessCount(@Param("id") Serializable id);

    @Select("select collect_count from tb_article where id = #{id}")
    int getCollectCount(@Param("id") Serializable id);

    @Update("ALTER TABLE tb_article DROP id;"+
            "ALTER TABLE tb_article ADD id int NOT NULL FIRST;"+
            "ALTER TABLE tb_article MODIFY COLUMN id int NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY(id);")
    boolean sort();
}
