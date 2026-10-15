package io.github.kriolos.opos.automation.narration;

import io.github.kriolos.opos.automation.narration.PosAudioNarrator.Language;
import java.io.File;

/**
 * Pluggable SPI for Text-to-Speech synthesis engines.
 *
 * <p>Enables interchangeable engines:
 * <ul>
 *   <li>Native OS engines (Linux RHVoice / spd-say, macOS say, Windows SAPI)</li>
 *   <li>Pure Java synthesizers (MaryTTS, FreeTTS)</li>
 *   <li>Synthetic mock engines for headless CI pipelines without audio hardware</li>
 * </ul>
 */
public interface TtsEngine {

    /**
     * Unique identifier or human-readable name of the engine.
     */
    String getName();

    /**
     * Checks if the engine is available on the current environment.
     */
    boolean isAvailable();

    /**
     * Synthesizes the given text into an output WAV audio file.
     *
     * @param text      text to synthesize
     * @param language  target language (PT, EN)
     * @param outputWav destination WAV file
     * @return the synthesized WAV file if successful, or {@code null} on failure
     */
    File synthesize(String text, Language language, File outputWav);
}
