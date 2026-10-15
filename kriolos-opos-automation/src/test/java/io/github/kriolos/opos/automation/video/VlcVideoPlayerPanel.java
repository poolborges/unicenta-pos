package io.github.kriolos.opos.automation.video;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.TrackDescription;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

/**
 * Embedded VLC Swing Player Panel for previewing and playing automated video recordings.
 *
 * <p>Uses VLCJ (https://github.com/caprica/vlcj) to bind to native LibVLC. Features:
 * <ul>
 *   <li>Easy video loading: "Open Video..." File Chooser, Quick Recordings Switcher dropdown, and Drag-and-Drop</li>
 *   <li>Native hardware-accelerated video rendering directly inside a Swing {@link JPanel}</li>
 *   <li>Transport controls: Play, Pause, Stop, Seek Slider</li>
 *   <li>Timecode display (current / total duration)</li>
 *   <li>Volume control slider (0 - 100%)</li>
 *   <li>Multi-language audio track selector (e.g. switching between Portuguese and English tracks)</li>
 *   <li>Resilient fallback panel if native LibVLC is not installed on the host OS</li>
 * </ul>
 */
public class VlcVideoPlayerPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(VlcVideoPlayerPanel.class.getName());

    private final boolean nativeVlcAvailable;
    private EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private JButton btnPlayPause;
    private JButton btnStop;
    private JButton btnOpenVideo;
    private JComboBox<File> cmbRecentVideos;
    private DefaultComboBoxModel<File> recentVideosModel;
    private JLabel lblCurrentFile;
    private JSlider timeSlider;
    private JLabel lblTime;
    private JComboBox<AudioTrackItem> cmbAudioTracks;
    private DefaultComboBoxModel<AudioTrackItem> audioTrackModel;
    private File currentVideoFile;
    private boolean isAdjustingSlider = false;

    public VlcVideoPlayerPanel() {
        super(new BorderLayout());
        setBackground(new Color(24, 24, 27));

        this.nativeVlcAvailable = checkNativeVlc();

        // 1. Top Quick-Load Header Bar
        initHeaderBar();

        // 2. Center & South
        if (nativeVlcAvailable) {
            initVlcPlayer();
        } else {
            initFallbackUi();
        }

        // 3. Drag and Drop support
        initDragAndDrop();
    }

    private boolean checkNativeVlc() {
        try {
            return new NativeDiscovery().discover();
        } catch (Throwable t) {
            LOGGER.log(Level.WARNING, "[VlcVideoPlayer] Native LibVLC discovery failed: " + t.getMessage());
            return false;
        }
    }

    private void initHeaderBar() {
        JPanel headerBar = new JPanel(new BorderLayout(8, 0));
        headerBar.setBackground(new Color(30, 30, 34));
        headerBar.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        // Left: Open File Button
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setOpaque(false);

        btnOpenVideo = new JButton("📂 Open Video...");
        btnOpenVideo.setToolTipText("Open another video file from disk");
        btnOpenVideo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnOpenVideo.addActionListener(e -> chooseAndLoadVideo());
        leftPanel.add(btnOpenVideo);

        // Center / Quick Switch Dropdown
        recentVideosModel = new DefaultComboBoxModel<>();
        cmbRecentVideos = new JComboBox<>(recentVideosModel);
        cmbRecentVideos.setToolTipText("Quickly switch between generated automation recordings");
        cmbRecentVideos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof File f) {
                    setText(f.getName() + " (" + (f.length() / 1024) + " KB)");
                }
                return this;
            }
        });
        cmbRecentVideos.addActionListener(e -> {
            File selected = (File) cmbRecentVideos.getSelectedItem();
            if (selected != null && !selected.equals(currentVideoFile)) {
                loadVideo(selected);
            }
        });
        refreshRecordingsList();
        leftPanel.add(new JLabel("Quick Switch:"));
        leftPanel.add(cmbRecentVideos);

        headerBar.add(leftPanel, BorderLayout.WEST);

        // Right: Current video status
        lblCurrentFile = new JLabel("No video loaded");
        lblCurrentFile.setForeground(new Color(161, 161, 170));
        lblCurrentFile.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        headerBar.add(lblCurrentFile, BorderLayout.EAST);

        add(headerBar, BorderLayout.NORTH);
    }

    private void initVlcPlayer() {
        // 1. Embedded Video Canvas
        this.mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        this.mediaPlayerComponent.setPreferredSize(new Dimension(854, 480));
        add(mediaPlayerComponent, BorderLayout.CENTER);

        // 2. Control Toolbar
        JPanel controlBar = new JPanel(new BorderLayout(8, 0));
        controlBar.setBackground(new Color(39, 39, 42));
        controlBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // Transport Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttonPanel.setOpaque(false);

        btnPlayPause = new JButton("Play");
        btnPlayPause.addActionListener(e -> togglePlayPause());
        buttonPanel.add(btnPlayPause);

        btnStop = new JButton("Stop");
        btnStop.addActionListener(e -> stopVideo());
        buttonPanel.add(btnStop);

        controlBar.add(buttonPanel, BorderLayout.WEST);

        // Timeline Slider
        timeSlider = new JSlider(0, 1000, 0);
        timeSlider.setOpaque(false);
        timeSlider.addChangeListener(e -> {
            if (timeSlider.getValueIsAdjusting()) {
                isAdjustingSlider = true;
                float position = timeSlider.getValue() / 1000.0f;
                mediaPlayerComponent.mediaPlayer().controls().setPosition(position);
            } else {
                isAdjustingSlider = false;
            }
        });
        controlBar.add(timeSlider, BorderLayout.CENTER);

        // Right side: Timecode + Audio Tracks + Volume
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        lblTime = new JLabel("00:00 / 00:00");
        lblTime.setForeground(new Color(228, 228, 231));
        lblTime.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        rightPanel.add(lblTime);

        // Audio Track Selector
        audioTrackModel = new DefaultComboBoxModel<>();
        cmbAudioTracks = new JComboBox<>(audioTrackModel);
        cmbAudioTracks.setToolTipText("Select Narration Audio Track");
        cmbAudioTracks.addActionListener(e -> {
            AudioTrackItem selected = (AudioTrackItem) cmbAudioTracks.getSelectedItem();
            if (selected != null && mediaPlayerComponent != null) {
                mediaPlayerComponent.mediaPlayer().audio().setTrack(selected.id);
                LOGGER.log(Level.INFO, "[VlcVideoPlayer] Switched audio track to: {0}", selected);
            }
        });
        rightPanel.add(cmbAudioTracks);

        // Volume Slider
        JLabel lblVol = new JLabel("Vol:");
        lblVol.setForeground(new Color(161, 161, 170));
        rightPanel.add(lblVol);

        JSlider volSlider = new JSlider(0, 100, 100);
        volSlider.setPreferredSize(new Dimension(80, 20));
        volSlider.setOpaque(false);
        volSlider.addChangeListener(e -> {
            if (mediaPlayerComponent != null) {
                mediaPlayerComponent.mediaPlayer().audio().setVolume(volSlider.getValue());
            }
        });
        rightPanel.add(volSlider);

        controlBar.add(rightPanel, BorderLayout.EAST);
        add(controlBar, BorderLayout.SOUTH);

        registerPlayerEvents();
    }

    private void initFallbackUi() {
        JPanel msgPanel = new JPanel(new BorderLayout(10, 10));
        msgPanel.setOpaque(false);
        msgPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("LibVLC Native Library Not Found", JLabel.CENTER);
        title.setForeground(new Color(248, 113, 113));
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        msgPanel.add(title, BorderLayout.NORTH);

        String os = RuntimeUtil.getLibVlcLibraryName();
        JLabel desc = new JLabel(
                "<html><center>"
                + "VLCJ requires native LibVLC installed on the host system to render video.<br><br>"
                + "<b>Linux:</b> <code>sudo apt install vlc libvlc-dev</code><br>"
                + "<b>macOS:</b> <code>brew install --cask vlc</code><br>"
                + "<b>Windows:</b> Install standard VLC Media Player 64-bit.<br><br>"
                + "Expected native library: <code>" + os + "</code>"
                + "</center></html>",
                JLabel.CENTER
        );
        desc.setForeground(new Color(228, 228, 231));
        msgPanel.add(desc, BorderLayout.CENTER);

        add(msgPanel, BorderLayout.CENTER);
    }

    private void initDragAndDrop() {
        new DropTarget(this, new DropTargetAdapter() {
            @Override
            @SuppressWarnings("unchecked")
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (droppedFiles != null && !droppedFiles.isEmpty()) {
                        File target = droppedFiles.get(0);
                        LOGGER.log(Level.INFO, "[VlcVideoPlayer] Drag-and-drop received file: {0}", target.getAbsolutePath());
                        loadVideo(target);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "[VlcVideoPlayer] Failed to handle dropped file: " + e.getMessage(), e);
                }
            }
        });
    }

    public void chooseAndLoadVideo() {
        File initialDir = (currentVideoFile != null && currentVideoFile.getParentFile() != null)
                ? currentVideoFile.getParentFile()
                : new File("target/recordings");

        JFileChooser chooser = new JFileChooser(initialDir);
        chooser.setDialogTitle("Select Video File to Play");
        chooser.setFileFilter(new FileNameExtensionFilter("Video Files (*.mp4, *.mkv, *.avi, *.mov, *.webm, *.gif)",
                "mp4", "mkv", "avi", "mov", "webm", "gif"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            loadVideo(selectedFile);
        }
    }

    public void refreshRecordingsList() {
        if (recentVideosModel == null) return;
        List<File> videos = new ArrayList<>();
        File recDir = new File("target/recordings");
        if (recDir.exists() && recDir.isDirectory()) {
            findVideosRecursive(recDir, videos);
        }

        recentVideosModel.removeAllElements();
        for (File f : videos) {
            recentVideosModel.addElement(f);
        }
    }

    private void findVideosRecursive(File dir, List<File> accumulator) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                findVideosRecursive(f, accumulator);
            } else {
                String name = f.getName().toLowerCase();
                if (name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".webm") || name.endsWith(".gif")) {
                    accumulator.add(f);
                }
            }
        }
    }

    private void registerPlayerEvents() {
        if (mediaPlayerComponent == null) {
            return;
        }

        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
                if (!isAdjustingSlider && timeSlider != null) {
                    SwingUtilities.invokeLater(() -> timeSlider.setValue((int) (newPosition * 1000)));
                }
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                long duration = mediaPlayer.status().length();
                if (lblTime != null) {
                    SwingUtilities.invokeLater(() -> lblTime.setText(formatTime(newTime) + " / " + formatTime(duration)));
                }
            }

            @Override
            public void playing(MediaPlayer mediaPlayer) {
                if (btnPlayPause != null) {
                    SwingUtilities.invokeLater(() -> btnPlayPause.setText("Pause"));
                }
                updateAudioTracks(mediaPlayer);
            }

            @Override
            public void paused(MediaPlayer mediaPlayer) {
                if (btnPlayPause != null) {
                    SwingUtilities.invokeLater(() -> btnPlayPause.setText("Play"));
                }
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(() -> {
                    if (btnPlayPause != null) btnPlayPause.setText("Play");
                    if (timeSlider != null) timeSlider.setValue(0);
                    if (lblTime != null) lblTime.setText("00:00 / 00:00");
                });
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(() -> {
                    if (btnPlayPause != null) btnPlayPause.setText("Play");
                    if (timeSlider != null) timeSlider.setValue(0);
                });
            }
        });
    }

    private void updateAudioTracks(MediaPlayer mediaPlayer) {
        SwingUtilities.invokeLater(() -> {
            if (audioTrackModel == null) return;
            List<TrackDescription> tracks = mediaPlayer.audio().trackDescriptions();
            int currentTrackId = mediaPlayer.audio().track();

            audioTrackModel.removeAllElements();
            AudioTrackItem selectedItem = null;
            for (TrackDescription td : tracks) {
                AudioTrackItem item = new AudioTrackItem(td.id(), td.description());
                audioTrackModel.addElement(item);
                if (td.id() == currentTrackId) {
                    selectedItem = item;
                }
            }
            if (selectedItem != null) {
                cmbAudioTracks.setSelectedItem(selectedItem);
            }
        });
    }

    public void loadVideo(File videoFile) {
        this.currentVideoFile = videoFile;
        if (lblCurrentFile != null) {
            lblCurrentFile.setText(videoFile != null ? videoFile.getName() : "No video loaded");
        }

        if (recentVideosModel != null && videoFile != null) {
            boolean found = false;
            for (int i = 0; i < recentVideosModel.getSize(); i++) {
                if (videoFile.equals(recentVideosModel.getElementAt(i))) {
                    cmbRecentVideos.setSelectedIndex(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                recentVideosModel.addElement(videoFile);
                cmbRecentVideos.setSelectedItem(videoFile);
            }
        }

        if (!nativeVlcAvailable || mediaPlayerComponent == null) {
            LOGGER.log(Level.INFO, "[VlcVideoPlayer] Video loaded at: {0} (VLC native display not active)",
                    (videoFile != null ? videoFile.getAbsolutePath() : "null"));
            return;
        }

        if (videoFile != null && videoFile.exists()) {
            mediaPlayerComponent.mediaPlayer().media().start(videoFile.getAbsolutePath());
            LOGGER.log(Level.INFO, "[VlcVideoPlayer] Playing video: {0}", videoFile.getAbsolutePath());
        }
    }

    public void togglePlayPause() {
        if (mediaPlayerComponent == null) return;
        if (mediaPlayerComponent.mediaPlayer().status().isPlaying()) {
            mediaPlayerComponent.mediaPlayer().controls().pause();
        } else {
            if (mediaPlayerComponent.mediaPlayer().status().isPlayable()) {
                mediaPlayerComponent.mediaPlayer().controls().play();
            } else if (currentVideoFile != null) {
                loadVideo(currentVideoFile);
            }
        }
    }

    public void stopVideo() {
        if (mediaPlayerComponent != null) {
            mediaPlayerComponent.mediaPlayer().controls().stop();
        }
    }

    public void release() {
        if (mediaPlayerComponent != null) {
            mediaPlayerComponent.release();
        }
    }

    public boolean isNativeVlcAvailable() {
        return nativeVlcAvailable;
    }

    public File getCurrentVideoFile() {
        return currentVideoFile;
    }

    private String formatTime(long millis) {
        if (millis < 0) return "00:00";
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Demo Launcher: Opens a standalone Swing Frame displaying this VLC player panel.
     */
    public static void showDemo(File videoFile) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("KriolOS POS - VLC Video Player Demo (vlcj)");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            VlcVideoPlayerPanel playerPanel = new VlcVideoPlayerPanel();
            frame.setContentPane(playerPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            if (videoFile != null && videoFile.exists()) {
                playerPanel.loadVideo(videoFile);
            }

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    playerPanel.release();
                }
            });
        });
    }

    public static void main(String[] args) {
        File demoVideo = (args.length > 0)
                ? new File(args[0])
                : new File("target/recordings/pos_login_run/session_youtube_multilingual.mp4");

        if (!demoVideo.exists()) {
            demoVideo = new File("target/recordings/pos_login_run/session_pt.mp4");
        }

        showDemo(demoVideo);
    }

    private static class AudioTrackItem {
        final int id;
        final String description;

        AudioTrackItem(int id, String description) {
            this.id = id;
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
