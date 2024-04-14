package cn.abyss4393.mapper;

import cn.abyss4393.po.EntryRecommend;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className RecommendMapper
 * @description TODO
 * @date 2024/3/23
 * @completion false
 */
@Mapper
public interface RecommendMapper  extends BaseMapper<EntryRecommend> {
    @Update("""
            ALTER TABLE tb_recommend DROP id;
            ALTER TABLE tb_recommend ADD id int NOT NULL FIRST;
            ALTER TABLE tb_recommend MODIFY COLUMN id int NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY(id);""")
    boolean sort();
}
