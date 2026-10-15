package com.openbravo.ui.data.binding;

import javax.swing.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Fluent builder for binding {@link JCheckBox} components to boolean bean properties.
 *
 * @param <B> bean type
 * @author KriolOS
 * @since 1.0.0
 */
public class CheckBoxBuilder<B> implements SwingBinder.BindingLifecycle<B> {

    private final JCheckBox checkBox;
    private final SwingBinder<B> binder;
    private final List<Validator<Boolean>> validators = new ArrayList<>();

    private Function<B, Boolean> activeGetter;
    private BiConsumer<B, Boolean> activeSetter;
    private boolean isRefreshing = false;

    public CheckBoxBuilder(JCheckBox checkBox, SwingBinder<B> binder) {
        this.checkBox = checkBox;
        this.binder = binder;
    }

    public CheckBoxBuilder<B> withValidator(Validator<Boolean> validator) {
        this.validators.add(validator);
        return this;
    }

    public CheckBoxBuilder<B> withValidator(Predicate<Boolean> predicate, String errorMessage) {
        return withValidator(Validator.from(predicate, errorMessage));
    }

    @Override
    public void readBean(B sourceBean) {
        if (activeGetter != null && sourceBean != null) {
            isRefreshing = true;
            checkBox.setSelected(Boolean.TRUE.equals(activeGetter.apply(sourceBean)));
            checkBox.setForeground(UIManager.getColor("CheckBox.foreground"));
            checkBox.setToolTipText(null);
            isRefreshing = false;
        }
    }

    @Override
    public boolean validateCurrentValue() {
        boolean currentValue = checkBox.isSelected();
        for (Validator<Boolean> validator : validators) {
            if (!validator.isValid(currentValue)) {
                checkBox.setForeground(Color.RED);
                checkBox.setToolTipText(validator.getMessage());
                binder.setValidationStatus(checkBox, false);
                return false;
            }
        }
        checkBox.setForeground(UIManager.getColor("CheckBox.foreground"));
        checkBox.setToolTipText(null);
        binder.setValidationStatus(checkBox, true);
        return true;
    }

    @Override
    public void writeBean(B targetBean) {
        if (activeSetter != null) {
            activeSetter.accept(targetBean, checkBox.isSelected());
        }
    }

    public void bind(Function<B, Boolean> getter, BiConsumer<B, Boolean> setter) {
        this.activeGetter = getter;
        this.activeSetter = setter;
        binder.registerBinding(this);
        readBean(binder.getBean());

        checkBox.addActionListener(e -> {
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
        checkBox.setSelected(false);
        checkBox.setForeground(UIManager.getColor("CheckBox.foreground"));
        checkBox.setToolTipText(null);
        isRefreshing = false;
    }

}
