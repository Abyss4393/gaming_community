package cn.abyss4393.mapper;

import cn.abyss4393.po.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageMapper extends BaseMapper<Message> {

    @Select("select count(*) from tb_message")
    int getCount();

    @Select("select id from tb_message where sender_id = #{sid} and receiver_id = #{rid}")
    Integer exitId(@Param("sid") Integer senderId, @Param("rid") Integer receiverId);
}
