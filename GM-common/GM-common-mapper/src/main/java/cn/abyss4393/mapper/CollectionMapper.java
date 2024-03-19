package cn.abyss4393.mapper;

import cn.abyss4393.po.Collection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className CollectionMapper
 * @description TODO
 * @date 5/9/2023
 */

@Repository
public interface CollectionMapper extends BaseMapper<Collection> {

    @Update("ALTER TABLE tb_collection DROP id;" +
            "ALTER TABLE tb_collection ADD id int NOT NULL FIRST;" +
            "ALTER TABLE tb_collection MODIFY COLUMN id int NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY(id);")
    boolean sort();


    @Select("select article_id from tb_collection where user_id = #{id}")
    List<Integer> getAllArticleId(@Param("id") Serializable id);

    @Select("select count(user_id) from tb_collection where article_id = #{id}")
    int countCollect(@Param("id") Serializable id);
}
