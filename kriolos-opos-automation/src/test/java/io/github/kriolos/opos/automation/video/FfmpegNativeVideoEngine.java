package io.github.kriolos.opos.automation.video;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Native FFmpeg Video Recorder Engine.
 *
 * <p>Streams raw BGR24 frames via pipe to an external {@code ffmpeg} process,
 * encoding directly to an H.264 MP4 container.
 */
public class FfmpegNativeVideoEngine implements VideoRecorderEngine {

    private static final Logger LOGGER = Logger.getLogger(FfmpegNativeVideoEngine.class.getName());

    private final String ffmpegExecutable;
    private final boolean available;
    private File outputFile;
    private Process ffmpegProcess;
    private OutputStream ffmpegStdin;

    public FfmpegNativeVideoEngine() {
        this.ffmpegExecutable = FfmpegLocator.getExecutablePath();
        this.available = (this.ffmpegExecutable != null);
    }

    @Override
    public String getName() {
        return "FFmpeg Native Engine (H.264 MP4)";
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public void start(File outputFile, int width, int height, int fps) throws IOException {
        if (!available) {
            throw new IOException("FFmpeg executable not found (neither in Maven bundle nor on system PATH)");
        }

        this.outputFile = outputFile;
        if (outputFile.getParentFile() != null) {
            outputFile.getParentFile().mkdirs();
        }

        ProcessBuilder pb = new ProcessBuilder(
                ffmpegExecutable, "-y",
                "-f", "rawvideo",
                "-pix_fmt", "bgr24",
                "-s", width + "x" + height,
                "-r", String.valueOf(fps),
                "-i", "-",
                "-c:v", "libx264",
                "-preset", "ultrafast",
                "-pix_fmt", "yuv420p",
                outputFile.getAbsolutePath()
        );
        pb.redirectErrorStream(true);

        try {
            ffmpegProcess = pb.start();
            ffmpegStdin = ffmpegProcess.getOutputStream();
            LOGGER.log(Level.INFO, "[FfmpegNativeVideoEngine] Started encoding MP4 ({0}x{1} @ {2} fps) -> {3}",
                    new Object[]{width, height, fps, outputFile.getAbsolutePath()});
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "[FfmpegNativeVideoEngine] Failed to start FFmpeg process: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void recordFrame(BufferedImage frame) {
        if (ffmpegStdin == null || frame == null) {
            return;
        }

        try {
            byte[] frameData;
            if (frame.getType() == BufferedImage.TYPE_3BYTE_BGR) {
                frameData = ((DataBufferByte) frame.getRaster().getDataBuffer()).getData();
            } else {
                BufferedImage bgrFrame = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                var g = bgrFrame.createGraphics();
                g.drawImage(frame, 0, 0, null);
                g.dispose();
                frameData = ((DataBufferByte) bgrFrame.getRaster().getDataBuffer()).getData();
            }

            ffmpegStdin.write(frameData);
            ffmpegStdin.flush();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "[FfmpegNativeVideoEngine] Failed to pipe frame to FFmpeg: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        if (ffmpegStdin != null) {
            try {
                ffmpegStdin.close();
            } catch (IOException ignored) {
            }
        }

        if (ffmpegProcess != null) {
            try {
                String processOutput = new String(ffmpegProcess.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                boolean finished = ffmpegProcess.waitFor(30, TimeUnit.SECONDS);
                if (!finished) {
                    ffmpegProcess.destroyForcibly();
                    LOGGER.log(Level.WARNING, "[FfmpegNativeVideoEngine] FFmpeg process timed out during shutdown");
                } else if (ffmpegProcess.exitValue() != 0) {
                    LOGGER.log(Level.WARNING, "[FfmpegNativeVideoEngine] FFmpeg exited with non-zero code {0}. Output:\n{1}",
                            new Object[]{ffmpegProcess.exitValue(), processOutput});
                } else {
                    LOGGER.log(Level.INFO, "[FfmpegNativeVideoEngine] Video finalized successfully ({0} bytes): {1}",
                            new Object[]{outputFile.length(), outputFile.getAbsolutePath()});
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "[FfmpegNativeVideoEngine] Error finalizing FFmpeg: " + e.getMessage(), e);
            } finally {
                ffmpegProcess = null;
                ffmpegStdin = null;
            }
        }
    }

    @Override
    public File getOutputFile() {
        return outputFile;
    }
}
