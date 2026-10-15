package com.openbravo.ui.data.binding;

import javax.swing.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Fluent Builder specialized in binding and validating a group of
 * {@link JRadioButton} components mapped to a single domain model property
 * (e.g., Enums or Strings).
 *
 * @param <B> the bean type
 * @param <T> the property type represented by the radio button selection
 * @author KriolOS
 * @since 1.0.0
 */
public class RadioButtonGroupBuilder<B, T> implements SwingBinder.BindingLifecycle<B> {

    private final Map<T, JRadioButton> buttonMap = new LinkedHashMap<>();
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final SwingBinder<B> binder;
    private final List<Validator<T>> validators = new ArrayList<>();

    private Function<B, T> activeGetter;
    private BiConsumer<B, T> activeSetter;
    private boolean isRefreshing = false;

    public RadioButtonGroupBuilder(SwingBinder<B> binder) {
        this.binder = binder;
    }

    /**
     * Maps a specific domain value to a physical JRadioButton instance,
     * automatically binding it to the group for mutual exclusivity.
     *
     * @param value the model representation value
     * @param button the corresponding JRadioButton UI component
     * @return the current builder instance for fluent chaining
     */
    public RadioButtonGroupBuilder<B, T> add(T value, JRadioButton button) {
        buttonMap.put(value, button);
        buttonGroup.add(button);
        return this;
    }

    /**
     * Adds a validation rule to the radio button group selection path.
     *
     * @param validator the validator containing business rules
     * @return the current builder instance for fluent chaining
     */
    public RadioButtonGroupBuilder<B, T> withValidator(Validator<T> validator) {
        this.validators.add(validator);
        return this;
    }

    /**
     * Convenient lambda-ready overload to add a validation rule to the radio
     * button group.
     *
     * @param predicate the validation logic predicate
     * @param errorMessage the error message to display when validation fails
     * @return the current builder instance for fluent chaining
     */
    public RadioButtonGroupBuilder<B, T> withValidator(java.util.function.Predicate<T> predicate, String errorMessage) {
        return withValidator(Validator.from(predicate, errorMessage));
    }

    private T getSelectedValue() {
        for (Map.Entry<T, JRadioButton> entry : buttonMap.entrySet()) {
            if (entry.getValue().isSelected()) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void readBean(B sourceBean) {
        if (activeGetter != null && sourceBean != null) {
            isRefreshing = true;
            T value = activeGetter.apply(sourceBean);
            JRadioButton targetButton = buttonMap.get(value);
            if (targetButton != null) {
                targetButton.setSelected(true);
            } else {
                for (JRadioButton btn : buttonMap.values()) {
                    btn.setSelected(false);
                }
            }
            isRefreshing = false;
        }
    }

    @Override
    public boolean validateCurrentValue() {
        if (buttonMap.isEmpty()) {
            return true;
        }

        T currentValue = getSelectedValue();
        JRadioButton referenceButton = buttonMap.values().iterator().next();

        for (Validator<T> validator : validators) {
            if (!validator.isValid(currentValue)) {
                binder.setValidationStatus(referenceButton, false);
                return false;
            }
        }
        binder.setValidationStatus(referenceButton, true);
        return true;
    }

    @Override
    public void writeBean(B targetBean) {
        if (activeSetter != null) {
            activeSetter.accept(targetBean, getSelectedValue());
        }
    }

    /**
     * Resets the entire radio group selection by deselecting all buttons and
     * clearing the local component validation state.
     */
    @Override
    public void clear() {
        isRefreshing = true;
        buttonGroup.clearSelection();
        for (JRadioButton btn : buttonMap.values()) {
            btn.setSelected(false);
        }
        if (!buttonMap.isEmpty()) {
            binder.setValidationStatus(buttonMap.values().iterator().next(), true);
        }
        isRefreshing = false;
    }

    /**
     * Connects the managed Radio Button Group to the designated Java property
     * methods.
     */
    public void bind(Function<B, T> getter, BiConsumer<B, T> setter) {
        this.activeGetter = getter;
        this.activeSetter = setter;
        binder.registerBinding(this);
        readBean(binder.getBean());

        for (JRadioButton button : buttonMap.values()) {
            button.addActionListener(e -> {
                if (isRefreshing) {
                    return;
                }
                validateCurrentValue();
                binder.fireStatusChanged();
            });
        }
    }
}
