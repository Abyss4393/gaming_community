package cn.abyss4393.webservice.handler;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className WebSocketHanlder
 * @description TODO
 * @date 15/10/2023
 */
public abstract class WebSocketHandler {

    abstract void handlerText(WebSocketHandlerBehavior handlerBehavior);

    abstract void handlerImage(WebSocketHandlerBehavior handlerBehavior);

    abstract void handlerAudio(WebSocketHandlerBehavior handlerBehavior);

    abstract void handlerVideo(WebSocketHandlerBehavior handlerBehavior);

    abstract void handlerFile(WebSocketHandlerBehavior handlerBehavior);

}
