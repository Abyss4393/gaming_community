package cn.abyss4393.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className Upvote
 * @description TODO
 * @date 20/11/2023
 * @completion false
 */

@TableName("tb_upvote")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Upvote {
    private Integer id;
    private Integer uId;
    private Integer aId;
}
