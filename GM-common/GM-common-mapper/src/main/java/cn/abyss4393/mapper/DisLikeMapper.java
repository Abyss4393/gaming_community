package cn.abyss4393.mapper;

import cn.abyss4393.po.DisLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className DisLikeMapper
 * @description TODO
 * @date 2024/3/11
 * @completion false
 */
@Mapper
public interface DisLikeMapper extends BaseMapper<DisLike> {

    @Update("ALTER TABLE tb_dislike DROP id;" +
            "ALTER TABLE tb_dislike ADD id int NOT NULL FIRST;" +
            "ALTER TABLE tb_dislike MODIFY COLUMN id int NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY(id);")
    boolean sort();


    @Select("select count(u_id) from tb_dislike where a_id = #{id}")
    int countDislike(@Param("id") Serializable id);
}
