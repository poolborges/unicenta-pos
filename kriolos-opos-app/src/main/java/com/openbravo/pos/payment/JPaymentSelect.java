package com.openbravo.pos.payment;

import com.openbravo.app.AppLocal;
import com.openbravo.data.gui.modal.PosUIModal;
import com.openbravo.format.Formats;
import com.openbravo.pos.admin.DataLogicSystem;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.customers.CustomerService;
import com.openbravo.pos.customers.DataLogicCustomers;
import com.openbravo.pos.panels.AppView;
import com.openbravo.pos.resources.ImageResources;
import com.openbravo.pos.resources.ResourceService;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import javax.swing.text.View;
import org.apache.commons.lang3.StringUtils;

/**
 * Payment selection panel and modal orchestrator.
 *
 * @author KriolOS
 */
public abstract class JPaymentSelect extends JPanel implements JPaymentNotifier {

    private static final long serialVersionUID = 1L;

    protected Component parentComponent;
    protected PosUIModal modalContext;

    private PaymentInfoList m_aPaymentInfo;
    private boolean accepted;

    private AppView app;
    private double m_dTotal;
    private CustomerInfoExt customerext;
    @Deprecated
    private DataLogicSystem dlSystem;
    private ResourceService resourceService;
    private CustomerService customerService;
    @Deprecated
    private DataLogicCustomers dlCustomers;

    private PaymentService paymentService;

    private final Map<String, JPaymentInterface> payments = new HashMap<>();
    private String m_sTransactionID;
    private static PaymentInfo returnPayment = null;

    public static PaymentInfo getReturnPayment() {
        return returnPayment;
    }

    public static void setReturnPayment(PaymentInfo returnPayment) {
        JPaymentSelect.returnPayment = returnPayment;
    }

    protected JPaymentSelect() {
        this((Component) null, (ComponentOrientation) null);
    }

    protected JPaymentSelect(Component parent, ComponentOrientation o) {
        this.parentComponent = parent;
        initComponents();
        if (o != null) {
            this.applyComponentOrientation(o);
        }
        initDomainAdapters();
    }

    @Deprecated
    protected JPaymentSelect(java.awt.Frame parent, boolean modal, ComponentOrientation o) {
        this((Component) parent, o);
    }

    @Deprecated
    protected JPaymentSelect(java.awt.Dialog parent, boolean modal, ComponentOrientation o) {
        this((Component) parent, o);
    }

    private void initDomainAdapters() {
        setName("kriolos:payment:select-panel");
        if (m_jButtonOK != null) {
            m_jButtonOK.setName("kriolos:payment:btn-ok");
        }
        if (m_jButtonCancel != null) {
            m_jButtonCancel.setName("kriolos:payment:btn-cancel");
        }
        if (m_jButtonAdd != null) {
            m_jButtonAdd.setName("kriolos:payment:btn-add");
        }
        if (m_jButtonRemove != null) {
            m_jButtonRemove.setName("kriolos:payment:btn-remove");
        }
        if (m_jButtonPrint != null) {
            m_jButtonPrint.setName("kriolos:payment:btn-print");
        }
        if (m_jTabPayment != null) {
            m_jTabPayment.setName("kriolos:payment:tabs");
        }
        if (m_jTotalEuros != null) {
            m_jTotalEuros.setName("kriolos:payment:lbl-total");
        }
        if (m_jRemaininglEuros != null) {
            m_jRemaininglEuros.setName("kriolos:payment:lbl-remaining");
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane != null && m_jButtonOK != null) {
            rootPane.setDefaultButton(m_jButtonOK);
        }
    }

    public void dispose() {
        if (modalContext != null) {
            modalContext.close();
            modalContext = null;
        }
    }

    public String getTitle() {
        return AppLocal.getIntString("payment.title");
    }

    public void init(AppView app, PaymentService paymentService) {
        this.app = app;
        this.paymentService = paymentService;
        resourceService = app.getBean(ResourceService.class);
        dlSystem = (resourceService instanceof DataLogicSystem) ? (DataLogicSystem) resourceService : null;
        customerService = app.getBean(CustomerService.class);
        dlCustomers = (customerService instanceof DataLogicCustomers) ? (DataLogicCustomers) customerService : app.getBean(DataLogicCustomers.class);

        m_jButtonPrint.setVisible(true);
        setPrintSelected(!Boolean.parseBoolean(app.getProperties().getProperty("till.receiptprintoff")));
    }

    public void setPrintSelected(boolean value) {
        m_jButtonPrint.setSelected(value);
        if (m_jButtonPrint.isSelected()) {
            jlblPrinterStatus.setText(AppLocal.getIntString("jpaymentselect.printer.on", "Printer on"));
        } else {
            jlblPrinterStatus.setText(AppLocal.getIntString("jpaymentselect.printer.off", "Printer off"));
        }
    }

    public boolean isPrintSelected() {
        return m_jButtonPrint.isSelected();
    }

    /**
     * List of PaymentInfo
     * 
     * @return list of selected payments
     */
    public List<PaymentInfo> getSelectedPayments() {
        return m_aPaymentInfo.getPayments();
    }

    /**
     * Get PaymentInfoList
     * 
     * @return payment info list
     */
    public PaymentInfoList getPaymentInfoList() {
        return m_aPaymentInfo;
    }

    /**
     * Get total
     *
     * @return total amount
     */
    public double getTotal() {
        return paymentService.getTotal(m_aPaymentInfo);
    }

    /**
     * Get total Paid
     *
     * @return total paid amount
     */
    public double getPaidTotal() {
        return paymentService.getPaidTotal(m_aPaymentInfo);
    }

    public boolean showDialog(double total, CustomerInfoExt customerext, double deposit) {
        return showDialog(total - deposit, customerext);
    }

    public boolean showDialog(double total, CustomerInfoExt customerext) {
        m_aPaymentInfo = paymentService.createPaymentList();
        accepted = false;
        m_dTotal = total;
        this.customerext = customerext;

        m_jTotalEuros.setText(Formats.CURRENCY.formatValue(m_dTotal));

        addTabs();

        if (m_jTabPayment.getTabCount() == 0) {
            // No payment panels available
            m_aPaymentInfo.add(getDefaultPayment(total));
            accepted = true;
        } else {
            printState();
            modalContext = PosUIModal.create(parentComponent != null ? parentComponent : this, this)
                    .setTitle(getTitle())
                    .setPreferredSize(new Dimension(800, 520))
                    .setModal(true);
            modalContext.show();
        }
        m_jTabPayment.removeAll();

        return accepted;
    }

    protected abstract void addTabs();

    protected abstract void setStatusPanel(boolean isPositive, boolean isComplete);

    protected abstract PaymentInfo getDefaultPayment(double total);

    protected void setOKEnabled(boolean value) {
        m_jButtonOK.setEnabled(value);
    }

    protected void setAddEnabled(boolean value) {
        m_jButtonAdd.setEnabled(value);
    }

    protected void addTabPayment(JPaymentCreator jpay) {
        if (app.hasPermission(jpay.getKey())) {

            JPaymentInterface jpayinterface = payments.get(jpay.getKey());
            if (jpayinterface == null) {
                jpayinterface = jpay.createJPayment();
                payments.put(jpay.getKey(), jpayinterface);
            }

            jpayinterface.getComponent().applyComponentOrientation(getComponentOrientation());

            String title = AppLocal.getIntString(jpay.getLabelKey());

            m_jTabPayment.addTab(
                    fixedStringRithPad(title),
                    ImageResources.getIcon(jpay.getIconKey()),
                    jpayinterface.getComponent(),
                    title);
        }
    }

    private String fixedStringRithPad(final String text) {
        return fixedStringRithPad(text, 10);
    }

    private String fixedStringRithPad(String text, int length) {
        if (text.length() > length) {
            text = text.substring(0, length - 1);
        }
        return StringUtils.rightPad(text, length, "");
    }

    public interface JPaymentCreator {

        public JPaymentInterface createJPayment();

        public String getKey();

        public String getLabelKey();

        public String getIconKey();
    }

    public class JPaymentCashCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentCashPos(JPaymentSelect.this, resourceService);
        }

        @Override
        public String getKey() {
            return "payment.cash";
        }

        @Override
        public String getLabelKey() {
            return "tab.cash";
        }

        @Override
        public String getIconKey() {
            return "/com/openbravo/images/cash.png";
        }
    }

    public class JPaymentChequeCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentCheque(JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "payment.cheque";
        }

        @Override
        public String getLabelKey() {
            return "tab.cheque";
        }

        @Override
        public String getIconKey() {
            return "/com/openbravo/images/cheque.png";
        }
    }

    public class JPaymentVoucherCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentVoucher(app, JPaymentSelect.this, "voucherin");
        }

        @Override
        public String getKey() {
            return "payment.voucher";
        }

        @Override
        public String getLabelKey() {
            return "tab.voucher";
        }

        @Override
        public String getIconKey() {
            return "/com/openbravo/images/voucher.png";
        }
    }

    public class JPaymentCardCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentCard(JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "payment.card";
        }

        @Override
        public String getLabelKey() {
            return "tab.card";
        }

        @Override
        public String getIconKey() {
            return "/com/openbravo/images/ccard.png";
        }
    }

    public class JPaymentMagcardCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentMagcard(app, JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "payment.magcard";
        }

        @Override
        public String getLabelKey() {
            return "tab.magcard";
        }

        @Override
        public String getIconKey() {
            return "/com/openbravo/images/ccard.png";
        }
    }

    public class JPaymentFreeCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentFree(JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "payment.free";
        }

        @Override
        public String getLabelKey() {
            return "tab.free";
        }

        @Override
        public String getIconKey() {
            return "/com/openbravo/images/wallet.png";
        }
    }

    public class JPaymentDebtCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentDebt(JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "payment.debt";
        }

        @Override
        public String getLabelKey() {
            return "tab.debt";
        }

        @Override
        public String getIconKey() {
            return "/com/openbravo/images/customer.png";
        }
    }

    public class JPaymentCashRefundCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentRefund(JPaymentSelect.this, "cashrefund");
        }

        @Override
        public String getKey() {
            return "refund.cash";
        }

        @Override
        public String getLabelKey() {
            return "tab.cashrefund";
        }

        @Override
        public String getIconKey() {
            return "/com/openbravo/images/cash.png";
        }
    }

    public class JPaymentChequeRefundCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentRefund(JPaymentSelect.this, "chequerefund");
        }

        @Override
        public String getKey() {
            return "refund.cheque";
        }

        @Override
        public String getLabelKey() {
            return "tab.chequerefund";
        }

        @Override
        public String getIconKey() {
            return "/com/openbravo/images/cheque.png";
        }
    }

    public class JPaymentVoucherRefundCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentRefund(JPaymentSelect.this, "voucherout");
        }

        @Override
        public String getKey() {
            return "refund.voucher";
        }

        @Override
        public String getLabelKey() {
            return "tab.voucher";
        }

        @Override
        public String getIconKey() {
            return "/com/openbravo/images/voucher.png";
        }
    }

    public class JPaymentMagcardRefundCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentMagcard(app, JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "refund.magcard";
        }

        @Override
        public String getLabelKey() {
            return "tab.magcard";
        }

        @Override
        public String getIconKey() {
            return "/com/openbravo/images/ccard.png";
        }
    }

    public class JPaymentBankCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentBank(JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "payment.bank";
        }

        @Override
        public String getLabelKey() {
            return "tab.bank";
        }

        @Override
        public String getIconKey() {
            return "/com/openbravo/images/bank.png";
        }
    }

    public class JPaymentSlipCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentSlip(JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "payment.slip";
        }

        @Override
        public String getLabelKey() {
            return "tab.slip";
        }

        @Override
        public String getIconKey() {
            return "/com/openbravo/images/slip.png";
        }
    }

    private void printState() {
        m_jRemaininglEuros.setText(Formats.CURRENCY.formatValue(
                paymentService.calculateRemaining(m_aPaymentInfo, m_dTotal)));
        m_jButtonRemove.setEnabled(!m_aPaymentInfo.isEmpty());
        m_jTabPayment.setSelectedIndex(0);
        ((JPaymentInterface) m_jTabPayment.getSelectedComponent())
                .activate(customerext,
                        paymentService.calculateRemaining(m_aPaymentInfo, m_dTotal),
                        m_sTransactionID);
    }

    @Deprecated
    protected static Window getWindow(Component parent) {
        if (parent == null) {
            return new JFrame();
        } else if (parent instanceof Window) {
            return (Window) parent;
        } else {
            return getWindow(parent.getParent());
        }
    }

    @Override
    public void setStatus(boolean isPositive, boolean isComplete) {
        setStatusPanel(isPositive, isComplete);
    }

    public void setTransactionID(String tID) {
        this.m_sTransactionID = tID;
    }

    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        m_jLblTotalEuros1 = new javax.swing.JLabel();
        m_jTotalEuros = new javax.swing.JLabel();
        m_jLblRemainingEuros = new javax.swing.JLabel();
        m_jRemaininglEuros = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        m_jButtonRemove = new javax.swing.JButton();
        m_jButtonAdd = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        m_jTabPayment = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        m_jButtonCancel = new javax.swing.JButton();
        m_jButtonOK = new javax.swing.JButton();
        m_jButtonPrint = new javax.swing.JToggleButton();
        jlblPrinterStatus = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());
        setPreferredSize(new java.awt.Dimension(800, 520));
        setMinimumSize(new java.awt.Dimension(600, 400));

        jPanel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        m_jLblTotalEuros1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        m_jLblTotalEuros1.setText(AppLocal.getIntString("label.totalcash")); // NOI18N
        m_jLblTotalEuros1.setPreferredSize(new java.awt.Dimension(100, 30));

        m_jTotalEuros.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        m_jTotalEuros.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jTotalEuros.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jTotalEuros.setOpaque(true);
        m_jTotalEuros.setPreferredSize(new java.awt.Dimension(150, 30));
        m_jTotalEuros.setRequestFocusEnabled(false);

        m_jLblRemainingEuros.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        m_jLblRemainingEuros.setText(AppLocal.getIntString("label.remainingcash")); // NOI18N
        m_jLblRemainingEuros.setPreferredSize(new java.awt.Dimension(120, 30));

        m_jRemaininglEuros.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        m_jRemaininglEuros.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jRemaininglEuros.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jRemaininglEuros.setOpaque(true);
        m_jRemaininglEuros.setPreferredSize(new java.awt.Dimension(150, 30));
        m_jRemaininglEuros.setRequestFocusEnabled(false);

        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        m_jButtonRemove.setFont(m_jButtonRemove.getFont());
        m_jButtonRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btnminus.png"))); // NOI18N
        m_jButtonRemove.setToolTipText(AppLocal.getIntString("jpaymentselect.payment.delpartial")); // NOI18N
        m_jButtonRemove.setPreferredSize(new java.awt.Dimension(80, 45));
        m_jButtonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonRemoveActionPerformed(evt);
            }
        });

        m_jButtonAdd.setFont(m_jButtonAdd.getFont());
        m_jButtonAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btnplus.png"))); // NOI18N
        m_jButtonAdd.setToolTipText(AppLocal.getIntString("jpaymentselect.payment.addpartial")); // NOI18N
        m_jButtonAdd.setPreferredSize(new java.awt.Dimension(80, 45));
        m_jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonAddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(m_jLblTotalEuros1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(m_jTotalEuros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(m_jLblRemainingEuros, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jRemaininglEuros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(m_jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jButtonRemove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jButtonRemove, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jButtonAdd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jLblTotalEuros1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jRemaininglEuros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jLblRemainingEuros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jTotalEuros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        add(jPanel4, java.awt.BorderLayout.NORTH);

        jPanel3.setName(""); // NOI18N
        jPanel3.setLayout(new java.awt.BorderLayout());

        m_jTabPayment.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        m_jTabPayment.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        m_jTabPayment.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jTabPayment.setRequestFocusEnabled(false);
        m_jTabPayment.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                m_jTabPaymentStateChanged(evt);
            }
        });
        m_jTabPayment.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                m_jTabPaymentKeyPressed(evt);
            }
        });
        jPanel3.add(m_jTabPayment, java.awt.BorderLayout.CENTER);

        add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel5.setLayout(new java.awt.BorderLayout());

        m_jButtonCancel.setFont(m_jButtonCancel.getFont());
        m_jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/cancel.png"))); // NOI18N
        m_jButtonCancel.setText(AppLocal.getIntString("button.cancel")); // NOI18N
        m_jButtonCancel.setToolTipText(m_jButtonCancel.getText());
        m_jButtonCancel.setFocusPainted(false);
        m_jButtonCancel.setFocusable(false);
        m_jButtonCancel.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonCancel.setPreferredSize(new java.awt.Dimension(110, 45));
        m_jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonCancelActionPerformed(evt);
            }
        });
        jPanel2.add(m_jButtonCancel);

        m_jButtonOK.setFont(m_jButtonOK.getFont());
        m_jButtonOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/ok.png"))); // NOI18N
        m_jButtonOK.setText(AppLocal.getIntString("button.ok")); // NOI18N
        m_jButtonOK.setToolTipText(m_jButtonOK.getText());
        m_jButtonOK.setFocusPainted(false);
        m_jButtonOK.setFocusable(false);
        m_jButtonOK.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonOK.setMaximumSize(new java.awt.Dimension(100, 44));
        m_jButtonOK.setPreferredSize(new java.awt.Dimension(110, 45));
        m_jButtonOK.setRequestFocusEnabled(false);
        m_jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonOKActionPerformed(evt);
            }
        });
        jPanel2.add(m_jButtonOK);

        jPanel5.add(jPanel2, java.awt.BorderLayout.LINE_END);

        m_jButtonPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/printer24_off.png"))); // NOI18N
        m_jButtonPrint.setSelected(true);
        m_jButtonPrint.setToolTipText(AppLocal.getIntString("jpaymentselect.printer.tooltip")); // NOI18N
        m_jButtonPrint.setFocusPainted(false);
        m_jButtonPrint.setFocusable(false);
        m_jButtonPrint.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonPrint.setPreferredSize(new java.awt.Dimension(80, 45));
        m_jButtonPrint.setRequestFocusEnabled(false);
        m_jButtonPrint.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/printer24.png"))); // NOI18N
        m_jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonPrintActionPerformed(evt);
            }
        });
        jPanel5.add(m_jButtonPrint, java.awt.BorderLayout.LINE_START);

        jlblPrinterStatus.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jlblPrinterStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jlblPrinterStatus.setText(bundle.getString("label.printerstatusOn")); // NOI18N
        jPanel5.add(jlblPrinterStatus, java.awt.BorderLayout.CENTER);

        add(jPanel5, java.awt.BorderLayout.SOUTH);
    }

    private void m_jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {
        paymentService.removeLastPayment(m_aPaymentInfo);
        printState();
    }

    private void m_jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {
        PaymentInfo returnPayment = ((JPaymentInterface) m_jTabPayment.getSelectedComponent())
                .executePayment();
        if (returnPayment != null) {
            paymentService.addPayment(m_aPaymentInfo, returnPayment);
            printState();
        }
    }

    private void m_jTabPaymentStateChanged(javax.swing.event.ChangeEvent evt) {
        if (m_jTabPayment.getSelectedComponent() != null) {
            ((JPaymentInterface) m_jTabPayment.getSelectedComponent())
                    .activate(customerext,
                            paymentService.calculateRemaining(m_aPaymentInfo, m_dTotal),
                            m_sTransactionID);
            m_jRemaininglEuros.setText(
                    Formats.CURRENCY.formatValue(
                            paymentService.calculateRemaining(m_aPaymentInfo, m_dTotal)));
        }
    }

    private void m_jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {
        SwingWorker<Object, Object> worker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() throws Exception {
                setReturnPayment(
                        ((JPaymentInterface) m_jTabPayment.getSelectedComponent())
                                .executePayment());
                return null;
            }

            @Override
            public void done() {
                m_jButtonOK.setEnabled(true);
                m_jButtonCancel.setEnabled(true);
                if (returnPayment != null) {
                    paymentService.addPayment(m_aPaymentInfo, returnPayment);
                    accepted = true;
                    dispose();
                }
            }
        };

        worker.execute();
    }

    private void m_jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private void m_jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {
        setPrintSelected(m_jButtonPrint.isSelected());
    }

    private void m_jTabPaymentKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_F1) {

        } else if (evt.getKeyCode() == KeyEvent.VK_F2) {
            // m_jEditLine.doClick();
        }
    }

    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel jlblPrinterStatus;
    private javax.swing.JButton m_jButtonAdd;
    private javax.swing.JButton m_jButtonCancel;
    private javax.swing.JButton m_jButtonOK;
    private javax.swing.JToggleButton m_jButtonPrint;
    private javax.swing.JButton m_jButtonRemove;
    private javax.swing.JLabel m_jLblRemainingEuros;
    private javax.swing.JLabel m_jLblTotalEuros1;
    private javax.swing.JLabel m_jRemaininglEuros;
    private javax.swing.JTabbedPane m_jTabPayment;
    private javax.swing.JLabel m_jTotalEuros;
}

class MyTabbedPaneUI extends MetalTabbedPaneUI {

    private final JTabbedPane tabPane;

    public MyTabbedPaneUI(JTabbedPane tabbedPane) {
        this.tabPane = tabbedPane;
    }

    @Override
    protected void layoutLabel(int tabPlacement,
            FontMetrics metrics, int tabIndex,
            String title, Icon icon,
            Rectangle tabRect, Rectangle iconRect,
            Rectangle textRect, boolean isSelected) {

        textRect.x = textRect.y = iconRect.x = iconRect.y = 0;
        View v = getTextViewForTab(tabIndex);
        if (v != null) {
            tabPane.putClientProperty("html", v);
        }
        SwingUtilities.layoutCompoundLabel((JComponent) tabPane,
                metrics, title, icon,
                SwingUtilities.CENTER,
                SwingUtilities.LEFT,
                SwingUtilities.CENTER,
                SwingUtilities.TRAILING,
                tabRect,
                iconRect,
                textRect,
                textIconGap);
        tabPane.putClientProperty("html", null);
        textRect.translate(tabInsets.left, 0);

        int xNudge = getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
        int yNudge = getTabLabelShiftY(tabPlacement, tabIndex, isSelected);
        iconRect.x += xNudge;
        iconRect.y += yNudge;
        textRect.x += xNudge;
        textRect.y += yNudge;
    }
}
