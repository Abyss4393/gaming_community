package cn.abyss4393.webservice.handler;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className WebSocketHanlder
 * @description TODO
 * @date 15/10/2023
 */
public abstract class WebSocketHandler {

    protected abstract void handlerText(WebSocketHandlerBehavior handlerBehavior);

    protected abstract void handlerImage(WebSocketHandlerBehavior handlerBehavior);

    protected abstract void handlerAudio(WebSocketHandlerBehavior handlerBehavior);

    protected abstract void handlerVideo(WebSocketHandlerBehavior handlerBehavior);

    protected abstract void handlerFile(WebSocketHandlerBehavior handlerBehavior);

}
