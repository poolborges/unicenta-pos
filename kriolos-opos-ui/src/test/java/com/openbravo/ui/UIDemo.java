package com.openbravo.ui;

import com.openbravo.ui.animation.*;
import com.openbravo.ui.components.*;
import com.openbravo.ui.data.binding.*;
import com.openbravo.ui.laf.FlexFactory;
import com.openbravo.ui.laf.FlexLayout;
import com.openbravo.ui.laf.WrapLayout;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.ParseException;

/**
 * Interactive showcase and verification suite for the KriolOS POS UI Module.
 * Demonstrates and exercises every component in the library:
 * - FlexLayout, FlexFactory & WrapLayout
 * - PaneBuilder (unified form builder & tab manager)
 * - SwingBinder with full validator/converter pipeline
 * - GenericUIBlocker (Spinner & Flash Error) via JLayer
 * - ToastManager & ToastNotification
 * - NativeAnimator, PropertySetter, ShakeEffect & KeyClickEffect
 * - RippleButton, RollingNumberLabel, SlidePanel, PulseEffect, ShimmerPanel, FlyEffect
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class UIDemo {

    // --- Sample Domain Model for Data Binding ---
    public static class OperatorProfile {
        private String name = "Paulo Borges";
        private String email = "paulo@kriolos.org";
        private String role = "Store Manager";
        private boolean active = true;
        private String terminalCode = "POS-001";
        private String currency = "CVE";
        private int discountCap = 25;
        private int maxDailySessions = 3;
        private BigDecimal creditLimit = new BigDecimal("150000.00");
        private String notes = "Authorized for supervisor override and drawer closing.";

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public String getTerminalCode() { return terminalCode; }
        public void setTerminalCode(String terminalCode) { this.terminalCode = terminalCode; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public int getDiscountCap() { return discountCap; }
        public void setDiscountCap(int discountCap) { this.discountCap = discountCap; }
        public int getMaxDailySessions() { return maxDailySessions; }
        public void setMaxDailySessions(int maxDailySessions) { this.maxDailySessions = maxDailySessions; }
        public BigDecimal getCreditLimit() { return creditLimit; }
        public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        @Override
        public String toString() {
            return String.format(
                    "Name: %s\nEmail: %s\nRole: %s\nActive: %b\nTerminal: %s\nCurrency: %s\nDiscount Cap: %d%%\nDaily Sessions: %d\nCredit Limit: %s\nNotes: %s",
                    name, email, role, active, terminalCode, currency, discountCap, maxDailySessions, creditLimit, notes
            );
        }
    }

    private final JFrame frame;
    private final GenericUIBlocker uiBlocker;
    private final OperatorProfile currentProfile = new OperatorProfile();
    private SwingBinder<OperatorProfile> binder;

    private JLabel lblStatusDirty;
    private JLabel lblStatusValid;
    private JTextArea txtLiveModelOutput;

    public UIDemo() {
        frame = new JFrame("KriolOS POS — Modern UI Components Showcase");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1080, 680);
        frame.setMinimumSize(new Dimension(800, 500));
        frame.setLocationRelativeTo(null);

        // Responsive revalidation for tiling window managers (COSMIC DE, Wayland, Xwayland)
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                frame.revalidate();
                frame.repaint();
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                frame.revalidate();
                frame.repaint();
            }
        });

        uiBlocker = new GenericUIBlocker();
        uiBlocker.setHintText("Press ESC or click Cancel to release lock");
        uiBlocker.setCancelCallback(() -> {
            uiBlocker.setBlocking(false);
            ToastManager.show(frame, "Operation aborted by user (ESC)", ToastManager.WARNING);
        });

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 247, 250));

        // 1. Top Header Banner
        contentPanel.add(createHeaderBanner(), BorderLayout.NORTH);

        // 2. Central Tabbed Workbench
        JTabbedPane mainTabs = new JTabbedPane();
        mainTabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        mainTabs.setBorder(new EmptyBorder(8, 12, 8, 12));

        mainTabs.addTab("Form & Data Binding (SwingBinder)", createDataBindingTab());
        mainTabs.addTab("Flexbox & Wrap Layouts", createLayoutsTab());
        mainTabs.addTab("Haptic Feedback & Animations", createAnimationsTab());
        mainTabs.addTab("Touch Micro-Interactions (New Animations)", createTouchAnimationsTab());
        mainTabs.addTab("Glass Blocker & Toasts", createFeedbackTab());

        contentPanel.add(mainTabs, BorderLayout.CENTER);

        // 3. Bottom Global Status Bar
        contentPanel.add(createGlobalStatusBar(), BorderLayout.SOUTH);

        // Wrap the whole content panel inside JLayer for UI Blocking capability
        JLayer<JPanel> layer = new JLayer<>(contentPanel, uiBlocker);

        // An opaque root panel guarantees immediate native background painting on X11 / Wayland
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(true);
        root.setBackground(new Color(245, 247, 250));
        root.add(layer, BorderLayout.CENTER);
        frame.setContentPane(root);

        // Pre-validate the layout tree before making the native window visible
        frame.revalidate();
    }

    public void show() {
        frame.setVisible(true);

        // Repaint all components immediately after peer initialization
        SwingUtilities.invokeLater(() -> {
            frame.invalidate();
            frame.validate();
            frame.repaint();
        });

        // COSMIC DE (cosmic-comp) applies tile geometry asynchronously after window mapping.
        // A short pulse timer ensures the layout syncs with actual tile bounds without requiring user click.
        Timer tileSync = new Timer(60, e -> {
            frame.revalidate();
            frame.repaint();
        });
        tileSync.setRepeats(false);
        tileSync.start();
    }

    // =========================================================================
    // 1. HEADER BANNER
    // =========================================================================
    private JPanel createHeaderBanner() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 43, 54));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("KriolOS POS — UI Component Architecture Showcase");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Layout Engine • Data Binding • Timing Framework • Form Builders • HUD Feedback");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(new Color(145, 158, 171));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(subtitle);

        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBox.setOpaque(false);

        JButton btnQuickToast = createStyledButton("Quick Toast", new Color(52, 152, 219), Color.WHITE);
        btnQuickToast.addActionListener(e -> {
            KeyClickEffect.play(btnQuickToast);
            ToastManager.show(frame, "Interactive Toast Notification Active!", ToastManager.SUCCESS);
        });

        JButton btnQuickBlock = createStyledButton("Block UI (2s)", new Color(231, 76, 60), Color.WHITE);
        btnQuickBlock.addActionListener(e -> {
            KeyClickEffect.play(btnQuickBlock);
            uiBlocker.setMainText("PROCESSING TRANSACTION...");
            uiBlocker.setBlocking(true, GenericUIBlocker.Mode.SPINNER);
            Timer timer = new Timer(2000, evt -> {
                uiBlocker.setBlocking(false);
                ToastManager.show(frame, "Transaction completed successfully!", ToastManager.SUCCESS);
            });
            timer.setRepeats(false);
            timer.start();
        });

        rightBox.add(btnQuickToast);
        rightBox.add(btnQuickBlock);

        header.add(titleBox, BorderLayout.WEST);
        header.add(rightBox, BorderLayout.EAST);
        return header;
    }

    // References to form fields for live buffer inspection
    private JTextField txtName;
    private JTextField txtEmail;
    private JComboBox<String> cmbRole;
    private JFormattedTextField txtTerminal;
    private JRadioButton rbCve;
    private JRadioButton rbEur;
    private JRadioButton rbUsd;
    private JSlider sliderDiscount;
    private JSpinner spinnerSessions;
    private JTextField txtCreditLimit;
    private JCheckBox chkActive;
    private JTextArea txtNotes;
    private JCheckBox chkAutoSync;

    // =========================================================================
    // 2. TAB 1: FORM & DATA BINDING (SwingBinder + PaneBuilder)
    // =========================================================================
    private JPanel createDataBindingTab() {
        JPanel split = new JPanel(new BorderLayout(12, 12));
        split.setOpaque(false);
        split.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Form Fields
        txtName = new JTextField();
        txtEmail = new JTextField();
        cmbRole = new JComboBox<>(new String[]{"Cashier", "Store Manager", "Inventory Clerk", "Auditor"});
        chkActive = new JCheckBox("Operator Active & Login Permitted");
        chkActive.setOpaque(false);

        txtTerminal = new JFormattedTextField();
        try {
            MaskFormatter mf = new MaskFormatter("POS-###");
            mf.setPlaceholderCharacter('0');
            mf.install(txtTerminal);
        } catch (ParseException ignored) {}

        rbCve = new JRadioButton("CVE (Escudo)");
        rbEur = new JRadioButton("EUR (Euro)");
        rbUsd = new JRadioButton("USD (Dollar)");
        rbCve.setOpaque(false);
        rbEur.setOpaque(false);
        rbUsd.setOpaque(false);

        ButtonGroup currencyGroup = new ButtonGroup();
        currencyGroup.add(rbCve);
        currencyGroup.add(rbEur);
        currencyGroup.add(rbUsd);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radioPanel.setOpaque(false);
        radioPanel.add(rbCve);
        radioPanel.add(rbEur);
        radioPanel.add(rbUsd);

        sliderDiscount = new JSlider(0, 50, 10);
        sliderDiscount.setMajorTickSpacing(10);
        sliderDiscount.setMinorTickSpacing(5);
        sliderDiscount.setPaintTicks(true);
        sliderDiscount.setPaintLabels(true);

        spinnerSessions = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        txtCreditLimit = new JTextField();
        txtNotes = new JTextArea(3, 20);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);

        // Assemble Form using PaneBuilder with responsive scrolling
        PaneBuilder formBuilder = new PaneBuilder().setScrollable(true);
        formBuilder.add("Full Operator Name:", txtName);
        formBuilder.add("Corporate Email:", txtEmail);
        formBuilder.add("Assigned Role:", cmbRole);
        formBuilder.add("Terminal Mask (POS-###):", txtTerminal);
        formBuilder.add("Base Currency:", radioPanel);
        formBuilder.add("Max Discount Cap (%):", sliderDiscount);
        formBuilder.add("Daily Sessions Limit:", spinnerSessions);
        formBuilder.add("Authorized Credit Limit:", txtCreditLimit);
        formBuilder.add("Account Status:", chkActive);
        formBuilder.addScroll("Operational Notes:", txtNotes, 1.0, 1.0);

        // Buttons for Action Footer
        JButton btnSave = createStyledButton("Save Bean", new Color(46, 204, 113), Color.WHITE);
        JButton btnDiscard = createStyledButton("Discard Changes", new Color(241, 196, 15), Color.BLACK);
        JButton btnClear = createStyledButton("Clear Form", new Color(149, 165, 166), Color.WHITE);
        JButton btnShakeInvalid = createStyledButton("Shake Name Field", new Color(155, 89, 182), Color.WHITE);

        formBuilder.addFooter(new double[]{0.25, 0.25, 0.25, 0.25}, btnSave, btnDiscard, btnClear, btnShakeInvalid);

        JPanel formPanel = formBuilder.build();
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 224, 230), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Right-Side Live Model Inspector
        JPanel rightInspector = new JPanel(new BorderLayout(8, 8));
        rightInspector.setPreferredSize(new Dimension(380, 0));
        rightInspector.setOpaque(false);

        JPanel inspectorHeader = new JPanel(new BorderLayout());
        inspectorHeader.setOpaque(false);

        JLabel lblInspectorTitle = new JLabel("Live Data Binding Inspector");
        lblInspectorTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblInspectorTitle.setForeground(new Color(44, 62, 80));

        chkAutoSync = new JCheckBox("Live Auto-Sync to Bean");
        chkAutoSync.setFont(new Font("SansSerif", Font.PLAIN, 11));
        chkAutoSync.setOpaque(false);
        chkAutoSync.setToolTipText("When checked, valid form changes immediately write to the backend bean on every keystroke");

        inspectorHeader.add(lblInspectorTitle, BorderLayout.WEST);
        inspectorHeader.add(chkAutoSync, BorderLayout.EAST);

        txtLiveModelOutput = new JTextArea();
        txtLiveModelOutput.setEditable(false);
        txtLiveModelOutput.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLiveModelOutput.setBackground(new Color(248, 249, 250));
        txtLiveModelOutput.setBorder(new EmptyBorder(8, 8, 8, 8));
        JScrollPane scrollInspector = new JScrollPane(txtLiveModelOutput);
        scrollInspector.setBorder(new LineBorder(new Color(200, 205, 215), 1));

        JLabel helpNote = new JLabel("<html><small>Type in fields to see dirty tracking and real-time live preview.<br>Press 'Save Bean' to commit or enable 'Live Auto-Sync'.</small></html>");
        helpNote.setForeground(new Color(127, 140, 141));

        rightInspector.add(inspectorHeader, BorderLayout.NORTH);
        rightInspector.add(scrollInspector, BorderLayout.CENTER);
        rightInspector.add(helpNote, BorderLayout.SOUTH);

        // Setup SwingBinder
        binder = new SwingBinder<>(currentProfile);

        binder.forField(txtName)
                .asRequired("Operator Name cannot be empty")
                .bind(OperatorProfile::getName, OperatorProfile::setName);

        binder.forField(txtEmail)
                .withValidator(new EmailValidator("Must provide a valid corporate email format"))
                .bind(OperatorProfile::getEmail, OperatorProfile::setEmail);

        binder.forField(cmbRole)
                .bind(OperatorProfile::getRole, OperatorProfile::setRole);

        binder.<String>forField(txtTerminal)
                .asRequired("Terminal ID is mandatory")
                .bind(OperatorProfile::getTerminalCode, OperatorProfile::setTerminalCode);

        binder.<String>forRadioGroup()
                .add("CVE", rbCve)
                .add("EUR", rbEur)
                .add("USD", rbUsd)
                .bind(OperatorProfile::getCurrency, OperatorProfile::setCurrency);

        binder.forField(sliderDiscount)
                .bind(OperatorProfile::getDiscountCap, OperatorProfile::setDiscountCap);

        binder.<Integer>forField(spinnerSessions)
                .bind(OperatorProfile::getMaxDailySessions, OperatorProfile::setMaxDailySessions);

        binder.forField(txtCreditLimit)
                .withConverter(new Converters.StringToBigDecimal())
                .asRequired("Credit limit must be a valid number")
                .bind(OperatorProfile::getCreditLimit, OperatorProfile::setCreditLimit);

        binder.forField(chkActive)
                .bind(OperatorProfile::isActive, OperatorProfile::setActive);

        binder.forField(txtNotes)
                .bind(OperatorProfile::getNotes, OperatorProfile::setNotes);

        // Listen for live binder status events
        binder.addStatusChangeListener(() -> {
            if (chkAutoSync != null && chkAutoSync.isSelected() && binder.isValid()) {
                try {
                    binder.writeBean(currentProfile);
                } catch (Exception ignored) {}
            }
            updateLiveInspector();
            updateStatusBadges();
        });

        // Button Listeners
        btnSave.addActionListener(e -> {
            KeyClickEffect.play(btnSave);
            try {
                binder.writeBean(currentProfile);
                updateLiveInspector();
                ToastManager.show(frame, "Model successfully committed & validated!", ToastManager.SUCCESS);
            } catch (SwingBinder.ValidationException ex) {
                ShakeEffect.play(formPanel);
                ToastManager.show(frame, "Validation Failed: " + ex.getMessage(), ToastManager.ERROR);
            }
        });

        btnDiscard.addActionListener(e -> {
            KeyClickEffect.play(btnDiscard);
            binder.discard();
            updateLiveInspector();
            ToastManager.show(frame, "Form inputs discarded back to model state.", ToastManager.WARNING);
        });

        btnClear.addActionListener(e -> {
            KeyClickEffect.play(btnClear);
            binder.clear();
            updateLiveInspector();
            ToastManager.show(frame, "All fields cleared.", ToastManager.INFO);
        });

        btnShakeInvalid.addActionListener(e -> {
            KeyClickEffect.play(btnShakeInvalid);
            txtName.requestFocusInWindow();
            ShakeEffect.play(txtName);
            ToastManager.show(frame, "Validation Feedback: Shaking Operator Name field!", ToastManager.WARNING);
        });

        split.add(formPanel, BorderLayout.CENTER);
        split.add(rightInspector, BorderLayout.EAST);

        updateLiveInspector();
        return split;
    }

    private String getSelectedCurrency() {
        if (rbCve != null && rbCve.isSelected()) return "CVE";
        if (rbEur != null && rbEur.isSelected()) return "EUR";
        if (rbUsd != null && rbUsd.isSelected()) return "USD";
        return "None";
    }

    private void updateLiveInspector() {
        if (txtLiveModelOutput != null && binder != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append(" 1. LIVE FORM BUFFER (REAL-TIME UI)\n");
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append(" • Name:        ").append(txtName != null ? txtName.getText() : "").append("\n");
            sb.append(" • Email:       ").append(txtEmail != null ? txtEmail.getText() : "").append("\n");
            sb.append(" • Role:        ").append(cmbRole != null ? cmbRole.getSelectedItem() : "").append("\n");
            sb.append(" • Terminal:    ").append(txtTerminal != null ? txtTerminal.getText() : "").append("\n");
            sb.append(" • Currency:    ").append(getSelectedCurrency()).append("\n");
            sb.append(" • Discount:    ").append(sliderDiscount != null ? sliderDiscount.getValue() : "").append("%\n");
            sb.append(" • Sessions:    ").append(spinnerSessions != null ? spinnerSessions.getValue() : "").append("\n");
            sb.append(" • Credit:      ").append(txtCreditLimit != null ? txtCreditLimit.getText() : "").append("\n");
            sb.append(" • Active:      ").append(chkActive != null && chkActive.isSelected()).append("\n");
            sb.append(" • Notes:       ").append(txtNotes != null ? txtNotes.getText().replace("\n", " ") : "").append("\n\n");

            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append(" 2. STATUS & VALIDATION FLAGS\n");
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append(" • Is Dirty:    ").append(binder.isDirty() ? "YES (Unsaved edits pending)" : "NO (Clean/Synced)").append("\n");
            sb.append(" • Is Valid:    ").append(binder.isValid() ? "YES (All rules pass)" : "NO (Validation error detected)").append("\n\n");

            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append(" 3. COMMITTED BACKEND BEAN (STATE)\n");
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            if (binder.getBean() != null) {
                sb.append(currentProfile.toString()).append("\n");
            } else {
                sb.append("[No bean bound - Form was cleared]\n");
            }

            txtLiveModelOutput.setText(sb.toString());
            txtLiveModelOutput.setCaretPosition(0);
        }
        updateStatusBadges();
    }

    private void updateStatusBadges() {
        if (lblStatusDirty != null && binder != null) {
            boolean dirty = binder.isDirty();
            lblStatusDirty.setText(dirty ? "● Dirty (Modified)" : "○ Clean (Synced)");
            lblStatusDirty.setForeground(dirty ? new Color(230, 126, 34) : new Color(39, 174, 96));
        }
        if (lblStatusValid != null && binder != null) {
            boolean valid = binder.isValid();
            lblStatusValid.setText(valid ? "✓ Valid Parameters" : "✗ Validation Errors");
            lblStatusValid.setForeground(valid ? new Color(39, 174, 96) : new Color(192, 57, 43));
        }
    }

    // =========================================================================
    // 3. TAB 2: FLEXBOX & WRAP LAYOUT SHOWCASE
    // =========================================================================
    private JPanel createLayoutsTab() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Sub-panel 1: FlexLayout Rows & Columns
        JPanel flexDemo = FlexFactory.createColumn(FlexLayout.JustifyContent.START, FlexLayout.AlignItems.STRETCH, 10);
        flexDemo.setBorder(BorderFactory.createTitledBorder("1. Pure Flexbox Layout Engine (com.openbravo.ui.laf.FlexLayout)"));

        // Row with Percent Basis
        JPanel flexPercentRow = FlexFactory.createRow(FlexLayout.JustifyContent.START, FlexLayout.AlignItems.CENTER, 8);
        JButton b1 = createCardButton("Basis: 25%", new Color(52, 152, 219));
        JButton b2 = createCardButton("Basis: 50%", new Color(41, 128, 185));
        JButton b3 = createCardButton("Basis: 25%", new Color(52, 152, 219));

        flexPercentRow.add(b1, "basis: 25%");
        flexPercentRow.add(b2, "basis: 50%");
        flexPercentRow.add(b3, "basis: 25%");
        flexDemo.add(flexPercentRow);

        // Row with FlexGrow
        JPanel flexGrowRow = FlexFactory.createRow(FlexLayout.JustifyContent.START, FlexLayout.AlignItems.CENTER, 8);
        JButton bg1 = createCardButton("Grow: 1.0", new Color(155, 89, 182));
        JButton bg2 = createCardButton("Grow: 2.0 (Double Width)", new Color(142, 68, 173));
        JButton bg3 = createCardButton("Grow: 1.0", new Color(155, 89, 182));

        flexGrowRow.add(bg1, "grow: 1.0");
        flexGrowRow.add(bg2, "grow: 2.0");
        flexGrowRow.add(bg3, "grow: 1.0");
        flexDemo.add(flexGrowRow);

        // Sub-panel 2: WrapLayout Catalog inside JScrollPane
        JPanel wrapCatalogPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
        wrapCatalogPanel.setBackground(Color.WHITE);

        String[] sampleItems = {
                "Café Expresso (80 CVE)", "Sumo Natural (150 CVE)", "Pastel com Diabo (120 CVE)",
                "Cachupa Rica (350 CVE)", "Água das Pedras (110 CVE)", "Sandes Mista (180 CVE)",
                "Cerveja Strela (160 CVE)", "Bife de Atum (750 CVE)", "Pudim Caseiro (200 CVE)",
                "Queijo Fogo (450 CVE)", "Grogue Velho (250 CVE)", "Vinho Chã (890 CVE)",
                "Mousse Chocolate (190 CVE)", "Fruta Época (130 CVE)", "Chá Verde (90 CVE)"
        };

        for (String item : sampleItems) {
            JButton card = new JButton("<html><center><b>" + item + "</b><br><small>Click to add</small></center></html>");
            card.setPreferredSize(new Dimension(175, 75));
            card.setBackground(new Color(240, 243, 246));
            card.setBorder(new CompoundBorder(new LineBorder(new Color(210, 215, 222), 1), new EmptyBorder(6, 6, 6, 6)));
            card.setFocusPainted(false);
            card.addActionListener(e -> {
                KeyClickEffect.play(card);
                ToastManager.show(frame, "Item selected: " + item, ToastManager.INFO);
            });
            wrapCatalogPanel.add(card);
        }

        JScrollPane scrollWrap = new JScrollPane(wrapCatalogPanel);
        scrollWrap.setBorder(BorderFactory.createTitledBorder("2. Dynamic WrapLayout with JScrollPane (Automatic height calculation)"));

        main.add(flexDemo, BorderLayout.NORTH);
        main.add(scrollWrap, BorderLayout.CENTER);
        return main;
    }

    private JButton createCardButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(120, 42));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        return btn;
    }

    private JButton createColorButton(String label, Color color, ActionListener listener) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(listener);
        return btn;
    }

    // =========================================================================
    // 4. TAB 3: HAPTIC FEEDBACK & ANIMATIONS
    // =========================================================================
    private JPanel createAnimationsTab() {
        JPanel main = new JPanel(new BorderLayout(14, 14));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Left: Interactive POS Touch Keypad with KeyClickEffect (Comfortable touch width)
        JPanel keypadPanel = new JPanel(new BorderLayout(8, 8));
        keypadPanel.setPreferredSize(new Dimension(300, 0));
        keypadPanel.setBorder(BorderFactory.createTitledBorder("Touch Keypad with KeyClickEffect & ShakeEffect"));

        JTextField display = new JTextField("1250.00");
        display.setFont(new Font("Monospaced", Font.BOLD, 26));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setBackground(new Color(245, 247, 250));
        keypadPanel.add(display, BorderLayout.NORTH);

        JPanel gridKeys = new JPanel(new GridLayout(4, 3, 8, 8));
        String[] keys = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "C", "0", "OK"};

        for (String k : keys) {
            JButton keyBtn = new JButton(k);
            keyBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
            keyBtn.setFocusPainted(false);

            if ("OK".equals(k)) {
                keyBtn.setBackground(new Color(46, 204, 113));
                keyBtn.setForeground(Color.WHITE);
            } else if ("C".equals(k)) {
                keyBtn.setBackground(new Color(231, 76, 60));
                keyBtn.setForeground(Color.WHITE);
            } else {
                keyBtn.setBackground(new Color(236, 240, 241));
            }

            keyBtn.addActionListener(e -> {
                KeyClickEffect.play(keyBtn);
                if ("C".equals(k)) {
                    display.setText("");
                } else if ("OK".equals(k)) {
                    if (display.getText().isEmpty()) {
                        ShakeEffect.play(display);
                        ToastManager.show(frame, "Cannot submit empty amount!", ToastManager.ERROR);
                    } else {
                        ToastManager.show(frame, "Submitted Amount: " + display.getText(), ToastManager.SUCCESS);
                    }
                } else {
                    display.setText(display.getText() + k);
                }
            });
            gridKeys.add(keyBtn);
        }
        keypadPanel.add(gridKeys, BorderLayout.CENTER);

        // Right: Timing Framework Curves & PropertySetter (Occupies ALL remaining width space)
        JPanel animShowcase = new JPanel(new BorderLayout(10, 10));
        animShowcase.setBorder(BorderFactory.createTitledBorder("Timing Framework: Bézier Curves & PropertySetter"));

        JPanel animCanvas = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int centerY = getHeight() / 2;
                g2.setColor(new Color(215, 222, 230));
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{6, 6}, 0));
                g2.drawLine(25, centerY, Math.max(25, getWidth() - 25), centerY);
                g2.dispose();
            }
        };
        animCanvas.setBackground(new Color(240, 242, 245));
        animCanvas.setBorder(new LineBorder(new Color(210, 215, 220), 1));

        JPanel movingBox = new JPanel();
        movingBox.setBackground(new Color(52, 152, 219));
        movingBox.setSize(80, 80);
        animCanvas.add(movingBox);

        // Keep moving box dynamically responsive to canvas width and vertically centered
        animCanvas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int y = Math.max(20, (animCanvas.getHeight() - movingBox.getHeight()) / 2);
                if (movingBox.getX() > 60) {
                    int endX = Math.max(25, animCanvas.getWidth() - movingBox.getWidth() - 25);
                    movingBox.setLocation(endX, y);
                } else {
                    movingBox.setLocation(25, y);
                }
            }
        });

        JPanel controls = new JPanel(new GridLayout(2, 2, 8, 8));

        JButton btnFastOutSlowIn = createStyledButton("FAST_OUT_SLOW_IN (Travel Across Width)", new Color(41, 128, 185), Color.WHITE);
        btnFastOutSlowIn.addActionListener(e -> {
            int startX = 25;
            int endX = Math.max(100, animCanvas.getWidth() - movingBox.getWidth() - 25);
            int y = Math.max(20, (animCanvas.getHeight() - movingBox.getHeight()) / 2);
            PropertySetter<Component, Point> setter = PropertySetter.getTarget(
                    movingBox, Component::setLocation, new Point(startX, y), new Point(endX, y)
            );
            NativeAnimator anim = NativeAnimator.createWithInterpolator(700, setter, NativeAnimator.FAST_OUT_SLOW_IN);
            anim.start();
        });

        JButton btnAnticipate = createStyledButton("ANTICIPATE (Return Across Width)", new Color(142, 68, 173), Color.WHITE);
        btnAnticipate.addActionListener(e -> {
            int startX = Math.max(100, animCanvas.getWidth() - movingBox.getWidth() - 25);
            int endX = 25;
            int y = Math.max(20, (animCanvas.getHeight() - movingBox.getHeight()) / 2);
            PropertySetter<Component, Point> setter = PropertySetter.getTarget(
                    movingBox, Component::setLocation, new Point(startX, y), new Point(endX, y)
            );
            NativeAnimator anim = NativeAnimator.createWithInterpolator(700, setter, NativeAnimator.ANTICIPATE);
            anim.start();
        });

        JButton btnColorCycle = createStyledButton("PropertySetter (Color Cycle)", new Color(230, 126, 34), Color.WHITE);
        btnColorCycle.addActionListener(e -> {
            PropertySetter<Component, Color> setter = PropertySetter.getTarget(
                    movingBox, Component::setBackground, new Color(52, 152, 219), new Color(231, 76, 60)
            );
            NativeAnimator anim = NativeAnimator.createWithInterpolator(500, setter, NativeAnimator.EASE_IN_OUT);
            anim.start();
        });

        JButton btnShakeBox = createStyledButton("Shake Effect", new Color(192, 57, 43), Color.WHITE);
        btnShakeBox.addActionListener(e -> ShakeEffect.play(movingBox));

        controls.add(btnFastOutSlowIn);
        controls.add(btnAnticipate);
        controls.add(btnColorCycle);
        controls.add(btnShakeBox);

        animShowcase.add(animCanvas, BorderLayout.CENTER);
        animShowcase.add(controls, BorderLayout.SOUTH);

        main.add(keypadPanel, BorderLayout.WEST);
        main.add(animShowcase, BorderLayout.CENTER);
        return main;
    }

    // =========================================================================
    // 5. TAB 4: GLASS BLOCKER & TOASTS
    // =========================================================================
    private JPanel createFeedbackTab() {
        JPanel main = new JPanel(new GridLayout(2, 1, 14, 14));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Blocker Section
        JPanel blockerSec = new JPanel(new BorderLayout(10, 10));
        blockerSec.setBorder(BorderFactory.createTitledBorder("GenericUIBlocker (JLayer Screen Locking, Transparency & Custom Colors)"));
        blockerSec.setOpaque(false);

        // Sub-panel 1: Trigger Buttons
        JPanel triggerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        triggerRow.setOpaque(false);

        JButton btnSpinner3s = createStyledButton("Trigger Spinner Blocker (3s)", new Color(41, 128, 185), Color.WHITE);
        btnSpinner3s.addActionListener(e -> {
            uiBlocker.setMainText("SYNCHRONIZING FISCAL PRINTER...");
            uiBlocker.setBlocking(true, GenericUIBlocker.Mode.SPINNER);
            Timer t = new Timer(3000, evt -> uiBlocker.setBlocking(false));
            t.setRepeats(false);
            t.start();
        });

        JButton btnFlashError = createStyledButton("Trigger Flash Error (600ms Tactile)", new Color(192, 57, 43), Color.WHITE);
        btnFlashError.addActionListener(e -> {
            uiBlocker.setMainText("COMMUNICATION ERROR: TIMEOUT");
            uiBlocker.setBlocking(true, GenericUIBlocker.Mode.FLASH_ERROR);
        });

        JButton btnEscAbort = createStyledButton("Test Blocker with ESC Abort", new Color(52, 73, 94), Color.WHITE);
        btnEscAbort.addActionListener(e -> {
            uiBlocker.setMainText("LONG OPERATION IN PROGRESS... PRESS ESC TO ABORT");
            uiBlocker.setBlocking(true, GenericUIBlocker.Mode.SPINNER);
        });

        triggerRow.add(btnSpinner3s);
        triggerRow.add(btnFlashError);
        triggerRow.add(btnEscAbort);

        // Sub-panel 2: Transparency & Color Customizer Controls
        JPanel configRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        configRow.setOpaque(false);

        int initAlpha = uiBlocker.getMaxAlpha();
        int initPct = Math.round((initAlpha / 255.0f) * 100);
        JLabel lblOpacity = new JLabel(String.format("Overlay Opacity: %d%% (%d/255)", initPct, initAlpha));
        lblOpacity.setFont(new Font("SansSerif", Font.BOLD, 12));

        JSlider sliderOpacity = new JSlider(25, 255, initAlpha);
        sliderOpacity.setPreferredSize(new Dimension(160, 40));
        sliderOpacity.setMajorTickSpacing(50);
        sliderOpacity.setPaintTicks(true);
        sliderOpacity.addChangeListener(e -> {
            int alpha = sliderOpacity.getValue();
            uiBlocker.setMaxAlpha(alpha);
            int pct = Math.round((alpha / 255.0f) * 100);
            lblOpacity.setText(String.format("Overlay Opacity: %d%% (%d/255)", pct, alpha));
        });

        JLabel lblColor = new JLabel("Overlay Color:");
        lblColor.setFont(new Font("SansSerif", Font.BOLD, 12));

        // Color Swatch Indicator
        JPanel colorSwatch = new JPanel();
        colorSwatch.setPreferredSize(new Dimension(26, 26));
        colorSwatch.setBackground(uiBlocker.getBackgroundColor());
        colorSwatch.setBorder(new LineBorder(Color.GRAY, 1));

        ActionListener colorPicker = evt -> {
            JButton source = (JButton) evt.getSource();
            Color chosen = source.getBackground();
            uiBlocker.setBackgroundColor(chosen);
            colorSwatch.setBackground(chosen);
        };

        JButton btnBlack = createColorButton("Dark", new Color(0, 0, 0), colorPicker);
        JButton btnNavy = createColorButton("Navy", new Color(15, 23, 42), colorPicker);
        JButton btnBlue = createColorButton("KriolOS Blue", new Color(21, 67, 96), colorPicker);
        JButton btnPurple = createColorButton("Indigo", new Color(44, 24, 76), colorPicker);
        JButton btnSlate = createColorButton("Slate", new Color(47, 53, 66), colorPicker);

        JButton btnCustomColor = new JButton("Choose Color...");
        btnCustomColor.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnCustomColor.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(frame, "Select Blocker Background Color", uiBlocker.getBackgroundColor());
            if (chosen != null) {
                uiBlocker.setBackgroundColor(chosen);
                colorSwatch.setBackground(chosen);
            }
        });

        configRow.add(lblOpacity);
        configRow.add(sliderOpacity);
        configRow.add(lblColor);
        configRow.add(colorSwatch);
        configRow.add(btnBlack);
        configRow.add(btnNavy);
        configRow.add(btnBlue);
        configRow.add(btnPurple);
        configRow.add(btnSlate);
        configRow.add(btnCustomColor);

        blockerSec.add(triggerRow, BorderLayout.NORTH);
        blockerSec.add(configRow, BorderLayout.CENTER);

        // Toasts Section
        JPanel toastSec = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 15));
        toastSec.setBorder(BorderFactory.createTitledBorder("ToastManager & Adaptive Translucent Toasts"));

        JButton btnToastSuccess = createStyledButton("Success Toast", ToastManager.SUCCESS, Color.WHITE);
        btnToastSuccess.addActionListener(e -> ToastManager.show(frame, "Invoice #48291 printed successfully!", ToastManager.SUCCESS));

        JButton btnToastInfo = createStyledButton("Info Toast", ToastManager.INFO, Color.WHITE);
        btnToastInfo.addActionListener(e -> ToastManager.show(frame, "Drawer balance verified: 45.200 CVE", ToastManager.INFO));

        JButton btnToastWarning = createStyledButton("Warning Toast", ToastManager.WARNING, Color.BLACK);
        btnToastWarning.addActionListener(e -> ToastManager.show(frame, "Low paper stock detected in printer #1", ToastManager.WARNING));

        JButton btnToastError = createStyledButton("Error Toast", ToastManager.ERROR, Color.WHITE);
        btnToastError.addActionListener(e -> ToastManager.show(frame, "Payment Gateway Rejected Card Authorization", ToastManager.ERROR));

        JButton btnToastStack = createStyledButton("Stack 3 Toasts Rapidly", new Color(142, 68, 173), Color.WHITE);
        btnToastStack.addActionListener(e -> {
            ToastManager.show(frame, "Step 1/3: Reading Barcode Scanner", ToastManager.INFO);
            ToastManager.show(frame, "Step 2/3: Applying 10% Discount", ToastManager.WARNING);
            ToastManager.show(frame, "Step 3/3: Transaction Committed", ToastManager.SUCCESS);
        });

        toastSec.add(btnToastSuccess);
        toastSec.add(btnToastInfo);
        toastSec.add(btnToastWarning);
        toastSec.add(btnToastError);
        toastSec.add(btnToastStack);

        main.add(blockerSec);
        main.add(toastSec);
        return main;
    }

    // =========================================================================
    // 5. TAB 5: TOUCH MICRO-INTERACTIONS (NEW ANIMATIONS)
    // =========================================================================
    private JPanel createTouchAnimationsTab() {
        JPanel container = new JPanel(new GridLayout(1, 2, 12, 12));
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(12, 12, 12, 12));

        // -------------------------------------------------------------
        // LEFT COLUMN: RippleButton, PulseEffect, ShimmerPanel
        // -------------------------------------------------------------
        JPanel leftCol = new JPanel(new GridLayout(3, 1, 10, 10));
        leftCol.setOpaque(false);

        // 1. Material Ripple Buttons
        JPanel rippleSec = new JPanel(new BorderLayout(8, 8));
        rippleSec.setBorder(BorderFactory.createTitledBorder("1. Material Ripple Buttons (com.openbravo.ui.animation.RippleButton)"));
        rippleSec.setOpaque(false);

        JPanel rippleGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        rippleGrid.setOpaque(false);

        RippleButton btnRipple1 = new RippleButton("Add Item (F1)");
        btnRipple1.setBackground(new Color(41, 128, 185));
        btnRipple1.setForeground(Color.WHITE);
        btnRipple1.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnRipple1.addActionListener(e -> ToastManager.show(frame, "Ripple Wave Confirmed: Item Added", ToastManager.INFO));

        RippleButton btnRipple2 = new RippleButton("Cash Drawer");
        btnRipple2.setBackground(new Color(39, 174, 96));
        btnRipple2.setForeground(Color.WHITE);
        btnRipple2.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnRipple2.addActionListener(e -> ToastManager.show(frame, "Drawer Opened", ToastManager.SUCCESS));

        RippleButton btnRipple3 = new RippleButton("Split Payment");
        btnRipple3.setBackground(new Color(230, 126, 34));
        btnRipple3.setForeground(Color.WHITE);
        btnRipple3.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnRipple3.addActionListener(e -> ToastManager.show(frame, "Split Dialog Triggered", ToastManager.WARNING));

        RippleButton btnRipple4 = new RippleButton("Cancel Order");
        btnRipple4.setBackground(new Color(192, 57, 43));
        btnRipple4.setForeground(Color.WHITE);
        btnRipple4.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnRipple4.addActionListener(e -> ToastManager.show(frame, "Order Cancelled", ToastManager.ERROR));

        rippleGrid.add(btnRipple1);
        rippleGrid.add(btnRipple2);
        rippleGrid.add(btnRipple3);
        rippleGrid.add(btnRipple4);

        JLabel lblRippleHint = new JLabel("Click any button to view capacitive radial wave expanding from contact point");
        lblRippleHint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblRippleHint.setForeground(new Color(127, 140, 141));

        rippleSec.add(rippleGrid, BorderLayout.CENTER);
        rippleSec.add(lblRippleHint, BorderLayout.SOUTH);
        leftCol.add(rippleSec);

        // 2. Attention Pulse Effect
        JPanel pulseSec = new JPanel(new BorderLayout(8, 8));
        pulseSec.setBorder(BorderFactory.createTitledBorder("2. Attention Pulse Glow (com.openbravo.ui.animation.PulseEffect)"));
        pulseSec.setOpaque(false);

        final PulseEffect[] pulseHolder = new PulseEffect[1];

        JButton btnPayAttention = new JButton("PAY TOTAL NOW (4,850 CVE)") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                AnimationUtils.applyHighQualityRenderingHints(g2);
                Color baseRed = new Color(231, 76, 60);
                Color glowRed = new Color(255, 95, 80);
                Color currentBg = (pulseHolder[0] != null && pulseHolder[0].isRunning())
                        ? AnimationUtils.blend(baseRed, glowRed, pulseHolder[0].getCurrentAlpha())
                        : baseRed;
                g2.setColor(currentBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnPayAttention.setContentAreaFilled(false);
        btnPayAttention.setOpaque(false);
        btnPayAttention.setFocusPainted(false);
        btnPayAttention.setBorderPainted(false);
        btnPayAttention.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnPayAttention.setForeground(Color.WHITE);
        btnPayAttention.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPayAttention.setBorder(new EmptyBorder(12, 24, 12, 24));

        PulseEffect btnPulse = PulseEffect.on(btnPayAttention)
                .setPulseColor(new Color(231, 76, 60))
                .setMaxExpansion(16);
        pulseHolder[0] = btnPulse;

        btnPayAttention.addActionListener(e -> {
            btnPulse.pulseOnce(800);
            ToastManager.show(frame, "Payment submitted with tactile pulse!", ToastManager.SUCCESS);
        });

        JPanel pulseCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20)) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                // Render the glowing expanding aura rings around the button in the parent container
                if (btnPulse.isRunning() && btnPayAttention.isShowing()) {
                    Point p = btnPayAttention.getLocation();
                    btnPulse.paintHalo((Graphics2D) g, p.x, p.y, btnPayAttention.getWidth(), btnPayAttention.getHeight());
                }
            }
        };
        pulseCenter.setBackground(new Color(245, 247, 250));
        pulseCenter.setBorder(new LineBorder(new Color(220, 225, 230), 1));
        pulseCenter.add(btnPayAttention);

        JPanel pulseControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        pulseControls.setOpaque(false);

        JButton btnStartPulse = new JButton("Start Heartbeat Loop");
        btnStartPulse.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnStartPulse.addActionListener(e -> btnPulse.start(800));

        JButton btnStopPulse = new JButton("Stop Heartbeat");
        btnStopPulse.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnStopPulse.addActionListener(e -> btnPulse.stop());

        JButton btnSinglePulse = new JButton("Pulse Once (500ms)");
        btnSinglePulse.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnSinglePulse.addActionListener(e -> btnPulse.pulseOnce(500));

        JButton btnLongPulse5s = new JButton("Pulse Once (5s)");
        btnLongPulse5s.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnLongPulse5s.addActionListener(e -> btnPulse.pulseOnce(5000));

        pulseControls.add(btnStartPulse);
        pulseControls.add(btnStopPulse);
        pulseControls.add(btnSinglePulse);
        pulseControls.add(btnLongPulse5s);

        pulseSec.add(pulseCenter, BorderLayout.CENTER);
        pulseSec.add(pulseControls, BorderLayout.SOUTH);
        leftCol.add(pulseSec);

        // 3. Skeleton Loading Shimmer
        JPanel shimmerSec = new JPanel(new BorderLayout(8, 8));
        shimmerSec.setBorder(BorderFactory.createTitledBorder("3. Skeleton Loading Screen (com.openbravo.ui.animation.ShimmerPanel)"));
        shimmerSec.setOpaque(false);

        ShimmerPanel shimmerPanel = new ShimmerPanel();
        shimmerPanel.setPreferredSize(new Dimension(300, 75));
        shimmerPanel.addBlock(10, 10, 80, 55, 8);
        shimmerPanel.addBlock(100, 15, 180, 16, 4);
        shimmerPanel.addBlock(100, 38, 120, 12, 4);
        shimmerPanel.addBlock(240, 38, 50, 12, 4);

        JPanel shimmerControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        shimmerControls.setOpaque(false);

        JButton btnStartShimmer = new JButton("Start Shimmer Wave");
        btnStartShimmer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnStartShimmer.addActionListener(e -> shimmerPanel.startShimmer());

        JButton btnStopShimmer = new JButton("Stop Shimmer");
        btnStopShimmer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnStopShimmer.addActionListener(e -> shimmerPanel.stopShimmer());

        shimmerControls.add(btnStartShimmer);
        shimmerControls.add(btnStopShimmer);

        shimmerSec.add(shimmerPanel, BorderLayout.CENTER);
        shimmerSec.add(shimmerControls, BorderLayout.SOUTH);
        leftCol.add(shimmerSec);

        // -------------------------------------------------------------
        // RIGHT COLUMN: RollingNumberLabel, SlidePanel, FlyEffect
        // -------------------------------------------------------------
        JPanel rightCol = new JPanel(new GridLayout(3, 1, 10, 10));
        rightCol.setOpaque(false);

        // 4. Rolling Number Display
        JPanel rollingSec = new JPanel(new BorderLayout(8, 8));
        rollingSec.setBorder(BorderFactory.createTitledBorder("4. Rolling Number Counter (com.openbravo.ui.animation.RollingNumberLabel)"));
        rollingSec.setOpaque(false);

        RollingNumberLabel rollingLabel = new RollingNumberLabel(1250.00, "CVE ", "");
        rollingLabel.setFont(new Font("Monospaced", Font.BOLD, 26));
        rollingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rollingLabel.setForeground(new Color(44, 62, 80));

        JPanel rollingButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        rollingButtons.setOpaque(false);

        JButton btnAdd250 = new JButton("+250 CVE");
        btnAdd250.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnAdd250.addActionListener(e -> rollingLabel.rollTo(rollingLabel.getTargetValue() + 250.0));

        JButton btnAdd1000 = new JButton("+1,000 CVE");
        btnAdd1000.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnAdd1000.addActionListener(e -> rollingLabel.rollTo(rollingLabel.getTargetValue() + 1000.0));

        JButton btnSub500 = new JButton("-500 CVE");
        btnSub500.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnSub500.addActionListener(e -> rollingLabel.rollTo(Math.max(0.0, rollingLabel.getTargetValue() - 500.0)));

        JButton btnResetRoll = new JButton("Reset (0.00)");
        btnResetRoll.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnResetRoll.addActionListener(e -> rollingLabel.rollTo(0.0));

        rollingButtons.add(btnAdd250);
        rollingButtons.add(btnAdd1000);
        rollingButtons.add(btnSub500);
        rollingButtons.add(btnResetRoll);

        rollingSec.add(rollingLabel, BorderLayout.CENTER);
        rollingSec.add(rollingButtons, BorderLayout.SOUTH);
        rightCol.add(rollingSec);

        // 5. SlidePanel View Transition
        JPanel slideSec = new JPanel(new BorderLayout(8, 8));
        slideSec.setBorder(BorderFactory.createTitledBorder("5. View Transition Slider (com.openbravo.ui.animation.SlidePanel)"));
        slideSec.setOpaque(false);

        SlidePanel slidePanel = new SlidePanel();
        slidePanel.setPreferredSize(new Dimension(300, 75));

        JPanel view1 = createCategorySlideView("Category 1: Beverages (Café, Cerveja Strela, Sumo)", new Color(52, 152, 219));
        JPanel view2 = createCategorySlideView("Category 2: Traditional Dishes (Cachupa, Bife Atum)", new Color(46, 204, 113));
        JPanel view3 = createCategorySlideView("Category 3: Desserts & Sweets (Pudim, Queijo Fogo)", new Color(155, 89, 182));

        slidePanel.addView("view1", view1);
        slidePanel.addView("view2", view2);
        slidePanel.addView("view3", view3);

        final int[] currentCat = {1};

        JPanel slideNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        slideNav.setOpaque(false);

        JButton btnPrevCat = new JButton("◀ Prev Category");
        btnPrevCat.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnPrevCat.addActionListener(e -> {
            if (currentCat[0] > 1) {
                currentCat[0]--;
                slidePanel.slideTo("view" + currentCat[0], SlidePanel.Direction.RIGHT);
            }
        });

        JButton btnNextCat = new JButton("Next Category ▶");
        btnNextCat.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnNextCat.addActionListener(e -> {
            if (currentCat[0] < 3) {
                currentCat[0]++;
                slidePanel.slideTo("view" + currentCat[0], SlidePanel.Direction.LEFT);
            }
        });

        slideNav.add(btnPrevCat);
        slideNav.add(btnNextCat);

        slideSec.add(slidePanel, BorderLayout.CENTER);
        slideSec.add(slideNav, BorderLayout.SOUTH);
        rightCol.add(slideSec);

        // 6. FlyEffect Parabolic Trajectory
        JPanel flySec = new JPanel(new BorderLayout(8, 8));
        flySec.setBorder(BorderFactory.createTitledBorder("6. Parabolic Fly-To-Cart (com.openbravo.ui.animation.FlyEffect)"));
        flySec.setOpaque(false);

        JPanel flyContent = new JPanel(new BorderLayout(8, 8));
        flyContent.setOpaque(false);

        final int[] cartItemCount = {0};
        JButton cartBadge = new JButton("🛒 Cart: 0 items");
        cartBadge.setFont(new Font("SansSerif", Font.BOLD, 12));
        cartBadge.setBackground(new Color(44, 62, 80));
        cartBadge.setForeground(Color.WHITE);
        cartBadge.setFocusPainted(false);

        JPanel catalogRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        catalogRow.setOpaque(false);

        JButton btnFlyCachupa = new JButton("🍛 Cachupa (350 CVE)");
        btnFlyCachupa.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnFlyCachupa.setBackground(new Color(243, 156, 18));
        btnFlyCachupa.setForeground(Color.WHITE);
        btnFlyCachupa.setFocusPainted(false);
        btnFlyCachupa.addActionListener(e -> {
            FlyEffect.fly(btnFlyCachupa, cartBadge, "Cachupa", () -> {
                cartItemCount[0]++;
                cartBadge.setText("🛒 Cart: " + cartItemCount[0] + " items");
                ToastManager.show(frame, "Cachupa landed in cart!", ToastManager.SUCCESS);
            });
        });

        JButton btnFlyStrela = new JButton("🍺 Strela (160 CVE)");
        btnFlyStrela.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnFlyStrela.setBackground(new Color(41, 128, 185));
        btnFlyStrela.setForeground(Color.WHITE);
        btnFlyStrela.setFocusPainted(false);
        btnFlyStrela.addActionListener(e -> {
            FlyEffect.fly(btnFlyStrela, cartBadge, "Strela", () -> {
                cartItemCount[0]++;
                cartBadge.setText("🛒 Cart: " + cartItemCount[0] + " items");
                ToastManager.show(frame, "Cerveja Strela landed in cart!", ToastManager.SUCCESS);
            });
        });

        catalogRow.add(btnFlyCachupa);
        catalogRow.add(btnFlyStrela);

        flyContent.add(catalogRow, BorderLayout.WEST);
        flyContent.add(cartBadge, BorderLayout.EAST);

        JLabel lblFlyHint = new JLabel("Click food item to fly badge along elevated Bézier parabola into Cart");
        lblFlyHint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblFlyHint.setForeground(new Color(127, 140, 141));

        flySec.add(flyContent, BorderLayout.CENTER);
        flySec.add(lblFlyHint, BorderLayout.SOUTH);
        rightCol.add(flySec);

        container.add(leftCol);
        container.add(rightCol);

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.add(scrollPane, BorderLayout.CENTER);
        return outer;
    }

    private JPanel createCategorySlideView(String title, Color accent) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(accent);
        p.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    // =========================================================================
    // 6. BOTTOM STATUS BAR
    // =========================================================================
    private JPanel createGlobalStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(235, 238, 242));
        bar.setBorder(new CompoundBorder(
                new LineBorder(new Color(205, 210, 218), 1),
                new EmptyBorder(6, 15, 6, 15)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        left.setOpaque(false);

        lblStatusDirty = new JLabel("○ Clean (Synced)");
        lblStatusDirty.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblStatusDirty.setForeground(new Color(39, 174, 96));

        lblStatusValid = new JLabel("✓ Valid Parameters");
        lblStatusValid.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblStatusValid.setForeground(new Color(39, 174, 96));

        left.add(lblStatusDirty);
        left.add(new JSeparator(SwingConstants.VERTICAL));
        left.add(lblStatusValid);

        JLabel right = new JLabel("KriolOS UI Suite • TimingFramework 7.4.3 • SwingBinder");
        right.setFont(new Font("SansSerif", Font.PLAIN, 11));
        right.setForeground(new Color(127, 140, 141));

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(new LineBorder(bg.darker(), 1), new EmptyBorder(8, 14, 8, 14)));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new UIDemo().show();
        });
    }
}
