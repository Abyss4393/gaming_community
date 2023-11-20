package cn.abyss4393.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className IBaseEnum
 * @description TODO
 * @date 15/11/2023
 * @completion false
 */
@Deprecated
public interface IBaseEnum<T> {

    default void initEnum(String msg, T code) {
        EnumContainer.putEnum(this, code, msg);
    }

    @SuppressWarnings("all")
    default T getCode() {
        return (T) EnumContainer.getEnumBean(this).getCode();
    }

    default String getMsg() {
        return EnumContainer.getEnumBean(this).getText();
    }

    @SuppressWarnings("all")
    class EnumContainer {
        private static final Map<IBaseEnum, EnumBean> ENUM_MAP = new ConcurrentHashMap<>();

        public static <T> void putEnum(IBaseEnum<T> baseEnum, T code, String msg) {
            ENUM_MAP.put(baseEnum, EnumBean.instanceEnumBean(code, msg));
        }

        public static <T> EnumBean<?> getEnumBean(IBaseEnum<T> enumObj) {
            return EnumContainer.ENUM_MAP.get(enumObj);
        }

    }



    class EnumBean<T> {
        private final T code;
        private final String text;

        private EnumBean(T code, String text) {
            this.code = code;
            this.text = text;
        }

        public T getCode() {
            return code;
        }

        public String getText() {
            return text;
        }

        public static <T> EnumBean<T> instanceEnumBean(T code, String text) {
            return new EnumBean<>(code, text);
        }
    }
}
