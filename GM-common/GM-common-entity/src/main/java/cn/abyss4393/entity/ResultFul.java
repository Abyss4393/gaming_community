package cn.abyss4393.entity;

import java.util.HashMap;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ResultFul
 * @description TODO
 * @date 3/9/2023
 */
public class ResultFul<T> extends HashMap<String, Object> {

    private static final String DATA = "data";
    private static final String RESULT = "meta";

    private ResultFul(final BaseCode b) {
        this.put(RESULT, b);
    }

    private ResultFul(final HashMap<String, Object> h) {
        this.put(RESULT, h);
    }

    private ResultFul(final BaseCode code, final T data) {
        this.put(RESULT, code);
        this.put(DATA, data);

    }

    @SuppressWarnings("all")
    public static ResultFul error(final BaseCode code) {
        return new ResultFul<>(code);
    }

    @SuppressWarnings("all")
    public static ResultFul error(final String code, String msg) {
        return new ResultFul<>(new HashMap<>() {{
            this.put("code", code);
            this.put("msg", msg);
        }});
    }

    @SuppressWarnings("all")
    public static <T> ResultFul success(final BaseCode code, final T data) {
        return new ResultFul<>(code, data);
    }

    @SuppressWarnings("all")
    public static ResultFul success(final BaseCode code) {
        return new ResultFul<>(code);
    }

    @SuppressWarnings("all")
    public static ResultFul success(final String code, String msg) {
        return new ResultFul<>(new HashMap<>() {{
            this.put("code", code);
            this.put("msg", msg);
        }});
    }

    @SuppressWarnings("all")
    public static <T> ResultFul fail(final BaseCode code, final T data) {
        return new ResultFul<>(code, data);
    }

    @SuppressWarnings("all")
    public static ResultFul fail(final BaseCode code) {
        return new ResultFul<>(code);
    }

    @SuppressWarnings("all")
    public static ResultFul fial(final String code, String msg) {
        return new ResultFul<>(new HashMap<>() {{
            this.put("code", code);
            this.put("msg", msg);
        }});
    }
}
