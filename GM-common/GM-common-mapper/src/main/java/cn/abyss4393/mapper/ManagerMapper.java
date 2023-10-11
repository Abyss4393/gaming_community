package cn.abyss4393.mapper;

import cn.abyss4393.po.Manager;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerMapper extends BaseMapper<Manager> {

    @Select("select count(*) from tb_manager")
    int getCount();
}
