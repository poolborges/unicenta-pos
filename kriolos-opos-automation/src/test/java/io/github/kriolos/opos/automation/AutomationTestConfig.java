package io.github.kriolos.opos.automation;

import com.openbravo.pos.config.AppConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Isolated Test Configuration Provider for UI Automation and Integration Tests.
 *
 * <p>Ensures automation runs strictly against an isolated in-memory or target-directory
 * test database, avoiding any access or modification to the developer's local/production
 * database (e.g. ~/.local/share/kriolos/kriolos-opos.properties).
 *
 * <p>Also enforces a deterministic LTR locale (en-US) so that test UI layouts and text
 * directions do not leak from host system configurations.
 */
public final class AutomationTestConfig {

    private static final Logger LOGGER = Logger.getLogger(AutomationTestConfig.class.getName());

    public static final String TEST_CONFIG_DIR = "target/automation-config";
    public static final String TEST_CONFIG_FILENAME = "kriolos-opos-test.properties";
    public static final String TEST_DB_DIR = "target/automation-db";

    private static volatile File configFile;

    private AutomationTestConfig() {
    }

    /**
     * Obtains or creates the dedicated automation test configuration file.
     */
    public static synchronized File getConfigFile() {
        if (configFile != null && configFile.exists()) {
            return configFile;
        }

        try {
            Path configDir = Paths.get(TEST_CONFIG_DIR).toAbsolutePath();
            Files.createDirectories(configDir);

            Path dbDir = Paths.get(TEST_DB_DIR).toAbsolutePath();
            Files.createDirectories(dbDir);

            configFile = configDir.resolve(TEST_CONFIG_FILENAME).toFile();

            Properties props = new Properties();

            // Machine & Instance settings
            props.setProperty("machine.hostname", "automation-test");
            props.setProperty("machine.uniqueinstance", "false");
            props.setProperty("pos.config.provider", "database");

            // Isolated Test Database (HSQLDB in target directory)
            String dbUrl = "jdbc:hsqldb:file:" + dbDir.resolve("kriolopos").toString().replace("\\", "/") + ";sql.syntax_mys=true";
            props.setProperty("db.name", "Automation Test DB");
            props.setProperty("db.URL", dbUrl);
            props.setProperty("db.user", "sa");
            props.setProperty("db.password", "");

            // Primary database setup
            props.setProperty("db.default", "Automation Test DB");

            // Explicitly enforce standard LTR locale (en-US) for deterministic UI behavior
            props.setProperty("localization.locale", "en-US");
            props.setProperty("user.language", "en");
            props.setProperty("user.country", "US");
            props.setProperty("user.variant", "");

            try (OutputStream out = new FileOutputStream(configFile)) {
                props.store(out, "KriolOS POS UI Automation Isolated Test Configuration");
            }

            LOGGER.log(Level.INFO, "[AutomationTestConfig] Created isolated test config at: {0} (DB: {1})",
                    new Object[]{configFile.getAbsolutePath(), dbUrl});

            return configFile;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize automation test configuration: " + e.getMessage(), e);
        }
    }

    /**
     * Applies system properties and initializes singleton AppConfig so any early
     * lookup before BootstrapPOS.main also resolves to this isolated test configuration.
     */
    public static synchronized void applyTestEnvironment() {
        File file = getConfigFile();
        String path = file.getAbsolutePath();

        System.setProperty("pos.config.file", path);
        System.setProperty("kriolos.config", path);
        System.setProperty("app.config", path);
        System.setProperty("config.file", path);
        System.setProperty("machine.uniqueinstance", "false");

        // Force AppConfig singleton to this test configuration file
        AppConfig.init(file);
    }

    /**
     * Returns the command line arguments to pass to BootstrapPOS.main(args).
     */
    public static String[] getMainArgs() {
        return new String[]{getConfigFile().getAbsolutePath()};
    }
}
