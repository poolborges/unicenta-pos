package com.openbravo.ui.data.binding;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SwingBinder} and fluent binding builders.
 * Validates two-way data binding, dirty tracking, validation lifecycle,
 * discard rollback, and bean synchronization.
 *
 * @author KriolOS
 * @since 1.0.0
 */
@DisplayName("SwingBinder Two-Way Data Binding Tests")
class SwingBinderTest {

    private static final String INITIAL_NAME = "Alice";
    private static final String INITIAL_EMAIL = "alice@example.com";
    private static final boolean INITIAL_ACTIVE = true;

    private static final String UPDATED_NAME = "Bob";
    private static final String UPDATED_EMAIL = "bob@example.com";
    private static final boolean UPDATED_ACTIVE = false;

    private static final String INVALID_EMAIL = "invalid-email-address";
    private static final String EMAIL_ERROR_MSG = "Invalid email format";

    /**
     * Test bean for data binding validation.
     */
    public static class TestUserBean {
        private String name;
        private String email;
        private boolean active;

        public TestUserBean(String name, String email, boolean active) {
            this.name = name;
            this.email = email;
            this.active = active;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    private TestUserBean sampleBean;
    private SwingBinder<TestUserBean> binder;
    private JTextField nameField;
    private JTextField emailField;
    private JCheckBox activeCheckBox;

    @BeforeEach
    void setUp() {
        sampleBean = new TestUserBean(INITIAL_NAME, INITIAL_EMAIL, INITIAL_ACTIVE);
        binder = new SwingBinder<>(sampleBean);

        nameField = new JTextField();
        emailField = new JTextField();
        activeCheckBox = new JCheckBox();

        binder.forField(nameField)
                .bind(TestUserBean::getName, TestUserBean::setName);

        binder.forField(emailField)
                .withValidator(new EmailValidator(EMAIL_ERROR_MSG))
                .bind(TestUserBean::getEmail, TestUserBean::setEmail);

        binder.forField(activeCheckBox)
                .bind(TestUserBean::isActive, TestUserBean::setActive);
    }

    @Test
    @DisplayName("Verify readBean initializes UI components from bean properties")
    void testReadBeanInitializesUI() {
        assertEquals(INITIAL_NAME, nameField.getText());
        assertEquals(INITIAL_EMAIL, emailField.getText());
        assertTrue(activeCheckBox.isSelected());
        assertFalse(binder.isDirty());
        assertTrue(binder.isValid());
    }

    @Test
    @DisplayName("Verify user edits mark binder as dirty and notify status change listeners")
    void testDirtyTrackingAndListeners() {
        AtomicBoolean listenerFired = new AtomicBoolean(false);
        binder.addStatusChangeListener(() -> listenerFired.set(true));

        assertFalse(binder.isDirty());
        assertFalse(listenerFired.get());

        nameField.setText(UPDATED_NAME);

        assertTrue(binder.isDirty(), "Binder should become dirty after field edit");
        assertTrue(listenerFired.get(), "Status change listener should be triggered");
    }

    @Test
    @DisplayName("Verify writeBean persists UI changes to target bean when valid")
    void testWriteBeanSuccess() throws SwingBinder.ValidationException {
        nameField.setText(UPDATED_NAME);
        emailField.setText(UPDATED_EMAIL);
        activeCheckBox.setSelected(UPDATED_ACTIVE);

        TestUserBean target = new TestUserBean("", "", true);
        binder.writeBean(target);

        assertEquals(UPDATED_NAME, target.getName());
        assertEquals(UPDATED_EMAIL, target.getEmail());
        assertEquals(UPDATED_ACTIVE, target.isActive());
        assertFalse(binder.isDirty(), "Binder should reset dirty flag after successful write");
    }

    @Test
    @DisplayName("Verify validation failure blocks writeBean and throws ValidationException")
    void testValidationFailureBlocksWrite() {
        emailField.setText(INVALID_EMAIL);

        assertFalse(binder.isValid(), "Binder should indicate invalid state");
        assertEquals(EMAIL_ERROR_MSG, emailField.getToolTipText());

        TestUserBean target = new TestUserBean("Orig", "orig@domain.com", true);

        assertThrows(SwingBinder.ValidationException.class, () -> binder.writeBean(target),
                "writeBean should reject save when validation fails");

        // Ensure target was not modified
        assertEquals("Orig", target.getName());
        assertEquals("orig@domain.com", target.getEmail());
    }

    @Test
    @DisplayName("Verify discard reverts UI to currently bound bean and clears errors")
    void testDiscardRevertsChanges() {
        nameField.setText(UPDATED_NAME);
        emailField.setText(INVALID_EMAIL);
        activeCheckBox.setSelected(UPDATED_ACTIVE);

        assertTrue(binder.isDirty());
        assertFalse(binder.isValid());

        binder.discard();

        assertEquals(INITIAL_NAME, nameField.getText());
        assertEquals(INITIAL_EMAIL, emailField.getText());
        assertEquals(INITIAL_ACTIVE, activeCheckBox.isSelected());
        assertFalse(binder.isDirty());
        assertTrue(binder.isValid());
        assertNull(emailField.getToolTipText());
    }

    @Test
    @DisplayName("Verify clear resets fields, bean reference, and flags")
    void testClearResetsForm() {
        binder.clear();

        assertEquals("", nameField.getText());
        assertEquals("", emailField.getText());
        assertFalse(activeCheckBox.isSelected());
        assertNull(binder.getBean());
        assertFalse(binder.isDirty());
        assertTrue(binder.isValid());
    }
}
