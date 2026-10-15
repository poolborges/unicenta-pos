package io.github.kriolos.opos.automation.video;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory and Auto-Discovery for Screen Recorder Video Engines.
 */
public final class VideoRecorderEngineFactory {

    private static final Logger LOGGER = Logger.getLogger(VideoRecorderEngineFactory.class.getName());

    public static final String PROPERTY_RECORDER_ENGINE = "test.recorder.engine";

    private VideoRecorderEngineFactory() {
    }

    /**
     * Resolves the appropriate {@link VideoRecorderEngine} based on system properties
     * or automatic environment detection.
     */
    public static VideoRecorderEngine createEngine() {
        String configuredEngine = System.getProperty(PROPERTY_RECORDER_ENGINE, "").trim().toLowerCase();

        if ("java".equals(configuredEngine) || "gif".equals(configuredEngine)) {
            LOGGER.log(Level.INFO, "[VideoRecorderFactory] Using explicitly configured Pure Java Engine.");
            return new JavaAnimatedGifVideoEngine();
        }

        if ("ffmpeg".equals(configuredEngine)) {
            FfmpegNativeVideoEngine ffmpegEngine = new FfmpegNativeVideoEngine();
            if (ffmpegEngine.isAvailable()) {
                LOGGER.log(Level.INFO, "[VideoRecorderFactory] Using explicitly configured FFmpeg Native Engine.");
                return ffmpegEngine;
            } else {
                LOGGER.log(Level.WARNING, "[VideoRecorderFactory] FFmpeg engine requested but binary not found. Falling back to Pure Java Engine.");
                return new JavaAnimatedGifVideoEngine();
            }
        }

        // Automatic Environment Discovery
        FfmpegNativeVideoEngine nativeEngine = new FfmpegNativeVideoEngine();
        if (nativeEngine.isAvailable()) {
            LOGGER.log(Level.INFO, "[VideoRecorderFactory] Native FFmpeg detected. Selected engine: {0}", nativeEngine.getName());
            return nativeEngine;
        }

        LOGGER.log(Level.INFO, "[VideoRecorderFactory] Native FFmpeg not found on PATH. Falling back to Pure Java Video Engine.");
        return new JavaAnimatedGifVideoEngine();
    }
}
