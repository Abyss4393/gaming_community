package cn.abyss4393.utils.audioUtils;


import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className AudioUtils
 * @description TODO
 * @date 16/10/2023
 */
public class AudioUtils {

    public static int getAudioDuration(String netPath) {
        try (Clip clip = AudioSystem.getClip()) {
            URL url = new URL(netPath);
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            clip.open(ais);
            return (int) (clip.getMicrosecondLength() / 1000000L);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
       File file = new File(new URI("https://abyss4393.cn/audio/v1/hjzf.mp3"));
        System.out.println(file);
    }
}
