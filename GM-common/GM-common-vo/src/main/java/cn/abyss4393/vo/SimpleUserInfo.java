package cn.abyss4393.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className SimpleUserInfo
 * @description TODO
 * @date 8/10/2023
 */
@Data
@AllArgsConstructor
public class SimpleUserInfo implements Serializable {
    private Integer uid;
    private String nickname;
    private String avatar;
}
