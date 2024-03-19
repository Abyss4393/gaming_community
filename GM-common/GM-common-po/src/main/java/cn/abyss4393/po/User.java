package cn.abyss4393.po;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className User
 * @description TODO
 * @date 2/9/2023
 */
@TableName("tb_user")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User extends BaseObj {


    private String mobile;

    private String email;

    private String avatar;

    private String gender;

    private String permission;

    @TableField(exist = false)
    public static final String DEFAULT_IMAGE = "https://gitee.com/abyss4393/gaming_community/blob/master/avatar/default.jpg";
    @TableField(exist = false)
    public static final String PERMISSION_OFFICIAL = "正式用户";

    @TableField(exist = false)
    public static final String PERMISSION_UNOFFICIAL = "游客";

}
