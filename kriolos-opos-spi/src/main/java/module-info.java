module kriolos.opos.spi {
    requires java.logging;

    exports com.openbravo.pos.printer;
    exports com.openbravo.pos.spi.annotation;
    exports com.openbravo.pos.spi.core;
    exports com.openbravo.pos.spi.hardware;
    exports com.openbravo.pos.spi.hardware.display;
    exports com.openbravo.pos.spi.hardware.printer;
    exports com.openbravo.pos.spi.hardware.scale;
    exports com.openbravo.pos.spi.hardware.scanner;
    exports com.openbravo.pos.spi.localization;
    exports com.openbravo.pos.spi.provider;
    exports com.openbravo.pos.spi.scripting;

    uses com.openbravo.pos.spi.scripting.ScriptEngineProvider;
    uses com.openbravo.pos.spi.localization.LocalizationProvider;
    uses com.openbravo.pos.spi.hardware.PeripheralProvider;
}


