package cn.abyss4393.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className DisLike
 * @description TODO
 * @date 2024/3/11
 * @completion false
 */
@TableName("tb_dislike")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DisLike {
    private Integer id;
    private Integer uId;
    private Integer aId;
}