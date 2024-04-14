package cn.abyss4393.webservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className WebSocketHandlerBehavior
 * @description TODO
 * @date 15/10/2023
 */


@FunctionalInterface
public interface WebSocketHandlerBehavior {
     void handler() throws JsonProcessingException;
}
