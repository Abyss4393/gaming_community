package cn.abyss4393.vo;


import cn.abyss4393.po.User;
import lombok.*;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className UserVo
 * @description TODO
 * @date 4/9/2023
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultVo<T> {
    private T data;

    private String token;
}
