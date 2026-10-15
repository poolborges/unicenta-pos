package io.github.kriolos.opos.automation.narration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * 100% Pure Java Audio Timeline Mixer using {@link javax.sound.sampled}.
 *
 * <p>Mixes multiple timed audio clips into a unified continuous PCM WAV soundtrack
 * with exact millisecond positioning and sample-level clipping protection.
 *
 * <p>Requires ZERO external CLI tools (no FFmpeg needed for audio timeline mixing).
 */
public final class JavaAudioTimelineMixer {

    private static final Logger LOGGER = Logger.getLogger(JavaAudioTimelineMixer.class.getName());

    public record TimedAudioClip(long offsetMs, File wavFile) {}

    private JavaAudioTimelineMixer() {
    }

    /**
     * Mixes the given timed audio clips into a single continuous WAV soundtrack file.
     *
     * @param clips     ordered list of audio clips with millisecond timeline offsets
     * @param outputWav target output continuous WAV file
     * @return true if mixing succeeded, false otherwise
     */
    public static boolean mixTimeline(List<TimedAudioClip> clips, File outputWav) {
        if (clips == null || clips.isEmpty()) {
            LOGGER.log(Level.WARNING, "[JavaAudioMixer] No audio clips provided for timeline mixing");
            return false;
        }

        try {
            AudioFormat baseFormat = null;

            // 1. Inspect first valid clip to determine common sample rate and channels
            for (TimedAudioClip clip : clips) {
                if (clip.wavFile() != null && clip.wavFile().exists() && clip.wavFile().length() > 44) {
                    try (AudioInputStream ais = AudioSystem.getAudioInputStream(clip.wavFile())) {
                        baseFormat = ais.getFormat();
                        break;
                    } catch (Exception ignored) {
                    }
                }
            }

            if (baseFormat == null) {
                LOGGER.log(Level.WARNING, "[JavaAudioMixer] Could not resolve audio format from input clips");
                return false;
            }

            float sampleRate = baseFormat.getSampleRate();
            int channels = baseFormat.getChannels();
            boolean isBigEndian = baseFormat.isBigEndian();

            List<DecodedClip> decodedClips = new ArrayList<>();
            long maxTotalFrames = 0;

            // 2. Decode all clips to 16-bit signed PCM short samples
            for (TimedAudioClip clip : clips) {
                if (clip.wavFile() == null || !clip.wavFile().exists()) {
                    continue;
                }

                try (AudioInputStream ais = AudioSystem.getAudioInputStream(clip.wavFile())) {
                    byte[] pcmBytes = ais.readAllBytes();
                    short[] samples = decodeBytesToShorts(pcmBytes, isBigEndian);

                    long startFrame = (long) (clip.offsetMs() * (sampleRate / 1000.0));
                    long framesInClip = samples.length / channels;
                    long endFrame = startFrame + framesInClip;

                    if (endFrame > maxTotalFrames) {
                        maxTotalFrames = endFrame;
                    }

                    decodedClips.add(new DecodedClip(startFrame * channels, samples));
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "[JavaAudioMixer] Error reading clip " + clip.wavFile() + ": " + e.getMessage());
                }
            }

            if (decodedClips.isEmpty() || maxTotalFrames == 0) {
                LOGGER.log(Level.WARNING, "[JavaAudioMixer] No valid audio frames decoded");
                return false;
            }

            // 3. Mix clips into master short sample buffer with clipping saturation
            int totalSamples = (int) (maxTotalFrames * channels);
            short[] masterBuffer = new short[totalSamples];

            for (DecodedClip dc : decodedClips) {
                int startSample = (int) dc.startSampleIndex;
                short[] src = dc.samples;

                for (int i = 0; i < src.length && (startSample + i) < masterBuffer.length; i++) {
                    int sum = masterBuffer[startSample + i] + src[i];
                    if (sum > Short.MAX_VALUE) {
                        sum = Short.MAX_VALUE;
                    } else if (sum < Short.MIN_VALUE) {
                        sum = Short.MIN_VALUE;
                    }
                    masterBuffer[startSample + i] = (short) sum;
                }
            }

            // 4. Encode master buffer to PCM byte array
            byte[] masterBytes = encodeShortsToBytes(masterBuffer, isBigEndian);

            // 5. Write master WAV audio file
            if (outputWav.getParentFile() != null) {
                outputWav.getParentFile().mkdirs();
            }

            try (ByteArrayInputStream bais = new ByteArrayInputStream(masterBytes);
                 AudioInputStream outAis = new AudioInputStream(bais, baseFormat, maxTotalFrames)) {
                AudioSystem.write(outAis, AudioFileFormat.Type.WAVE, outputWav);
            }

            LOGGER.log(Level.INFO, "[JavaAudioMixer] Successfully mixed {0} clips into pure Java WAV ({1} frames, {2} bytes): {3}",
                    new Object[]{decodedClips.size(), maxTotalFrames, outputWav.length(), outputWav.getName()});

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[JavaAudioMixer] Failure mixing audio timeline: " + e.getMessage(), e);
            return false;
        }
    }

    private static short[] decodeBytesToShorts(byte[] bytes, boolean bigEndian) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        short[] shorts = new short[bytes.length / 2];
        for (int i = 0; i < shorts.length; i++) {
            shorts[i] = bb.getShort();
        }
        return shorts;
    }

    private static byte[] encodeShortsToBytes(short[] shorts, boolean bigEndian) {
        ByteBuffer bb = ByteBuffer.allocate(shorts.length * 2);
        bb.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        for (short s : shorts) {
            bb.putShort(s);
        }
        return bb.array();
    }

    private record DecodedClip(long startSampleIndex, short[] samples) {}
}
