package io.github.kriolos.opos.automation.narration;

import io.github.kriolos.opos.automation.narration.PosAudioNarrator.Language;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Native RHVoice TTS Engine implementation.
 */
public class RhVoiceTtsEngine implements TtsEngine {

    private static final Logger LOGGER = Logger.getLogger(RhVoiceTtsEngine.class.getName());

    private final boolean available;

    public RhVoiceTtsEngine() {
        this.available = checkAvailability();
    }

    private boolean checkAvailability() {
        try {
            Process process = new ProcessBuilder("which", "RHVoice-test").start();
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);
            return finished && process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getName() {
        return "RHVoice (Native)";
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public File synthesize(String text, Language language, File outputWav) {
        if (!available) {
            return null;
        }

        try {
            if (outputWav.getParentFile() != null) {
                outputWav.getParentFile().mkdirs();
            }

            int speechRate = Integer.getInteger("test.narration.rate", 92);
            int speechPitch = Integer.getInteger("test.narration.pitch", 98);
            boolean smoothFilter = Boolean.parseBoolean(System.getProperty("test.narration.smooth", "true"));

            File rawWav = smoothFilter ? new File(outputWav.getParentFile(), "raw_" + outputWav.getName()) : outputWav;

            ProcessBuilder pb = new ProcessBuilder(
                    "RHVoice-test",
                    "-p", language.getVoiceName(),
                    "-q", "best",
                    "-r", String.valueOf(speechRate),
                    "-t", String.valueOf(speechPitch),
                    "-o", rawWav.getAbsolutePath()
            );
            pb.redirectErrorStream(true);

            Process process = pb.start();
            try (var os = process.getOutputStream()) {
                os.write(text.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            boolean finished = process.waitFor(15, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                LOGGER.log(Level.WARNING, "[RhVoiceTts] Synthesis timed out for language: {0}", language);
                return null;
            }

            if (process.exitValue() == 0 && rawWav.exists() && rawWav.length() > 0) {
                if (smoothFilter) {
                    applySmoothFilter(rawWav, outputWav);
                    rawWav.delete();
                }
                return outputWav;
            } else {
                LOGGER.log(Level.WARNING, "[RhVoiceTts] Process exited with code {0}", process.exitValue());
                return null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "[RhVoiceTts] Error synthesizing speech: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Applies acoustic vocal post-processing via FFmpeg:
     * - Attenuates 3kHz metallic formant resonance (-2.5 dB)
     * - Low-passes harsh digital synthesis hiss above 7.5 kHz
     * - High-passes low-end rumble below 80 Hz
     * - Dynamic compression to smooth out plosives and improve clarity
     */
    private void applySmoothFilter(File inputWav, File outputWav) {
        String ffmpeg = io.github.kriolos.opos.automation.video.FfmpegLocator.getExecutablePath();
        if (ffmpeg == null) {
            inputWav.renameTo(outputWav);
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    ffmpeg, "-y",
                    "-i", inputWav.getAbsolutePath(),
                    "-af", "lowpass=f=7500,highpass=f=80,equalizer=f=3000:t=q:w=1.5:g=-2.5,compand=attacks=0.05:decays=0.2:points=-80/-80|-24/-20|0/-3,volume=1.1",
                    outputWav.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            boolean ok = p.waitFor(5, TimeUnit.SECONDS);
            if (!ok || p.exitValue() != 0 || !outputWav.exists()) {
                inputWav.renameTo(outputWav);
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "[RhVoiceTts] Acoustic post-filter skipped: {0}", e.getMessage());
            inputWav.renameTo(outputWav);
        }
    }
}
