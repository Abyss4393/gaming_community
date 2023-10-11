package cn.abyss4393.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className Collection
 * @description TODO
 * @date 5/9/2023
 */
@TableName("tb_collection")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Collection {
    private Integer id;
    private Integer userId;
    private Integer articleId;
}
