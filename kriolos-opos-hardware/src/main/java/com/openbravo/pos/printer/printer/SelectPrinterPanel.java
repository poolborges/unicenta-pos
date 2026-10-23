package com.openbravo.pos.printer.printer;

import com.openbravo.basic.AppLocal;
import com.openbravo.data.gui.modal.PosUIModal;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * Modern modal panel for selecting target system print service.
 * Replaces legacy {@link SelectPrinter} dialog with a panel-first {@link PosUIModal} architecture.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class SelectPrinterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private PosUIModal modalContext;
    private boolean accepted = false;
    private String selectedPrintService = null;

    private JComboBox<String> cboPrinters;
    private JLabel lblPrinter;
    private JButton btnOk;
    private JButton btnCancel;

    public SelectPrinterPanel(String[] printers) {
        initComponents(printers);
        initDomainAdapters();
    }

    private void initComponents(String[] printers) {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        setPreferredSize(new Dimension(440, 170));
        setMinimumSize(new Dimension(380, 150));

        // Form fields panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblPrinter = new JLabel(AppLocal.getIntString("label.MachinePrinter"));
        lblPrinter.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        formPanel.add(lblPrinter, gbc);

        cboPrinters = new JComboBox<>();
        cboPrinters.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        cboPrinters.setPreferredSize(new Dimension(240, 36));
        cboPrinters.addItem("(Default)");
        if (printers != null) {
            for (String printer : printers) {
                cboPrinters.addItem(printer);
            }
        }
        cboPrinters.setSelectedIndex(0);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(cboPrinters, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Action buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 10, 0));
        actionsPanel.setOpaque(false);

        btnCancel = new JButton(AppLocal.getIntString("button.cancel"));
        btnCancel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        btnCancel.setPreferredSize(new Dimension(95, 38));
        btnCancel.addActionListener(e -> handleCancel());
        actionsPanel.add(btnCancel);

        btnOk = new JButton(AppLocal.getIntString("button.OK"));
        btnOk.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnOk.setPreferredSize(new Dimension(95, 38));
        btnOk.addActionListener(e -> handleAccept());
        actionsPanel.add(btnOk);

        add(actionsPanel, BorderLayout.SOUTH);
    }

    private void initDomainAdapters() {
        setName("kriolos:printer:select-panel");
        lblPrinter.setName("kriolos:printer:lbl-printer");
        cboPrinters.setName("kriolos:printer:cbo-printers");
        btnOk.setName("kriolos:printer:btn-ok");
        btnCancel.setName("kriolos:printer:btn-cancel");
    }

    @Override
    public void addNotify() {
        super.addNotify();
        JRootPane root = SwingUtilities.getRootPane(this);
        if (root != null) {
            root.setDefaultButton(btnOk);
        }
        cboPrinters.requestFocusInWindow();
    }

    public void setModalContext(PosUIModal modalContext) {
        this.modalContext = modalContext;
    }

    public String getTitle() {
        return AppLocal.getIntString("form.selectprintertitle");
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getSelectedPrintService() {
        return selectedPrintService;
    }

    private void handleAccept() {
        this.accepted = true;
        this.selectedPrintService = (String) cboPrinters.getSelectedItem();
        if (modalContext != null) {
            modalContext.setResult(this.selectedPrintService);
            modalContext.close();
        }
    }

    private void handleCancel() {
        this.accepted = false;
        this.selectedPrintService = null;
        if (modalContext != null) {
            modalContext.setResult(null);
            modalContext.close();
        }
    }

    /**
     * Displays printer selection modal via {@link PosUIModal}.
     *
     * @param parent candidate parent component
     * @param printers list of printer service names
     * @return selected print service name, or {@code null} if cancelled
     */
    public static String show(Component parent, String[] printers) {
        SelectPrinterPanel panel = new SelectPrinterPanel(printers);
        if (parent != null) {
            panel.applyComponentOrientation(parent.getComponentOrientation());
        }

        PosUIModal modal = PosUIModal.create(parent, panel)
                .setTitle(panel.getTitle())
                .setPreferredSize(panel.getPreferredSize())
                .setModal(true)
                .setResizable(false);

        panel.setModalContext(modal);
        modal.show();

        return panel.isAccepted() ? panel.getSelectedPrintService() : null;
    }
}
