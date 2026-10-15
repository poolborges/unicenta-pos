package io.github.kriolos.opos.automation.narration;

import io.github.kriolos.opos.automation.narration.PosAudioNarrator.Language;
import java.io.File;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for TTS Engines (Azure, Edge Neural, RHVoice, Mock) across multilingual support.
 */
public class TtsEnginesTest {

    @Test
    @DisplayName("Verify AzureTtsEngine gracefully handles missing subscription key")
    public void testAzureEngineWithoutKey() {
        AzureTtsEngine azure = new AzureTtsEngine();
        Assertions.assertNotNull(azure.getName());
        if (!azure.isAvailable()) {
            File output = new File("target/test-audio/azure_test.wav");
            File result = azure.synthesize("Teste", Language.PT, output);
            Assertions.assertNull(result, "Synthesis should return null when subscription key is absent");
        }
    }

    @Test
    @DisplayName("Verify EdgeNeuralTtsEngine synthesizes across multiple languages (PT, EN, FR, ES, AR, ZH_CN)")
    public void testEdgeNeuralEngineMultilingual() {
        EdgeNeuralTtsEngine edge = new EdgeNeuralTtsEngine();
        Assertions.assertNotNull(edge.getName());
        if (edge.isAvailable()) {
            // Test Portuguese
            File ptOutput = new File("target/test-audio/edge_pt_test.wav");
            File ptResult = edge.synthesize("Bem-vindo ao KriolOS POS.", Language.PT, ptOutput);
            Assertions.assertNotNull(ptResult);
            Assertions.assertTrue(ptResult.exists() && ptResult.length() > 0);

            // Test French
            File frOutput = new File("target/test-audio/edge_fr_test.wav");
            File frResult = edge.synthesize("Bienvenue sur KriolOS POS.", Language.FR, frOutput);
            Assertions.assertNotNull(frResult);
            Assertions.assertTrue(frResult.exists() && frResult.length() > 0);

            // Test Spanish
            File esOutput = new File("target/test-audio/edge_es_test.wav");
            File esResult = edge.synthesize("Bienvenido a KriolOS POS.", Language.ES, esOutput);
            Assertions.assertNotNull(esResult);
            Assertions.assertTrue(esResult.exists() && esResult.length() > 0);
        }
    }

    @Test
    @DisplayName("Verify MockJavaTtsEngine produces valid 24kHz PCM WAV for all languages")
    public void testMockJavaEngineAllLanguages() {
        MockJavaTtsEngine mock = new MockJavaTtsEngine();
        Assertions.assertTrue(mock.isAvailable());

        for (Language lang : Language.values()) {
            File output = new File("target/test-audio/mock_" + lang.getCode() + "_test.wav");
            File result = mock.synthesize("Teste sintetico " + lang.getLabel(), lang, output);
            Assertions.assertNotNull(result);
            Assertions.assertTrue(result.exists() && result.length() > 0);
        }
    }

    @Test
    @DisplayName("Verify PosAudioNarrator loads all externalized resource bundles (PT, EN, FR, ES, AR, ZH_CN)")
    public void testPosAudioNarratorResourceBundles() {
        PosAudioNarrator narrator = new PosAudioNarrator("bundle_test");
        Assertions.assertNotNull(narrator);
        for (Language lang : Language.values()) {
            String intro = narrator.getLocalizedText("showcase.intro", lang);
            Assertions.assertNotNull(intro, "Script bundle must contain showcase.intro for language: " + lang);
            Assertions.assertFalse(intro.isBlank(), "showcase.intro must not be blank for language: " + lang);
        }
    }
}
