package cn.abyss4393.exception;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ServiceException
 * @description TODO
 * @date 3/9/2023
 */
public class ServiceException extends RuntimeException {
    private final String code;

    public ServiceException(String msg) {
        super(msg);
        this.code = "500";
    }


    public ServiceException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
