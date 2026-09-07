package com.openbravo.pos.l10n.ptcv;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Currency;

/**
 * System utility designed for Pop!_OS terminal boundaries to print raw
 * localization formatting structures compiled inside the active OpenJDK CLDR
 * database matrix.
 *
 * @author KriolOS POS
 * @since 1.0.0
 */
public class InspectLocale {

    public static void main(String[] args) {
        // Enforces evaluation targeting Cape Verde regional parameters
        System.out.println("==================================================");
        System.out.println("     KRIOLOS POS - CLDR INSPECTOR ON POP!_OS      ");
        System.out.println("==================================================");
        System.out.println("JVM Vendor    : " + System.getProperty("java.vendor"));
        System.out.println("Java Version  : " + System.getProperty("java.version"));
        System.out.println("--------------------------------------------------");

        printCountryLocale("pt", "AO", "AOA"); //Angola
        printCountryLocale("pt", "BR", "BRL"); //Brasil
        printCountryLocale("pt", "CV", "CVE"); //Cabo Verde
        printCountryLocale("pt", "GW", "XOF"); // Guine-Bissau
        printCountryLocale("pt", "MZ", "MZN"); //Mozambique
        printCountryLocale("pt", "ST", "STN"); //São Tomé e Principe: STN = New, STD = [1975, 2017]
        printCountryLocale("pt", "PT", "EUR"); //Portugal
    }

    private static void printCountryLocale(String lang, String country, String currencySymbol) {
        Locale cvLocale = new Locale.Builder().setLanguage(lang).setRegion(country).build();

        ZonedDateTime now = ZonedDateTime.now();


        // 1. Raw Core Numeric Formatting Patterns
        NumberFormat num = NumberFormat.getNumberInstance(cvLocale);
        NumberFormat integer = NumberFormat.getIntegerInstance(cvLocale);
        NumberFormat percent = NumberFormat.getPercentInstance(cvLocale);
        NumberFormat currency = NumberFormat.getCurrencyInstance(cvLocale);
        
        System.out.println("--------------------------------------------------");
        
        System.out.println("Target Locale (Country Code) : " + cvLocale.toString());
        System.out.println("Raw Double Formatter (1500.75)    : " + num.format(1500.75));
        System.out.println("Raw Integer Formatter (1500.75)   : " + integer.format(1500.75));
        System.out.println("Raw Percent Formatter (0.15)      : " + percent.format(0.15));
        System.out.println("Raw Currency Formatter (1500.0)   : " + currency.format(1500.0));
        System.out.println("Raw Currency Formatter (1200.50)   : " + currency.format(1200.50));
        System.out.println("Raw Currency Symbol      : " + Currency.getInstance(cvLocale).getSymbol());
        System.out.println("Raw Currency Name     : " + Currency.getInstance(cvLocale).getDisplayName());
        System.out.println("Raw Currency Symbol      : " + Currency.getInstance(currencySymbol).getSymbol(cvLocale));
        System.out.println("Raw Currency Name     : " + Currency.getInstance(currencySymbol).getDisplayName(cvLocale));

        // 2. Underlying Bytecode Mask Patterns (Patterns used by the JVM)
        if (currency instanceof DecimalFormat df) {
            System.out.println("Underlying Decimal Pattern String : " + df.toPattern());
        }

        System.out.println("--------------------------------------------------");

        // 3. Temporal Java.Time Date/Time Formatting Patterns
        DateTimeFormatter dateFmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(cvLocale);
        DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM).withLocale(cvLocale);
        DateTimeFormatter timeFmt = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withLocale(cvLocale);
        DateTimeFormatter hourMinFmt = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(cvLocale);

        System.out.println("Date Formatter (MEDIUM)           : " + dateFmt.format(now));
        System.out.println("DateTime Formatter (MEDIUM)       : " + dateTimeFmt.format(now));
        System.out.println("Time Formatter (MEDIUM)           : " + timeFmt.format(now));
        System.out.println("HourMin Formatter (SHORT)         : " + hourMinFmt.format(now));
        System.out.println("==================================================");
    }
}


/*
# 1. Lista o fuso horário e a hora ativa do sistema operativo
timedatectl

# 2. Exibe as variáveis de ambiente de localização ativas no teu utilizador
locale

# 3. Imprime as regras de formatação monetária configuradas no Linux
locale -k LC_MONETARY
*/
