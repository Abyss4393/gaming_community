package cn.abyss4393.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className Manager
 * @description TODO
 * @date 5/9/2023
 */
@TableName("tb_manager")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Manager extends BaseObj  implements Serializable {
    private Integer id;

    private String username;

    private String password;

    private String nickname;

    private String createTime;

    private String lastLandingTime;
}
