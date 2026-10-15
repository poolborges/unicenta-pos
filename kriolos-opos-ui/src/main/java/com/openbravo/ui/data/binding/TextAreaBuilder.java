package com.openbravo.ui.data.binding;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Fluent Builder specialized in binding and validating {@link JTextArea} multi-line text components.
 *
 * @param <B> the bean type
 * @author KriolOS
 * @since 1.0.0
 */
public class TextAreaBuilder<B> implements SwingBinder.BindingLifecycle<B> {
    private final JTextArea textArea;
    private final SwingBinder<B> binder;
    private final List<Validator<String>> validators = new ArrayList<>();
    
    private Function<B, String> activeGetter;
    private BiConsumer<B, String> activeSetter;
    private boolean isRefreshing = false;

    public TextAreaBuilder(JTextArea textArea, SwingBinder<B> binder) {
        this.textArea = textArea;
        this.binder = binder;
    }

    /**
     * Adds a validation rule to the text area value path.
     *
     * @param validator the validator containing business rules
     * @return the current builder instance for fluent chaining
     */
    public TextAreaBuilder<B> withValidator(Validator<String> validator) {
        this.validators.add(validator);
        return this;
    }
    
    /**
     * Convenient lambda-ready overload to add a validation rule.
     *
     * @param predicate    the validation logic predicate
     * @param errorMessage the error message to display when validation fails
     * @return the current builder instance for fluent chaining
     */
    public TextAreaBuilder<B> withValidator(java.util.function.Predicate<String> predicate, String errorMessage) {
        return withValidator(Validator.from(predicate, errorMessage));
    }


    @Override
    public void readBean(B sourceBean) {
        if (activeGetter != null && sourceBean != null) {
            isRefreshing = true;
            String val = activeGetter.apply(sourceBean);
            textArea.setText(val != null ? val : "");
            textArea.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextArea.border"));
            textArea.setToolTipText(null);
            isRefreshing = false;
        }
    }

    @Override
    public boolean validateCurrentValue() {
        String currentText = textArea.getText();
        for (Validator<String> validator : validators) {
            if (!validator.isValid(currentText)) {
                textArea.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                textArea.setToolTipText(validator.getMessage());
                binder.setValidationStatus(textArea, false);
                return false;
            }
        }
        textArea.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextArea.border"));
        textArea.setToolTipText(null);
        binder.setValidationStatus(textArea, true);
        return true;
    }

    @Override
    public void writeBean(B targetBean) {
        if (activeSetter != null) {
            activeSetter.accept(targetBean, textArea.getText());
        }
    }

    /**
     * Resets the text area component to an empty state and clears any current 
     * visual validation errors.
     */
    @Override
    public void clear() {
        isRefreshing = true;
        textArea.setText("");
        textArea.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextArea.border"));
        textArea.setToolTipText(null);
        isRefreshing = false;
    }

    /**
     * Connects the JTextArea to the designated Java property methods.
     */
    public void bind(Function<B, String> getter, BiConsumer<B, String> setter) {
        this.activeGetter = getter;
        this.activeSetter = setter;
        binder.registerBinding(this);
        readBean(binder.getBean());

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            private void handleInput() {
                if (isRefreshing) return;
                validateCurrentValue();
                binder.fireStatusChanged();
            }
            @Override public void insertUpdate(DocumentEvent e) { handleInput(); }
            @Override public void removeUpdate(DocumentEvent e) { handleInput(); }
            @Override public void changedUpdate(DocumentEvent e) { handleInput(); }
        });
    }
}
