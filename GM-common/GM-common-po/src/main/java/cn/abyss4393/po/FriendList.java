package cn.abyss4393.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className Friend
 * @description TODO
 * @date 5/9/2023
 */
@TableName("tb_friend")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FriendList  implements Serializable {


    private Integer id;
    private Integer rootId;
    private Integer userId;
}
