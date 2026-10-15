package io.github.kriolos.opos.automation.video;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;

/**
 * Universal FFmpeg Binary Locator.
 *
 * <p>Discovers FFmpeg in the following order:
 * <ol>
 *   <li>System properties: {@code opos.ffmpeg.path} or {@code test.ffmpeg.path}</li>
 *   <li>Bundled cross-platform binary via Maven dependency ({@code ws.schild:jave-all-deps})</li>
 *   <li>Host system executable on {@code PATH} (e.g. {@code /usr/bin/ffmpeg})</li>
 * </ol>
 */
public final class FfmpegLocator {

    private static final Logger LOGGER = Logger.getLogger(FfmpegLocator.class.getName());
    private static volatile String cachedExecutable = null;

    private FfmpegLocator() {
    }

    /**
     * Resolves the executable path to FFmpeg, or {@code null} if not available.
     */
    public static synchronized String getExecutablePath() {
        if (cachedExecutable != null) {
            return cachedExecutable;
        }

        // 1. Explicit system property
        String customPath = System.getProperty("opos.ffmpeg.path");
        if (customPath == null || customPath.isBlank()) {
            customPath = System.getProperty("test.ffmpeg.path");
        }
        if (customPath != null && !customPath.isBlank() && new File(customPath).canExecute()) {
            LOGGER.log(Level.INFO, "[FfmpegLocator] Using explicitly configured FFmpeg: {0}", customPath);
            return cachedExecutable = customPath;
        }

        // 2. Bundled FFmpeg via Maven (ws.schild:jave-all-deps)
        try {
            DefaultFFMPEGLocator javeLocator = new DefaultFFMPEGLocator();
            String javePath = javeLocator.getExecutablePath();
            if (javePath != null && !javePath.isBlank()) {
                File javeFile = new File(javePath);
                if (javeFile.exists() && (javeFile.canExecute() || javeFile.setExecutable(true))) {
                    LOGGER.log(Level.INFO, "[FfmpegLocator] Found embedded FFmpeg binary via Maven (JAVE): {0}", javePath);
                    return cachedExecutable = javePath;
                }
            }
        } catch (Throwable t) {
            LOGGER.log(Level.FINE, "[FfmpegLocator] JAVE embedded FFmpeg locator unavailable: {0}", t.getMessage());
        }

        // 3. System PATH ffmpeg
        try {
            Process process = new ProcessBuilder("which", "ffmpeg").start();
            boolean finished = process.waitFor(3, TimeUnit.SECONDS);
            if (finished && process.exitValue() == 0) {
                LOGGER.log(Level.INFO, "[FfmpegLocator] Found system FFmpeg on PATH");
                return cachedExecutable = "ffmpeg";
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * Checks whether an FFmpeg binary can be located.
     */
    public static boolean isAvailable() {
        return getExecutablePath() != null;
    }
}
