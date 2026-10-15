package com.openbravo.ui.data.binding;

import javax.swing.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Fluent Builder specialized in binding and validating
 * {@link JFormattedTextField} masked components.
 *
 * @param <B> the bean type
 * @param <T> the expected model type from the formatted field (e.g., Date, Long, String)
 * @author KriolOS
 * @since 1.0.0
 */
public class FormattedTextFieldBuilder<B, T> implements SwingBinder.BindingLifecycle<B> {

    private final JFormattedTextField field;
    private final SwingBinder<B> binder;
    private final List<Validator<T>> validators = new ArrayList<>();

    private Function<B, T> activeGetter;
    private BiConsumer<B, T> activeSetter;
    private boolean isRefreshing = false;

    public FormattedTextFieldBuilder(JFormattedTextField field, SwingBinder<B> binder) {
        this.field = field;
        this.binder = binder;
    }

    /**
     * Adds a validation rule to the formatted field value path.
     *
     * @param validator the validator containing business rules
     * @return the current builder instance for fluent chaining
     */
    public FormattedTextFieldBuilder<B, T> withValidator(Validator<T> validator) {
        this.validators.add(validator);
        return this;
    }

    /**
     * Convenient lambda-ready overload to add a validation rule.
     */
    public FormattedTextFieldBuilder<B, T> withValidator(Predicate<T> predicate, String errorMessage) {
        return withValidator(Validator.from(predicate, errorMessage));
    }

    /**
     * Requires the field to have a non-null, non-empty value.
     */
    public FormattedTextFieldBuilder<B, T> asRequired(String errorMessage) {
        return withValidator(value -> {
            if (value == null) return false;
            if (value instanceof String) return !((String) value).trim().isEmpty();
            return true;
        }, errorMessage);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readBean(B sourceBean) {
        if (activeGetter != null && sourceBean != null) {
            isRefreshing = true;
            T value = activeGetter.apply(sourceBean);
            field.setValue(value);

            field.setBorder(UIManager.getLookAndFeelDefaults().getBorder("FormattedTextField.border"));
            field.setToolTipText(null);
            isRefreshing = false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean validateCurrentValue() {
        try {
            // Garante que o Swing faz o commit do texto atual para o valor tipado antes de validar
            field.commitEdit();
        } catch (java.text.ParseException e) {
            // Se o texto não estiver de acordo com a máscara/formato estruturado
            field.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            binder.setValidationStatus(field, false);
            return false;
        }

        T currentValue = (T) field.getValue();
        for (Validator<T> validator : validators) {
            if (!validator.isValid(currentValue)) {
                field.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                field.setToolTipText(validator.getMessage());
                binder.setValidationStatus(field, false);
                return false;
            }
        }

        field.setBorder(UIManager.getLookAndFeelDefaults().getBorder("FormattedTextField.border"));
        field.setToolTipText(null);
        binder.setValidationStatus(field, true);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeBean(B targetBean) {
        if (activeSetter != null) {
            try {
                field.commitEdit();
                activeSetter.accept(targetBean, (T) field.getValue());
            } catch (Exception ex) {
                // Fail-safe protection se o valor estiver corrompido
            }
        }
    }

    /**
     * Connects the JFormattedTextField to the designated Java property methods.
     */
    public void bind(Function<B, T> getter, BiConsumer<B, T> setter) {
        this.activeGetter = getter;
        this.activeSetter = setter;
        binder.registerBinding(this);
        readBean(binder.getBean());

        // Escuta mudanças de valor real ("value") em vez de capturar cada tecla individual
        field.addPropertyChangeListener("value", evt -> {
            if (isRefreshing) {
                return;
            }
            validateCurrentValue();
            binder.fireStatusChanged();
        });
    }

    @Override
    public void clear() {
        isRefreshing = true;
        field.setValue(null);
        field.setText("");
        field.setBorder(UIManager.getLookAndFeelDefaults().getBorder("FormattedTextField.border"));
        field.setToolTipText(null);
        isRefreshing = false;
    }

}
