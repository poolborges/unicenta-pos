package com.openbravo.ui.data.binding;

import javax.swing.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Fluent builder for binding {@link JComboBox} components to bean properties.
 *
 * @param <B> bean type
 * @param <T> combo item type
 * @author KriolOS
 * @since 1.0.0
 */
public class ComboBoxBuilder<B, T> implements SwingBinder.BindingLifecycle<B> {

    private final JComboBox<T> comboBox;
    private final SwingBinder<B> binder;
    private final List<Validator<T>> validators = new ArrayList<>();

    private Function<B, T> activeGetter;
    private BiConsumer<B, T> activeSetter;
    private boolean isRefreshing = false;

    public ComboBoxBuilder(JComboBox<T> comboBox, SwingBinder<B> binder) {
        this.comboBox = comboBox;
        this.binder = binder;
    }

    public ComboBoxBuilder<B, T> withValidator(Validator<T> validator) {
        this.validators.add(validator);
        return this;
    }

    public ComboBoxBuilder<B, T> withValidator(Predicate<T> predicate, String errorMessage) {
        return withValidator(Validator.from(predicate, errorMessage));
    }

    @Override
    public void readBean(B sourceBean) {
        if (activeGetter != null && sourceBean != null) {
            isRefreshing = true;
            comboBox.setSelectedItem(activeGetter.apply(sourceBean));
            comboBox.setBorder(UIManager.getLookAndFeelDefaults().getBorder("ComboBox.border"));
            comboBox.setToolTipText(null);
            isRefreshing = false;
        }
    }

    @Override
    public boolean validateCurrentValue() {
        @SuppressWarnings("unchecked")
        T currentSelection = (T) comboBox.getSelectedItem();
        for (Validator<T> validator : validators) {
            if (!validator.isValid(currentSelection)) {
                comboBox.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                comboBox.setToolTipText(validator.getMessage());
                binder.setValidationStatus(comboBox, false);
                return false;
            }
        }
        comboBox.setBorder(UIManager.getLookAndFeelDefaults().getBorder("ComboBox.border"));
        comboBox.setToolTipText(null);
        binder.setValidationStatus(comboBox, true);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeBean(B targetBean) {
        if (activeSetter != null) {
            activeSetter.accept(targetBean, (T) comboBox.getSelectedItem());
        }
    }

    public void bind(Function<B, T> getter, BiConsumer<B, T> setter) {
        this.activeGetter = getter;
        this.activeSetter = setter;
        binder.registerBinding(this);
        readBean(binder.getBean());

        comboBox.addActionListener(e -> {
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
        if (comboBox.getItemCount() > 0) {
            comboBox.setSelectedIndex(0); // Repõe no primeiro item (geralmente "-- Selecione --")
        } else {
            comboBox.setSelectedItem(null);
        }
        comboBox.setBorder(UIManager.getLookAndFeelDefaults().getBorder("ComboBox.border"));
        comboBox.setToolTipText(null);
        isRefreshing = false;
    }

}
