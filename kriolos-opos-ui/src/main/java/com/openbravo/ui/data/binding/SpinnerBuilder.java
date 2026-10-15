package com.openbravo.ui.data.binding;

import javax.swing.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Fluent Builder specialized in binding and validating {@link JSpinner} components 
 * targeting numeric values (e.g., Integer, Double, Float).
 *
 * @param <B> the bean type
 * @param <N> the specific numeric type handled by the spinner
 * @author KriolOS
 * @since 1.0.0
 */
public class SpinnerBuilder<B, N extends Number> implements SwingBinder.BindingLifecycle<B> {
    private final JSpinner spinner;
    private final SwingBinder<B> binder;
    private final List<Validator<N>> validators = new ArrayList<>();
    
    private Function<B, N> activeGetter;
    private BiConsumer<B, N> activeSetter;
    private boolean isRefreshing = false;

    public SpinnerBuilder(JSpinner spinner, SwingBinder<B> binder) {
        this.spinner = spinner;
        this.binder = binder;
    }

    /**
     * Adds a validation rule to the spinner value path.
     *
     * @param validator the validator containing business rules
     * @return the current builder instance for fluent chaining
     */
    public SpinnerBuilder<B, N> withValidator(Validator<N> validator) {
        this.validators.add(validator);
        return this;
    }
    
    /**
     * Convenient lambda-ready overload to add a validation rule.
     *
     * @param predicate    the validation logic predicate
     * @param errorMessage the error message to bind
     * @return the current builder instance for fluent chaining
     */
    public SpinnerBuilder<B, N> withValidator(java.util.function.Predicate<N> predicate, String errorMessage) {
        return withValidator(Validator.from(predicate, errorMessage));
    }

    @Override
    public void readBean(B sourceBean) {
        if (activeGetter != null && sourceBean != null) {
            isRefreshing = true;
            spinner.setValue(activeGetter.apply(sourceBean));
            spinner.setBorder(UIManager.getLookAndFeelDefaults().getBorder("Spinner.border"));
            spinner.setToolTipText(null);
            isRefreshing = false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean validateCurrentValue() {
        N currentValue = (N) spinner.getValue();
        for (Validator<N> validator : validators) {
            if (!validator.isValid(currentValue)) {
                spinner.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                spinner.setToolTipText(validator.getMessage());
                binder.setValidationStatus(spinner, false);
                return false;
            }
        }
        spinner.setBorder(UIManager.getLookAndFeelDefaults().getBorder("Spinner.border"));
        spinner.setToolTipText(null);
        binder.setValidationStatus(spinner, true);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeBean(B targetBean) {
        if (activeSetter != null) {
            activeSetter.accept(targetBean, (N) spinner.getValue());
        }
    }

    /**
     * Resets the spinner component to its default minimal or zero value, 
     * and clears any current visual validation errors.
     */
    @Override
    public void clear() {
        isRefreshing = true;
        
        // Se o spinner usar um modelo numérico, tenta repor para o valor mínimo configurado
        if (spinner.getModel() instanceof SpinnerNumberModel) {
            SpinnerNumberModel numberModel = (SpinnerNumberModel) spinner.getModel();
            if (numberModel.getMinimum() != null) {
                spinner.setValue(numberModel.getMinimum());
            } else {
                // Caso não haja mínimo, tenta inferir um valor zero seguro com base na classe ativa
                Object currentVal = numberModel.getValue();
                if (currentVal instanceof Double) spinner.setValue(0.0);
                else if (currentVal instanceof Float) spinner.setValue(0.0f);
                else if (currentVal instanceof Long) spinner.setValue(0L);
                else spinner.setValue(0);
            }
        } else {
            // Fallback genérico para outros tipos de SpinnerModels
            if (spinner.getModel().getValue() instanceof Number) {
                spinner.setValue(0);
            }
        }

        spinner.setBorder(UIManager.getLookAndFeelDefaults().getBorder("Spinner.border"));
        spinner.setToolTipText(null);
        isRefreshing = false;
    }

    /**
     * Connects the JSpinner to the designated Java property methods.
     */
    public void bind(Function<B, N> getter, BiConsumer<B, N> setter) {
        this.activeGetter = getter;
        this.activeSetter = setter;
        binder.registerBinding(this);
        readBean(binder.getBean());

        spinner.addChangeListener(e -> {
            if (isRefreshing) return;
            validateCurrentValue();
            binder.fireStatusChanged();
        });
    }
}
