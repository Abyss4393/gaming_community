package cn.abyss4393.mapper;

import cn.abyss4393.po.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className CommentMapper
 * @description TODO
 * @date 19/11/2023
 * @completion false
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {


    @Update("""
            ALTER TABLE tb_comment DROP id;
            ALTER TABLE tb_comment ADD id int NOT NULL FIRST;
            ALTER TABLE tb_comment MODIFY COLUMN id int NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY(id);""")
    boolean sort();
}
