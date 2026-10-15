package io.github.kriolos.opos.automation;

import io.github.kriolos.opos.automation.narration.JavaAudioTimelineMixer;
import io.github.kriolos.opos.automation.narration.JavaAudioTimelineMixer.TimedAudioClip;
import io.github.kriolos.opos.automation.narration.MockJavaTtsEngine;
import io.github.kriolos.opos.automation.narration.RhVoiceTtsEngine;
import io.github.kriolos.opos.automation.narration.TtsEngine;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Multi-Language Audio Narration & Video Soundtrack Generator for KriolOS POS Automation.
 *
 * <p>Features:
 * <ul>
 *   <li>Externalized multi-language narration scripts loaded from {@code /narration/narration_{lang}.properties}</li>
 *   <li>Pluggable {@link TtsEngine} architecture supporting native synthesizers and pure Java fallback</li>
 *   <li>Pure Java timeline audio mixing via {@link JavaAudioTimelineMixer} using {@link javax.sound.sampled}</li>
 *   <li>Synchronized timestamp recording aligned with UI automation actions</li>
 *   <li>Automatic compilation into single-language MP4s ({@code session_pt.mp4}, {@code session_en.mp4})</li>
 *   <li>Automatic compilation into YouTube-compliant dual-audio MP4 ({@code session_youtube_multilingual.mp4})
 *       containing both Portuguese and English audio tracks with ISO 639-2 language metadata</li>
 * </ul>
 */
public class PosAudioNarrator {

    private static final Logger LOGGER = Logger.getLogger(PosAudioNarrator.class.getName());

    public enum Language {
        PT("pt", "por", "Português", "Leticia-F123"),
        EN("en", "eng", "English", "slt");

        private final String code;
        private final String iso6392;
        private final String label;
        private final String voiceName;

        Language(String code, String iso6392, String label, String voiceName) {
            this.code = code;
            this.iso6392 = iso6392;
            this.label = label;
            this.voiceName = voiceName;
        }

        public String getCode() {
            return code;
        }

        public String getIso6392() {
            return iso6392;
        }

        public String getLabel() {
            return label;
        }

        public String getVoiceName() {
            return voiceName;
        }
    }

    private static class AudioCue {
        final long offsetMs;
        final Map<Language, File> audioFiles = new EnumMap<>(Language.class);

        AudioCue(long offsetMs) {
            this.offsetMs = offsetMs;
        }
    }

    private final String scenarioName;
    private final TtsEngine ttsEngine;
    private final Map<Language, Properties> scriptBundles = new EnumMap<>(Language.class);
    private final List<AudioCue> cues = Collections.synchronizedList(new ArrayList<>());
    private long startTimeMs = 0;

    public PosAudioNarrator(String scenarioName) {
        this(scenarioName, resolveDefaultTtsEngine());
    }

    public PosAudioNarrator(String scenarioName, TtsEngine ttsEngine) {
        this.scenarioName = scenarioName;
        this.ttsEngine = (ttsEngine != null) ? ttsEngine : new MockJavaTtsEngine();
        loadBundles();
        LOGGER.log(Level.INFO, "[AudioNarrator] Initialized with TTS Engine: {0}", this.ttsEngine.getName());
    }

    private static TtsEngine resolveDefaultTtsEngine() {
        RhVoiceTtsEngine nativeEngine = new RhVoiceTtsEngine();
        if (nativeEngine.isAvailable()) {
            return nativeEngine;
        }
        return new MockJavaTtsEngine();
    }

    private void loadBundles() {
        for (Language lang : Language.values()) {
            String path = "/narration/narration_" + lang.getCode() + ".properties";
            Properties props = new Properties();
            try (InputStream in = getClass().getResourceAsStream(path)) {
                if (in != null) {
                    props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
                    scriptBundles.put(lang, props);
                    LOGGER.log(Level.INFO, "[AudioNarrator] Loaded {0} narration script: {1} keys",
                            new Object[]{lang.getLabel(), props.size()});
                } else {
                    LOGGER.log(Level.WARNING, "[AudioNarrator] Narration bundle not found on classpath: {0}", path);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "[AudioNarrator] Could not load narration bundle " + path + ": " + e.getMessage(), e);
            }
        }
    }

    public TtsEngine getTtsEngine() {
        return ttsEngine;
    }

    public boolean isTtsAvailable() {
        return ttsEngine != null && ttsEngine.isAvailable();
    }

    /**
     * Starts the narration timeline.
     */
    public synchronized void start() {
        this.startTimeMs = System.currentTimeMillis();
        this.cues.clear();
        LOGGER.log(Level.INFO, "[AudioNarrator] Narration timeline started for scenario: {0}", scenarioName);
    }

    /**
     * Triggers a narration cue using a key defined in the externalized bundles.
     *
     * @param scriptKey key name in {@code narration_{lang}.properties}
     */
    public void narrateKey(String scriptKey) {
        if (!isTtsAvailable() || startTimeMs == 0) {
            return;
        }

        long offsetMs = Math.max(0, System.currentTimeMillis() - startTimeMs);
        AudioCue cue = new AudioCue(offsetMs);

        File tempDir = new File("target/recordings/" + scenarioName + "/audio_cues");
        tempDir.mkdirs();

        for (Language lang : Language.values()) {
            Properties props = scriptBundles.get(lang);
            String text = (props != null) ? props.getProperty(scriptKey) : null;
            if (text != null && !text.isBlank()) {
                String safeName = scriptKey.replaceAll("[^a-zA-Z0-9_\\-]", "_") + "_" + lang.getCode() + "_" + System.currentTimeMillis() + ".wav";
                File wavFile = new File(tempDir, safeName);

                File synthesized = ttsEngine.synthesize(text, lang, wavFile);
                if (synthesized != null && synthesized.exists()) {
                    cue.audioFiles.put(lang, synthesized);
                }
            }
        }

        if (!cue.audioFiles.isEmpty()) {
            cues.add(cue);
            LOGGER.log(Level.INFO, "[AudioNarrator] Registered cue '{0}' at {1}ms for {2} languages",
                    new Object[]{scriptKey, offsetMs, cue.audioFiles.size()});
        }
    }

    /**
     * Compiles the raw video recording and synchronized audio tracks into final MP4 videos:
     * <ul>
     *   <li>{@code session_pt.mp4} (Portuguese soundtrack)</li>
     *   <li>{@code session_en.mp4} (English soundtrack)</li>
     *   <li>{@code session_youtube_multilingual.mp4} (Dual-audio track YouTube MP4)</li>
     * </ul>
     */
    public void compileNarratedVideos(File rawVideoMp4, File outputDir) {
        if (!isTtsAvailable() || cues.isEmpty() || rawVideoMp4 == null || !rawVideoMp4.exists()) {
            LOGGER.log(Level.INFO, "[AudioNarrator] Skipping video audio compilation (no cues or video missing).");
            return;
        }

        LOGGER.log(Level.INFO, "[AudioNarrator] Compiling {0} audio cues into multilingual videos...", cues.size());

        Map<Language, File> mixedAudioTracks = new EnumMap<>(Language.class);

        // 1. Build mixed soundtrack for each language using 100% pure Java Audio Mixer
        for (Language lang : Language.values()) {
            List<TimedAudioClip> clips = new ArrayList<>();
            for (AudioCue cue : cues) {
                File wav = cue.audioFiles.get(lang);
                if (wav != null && wav.exists()) {
                    clips.add(new TimedAudioClip(cue.offsetMs, wav));
                }
            }

            if (!clips.isEmpty()) {
                File mixedWav = new File(outputDir, "narration_" + lang.getCode() + ".wav");
                boolean mixed = JavaAudioTimelineMixer.mixTimeline(clips, mixedWav);
                if (mixed && mixedWav.exists()) {
                    mixedAudioTracks.put(lang, mixedWav);
                    File singleLangVideo = new File(outputDir, "session_" + lang.getCode() + ".mp4");
                    muxVideoWithAudio(rawVideoMp4, mixedWav, singleLangVideo, lang.getIso6392(), lang.getLabel());
                }
            }
        }

        // 2. Build multi-track YouTube video if both tracks exist
        if (mixedAudioTracks.containsKey(Language.PT) && mixedAudioTracks.containsKey(Language.EN)) {
            File ytVideo = new File(outputDir, "session_youtube_multilingual.mp4");
            muxYouTubeDualAudio(rawVideoMp4, mixedAudioTracks.get(Language.PT), mixedAudioTracks.get(Language.EN), ytVideo);
        }
    }

    private void muxVideoWithAudio(File video, File audio, File outputVideo, String isoLang, String langTitle) {
        List<String> cmd = List.of(
                "ffmpeg", "-y",
                "-i", video.getAbsolutePath(),
                "-i", audio.getAbsolutePath(),
                "-c:v", "copy",
                "-c:a", "aac",
                "-b:a", "192k",
                "-metadata:s:a:0", "language=" + isoLang,
                "-metadata:s:a:0", "title=" + langTitle,
                "-shortest",
                outputVideo.getAbsolutePath()
        );
        executeProcess(cmd, "Mux Video (" + langTitle + ")");
        if (outputVideo.exists()) {
            LOGGER.log(Level.INFO, "[AudioNarrator] Successfully generated {0} ({1} bytes)",
                    new Object[]{outputVideo.getName(), outputVideo.length()});
        }
    }

    private void muxYouTubeDualAudio(File video, File ptAudio, File enAudio, File outputVideo) {
        List<String> cmd = List.of(
                "ffmpeg", "-y",
                "-i", video.getAbsolutePath(),
                "-i", ptAudio.getAbsolutePath(),
                "-i", enAudio.getAbsolutePath(),
                "-map", "0:v",
                "-map", "1:a",
                "-map", "2:a",
                "-c:v", "copy",
                "-c:a", "aac",
                "-b:a", "192k",
                "-metadata:s:a:0", "language=por",
                "-metadata:s:a:0", "title=Português",
                "-metadata:s:a:1", "language=eng",
                "-metadata:s:a:1", "title=English",
                "-shortest",
                outputVideo.getAbsolutePath()
        );
        executeProcess(cmd, "Mux YouTube Multilingual Video");
        if (outputVideo.exists()) {
            LOGGER.log(Level.INFO, "[AudioNarrator] Successfully generated YouTube Multilingual Video: {0} ({1} bytes)",
                    new Object[]{outputVideo.getName(), outputVideo.length()});
        }
    }

    private void executeProcess(List<String> cmd, String taskName) {
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            boolean finished = process.waitFor(60, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                LOGGER.log(Level.WARNING, "[AudioNarrator] {0} timed out", taskName);
            } else if (process.exitValue() != 0) {
                LOGGER.log(Level.WARNING, "[AudioNarrator] {0} failed with exit code: {1}. Output:\n{2}",
                        new Object[]{taskName, process.exitValue(), output});
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "[AudioNarrator] Error in " + taskName + ": " + e.getMessage(), e);
        }
    }
}
