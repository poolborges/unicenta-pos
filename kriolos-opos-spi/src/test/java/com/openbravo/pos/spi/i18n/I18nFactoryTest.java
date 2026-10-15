package com.openbravo.pos.spi.i18n;

import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class I18nFactoryTest {

    @Test
    public void testFactorySingletonAndAvailable() {
        I18nFactory factory = I18nFactory.getInstance();
        Assertions.assertNotNull(factory);
        Assertions.assertNotNull(factory.getAvailableProviders());
    }

    @Test
    public void testCustomProviderLookup() {
        I18nProvider testProvider = new I18nProvider() {
            @Override
            public Locale getLocale() {
                return Locale.forLanguageTag("pt");
            }

            @Override
            public String getDisplayName() {
                return "Português";
            }
        };

        Assertions.assertEquals("pt", testProvider.getLanguageCode());
        Assertions.assertEquals("Português", testProvider.getDisplayName());
        Assertions.assertTrue(testProvider.supports(Locale.forLanguageTag("pt")));
        Assertions.assertTrue(testProvider.supports(Locale.forLanguageTag("pt-PT")));
        Assertions.assertFalse(testProvider.supports(Locale.forLanguageTag("fr")));
    }
}
