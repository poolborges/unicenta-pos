package com.openbravo.pos.config;

import com.openbravo.basic.AppLocal;
import com.openbravo.app.config.LegacyBootstrapConfiguration;
import com.openbravo.data.gui.JMessagePanel;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.Session;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.ui.components.ButtonFactory;
import com.openbravo.ui.components.ButtonSize;
import com.openbravo.ui.components.ComponentFactory;
import com.openbravo.ui.components.PaneBuilder;
import com.openbravo.ui.components.UIColor;
import com.openbravo.ui.icon.IconKey;
import com.openbravo.util.AltEncrypter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Modern, handcrafted Database Configuration panel.
 *
 * <p>Provides full CRUD management over primary and secondary database connection
 * profiles backed by {@link AppConfig}, featuring an interactive table, detail form,
 * connection testing, and OS-independent file-based database paths (defaulting to SQLite).</p>
 *
 * @author KriolOS
 */
public class JPanelConfigDatabase extends JPanel implements PanelConfig {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(JPanelConfigDatabase.class.getName());

    private final DirtyManager dirty = new DirtyManager();
    private boolean updatingForm = false;

    // Profiles model
    private final List<DatabaseProfile> profiles = new ArrayList<>();
    private final DatabaseTableModel tableModel = new DatabaseTableModel();
    private int selectedIndex = -1;

    // UI Components: Table & Actions
    private JTable tblDatabases;
    private JButton btnNew;
    private JButton btnDelete;
    private JLabel lblTableStatus;

    // UI Components: Form
    private JLabel lblFormTitle;
    private JTextField txtDbName;
    private JTextField txtDbURL;
    private JTextField txtDbSchema;
    private JTextField txtDbOptions;
    private JTextField txtDbUser;
    private JPasswordField txtDbPassword;
    private JButton btnTestConnection;
    private JLabel lblConnectionStatus;

    /**
     * Creates new database configuration panel.
     */
    public JPanelConfigDatabase() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Center: Split Pane (Table on Left, Form on Right)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createTablePanel(), createFormPanel());
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.40);
        splitPane.setDividerLocation(380);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel(AppLocal.getIntString("label.database"));
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
        titlePanel.add(lblTitle);

        JLabel lblDesc = new JLabel(AppLocal.getIntString("label.database.checkingparams"));
        lblDesc.setFont(lblDesc.getFont().deriveFont(Font.PLAIN, 12f));
        lblDesc.setForeground(UIColor.MUTED);
        titlePanel.add(lblDesc);

        header.add(titlePanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIColor.MUTED, 1),
                " " + AppLocal.getIntString("label.database") + " "
        ));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEADING, 6, 6));
        toolbar.setOpaque(false);

        btnNew = ButtonFactory.createActionButton(
                " " + AppLocal.getIntString("Button.newcustomer"),
                IconKey.PLUS,
                ButtonSize.MEDIUM,
                e -> onNewDatabase()
        );
        btnNew.setToolTipText("Add new database connection slot");

        btnDelete = ButtonFactory.createActionButton(
                " " + AppLocal.getIntString("button.delete"),
                IconKey.DELETE,
                ButtonSize.MEDIUM,
                e -> onDeleteDatabase()
        );
        btnDelete.setToolTipText("Delete selected secondary database connection");

        toolbar.add(btnNew);
        toolbar.add(btnDelete);
        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        tblDatabases = new JTable(tableModel);
        tblDatabases.setRowHeight(34);
        tblDatabases.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDatabases.setShowGrid(true);
        tblDatabases.setFillsViewportHeight(true);

        // Alignment & Column Widths
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblDatabases.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblDatabases.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblDatabases.getColumnModel().getColumn(1).setPreferredWidth(120);
        tblDatabases.getColumnModel().getColumn(2).setPreferredWidth(180);

        tblDatabases.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblDatabases.getSelectedRow();
                if (row >= 0 && row < profiles.size()) {
                    selectProfile(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblDatabases);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Footer status
        lblTableStatus = new JLabel("0 databases configured");
        lblTableStatus.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        lblTableStatus.setFont(lblTableStatus.getFont().deriveFont(Font.PLAIN, 11f));
        lblTableStatus.setForeground(UIColor.MUTED);
        panel.add(lblTableStatus, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel formContainer = new JPanel(new BorderLayout(8, 8));
        formContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIColor.MUTED, 1),
                " Connection Details "
        ));

        // Form Title
        lblFormTitle = new JLabel("Database Parameters", SwingConstants.LEFT);
        lblFormTitle.setFont(lblFormTitle.getFont().deriveFont(Font.BOLD, 14f));
        lblFormTitle.setBorder(BorderFactory.createEmptyBorder(6, 12, 4, 12));
        formContainer.add(lblFormTitle, BorderLayout.NORTH);

        // Fields
        txtDbName = new JTextField();
        txtDbURL = new JTextField();
        txtDbSchema = new JTextField();
        txtDbOptions = new JTextField();
        txtDbUser = new JTextField();
        txtDbPassword = new JPasswordField();

        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onFormFieldChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onFormFieldChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onFormFieldChanged();
            }
        };

        txtDbName.getDocument().addDocumentListener(docListener);
        txtDbURL.getDocument().addDocumentListener(docListener);
        txtDbSchema.getDocument().addDocumentListener(docListener);
        txtDbOptions.getDocument().addDocumentListener(docListener);
        txtDbUser.getDocument().addDocumentListener(docListener);
        txtDbPassword.getDocument().addDocumentListener(docListener);

        // Build with PaneBuilder
        PaneBuilder builder = new PaneBuilder()
                .setLabelSize(120)
                .setFieldHeight(34)
                .setScrollable(true)
                .add(AppLocal.getIntString("label.DbName"), true, txtDbName)
                .add(AppLocal.getIntString("label.DbURL"), true, txtDbURL)
                .add(AppLocal.getIntString("label.DbSchema"), txtDbSchema)
                .add(AppLocal.getIntString("label.DbOptions"), txtDbOptions)
                .add(AppLocal.getIntString("label.DbUser"), txtDbUser)
                .add(AppLocal.getIntString("label.DbPassword"), txtDbPassword);

        formContainer.add(builder.build(), BorderLayout.CENTER);

        // Footer: Test Connection & Feedback
        JPanel footer = new JPanel(new BorderLayout(8, 8));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 12, 10, 12));

        lblConnectionStatus = new JLabel(" ");
        lblConnectionStatus.setFont(lblConnectionStatus.getFont().deriveFont(Font.PLAIN, 12f));
        footer.add(lblConnectionStatus, BorderLayout.CENTER);

        btnTestConnection = ButtonFactory.createActionButton(
                " " + AppLocal.getIntString("button.test"),
                IconKey.DATABASE,
                ButtonSize.MEDIUM,
                e -> onTestConnection()
        );
        ComponentFactory.enforceFooterSize(btnTestConnection);
        footer.add(btnTestConnection, BorderLayout.EAST);

        formContainer.add(footer, BorderLayout.SOUTH);

        return formContainer;
    }

    private void onFormFieldChanged() {
        if (updatingForm || selectedIndex < 0 || selectedIndex >= profiles.size()) {
            return;
        }

        DatabaseProfile p = profiles.get(selectedIndex);
        p.setName(txtDbName.getText().trim());
        p.setUrl(txtDbURL.getText().trim());
        p.setSchema(txtDbSchema.getText().trim());
        p.setOptions(txtDbOptions.getText().trim());
        p.setUser(txtDbUser.getText().trim());
        p.setPassword(new String(txtDbPassword.getPassword()));

        tableModel.fireTableRowsUpdated(selectedIndex, selectedIndex);
        lblFormTitle.setText("Editing: " + p.getName() + (p.isPrimary() ? " (Primary)" : ""));
        dirty.setDirty(true);
    }

    private void selectProfile(int index) {
        if (index < 0 || index >= profiles.size()) {
            return;
        }
        selectedIndex = index;
        DatabaseProfile p = profiles.get(index);

        updatingForm = true;
        try {
            txtDbName.setText(p.getName());
            txtDbURL.setText(p.getUrl());
            txtDbSchema.setText(p.getSchema());
            txtDbOptions.setText(p.getOptions());
            txtDbUser.setText(p.getUser());
            txtDbPassword.setText(p.getPassword());

            lblFormTitle.setText("Editing: " + p.getName() + (p.isPrimary() ? " (Primary)" : ""));
            lblConnectionStatus.setText(" ");

            btnDelete.setEnabled(!p.isPrimary());
        } finally {
            updatingForm = false;
        }
    }

    private void onNewDatabase() {
        // Find next available slot id
        int nextId = 1;
        Set<String> existingIds = new HashSet<>();
        for (DatabaseProfile p : profiles) {
            existingIds.add(p.getId());
        }
        while (existingIds.contains(String.valueOf(nextId))) {
            nextId++;
        }

        String slotId = String.valueOf(nextId);
        String defaultPath = getOsIndependentSqlitePath("kriolopos_" + slotId + ".db");

        DatabaseProfile newProfile = new DatabaseProfile(
                slotId,
                "DB " + slotId + " (Secondary)",
                defaultPath,
                "",
                "",
                "",
                "",
                false
        );

        profiles.add(newProfile);
        tableModel.fireTableRowsInserted(profiles.size() - 1, profiles.size() - 1);

        tblDatabases.setRowSelectionInterval(profiles.size() - 1, profiles.size() - 1);
        updateTableControls();
        dirty.setDirty(true);
        txtDbName.requestFocusInWindow();
    }

    private void onDeleteDatabase() {
        if (selectedIndex < 0 || selectedIndex >= profiles.size()) {
            return;
        }

        DatabaseProfile p = profiles.get(selectedIndex);
        if (p.isPrimary()) {
            JMessagePanel.showMessage(this,
                    new MessageInf(MessageInf.SGN_WARNING, "The primary database cannot be deleted."));
            return;
        }

        int res = JMessagePanel.showConfirmDialog(this,
                new MessageInf(MessageInf.SGN_WARNING, "Are you sure you want to delete profile '" + p.getName() + "'?"));

        if (res == 0) { // Confirmed
            int toRemove = selectedIndex;
            profiles.remove(toRemove);
            tableModel.fireTableRowsDeleted(toRemove, toRemove);

            int nextSelect = Math.min(toRemove, profiles.size() - 1);
            if (nextSelect >= 0) {
                tblDatabases.setRowSelectionInterval(nextSelect, nextSelect);
            }
            updateTableControls();
            dirty.setDirty(true);
        }
    }

    private void onTestConnection() {
        if (selectedIndex < 0 || selectedIndex >= profiles.size()) {
            return;
        }

        DatabaseProfile p = profiles.get(selectedIndex);
        String fullUrl = p.getFullUrl();
        String user = p.getUser();
        String password = p.getPassword();

        lblConnectionStatus.setText("Testing connection...");
        lblConnectionStatus.setForeground(UIColor.PRIMARY);

        try {
            Session session = new Session(fullUrl, user, password);
            Connection conn = session.getConnection();
            boolean isValid = (conn != null) && conn.isValid(2000);

            if (conn != null && !conn.isClosed()) {
                conn.close();
            }

            if (isValid) {
                lblConnectionStatus.setText("✓ Connection successful!");
                lblConnectionStatus.setForeground(UIColor.SUCCESS);
                JMessagePanel.showMessage(this,
                        new MessageInf(MessageInf.SGN_SUCCESS, AppLocal.getIntString("message.databasesuccess")));
            } else {
                lblConnectionStatus.setText("✗ Connection failed: invalid connection.");
                lblConnectionStatus.setForeground(UIColor.DANGER);
                JMessagePanel.showMessage(this,
                        new MessageInf(MessageInf.SGN_WARNING, "Connection Error: Database connection is not valid."));
            }
        } catch (SQLException e) {
            lblConnectionStatus.setText("✗ Connection error: " + e.getMessage());
            lblConnectionStatus.setForeground(UIColor.DANGER);
            JMessagePanel.showMessage(this,
                    new MessageInf(MessageInf.SGN_WARNING,
                            AppLocal.getIntString("message.databaseconnectionerror"), e));
        } catch (Exception e) {
            lblConnectionStatus.setText("✗ " + e.getMessage());
            lblConnectionStatus.setForeground(UIColor.DANGER);
            JMessagePanel.showMessage(this,
                    new MessageInf(MessageInf.SGN_WARNING, "Unknown exception", e));
        }
    }

    private String getOsIndependentSqlitePath(String fileName) {
        File baseDir = LegacyBootstrapConfiguration.getBaseApplicationDataDirectory();
        return "jdbc:sqlite:" + new File(baseDir, fileName).getAbsolutePath().replace('\\', '/');
    }

    private void updateTableControls() {
        lblTableStatus.setText(profiles.size() + " database connection(s) configured");
    }

    @Override
    public boolean hasChanged() {
        return dirty.isDirty();
    }

    @Override
    public Component getConfigComponent() {
        return this;
    }

    @Override
    public void loadProperties(AppConfig config) {
        updatingForm = true;
        try {
            profiles.clear();

            // 1. Primary DB (Default to OS-independent file-based SQLite)
            String defaultSqliteUrl = getOsIndependentSqlitePath("kriolopos.db");
            String pName = config.getProperty("db.name", "DB 0 (SQLite)");
            String pUrl = config.getProperty("db.URL", defaultSqliteUrl);
            String pSchema = config.getProperty("db.schema", "");
            String pOptions = config.getProperty("db.options", "");
            String pUser = config.getProperty("db.user", "");
            String pPass = decryptPassword(pUser, config.getProperty("db.password", ""));

            DatabaseProfile primary = new DatabaseProfile("primary", pName, pUrl, pSchema, pOptions, pUser, pPass, true);
            profiles.add(primary);

            // 2. Discover Secondary DBs
            Set<String> secondaryIds = new LinkedHashSet<>();

            // Modern db.{id}.name or db.{id}.URL
            if (config.getProperties() != null) {
                for (String key : config.getProperties().stringPropertyNames()) {
                    if (key.startsWith("db.") && key.endsWith(".name") && !key.equals("db.name")) {
                        String sid = key.substring(3, key.length() - 5);
                        secondaryIds.add(sid);
                    } else if (key.startsWith("db.") && key.endsWith(".URL") && !key.equals("db.URL")) {
                        String sid = key.substring(3, key.length() - 4);
                        secondaryIds.add(sid);
                    } else if (key.startsWith("db") && key.endsWith(".name") && !key.equals("db.name")) {
                        // Legacy db1.name
                        String sid = key.substring(2, key.length() - 5);
                        if (!sid.isEmpty()) {
                            secondaryIds.add(sid);
                        }
                    }
                }
            }

            for (String sid : secondaryIds) {
                String sName = config.getProperty("db." + sid + ".name", config.getProperty("db" + sid + ".name", "DB " + sid));
                String sUrl = config.getProperty("db." + sid + ".URL", config.getProperty("db" + sid + ".URL", ""));
                String sSchema = config.getProperty("db." + sid + ".schema", config.getProperty("db" + sid + ".schema", ""));
                String sOptions = config.getProperty("db." + sid + ".options", config.getProperty("db" + sid + ".options", ""));
                String sUser = config.getProperty("db." + sid + ".user", config.getProperty("db" + sid + ".user", ""));
                String sPass = decryptPassword(sUser, config.getProperty("db." + sid + ".password", config.getProperty("db" + sid + ".password", "")));

                if (!sUrl.isEmpty() || !sName.isEmpty()) {
                    DatabaseProfile sec = new DatabaseProfile(sid, sName, sUrl, sSchema, sOptions, sUser, sPass, false);
                    profiles.add(sec);
                }
            }

            tableModel.fireTableDataChanged();
            if (!profiles.isEmpty()) {
                tblDatabases.setRowSelectionInterval(0, 0);
                selectProfile(0);
            }
            updateTableControls();
            dirty.setDirty(false);
        } finally {
            updatingForm = false;
        }
    }

    @Override
    public void saveProperties(AppConfig config) {
        if (profiles.isEmpty()) {
            dirty.setDirty(false);
            return;
        }

        // Save Primary DB
        DatabaseProfile primary = profiles.get(0);
        config.setProperty("db.name", primary.getName());
        config.setProperty("db.URL", primary.getUrl());
        config.setProperty("db.schema", primary.getSchema());
        config.setProperty("db.options", primary.getOptions());
        config.setProperty("db.user", primary.getUser());
        config.setProperty("db.password", encryptPassword(primary.getUser(), primary.getPassword()));

        // Save Secondary DBs
        for (int i = 1; i < profiles.size(); i++) {
            DatabaseProfile sec = profiles.get(i);
            String id = sec.getId();

            config.setProperty("db." + id + ".name", sec.getName());
            config.setProperty("db." + id + ".URL", sec.getUrl());
            config.setProperty("db." + id + ".schema", sec.getSchema());
            config.setProperty("db." + id + ".options", sec.getOptions());
            config.setProperty("db." + id + ".user", sec.getUser());
            config.setProperty("db." + id + ".password", encryptPassword(sec.getUser(), sec.getPassword()));

            // Mirror legacy db1.* keys if id is 1
            if ("1".equals(id)) {
                config.setProperty("db1.name", sec.getName());
                config.setProperty("db1.URL", sec.getUrl());
                config.setProperty("db1.schema", sec.getSchema());
                config.setProperty("db1.options", sec.getOptions());
                config.setProperty("db1.user", sec.getUser());
                config.setProperty("db1.password", encryptPassword(sec.getUser(), sec.getPassword()));
            }
        }

        dirty.setDirty(false);
    }

    private String decryptPassword(String user, String pass) {
        if (user != null && pass != null && pass.startsWith("crypt:")) {
            try {
                AltEncrypter cypher = new AltEncrypter("cypherkey" + user);
                return cypher.decrypt(pass.substring(6));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to decrypt password for user " + user, e);
                return "";
            }
        }
        return pass != null ? pass : "";
    }

    private String encryptPassword(String user, String pass) {
        if (pass == null || pass.isEmpty()) {
            return "";
        }
        try {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + (user != null ? user : ""));
            return "crypt:" + cypher.encrypt(pass);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to encrypt password", e);
            return pass;
        }
    }

    // =========================================================================
    // INNER CLASSES: TableModel & Profile Model
    // =========================================================================

    private class DatabaseTableModel extends AbstractTableModel {

        private final String[] columns = {"Role", "Name", "JDBC URL"};

        @Override
        public int getRowCount() {
            return profiles.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex < 0 || rowIndex >= profiles.size()) {
                return null;
            }
            DatabaseProfile p = profiles.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return p.isPrimary() ? "★ Primary" : "Slot " + p.getId();
                case 1:
                    return p.getName();
                case 2:
                    return p.getFullUrl();
                default:
                    return "";
            }
        }
    }

    public static class DatabaseProfile {

        private String id;
        private String name;
        private String url;
        private String schema;
        private String options;
        private String user;
        private String password;
        private boolean primary;

        public DatabaseProfile(String id, String name, String url,
                String schema, String options, String user, String password, boolean primary) {
            this.id = id;
            this.name = name;
            this.url = url;
            this.schema = schema;
            this.options = options;
            this.user = user;
            this.password = password;
            this.primary = primary;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        public String getOptions() {
            return options;
        }

        public void setOptions(String options) {
            this.options = options;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isPrimary() {
            return primary;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }

        public String getFullUrl() {
            return (url != null ? url : "")
                    + (schema != null ? schema : "")
                    + (options != null ? options : "");
        }
    }
}
