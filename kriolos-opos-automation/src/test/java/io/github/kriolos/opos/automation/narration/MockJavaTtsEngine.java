package io.github.kriolos.opos.automation.narration;

import io.github.kriolos.opos.automation.narration.PosAudioNarrator.Language;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * 100% Pure Java Synthetic Fallback TTS Engine.
 *
 * <p>Generates deterministic audio tone markers using standard Java {@link javax.sound.sampled}
 * APIs, ensuring automation test suites pass cleanly across all operating systems and CI runners
 * without requiring any native speech libraries.
 */
public class MockJavaTtsEngine implements TtsEngine {

    private static final Logger LOGGER = Logger.getLogger(MockJavaTtsEngine.class.getName());

    private static final float SAMPLE_RATE = 24000.0f;
    private static final int CHANNELS = 1;

    @Override
    public String getName() {
        return "Java Synthetic Audio Engine";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public File synthesize(String text, Language language, File outputWav) {
        try {
            if (outputWav.getParentFile() != null) {
                outputWav.getParentFile().mkdirs();
            }

            // Generate ~1.2s tone based on text hash
            int durationMs = Math.min(3000, Math.max(800, text.length() * 45));
            int numSamples = (int) (SAMPLE_RATE * (durationMs / 1000.0f));

            double frequency = (language == Language.PT) ? 587.33 : 523.25; // D5 vs C5
            short[] samples = new short[numSamples];

            for (int i = 0; i < numSamples; i++) {
                double time = i / (double) SAMPLE_RATE;
                double envelope = Math.sin(Math.PI * (i / (double) numSamples)); // Soft fade in / out
                double wave = Math.sin(2.0 * Math.PI * frequency * time);
                samples[i] = (short) (wave * envelope * 8000);
            }

            ByteBuffer bb = ByteBuffer.allocate(samples.length * 2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            for (short s : samples) {
                bb.putShort(s);
            }

            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, CHANNELS, true, false);

            try (ByteArrayInputStream bais = new ByteArrayInputStream(bb.array());
                 AudioInputStream ais = new AudioInputStream(bais, format, numSamples)) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputWav);
            }

            return outputWav;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "[MockJavaTts] Failed to generate synthetic audio clip: " + e.getMessage(), e);
            return null;
        }
    }
}
