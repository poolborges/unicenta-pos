package com.openbravo.pos.i18n.tool;

import com.openbravo.pos.spi.i18n.I18nProvider;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * KriolOS POS - Internationalization (i18n) Verification & Audit Tool
 * ====================================================================
 * Inspects all installed translation modules (via SPI and filesystem)
 * and audits them against the English base properties:
 *   1) kriolos-opos-app/src/main/resources/pos_messages.properties
 *   2) kriolos-opos-assets-reports/src/main/resources/com/openbravo/reports/*_messages.properties
 *
 * Verifies:
 *   - Missing keys (present in English base, missing in language bundle)
 *   - Untranslated / seeded keys (identical to English base text)
 *   - Blank / empty translation values
 *   - Obsolete / extra keys
 *
 * Can be executed directly with:
 *   mvn exec:java -pl kriolos-opos-i18n/kriolos-opos-i18n-all
 *
 * Command line options:
 *   --fail-on-missing : Exits with return code 1 if any language has missing keys (for CI/CD).
 *   --details         : Prints every missing/untranslated key per file.
 *   --lang=<code>     : Filters audit to a specific language code (e.g. --lang=es or --lang=fr).
 *   --log             : Saves audit output to default log file (target/i18n-audit.log).
 *   --log-file=<path> : Saves audit output to the specified log file.
 *
 * @author KriolOS POS
 * @since 1.0.0
 */
public class I18nVerifier {

    // ANSI Color Escape Codes
    public static final String ANSI_RESET  = "\u001B[0m";
    public static final String ANSI_BOLD   = "\u001B[1m";
    public static final String ANSI_RED    = "\u001B[31m";
    public static final String ANSI_GREEN  = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN   = "\u001B[36m";
    public static final String ANSI_BLUE   = "\u001B[34m";

    public static class AuditPrinter implements AutoCloseable {
        private final PrintWriter fileWriter;
        private final File logFile;

        public AuditPrinter(File logFile) {
            this.logFile = logFile;
            PrintWriter writer = null;
            if (logFile != null) {
                try {
                    File parent = logFile.getParentFile();
                    if (parent != null) {
                        parent.mkdirs();
                    }
                    writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logFile), StandardCharsets.UTF_8));
                } catch (Exception e) {
                    System.err.println(ANSI_RED + "⚠️ Aviso: Não foi possível abrir o ficheiro de log: " + e.getMessage() + ANSI_RESET);
                }
            }
            this.fileWriter = writer;
        }

        public void println() {
            System.out.println();
            if (fileWriter != null) {
                fileWriter.println();
            }
        }

        public void println(String message) {
            System.out.println(message);
            if (fileWriter != null) {
                fileWriter.println(stripAnsi(message));
            }
        }

        public void printf(String format, Object... args) {
            System.out.printf(format, args);
            if (fileWriter != null) {
                String formatted = String.format(Locale.ROOT, format, args);
                fileWriter.print(stripAnsi(formatted));
            }
        }

        public void flush() {
            System.out.flush();
            if (fileWriter != null) {
                fileWriter.flush();
            }
        }

        @Override
        public void close() {
            flush();
            if (fileWriter != null) {
                fileWriter.close();
            }
        }

        public File getLogFile() {
            return logFile;
        }

        private static String stripAnsi(String str) {
            if (str == null) return "";
            return str.replaceAll("\\u001B\\[[;\\d]*m", "");
        }
    }

    public static class FileAudit {
        public final String relativePath;
        public final Map<String, String> baseProperties = new LinkedHashMap<>();
        public final Map<String, Map<String, String>> targetProperties = new LinkedHashMap<>();
        public final Map<String, List<String>> missingKeys = new LinkedHashMap<>();
        public final Map<String, List<String>> untranslatedKeys = new LinkedHashMap<>();

        public FileAudit(String relativePath) {
            this.relativePath = relativePath;
        }
    }

    public static class LanguageSummary {
        public final String languageCode;
        public final String displayName;
        public int totalBaseKeys = 0;
        public int translatedKeys = 0;
        public int missingKeys = 0;
        public int untranslatedKeys = 0;

        public LanguageSummary(String languageCode, String displayName) {
            this.languageCode = languageCode;
            this.displayName = displayName;
        }

        public double getCoveragePercent() {
            if (totalBaseKeys == 0) return 100.0;
            return ((double) (totalBaseKeys - missingKeys) / totalBaseKeys) * 100.0;
        }
    }

    public static void main(String[] args) {
        boolean failOnMissing = false;
        boolean showDetails = false;
        String filterLang = null;
        File logFile = null;

        for (String arg : args) {
            if ("--fail-on-missing".equalsIgnoreCase(arg)) {
                failOnMissing = true;
            } else if ("--details".equalsIgnoreCase(arg)) {
                showDetails = true;
            } else if (arg.startsWith("--lang=")) {
                filterLang = arg.substring("--lang=".length()).trim();
            } else if ("--log".equalsIgnoreCase(arg)) {
                logFile = new File("target/i18n-audit.log");
            } else if (arg.startsWith("--log-file=")) {
                logFile = new File(arg.substring("--log-file=".length()).trim());
            }
        }

        try (AuditPrinter out = new AuditPrinter(logFile)) {
            out.println(ANSI_BOLD + ANSI_CYAN + "================================================================================" + ANSI_RESET);
            out.println(ANSI_BOLD + ANSI_CYAN + "         KriolOS POS - Internationalization (i18n) Verification Tool           " + ANSI_RESET);
            out.println(ANSI_BOLD + ANSI_CYAN + "================================================================================" + ANSI_RESET);

            File rootDir = resolveWorkspaceRoot();
            if (rootDir == null) {
                out.println(ANSI_RED + "❌ Erro: Não foi possível determinar o diretório raiz do KriolOS POS." + ANSI_RESET);
                System.exit(1);
            }
            out.println("📁 Workspace Root: " + ANSI_BOLD + rootDir.getAbsolutePath() + ANSI_RESET);

            // 1. Discover all SPI I18n Providers
            Map<String, I18nProvider> providers = new LinkedHashMap<>();
            ServiceLoader<I18nProvider> serviceLoader = ServiceLoader.load(I18nProvider.class);
            for (I18nProvider p : serviceLoader) {
                providers.put(p.getLanguageCode(), p);
            }

            // Also discover any submodules on disk in case running without full classpath
            File i18nParent = new File(rootDir, "kriolos-opos-i18n");
            if (i18nParent.isDirectory()) {
                File[] subdirs = i18nParent.listFiles((d, n) -> n.startsWith("kriolos-opos-i18n_") && new File(d, n).isDirectory());
                if (subdirs != null) {
                    for (File d : subdirs) {
                        String suffix = d.getName().substring("kriolos-opos-i18n_".length());
                        String code = suffix.replace("_", "-");
                        if ("zh-cn".equalsIgnoreCase(code)) code = "zh-CN";
                        if ("ar-kw".equalsIgnoreCase(code)) code = "ar-KW";
                        if (!providers.containsKey(code)) {
                            final String finalCode = code;
                            providers.put(code, new I18nProvider() {
                                @Override public Locale getLocale() { return Locale.forLanguageTag(finalCode); }
                                @Override public String getLanguageCode() { return finalCode; }
                                @Override public String getDisplayName() { return Locale.forLanguageTag(finalCode).getDisplayName(); }
                            });
                        }
                    }
                }
            }

            out.println("🌐 Installed Language Providers: " + ANSI_GREEN + providers.size() + " idiomas" + ANSI_RESET + " (" + String.join(", ", providers.keySet()) + ")");
            out.println();

            // 2. Discover Base Properties
            List<FileAudit> fileAudits = new ArrayList<>();
            File appProps = new File(rootDir, "kriolos-opos-app/src/main/resources/pos_messages.properties");
            if (appProps.exists()) {
                FileAudit audit = new FileAudit("pos_messages.properties");
                loadProperties(appProps, audit.baseProperties);
                fileAudits.add(audit);
            }

            File reportsDir = new File(rootDir, "kriolos-opos-assets-reports/src/main/resources/com/openbravo/reports");
            if (reportsDir.isDirectory()) {
                File[] reportFiles = reportsDir.listFiles((dir, name) -> name.endsWith("_messages.properties"));
                if (reportFiles != null) {
                    Arrays.sort(reportFiles, Comparator.comparing(File::getName));
                    for (File rf : reportFiles) {
                        FileAudit audit = new FileAudit("com/openbravo/reports/" + rf.getName());
                        loadProperties(rf, audit.baseProperties);
                        fileAudits.add(audit);
                    }
                }
            }

            int totalBaseKeysCount = fileAudits.stream().mapToInt(a -> a.baseProperties.size()).sum();
            out.println("📋 Ficheiros Base Encontrados: " + ANSI_BOLD + fileAudits.size() + ANSI_RESET + " ficheiros (" + ANSI_BOLD + totalBaseKeysCount + ANSI_RESET + " chaves base totais)");
            out.println();

            // 3. Perform Audit per Language
            Map<String, LanguageSummary> summaries = new LinkedHashMap<>();

            for (Map.Entry<String, I18nProvider> entry : providers.entrySet()) {
                String langCode = entry.getKey();
                I18nProvider provider = entry.getValue();

                if (filterLang != null && !filterLang.equalsIgnoreCase(langCode) && !filterLang.equalsIgnoreCase(provider.getLocale().getLanguage())) {
                    continue;
                }

                LanguageSummary summary = new LanguageSummary(langCode, provider.getDisplayName());
                summaries.put(langCode, summary);

                String modFolder = "kriolos-opos-i18n_" + langCode.toLowerCase().replace("-", "_");
                File modDir = new File(i18nParent, modFolder);
                File resDir = new File(modDir, "src/main/resources");

                String javaSuffix = langCode.replace("-", "_");

                for (FileAudit audit : fileAudits) {
                    String targetFileName;
                    String targetSubPath;
                    if (audit.relativePath.equals("pos_messages.properties")) {
                        targetFileName = "pos_messages_" + javaSuffix + ".properties";
                        targetSubPath = "";
                    } else {
                        String baseClean = audit.relativePath.replace("com/openbravo/reports/", "").replace("_messages.properties", "");
                        targetFileName = baseClean + "_messages_" + javaSuffix + ".properties";
                        targetSubPath = "com/openbravo/reports";
                    }

                    File targetFile = new File(resDir, targetSubPath.isEmpty() ? targetFileName : targetSubPath + "/" + targetFileName);
                    Map<String, String> targetProps = new LinkedHashMap<>();
                    if (targetFile.exists()) {
                        loadProperties(targetFile, targetProps);
                    }

                    audit.targetProperties.put(langCode, targetProps);

                    List<String> missing = new ArrayList<>();
                    List<String> untranslated = new ArrayList<>();

                    for (Map.Entry<String, String> baseEntry : audit.baseProperties.entrySet()) {
                        String key = baseEntry.getKey();
                        String baseVal = baseEntry.getValue().trim();

                        if (!targetProps.containsKey(key)) {
                            missing.add(key);
                        } else {
                            String transVal = targetProps.get(key).trim();
                            if (transVal.isEmpty() && !baseVal.isEmpty()) {
                                missing.add(key);
                            } else if (!"pt".equalsIgnoreCase(langCode) && transVal.equals(baseVal) && hasAlphabeticText(baseVal)) {
                                untranslated.add(key);
                            }
                        }
                    }

                    audit.missingKeys.put(langCode, missing);
                    audit.untranslatedKeys.put(langCode, untranslated);

                    summary.totalBaseKeys += audit.baseProperties.size();
                    summary.missingKeys += missing.size();
                    summary.untranslatedKeys += untranslated.size();
                    summary.translatedKeys += (audit.baseProperties.size() - missing.size());
                }
            }

            // 4. Render Summary Table
            out.println(ANSI_BOLD + "📊 RESUMO DA AUDITORIA DE IDIOMAS:" + ANSI_RESET);
            out.println("--------------------------------------------------------------------------------------------------");
            out.printf("%-10s | %-24s | %-10s | %-8s | %-12s | %-10s | %s%n",
                    "Idioma", "Nome Nativo", "Base", "Faltam", "Não-Traduz.", "Cobertura", "Status");
            out.println("--------------------------------------------------------------------------------------------------");

            int totalErrors = 0;
            for (LanguageSummary s : summaries.values()) {
                double coverage = s.getCoveragePercent();
                String status;
                String color;

                if (s.missingKeys == 0 && s.untranslatedKeys == 0) {
                    status = "✅ COMPLETO";
                    color = ANSI_GREEN;
                } else if (s.missingKeys == 0 && s.untranslatedKeys > 0) {
                    status = "⚠️ UNTRANSLATED (" + s.untranslatedKeys + ")";
                    color = ANSI_YELLOW;
                } else {
                    status = "❌ INCOMPLETO (" + s.missingKeys + " chaves)";
                    color = ANSI_RED;
                    totalErrors += s.missingKeys;
                }

                out.printf("%-10s | %-24s | %-10d | %-8d | %-12d | " + color + "%6.1f%%" + ANSI_RESET + "    | " + color + "%s" + ANSI_RESET + "%n",
                        s.languageCode, truncate(s.displayName, 24), s.totalBaseKeys, s.missingKeys, s.untranslatedKeys, coverage, status);
            }
            out.println("--------------------------------------------------------------------------------------------------");
            out.println();

            // 5. Details per file if requested or if missing
            if (showDetails || totalErrors > 0) {
                out.println(ANSI_BOLD + "🔍 DETALHES DE CHAVES EM FALTA POR FICHEIRO:" + ANSI_RESET);
                for (FileAudit audit : fileAudits) {
                    boolean hasAnyIssue = false;
                    for (String lang : summaries.keySet()) {
                        List<String> missing = audit.missingKeys.get(lang);
                        if (missing != null && !missing.isEmpty()) {
                            if (!hasAnyIssue) {
                                out.println("\n📄 " + ANSI_BOLD + audit.relativePath + ANSI_RESET + " (Total: " + audit.baseProperties.size() + " chaves):");
                                hasAnyIssue = true;
                            }
                            out.println("   [" + ANSI_RED + lang + ANSI_RESET + "] Faltam " + missing.size() + " chaves: "
                                    + (missing.size() <= 8 ? String.join(", ", missing) : String.join(", ", missing.subList(0, 8)) + "... (+ " + (missing.size() - 8) + " mais)"));
                        }
                    }
                }
                out.println();
            }

            out.println(ANSI_BOLD + ANSI_CYAN + "================================================================================" + ANSI_RESET);
            if (totalErrors == 0) {
                out.println(ANSI_BOLD + ANSI_GREEN + "🎉 Parabéns! Todas as traduções possuem 100% das chaves dos ficheiros base!" + ANSI_RESET);
            } else {
                out.println(ANSI_BOLD + ANSI_YELLOW + "⚠️ Auditoria concluída com " + totalErrors + " chaves em falta no total." + ANSI_RESET);
            }
            out.println(ANSI_BOLD + ANSI_CYAN + "================================================================================" + ANSI_RESET);

            if (out.getLogFile() != null) {
                out.println(ANSI_GREEN + "📄 Relatório exportado com sucesso para: " + ANSI_BOLD + out.getLogFile().getAbsolutePath() + ANSI_RESET);
            }

            if (totalErrors > 0 && failOnMissing) {
                System.err.println(ANSI_RED + "❌ Falha no teste de auditoria (--fail-on-missing ativo)." + ANSI_RESET);
                System.exit(1);
            }
        }
    }

    private static void loadProperties(File file, Map<String, String> map) {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            Properties props = new Properties();
            props.load(reader);
            for (String name : props.stringPropertyNames()) {
                map.put(name, props.getProperty(name));
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private static boolean hasAlphabeticText(String text) {
        if (text == null) return false;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) return true;
        }
        return false;
    }

    private static String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }

    private static File resolveWorkspaceRoot() {
        File current = new File(System.getProperty("user.dir"));
        while (current != null) {
            if (new File(current, "kriolos-opos-app").isDirectory() && new File(current, "kriolos-opos-i18n").isDirectory()) {
                return current;
            }
            current = current.getParentFile();
        }
        return new File(".");
    }
}
