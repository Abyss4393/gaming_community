package cn.abyss4393.webservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className WebSocketHanlder
 * @description TODO
 * @date 15/10/2023
 */
public abstract class WebSocketHandler {

    protected abstract void handlerText(WebSocketHandlerBehavior handlerBehavior) throws JsonProcessingException;

    protected abstract void handlerImage(WebSocketHandlerBehavior handlerBehavior) throws JsonProcessingException;

    protected abstract void handlerAudio(WebSocketHandlerBehavior handlerBehavior) throws JsonProcessingException;

    protected abstract void handlerVideo(WebSocketHandlerBehavior handlerBehavior) throws JsonProcessingException;

    protected abstract void handlerFile(WebSocketHandlerBehavior handlerBehavior) throws JsonProcessingException;

}
