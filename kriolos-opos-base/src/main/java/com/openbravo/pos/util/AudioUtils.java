package com.openbravo.pos.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author psb
 */
public class AudioUtils {

    private final static Logger LOGGER = Logger.getLogger(AudioUtils.class.getName());
    
    public static void play(String resource) {
        Clip oAudio;
        try {
            // Get the URL of the audio resource
            URL audioURL = AudioUtils.class.getClass().getClassLoader().getResource(resource);

            if (audioURL != null) {
                // Open an InputStream from the URL
                try (InputStream audioStream = audioURL.openStream()) {
                    // Wrap it in a BufferedInputStream for better performance
                    BufferedInputStream bis = new BufferedInputStream(audioStream);

                    // Get an AudioInputStream from the BufferedInputStream
                    AudioInputStream ais = AudioSystem.getAudioInputStream(bis);

                    // Get a Clip instance
                    oAudio = AudioSystem.getClip();

                    // Open the Clip with the AudioInputStream
                    oAudio.open(ais);

                    // Now you can play the audio:
                    oAudio.start(); // To play once
                    // oAudio.loop(Clip.LOOP_CONTINUOUSLY); // To loop continuously
                }

            } else {
                LOGGER.warning("Audio resource not found: " + resource);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exeception play audio from classpath resource: "+resource);
        }
    }
}
