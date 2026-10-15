package io.github.kriolos.opos.automation.narration;

import io.github.kriolos.opos.automation.narration.PosAudioNarrator.Language;
import io.github.kriolos.opos.automation.video.FfmpegLocator;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Microsoft Edge Neural Speech TTS Engine.
 *
 * <p>Produces studio-grade broadcast speech without API keys or accounts using the Edge Neural Speech protocol.
 * Uses the same natural neural voices as Azure Speech:
 * <ul>
 *   <li>Portuguese: {@code pt-PT-RaquelNeural} or {@code pt-BR-FranciscaNeural}</li>
 *   <li>English: {@code en-US-AriaNeural} or {@code en-US-GuyNeural}</li>
 * </ul>
 */
public class EdgeNeuralTtsEngine implements TtsEngine {

    private static final Logger LOGGER = Logger.getLogger(EdgeNeuralTtsEngine.class.getName());

    private final String executablePath;
    private final boolean available;

    public EdgeNeuralTtsEngine() {
        this.executablePath = locateEdgeTts();
        this.available = (this.executablePath != null);
    }

    private static String locateEdgeTts() {
        // 1. Explicit property
        String prop = System.getProperty("test.edgetts.path");
        if (prop != null && !prop.isBlank() && new File(prop).canExecute()) {
            return prop;
        }

        // 2. target/tts-env/bin/edge-tts (Project managed venv)
        File venvExe = new File("target/tts-env/bin/edge-tts");
        if (venvExe.exists() && venvExe.canExecute()) {
            return venvExe.getAbsolutePath();
        }

        // 3. User home ~/.local/bin/edge-tts
        String home = System.getProperty("user.home");
        if (home != null) {
            File userExe = new File(home, ".local/bin/edge-tts");
            if (userExe.exists() && userExe.canExecute()) {
                return userExe.getAbsolutePath();
            }
        }

        // 4. System PATH
        try {
            Process p = new ProcessBuilder("which", "edge-tts").start();
            boolean ok = p.waitFor(3, TimeUnit.SECONDS);
            if (ok && p.exitValue() == 0) {
                return "edge-tts";
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    @Override
    public String getName() {
        return "Edge Neural Speech (" + (available ? "Active" : "Not Found") + ")";
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

            File tempMp3 = new File(outputWav.getParentFile(), "edge_" + System.nanoTime() + ".mp3");
            String voiceName = resolveVoice(language);

            ProcessBuilder pb = new ProcessBuilder(
                    executablePath,
                    "--text", text,
                    "--voice", voiceName,
                    "--write-media", tempMp3.getAbsolutePath()
            );
            pb.redirectErrorStream(true);

            Process process = pb.start();
            boolean finished = process.waitFor(20, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                tempMp3.delete();
                LOGGER.log(Level.WARNING, "[EdgeNeuralTts] Synthesis timed out for voice: {0}", voiceName);
                return null;
            }

            if (process.exitValue() != 0 || !tempMp3.exists() || tempMp3.length() == 0) {
                LOGGER.log(Level.WARNING, "[EdgeNeuralTts] edge-tts process failed with code {0}", process.exitValue());
                tempMp3.delete();
                return null;
            }

            // Convert to 24kHz mono PCM WAV required by JavaAudioTimelineMixer
            boolean converted = convertMp3ToWav(tempMp3, outputWav);
            tempMp3.delete();

            if (converted && outputWav.exists() && outputWav.length() > 0) {
                LOGGER.log(Level.INFO, "[EdgeNeuralTts] Synthesized neural audio ({0} bytes) using {1}: {2}",
                        new Object[]{outputWav.length(), voiceName, outputWav.getName()});
                return outputWav;
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[EdgeNeuralTts] Error synthesizing speech: " + e.getMessage(), e);
            return null;
        }
    }

    private boolean convertMp3ToWav(File inputMp3, File outputWav) {
        String ffmpeg = FfmpegLocator.getExecutablePath();
        if (ffmpeg == null) {
            LOGGER.log(Level.WARNING, "[EdgeNeuralTts] FFmpeg required to convert MP3 to WAV");
            return false;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    ffmpeg, "-y",
                    "-i", inputMp3.getAbsolutePath(),
                    "-ar", "24000",
                    "-ac", "1",
                    outputWav.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            boolean ok = p.waitFor(10, TimeUnit.SECONDS);
            return ok && p.exitValue() == 0;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "[EdgeNeuralTts] FFmpeg audio conversion failed: " + e.getMessage());
            return false;
        }
    }

    private String resolveVoice(Language language) {
        String custom = System.getProperty("edge.voice." + language.getCode().toLowerCase());
        if (custom != null && !custom.isBlank()) {
            return custom;
        }
        return language.getNeuralVoice();
    }
}
