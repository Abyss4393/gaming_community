package cn.abyss4393.webservice.utils;

import java.util.Base64;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className MessageDecoder
 * @description TODO
 * @date 13/10/2023
 */
public class WebSocketMessageDecoder {
    public static byte[] decoder(String base64) {
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(base64);
    }
}
