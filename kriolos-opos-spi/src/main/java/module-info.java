module kriolos.opos.spi {
    requires java.logging;
    requires java.sql;

    exports com.openbravo.pos.spi.annotation;
    exports com.openbravo.pos.spi.config;
    exports com.openbravo.pos.spi.core;
    exports com.openbravo.pos.spi.datasource;
    exports com.openbravo.pos.spi.hardware;
    exports com.openbravo.pos.spi.hardware.display;
    exports com.openbravo.pos.spi.hardware.printer;
    exports com.openbravo.pos.spi.hardware.scale;
    exports com.openbravo.pos.spi.hardware.scanner;
    exports com.openbravo.pos.spi.i18n;
    exports com.openbravo.pos.spi.localization;
    exports com.openbravo.pos.spi.provider;
    exports com.openbravo.pos.spi.scripting;

    uses com.openbravo.pos.spi.config.BootstrapConfigProvider;
    uses com.openbravo.pos.spi.datasource.DataSourceProvider;
    uses com.openbravo.pos.spi.scripting.ScriptEngineProvider;
    uses com.openbravo.pos.spi.localization.LocalizationProvider;
    uses com.openbravo.pos.spi.hardware.PeripheralProvider;
    uses com.openbravo.pos.spi.i18n.I18nProvider;
    uses com.openbravo.pos.spi.core.PluginContractProvider;
}


