package io.github.kriolos.opos.automation;

import com.openbravo.pos.forms.BootstrapPOS;
import io.github.kriolos.opos.automation.actions.DatabaseSelectionAction;
import io.github.kriolos.opos.automation.actions.InstanceManagerAction;
import io.github.kriolos.opos.automation.actions.LoginAction;
import io.github.kriolos.opos.automation.actions.MainWindowAction;
import io.github.kriolos.opos.automation.actions.MenuNavigationAction;
import io.github.kriolos.opos.automation.actions.NavigationHistoryAction;
import io.github.kriolos.opos.automation.actions.PaymentDialogAction;
import io.github.kriolos.opos.automation.actions.SalesAction;
import io.github.kriolos.opos.automation.narration.PosAudioNarrator;
import io.github.kriolos.opos.automation.video.FfmpegScreenGifGenerator;
import io.github.kriolos.opos.automation.video.FfmpegScreenRecorder;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract Base Integration Test Harness for KriolOS POS Automation.
 *
 * <p>Provides:
 * <ul>
 *   <li>Application lifecycle management (launching BootstrapPOS on a background thread)</li>
 *   <li>AssertJ-Swing Robot initialization and cleanup</li>
 *   <li>Wayland-safe in-memory screenshot and video recording management</li>
 *   <li>Configurable execution pacing (Human observational vs Robot fast mode)</li>
 *   <li>Pre-instantiated, reusable Action Drivers:
 *     <ul>
 *       <li>{@link DatabaseSelectionAction}</li>
 *       <li>{@link MainWindowAction}</li>
 *       <li>{@link LoginAction}</li>
 *       <li>{@link MenuNavigationAction}</li>
 *       <li>{@link SalesAction}</li>
 *       <li>{@link PaymentDialogAction}</li>
 *       <li>{@link InstanceManagerAction}</li>
 *     </ul>
 *   </li>
 * </ul>
 */
public abstract class BasePosRobotIT {

    private static final Logger LOGGER = Logger.getLogger(BasePosRobotIT.class.getName());

    protected Robot robot;
    protected ScreenshotHelper screenshotHelper;
    protected FfmpegScreenRecorder videoRecorder;
    protected PosAudioNarrator narrator;

    // Reusable Action Drivers
    protected DatabaseSelectionAction dbSelection;
    protected MainWindowAction mainWindow;
    protected LoginAction login;
    protected MenuNavigationAction menuNav;
    protected SalesAction sales;
    protected NavigationHistoryAction navHistory;
    protected InstanceManagerAction instanceManager;

    // Common Configuration
    protected static final boolean RECORD_VIDEO = Boolean.parseBoolean(System.getProperty("test.record.video", "true"));
    protected static final boolean RECORD_GIF = Boolean.parseBoolean(System.getProperty("test.record.gif", "true"));
    protected static final String TARGET_OPERATOR = System.getProperty("test.user", "admin");
    protected static final String TARGET_PASSWORD = System.getProperty("test.password", TARGET_OPERATOR);
    protected static final String TARGET_DB = System.getProperty("test.db", "");
    protected static final boolean MAXIMIZE_WINDOW = Boolean.parseBoolean(System.getProperty("test.window.maximize", "true"));

    protected static final String BEHAVIOR = System.getProperty("test.behavior",
            System.getProperty("behavior", "human")).trim().toLowerCase();
    protected static final boolean IS_HUMAN = "human".equals(BEHAVIOR);

    /**
     * Subclasses can override this to customize recording session directory names.
     */
    protected String getScenarioName() {
        return "pos_sales_run";
    }

    @BeforeEach
    public void setUpBase() throws Exception {
        screenshotHelper = new ScreenshotHelper(getScenarioName());
        robot = BasicRobot.robotWithCurrentAwtHierarchy();

        // 1. By default for automated UI testing, isolate configuration and disable unique instance check
        AutomationTestConfig.applyTestEnvironment();
        System.setProperty("machine.uniqueinstance", System.getProperty("test.pos.uniqueinstance", "false"));

        // Initialize Audio Narrator
        narrator = new PosAudioNarrator(getScenarioName());
        narrator.start();

        // 2. Initialize Reusable Action Drivers
        dbSelection = new DatabaseSelectionAction(robot, screenshotHelper, IS_HUMAN);
        mainWindow = new MainWindowAction(robot, screenshotHelper, IS_HUMAN);
        login = new LoginAction(robot, screenshotHelper, IS_HUMAN);
        menuNav = new MenuNavigationAction(robot, screenshotHelper, IS_HUMAN);
        sales = new SalesAction(robot, screenshotHelper, IS_HUMAN);
        navHistory = new NavigationHistoryAction(screenshotHelper, IS_HUMAN);
        instanceManager = new InstanceManagerAction(robot, screenshotHelper, IS_HUMAN);

        // 3. Start screen recording if enabled
        if (RECORD_VIDEO) {
            File videoFile = new File("target/recordings/" + getScenarioName() + "/session.mp4");
            videoRecorder = new FfmpegScreenRecorder(videoFile);
            videoRecorder.start(System.getenv("DISPLAY"), 1920, 1080, 25);
        }

        LOGGER.log(Level.INFO, "[BasePosRobotIT] Pacing mode: {0}",
                (IS_HUMAN ? "HUMAN (observational delays enabled)" : "ROBOT (fast/no artificial delays)"));

        // 4. Launch application with isolated test configuration passed as main argument
        launchApplication();
    }

    /**
     * Launches BootstrapPOS in a background thread with the isolated test configuration file.
     */
    protected void launchApplication() {
        String[] mainArgs = AutomationTestConfig.getMainArgs();
        LOGGER.log(Level.INFO, "[BasePosRobotIT] Launching KriolOS POS Application with test config: {0}...",
                (mainArgs.length > 0 ? mainArgs[0] : "none"));
        Thread posThread = new Thread(() -> {
            try {
                BootstrapPOS.main(mainArgs);
            } catch (Throwable t) {
                LOGGER.log(Level.SEVERE, "[BasePosRobotIT] Exception in BootstrapPOS: " + t.getMessage(), t);
            }
        }, "PosAppThread");
        posThread.setDaemon(true);
        posThread.start();
    }

    /**
     * Helper to boot the app, handle DB selection if present, attach to main window, and configure sizing.
     */
    protected FrameFixture bootAndAttachMainWindow() {
        dbSelection.handleIfPresent(TARGET_DB);

        // Fail-fast if another instance warning dialog appears
        if (instanceManager.isDuplicateInstanceDialogPresent(1000)) {
            LOGGER.log(Level.WARNING, "[BasePosRobotIT] Detected Duplicate Instance dialog! Capturing and dismissing...");
            instanceManager.assertDuplicateInstanceDialog(2000);
            instanceManager.dismissDuplicateInstanceDialog();
            throw new IllegalStateException("Another instance of KriolOS POS is already running. Set machine.uniqueinstance=false or close the existing instance.");
        }

        FrameFixture window = mainWindow.attach(45000);
        mainWindow.configureWindowMode(MAXIMIZE_WINDOW);
        dbSelection.selectDatabaseInPanel(window.target(), TARGET_DB);
        try {
            screenshotHelper.captureComponent(window.target(), "01_app_started");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "[BasePosRobotIT] Could not capture screenshot: {0}", e.getMessage());
        }
        return window;
    }

    /**
     * Helper to boot the app, handle DB selection, attach window, and log in operator.
     */
    protected FrameFixture bootAndLogin() {
        FrameFixture window = bootAndAttachMainWindow();
        login.login(window, TARGET_OPERATOR, TARGET_PASSWORD);
        return window;
    }

    protected void pace(long humanMs) {
        pace(humanMs, 0);
    }

    protected void pace(long humanMs, long robotMs) {
        long delay = IS_HUMAN ? humanMs : robotMs;
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @AfterEach
    public void tearDownBase() {
        LOGGER.log(Level.INFO, "[BasePosRobotIT] Cleaning up test resources...");
        if (mainWindow != null) {
            mainWindow.cleanUp();
        }
        if (videoRecorder != null) {
            videoRecorder.stop();
            File videoFile = videoRecorder.getOutputFile();
            if (videoFile != null && videoFile.exists()) {
                if (RECORD_GIF) {
                    File gifFile = new File(videoFile.getParentFile(), "session.gif");
                    FfmpegScreenGifGenerator.generateGif(videoFile, gifFile);
                }
                if (narrator != null) {
                    narrator.compileNarratedVideos(videoFile, videoFile.getParentFile());
                }
                if (Boolean.getBoolean("test.playback.vlc") || Boolean.getBoolean("opos.playback.vlc")) {
                    File playFile = new File(videoFile.getParentFile(), "session_youtube_multilingual.mp4");
                    if (!playFile.exists()) {
                        playFile = new File(videoFile.getParentFile(), "session_pt.mp4");
                    }
                    if (!playFile.exists()) {
                        playFile = videoFile;
                    }
                    io.github.kriolos.opos.automation.video.VlcVideoPlayerPanel.showDemo(playFile);
                }
            }
        }
    }
}
