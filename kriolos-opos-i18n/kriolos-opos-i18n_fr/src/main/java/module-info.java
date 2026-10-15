module io.github.kriolos.opos.i18n.fr {
    requires kriolos.opos.spi;
    requires static com.google.auto.service;

    provides com.openbravo.pos.spi.i18n.I18nProvider with io.github.kriolos.opos.i18n.fr.FrI18nProvider;
}
