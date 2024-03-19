package cn.abyss4393.mapper;

import cn.abyss4393.po.Upvote;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;
import java.util.List;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className UpvoteMapper
 * @description TODO
 * @date 20/11/2023
 * @completion false
 */
@Mapper
public interface UpvoteMapper extends BaseMapper<Upvote> {
    @Update("ALTER TABLE tb_upvote DROP id;" +
            "ALTER TABLE tb_upvote ADD id int NOT NULL FIRST;" +
            "ALTER TABLE tb_upvote MODIFY COLUMN id int NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY(id);")
    boolean sort();

    @Select("select a_id from tb_upvote where u_id = #{id}")
    List<Integer> getAllArticleId(@Param("id") Serializable id);

    @Select("select count(u_id) from tb_upvote where a_id = #{id}")
    int countLike(@Param("id") Serializable id);
}
