package cn.abyss4393.mapper;

import cn.abyss4393.po.Reply;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ReplyMapper
 * @description TODO
 * @date 26/11/2023
 * @completion false
 */
@Mapper
public interface ReplyMapper extends BaseMapper<Reply> {
    @Update("""
            ALTER TABLE tb_reply DROP id;
            ALTER TABLE tb_reply ADD id int NOT NULL FIRST;
            ALTER TABLE tb_reply MODIFY COLUMN id int NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY(id);""")
    boolean sort();
}
