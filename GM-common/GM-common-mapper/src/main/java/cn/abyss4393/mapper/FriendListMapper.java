package cn.abyss4393.mapper;

import cn.abyss4393.po.FriendList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendListMapper extends BaseMapper<FriendList> {

    @Select("SELECT user_id FROM `tb_friend` WHERE root_id = 1")
    List<Integer> getFriendsIdByRootId(@Param("id")Integer id);
}
