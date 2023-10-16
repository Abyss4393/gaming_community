package cn.abyss4393.webservice.utils;

import cn.hutool.core.codec.Base64Decoder;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className MessageDecoder
 * @description TODO
 * @date 13/10/2023
 */
public class WebSocketMessageDecoder {
    public static byte[] decoder(String base64){

        return Base64Decoder.decode(base64);
    }
}
