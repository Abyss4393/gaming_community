package cn.abyss4393.mapper;

import cn.abyss4393.po.ManagerNotification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ManagerNotificationMapper
 * @description TODO
 * @date 2024/3/19
 * @completion false
 */
public interface ManagerNotificationMapper extends BaseMapper<ManagerNotification> {

    @Update("ALTER TABLE tb_manager_notification DROP id;"+
            "ALTER TABLE tb_manager_notification ADD id int NOT NULL FIRST;"+
            "ALTER TABLE tb_manager_notification MODIFY COLUMN id int NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY(id);")
    boolean sort();
}
