package cn.abyss4393.exception;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className GlobalException
 * @description TODO
 * @date 3/9/2023
 */
@ControllerAdvice
@Slf4j
public class GlobalException {

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public <T extends ServiceException> ResultFul<?> serviceException(T e) {
        log.error("exception", e);
        return ResultFul.error(BaseCode.SYSTEM_ERROR);
    }

}
