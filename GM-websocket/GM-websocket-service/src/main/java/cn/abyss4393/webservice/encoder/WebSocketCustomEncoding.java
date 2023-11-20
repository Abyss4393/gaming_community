package cn.abyss4393.webservice.encoder;

import cn.hutool.json.JSONUtil;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className WebSocketCustomEncoding
 * @description TODO
 * @date 21/9/2023
 */
public class WebSocketCustomEncoding implements Encoder.Text<HashMap<String, Object>> {

    private static final Logger log = LoggerFactory.getLogger(WebSocketCustomEncoding.class);

    @Override
    public String encode(HashMap hashMap) throws EncodeException {
        if (hashMap != null) {
            try {
                return JSONUtil.toJsonStr(hashMap);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return null;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        Text.super.init(endpointConfig);
    }

    @Override
    public void destroy() {
        Text.super.destroy();
    }
}
