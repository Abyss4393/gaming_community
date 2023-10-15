package cn.abyss4393.webservice.handler;

import java.io.File;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className WebSocketHandlerBehavior
 * @description TODO
 * @date 15/10/2023
 */


@FunctionalInterface
public interface WebSocketHandlerBehavior {

   <T> void handler(T t);
}
