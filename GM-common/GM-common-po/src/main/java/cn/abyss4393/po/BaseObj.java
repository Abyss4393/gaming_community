package cn.abyss4393.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className BaseObj
 * @description TODO
 * @date 6/9/2023
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseObj implements Serializable {
    private Integer id;

    private String username;

    private String password;

    private String nickname;

    private String createTime;

    private String lastLandingTime;
}
