module io.github.kriolos.opos.i18n.ru {
    requires kriolos.opos.spi;
    requires static com.google.auto.service;

    provides com.openbravo.pos.spi.i18n.I18nProvider with io.github.kriolos.opos.i18n.ru.RuI18nProvider;
}
