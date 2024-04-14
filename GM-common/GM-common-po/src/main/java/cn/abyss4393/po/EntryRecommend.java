package cn.abyss4393.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className Recommend
 * @description TODO
 * @date 2024/3/23
 * @completion false
 */
@TableName("tb_recommend")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EntryRecommend {
    private Integer id;
    private Integer aId;
    private String cover;
    private Integer isHot;
}
