package cn.abyss4393.mapper;

import cn.abyss4393.po.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<User> {
    @Select("select count(*) from tb_user")
    int getCount();

    @Select("select id from tb_user")
    List<Integer> getAllUserId();

    @Select("select nickname,avatar from tb_user where id = #{id}")
    User getSimpleUserInfo(@Param("id") Integer uid);

    @Update("ALTER TABLE tb_user DROP id;"+
            "ALTER TABLE tb_user ADD id int NOT NULL FIRST;"+
            "ALTER TABLE tb_user MODIFY COLUMN id int NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY(id);")
    boolean sort();

    @Select("<script> select * from tb_user" +
            "<where> " +
            "<if test='key != null'" + ">" +
            "username like concat('%',#{key},'%') " +
            "or book_isbn like concat('%',#{key},'%')" +
            "or nickname like concat('%',#{key},'%')" +
            "or email like concat('%',#{key},'%')" +
            "</if>" +
            "</where>" +
            "</script>")
    List<User> search(@Param("key") String key);

}
