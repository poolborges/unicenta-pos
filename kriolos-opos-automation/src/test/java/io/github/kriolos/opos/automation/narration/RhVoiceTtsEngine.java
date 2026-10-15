package io.github.kriolos.opos.automation.narration;

import io.github.kriolos.opos.automation.PosAudioNarrator.Language;
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

            ProcessBuilder pb = new ProcessBuilder(
                    "RHVoice-test",
                    "-p", language.getVoiceName(),
                    "-o", outputWav.getAbsolutePath()
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

            if (process.exitValue() == 0 && outputWav.exists() && outputWav.length() > 0) {
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
}
