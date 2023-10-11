package cn.abyss4393.mapper;

import cn.abyss4393.po.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
    @Select("select count(*) from tb_user")
    int getCount();

    @Select("select nickname,avatar from tb_user where id = #{id}")
    User getSimpleUserInfo(@Param("id") Integer uid);

}
