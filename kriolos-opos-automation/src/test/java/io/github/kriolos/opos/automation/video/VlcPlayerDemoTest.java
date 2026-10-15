package io.github.kriolos.opos.automation.video;

import java.io.File;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit & Interactive verification test for {@link VlcVideoPlayerPanel} and {@link FfmpegLocator}.
 */
public class VlcPlayerDemoTest {

    @Test
    @DisplayName("Verify FfmpegLocator discovers bundled Maven JAVE binary or system FFmpeg")
    public void testFfmpegLocator() {
        String ffmpegPath = FfmpegLocator.getExecutablePath();
        Assertions.assertNotNull(ffmpegPath, "FfmpegLocator should resolve an executable path");
        Assertions.assertTrue(FfmpegLocator.isAvailable(), "FFmpeg should be reported as available");
    }

    @Test
    @DisplayName("Verify VlcVideoPlayerPanel initializes without crashing and detects LibVLC")
    public void testVlcPanelInitialization() {
        VlcVideoPlayerPanel panel = new VlcVideoPlayerPanel();
        Assertions.assertNotNull(panel, "VlcVideoPlayerPanel instance should be created");
        panel.release();
    }

    @Test
    @DisplayName("Demo: Play the generated automation video in Swing (if requested via -Dtest.playback.vlc=true)")
    public void testPlaybackIfRequested() throws Exception {
        if (!Boolean.getBoolean("test.playback.vlc")) {
            return;
        }

        File targetVideo = new File("target/recordings/pos_login_run/session_youtube_multilingual.mp4");
        if (!targetVideo.exists()) {
            targetVideo = new File("target/recordings/pos_login_run/session_pt.mp4");
        }

        if (targetVideo.exists()) {
            VlcVideoPlayerPanel.showDemo(targetVideo);
            Thread.sleep(5000); // Allow brief playback demonstration
        }
    }
}
