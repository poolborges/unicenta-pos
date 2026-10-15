package io.github.kriolos.opos.automation.narration;

import io.github.kriolos.opos.automation.narration.PosAudioNarrator.Language;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Microsoft Azure Cognitive Services Speech Neural TTS Engine.
 *
 * <p>Produces studio-grade natural human speech using Azure's Neural Speech REST API.
 * Configured via System properties or Environment variables:
 * <ul>
 *   <li>{@code azure.speech.key} or {@code AZURE_SPEECH_KEY}: Azure Cognitive Services Subscription Key</li>
 *   <li>{@code azure.speech.region} or {@code AZURE_SPEECH_REGION}: Azure Region (default: {@code westeurope})</li>
 *   <li>{@code azure.voice.pt}: Override Portuguese voice (default: {@code pt-PT-RaquelNeural})</li>
 *   <li>{@code azure.voice.en}: Override English voice (default: {@code en-US-AriaNeural})</li>
 * </ul>
 */
public class AzureTtsEngine implements TtsEngine {

    private static final Logger LOGGER = Logger.getLogger(AzureTtsEngine.class.getName());

    private final String subscriptionKey;
    private final String region;
    private final HttpClient httpClient;

    public AzureTtsEngine() {
        this.subscriptionKey = resolveConfig("azure.speech.key", "AZURE_SPEECH_KEY");
        String resolvedRegion = resolveConfig("azure.speech.region", "AZURE_SPEECH_REGION");
        this.region = (resolvedRegion != null && !resolvedRegion.isBlank()) ? resolvedRegion : "westeurope";
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    private static String resolveConfig(String propName, String envName) {
        String val = System.getProperty(propName);
        if (val == null || val.isBlank()) {
            val = System.getenv(envName);
        }
        return (val != null && !val.isBlank()) ? val.trim() : null;
    }

    @Override
    public String getName() {
        return "Azure Neural Speech (" + region + ")";
    }

    @Override
    public boolean isAvailable() {
        return subscriptionKey != null && !subscriptionKey.isBlank();
    }

    @Override
    public File synthesize(String text, Language language, File outputWav) {
        if (!isAvailable()) {
            LOGGER.log(Level.WARNING, "[AzureTts] Cannot synthesize: subscription key not configured");
            return null;
        }

        try {
            if (outputWav.getParentFile() != null) {
                outputWav.getParentFile().mkdirs();
            }

            String voiceName = resolveVoice(language);
            String xmlLang = language.getBcp47();
            String rate = System.getProperty("azure.speech.rate", "+0%");
            String pitch = System.getProperty("azure.speech.pitch", "+0%");

            String ssml = String.format(
                    "<speak version='1.0' xml:lang='%s'>"
                    + "<voice xml:lang='%s' name='%s'>"
                    + "<prosody rate='%s' pitch='%s'>%s</prosody>"
                    + "</voice>"
                    + "</speak>",
                    xmlLang, xmlLang, voiceName, rate, pitch, escapeXml(text)
            );

            URI uri = URI.create("https://" + region + ".tts.speech.microsoft.com/cognitiveservices/v1");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(20))
                    .header("Ocp-Apim-Subscription-Key", subscriptionKey)
                    .header("Content-Type", "application/ssml+xml")
                    .header("X-Microsoft-OutputFormat", "riff-24khz-16bit-mono-pcm")
                    .header("User-Agent", "kriolos-pos-automation")
                    .POST(HttpRequest.BodyPublishers.ofString(ssml, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<java.nio.file.Path> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofFile(outputWav.toPath()));

            if (response.statusCode() == 200 && outputWav.exists() && outputWav.length() > 0) {
                LOGGER.log(Level.INFO, "[AzureTts] Successfully synthesized ({0} bytes) using {1}: {2}",
                        new Object[]{outputWav.length(), voiceName, outputWav.getName()});
                return outputWav;
            } else {
                LOGGER.log(Level.WARNING, "[AzureTts] Azure API returned HTTP {0} for text: {1}",
                        new Object[]{response.statusCode(), text});
                return null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[AzureTts] Failed to synthesize via Azure: " + e.getMessage(), e);
            return null;
        }
    }

    private String resolveVoice(Language language) {
        String custom = System.getProperty("azure.voice." + language.getCode().toLowerCase());
        if (custom != null && !custom.isBlank()) {
            return custom;
        }
        return language.getNeuralVoice();
    }

    private String escapeXml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
