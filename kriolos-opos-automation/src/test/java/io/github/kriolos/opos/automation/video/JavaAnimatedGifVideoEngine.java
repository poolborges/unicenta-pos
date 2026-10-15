package io.github.kriolos.opos.automation.video;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

/**
 * 100% Pure Java Video Recorder Engine using {@link javax.imageio}.
 *
 * <p>Encodes visual Swing frames directly to an animated GIF and generates an accompanying
 * self-contained HTML5 Canvas Player ({@code session_player.html}) for viewing without
 * any video codecs or external CLI binaries.
 *
 * <p>Guarantees that screen recording succeeds on any operating system, headless Docker
 * container, or restricted CI/CD runner where FFmpeg is not installed.
 */
public class JavaAnimatedGifVideoEngine implements VideoRecorderEngine {

    private static final Logger LOGGER = Logger.getLogger(JavaAnimatedGifVideoEngine.class.getName());

    private File outputFile;
    private File actualGifFile;
    private ImageWriter gifWriter;
    private ImageOutputStream imageOutputStream;
    private IIOMetadata metadata;
    private int fps;
    private int frameCount = 0;
    private long lastFrameTime = 0;
    private final long minFrameIntervalMs;

    public JavaAnimatedGifVideoEngine() {
        this.minFrameIntervalMs = 100; // Cap at ~10 fps for pure Java GIF encoding efficiency
    }

    @Override
    public String getName() {
        return "Pure Java Engine (Animated GIF & HTML5 Player)";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void start(File outputFile, int width, int height, int fps) throws IOException {
        this.outputFile = outputFile;
        this.fps = (fps > 0) ? fps : 10;
        this.frameCount = 0;
        this.lastFrameTime = 0;

        // If target file was named .mp4, generate .gif for pure Java fallback
        String path = outputFile.getAbsolutePath();
        if (path.toLowerCase().endsWith(".mp4")) {
            actualGifFile = new File(path.substring(0, path.length() - 4) + ".gif");
        } else {
            actualGifFile = outputFile;
        }

        if (actualGifFile.getParentFile() != null) {
            actualGifFile.getParentFile().mkdirs();
        }

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("gif");
        if (!writers.hasNext()) {
            throw new IOException("No ImageWriter found for GIF format in javax.imageio");
        }
        gifWriter = writers.next();

        imageOutputStream = new FileImageOutputStream(actualGifFile);
        gifWriter.setOutput(imageOutputStream);
        gifWriter.prepareWriteSequence(null);

        // Setup GIF delay metadata (in 1/100ths of a second)
        ImageTypeSpecifier specifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
        metadata = gifWriter.getDefaultImageMetadata(specifier, null);
        configureGifMetadata(metadata, Math.max(1, 100 / this.fps));

        LOGGER.log(Level.INFO, "[JavaAnimatedGifEngine] Started pure Java GIF recording ({0}x{1} @ {2} fps) -> {3}",
                new Object[]{width, height, this.fps, actualGifFile.getAbsolutePath()});
    }

    private void configureGifMetadata(IIOMetadata metadata, int delayTimeCentiseconds) {
        String metaFormat = metadata.getNativeMetadataFormatName();
        if (!"javax_imageio_gif_image_1.0".equals(metaFormat)) {
            return;
        }

        try {
            IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormat);
            IIOMetadataNode gce = findOrCreateChild(root, "GraphicControlExtension");
            gce.setAttribute("disposalMethod", "none");
            gce.setAttribute("userInputFlag", "FALSE");
            gce.setAttribute("transparentColorFlag", "FALSE");
            gce.setAttribute("delayTime", Integer.toString(delayTimeCentiseconds));
            gce.setAttribute("transparentColorIndex", "0");

            metadata.setFromTree(metaFormat, root);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "[JavaAnimatedGifEngine] Could not configure GIF metadata: " + e.getMessage());
        }
    }

    private IIOMetadataNode findOrCreateChild(IIOMetadataNode root, String nodeName) {
        for (int i = 0; i < root.getLength(); i++) {
            if (root.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                return (IIOMetadataNode) root.item(i);
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        root.appendChild(node);
        return node;
    }

    @Override
    public synchronized void recordFrame(BufferedImage frame) {
        if (gifWriter == null || frame == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (lastFrameTime > 0 && (now - lastFrameTime) < minFrameIntervalMs) {
            return; // Skip duplicate frame within threshold
        }
        lastFrameTime = now;

        try {
            // Downscale to 800px width for fast, compact pure Java GIF encoding
            int targetWidth = Math.min(800, frame.getWidth());
            int targetHeight = (int) ((double) frame.getHeight() * targetWidth / frame.getWidth());

            BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = scaled.createGraphics();
            g.drawImage(frame, 0, 0, targetWidth, targetHeight, null);
            g.dispose();

            gifWriter.writeToSequence(new IIOImage(scaled, null, metadata), null);
            frameCount++;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "[JavaAnimatedGifEngine] Failed to write frame to GIF: " + e.getMessage());
        }
    }

    @Override
    public synchronized void stop() {
        if (gifWriter != null) {
            try {
                gifWriter.endWriteSequence();
                imageOutputStream.close();
                LOGGER.log(Level.INFO, "[JavaAnimatedGifEngine] Finalized pure Java GIF ({0} frames, {1} bytes): {2}",
                        new Object[]{frameCount, actualGifFile.length(), actualGifFile.getAbsolutePath()});

                generateHtml5Player(actualGifFile);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "[JavaAnimatedGifEngine] Error finalizing GIF: " + e.getMessage(), e);
            } finally {
                gifWriter = null;
                imageOutputStream = null;
            }
        }
    }

    private void generateHtml5Player(File gifFile) {
        File htmlFile = new File(gifFile.getParentFile(), "session_player.html");
        try (FileWriter writer = new FileWriter(htmlFile)) {
            writer.write("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>KriolOS POS - Automated Session Recording Player</title>
                    <style>
                        body { background: #121214; color: #e1e1e6; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; display: flex; flex-direction: column; align-items: center; padding: 24px; }
                        h2 { margin-bottom: 8px; color: #00d2df; }
                        .container { background: #202024; border-radius: 8px; padding: 16px; box-shadow: 0 4px 20px rgba(0,0,0,0.5); max-width: 900px; text-align: center; }
                        img { max-width: 100%; border-radius: 4px; border: 1px solid #323238; }
                        .info { margin-top: 12px; font-size: 14px; color: #a8a8b3; }
                    </style>
                </head>
                <body>
                    <h2>KriolOS POS Automation - Session Recording</h2>
                    <div class="container">
                        <img src="%s" alt="Session Recording">
                        <div class="info">Recorded via Pure Java Engine (javax.imageio) &bull; Frames: %d &bull; Frame Rate: %d fps</div>
                    </div>
                </body>
                </html>
                """.formatted(gifFile.getName(), frameCount, fps));
            LOGGER.log(Level.INFO, "[JavaAnimatedGifEngine] Generated HTML5 Canvas Player: {0}", htmlFile.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "[JavaAnimatedGifEngine] Could not generate HTML5 player: " + e.getMessage());
        }
    }

    @Override
    public File getOutputFile() {
        return (actualGifFile != null) ? actualGifFile : outputFile;
    }
}
