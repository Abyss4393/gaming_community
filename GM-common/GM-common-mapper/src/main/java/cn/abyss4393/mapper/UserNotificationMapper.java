package cn.abyss4393.mapper;

import cn.abyss4393.po.UserNotification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className UserNotificationMapper
 * @description TODO
 * @date 2024/3/5
 * @completion false
 */
@Mapper
public interface UserNotificationMapper extends BaseMapper<UserNotification> {
    @Update("ALTER TABLE tb_user_notification DROP id;"+
            "ALTER TABLE tb_user_notification ADD id int NOT NULL FIRST;"+
            "ALTER TABLE tb_user_notification MODIFY COLUMN id int NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY(id);")
    boolean sort();
}
