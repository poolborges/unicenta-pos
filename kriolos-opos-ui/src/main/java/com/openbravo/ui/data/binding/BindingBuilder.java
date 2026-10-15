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
 * Fluent builder for configuring two-way data bindings between {@link JTextField}
 * components and Java bean properties, supporting converters and validators.
 *
 * @param <B> bean type
 * @param <P> presentation property type
 * @author KriolOS
 * @since 1.0.0
 */
public class BindingBuilder<B, P> implements SwingBinder.BindingLifecycle<B> {

    protected final JTextField field;
    protected final SwingBinder<B> binder;
    protected final List<Validator<P>> validators = new ArrayList<>();
    protected final Converter<String, P> internalConverter;

    private Function<B, P> activeGetter;
    private BiConsumer<B, P> activeSetter;
    private boolean isRefreshing = false;

    @SuppressWarnings("unchecked")
    public BindingBuilder(JTextField field, SwingBinder<B> binder) {
        this.field = field;
        this.binder = binder;
        this.internalConverter = (Converter<String, P>) new Converter<String, String>() {
            @Override
            public String toModel(String p) {
                return p;
            }

            @Override
            public String toPresentation(String m) {
                return m;
            }
        };
    }

    protected BindingBuilder(JTextField field, SwingBinder<B> binder, Converter<String, P> converter, List<Validator<P>> validators) {
        this.field = field;
        this.binder = binder;
        this.internalConverter = converter;
        this.validators.addAll(validators);
    }

    public <T> BindingBuilder<B, T> withConverter(Converter<P, T> converter) {
        Converter<String, T> composedConverter = new Converter<String, T>() {
            @Override
            public T toModel(String presentationValue) {
                P pivot = internalConverter.toModel(presentationValue);
                return pivot == null ? null : converter.toModel(pivot);
            }

            @Override
            public String toPresentation(T modelValue) {
                P pivot = converter.toPresentation(modelValue);
                return internalConverter.toPresentation(pivot);
            }
        };
        return new BindingBuilder<>(this.field, this.binder, composedConverter, new ArrayList<>());
    }

    public BindingBuilder<B, P> withValidator(Validator<P> validator) {
        this.validators.add(validator);
        return this;
    }

    public BindingBuilder<B, P> withValidator(java.util.function.Predicate<P> predicate, String errorMessage) {
        return withValidator(Validator.from(predicate, errorMessage));
    }

    @Override
    public void readBean(B sourceBean) {
        if (activeGetter != null && sourceBean != null) {
            isRefreshing = true;
            P initialModelValue = activeGetter.apply(sourceBean);
            field.setText(internalConverter.toPresentation(initialModelValue));
            field.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextField.border"));
            field.setToolTipText(null);
            isRefreshing = false;
        }
    }

    @Override
    public boolean validateCurrentValue() {
        try {
            String rawText = field.getText().trim();
            P convertedValue = internalConverter.toModel(rawText);
            for (Validator<P> validator : validators) {
                if (!validator.isValid(convertedValue)) {
                    field.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                    field.setToolTipText(validator.getMessage());
                    binder.setValidationStatus(field, false);
                    return false;
                }
            }
            field.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextField.border"));
            field.setToolTipText(null);
            binder.setValidationStatus(field, true);
            return true;
        } catch (Exception ex) {
            field.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            binder.setValidationStatus(field, false);
            return false;
        }
    }

    @Override
    public void writeBean(B targetBean) {
        if (activeSetter != null) {
            try {
                String rawText = field.getText().trim();
                P convertedValue = internalConverter.toModel(rawText);
                activeSetter.accept(targetBean, convertedValue);
            } catch (Exception ex) {
                // Fail-safe protection
            }
        }
    }

    public void bind(Function<B, P> getter, BiConsumer<B, P> setter) {
        this.activeGetter = getter;
        this.activeSetter = setter;
        binder.registerBinding(this);
        readBean(binder.getBean());

        field.getDocument().addDocumentListener(new DocumentListener() {
            private void handleInput() {
                if (isRefreshing) {
                    return;
                }
                validateCurrentValue();
                binder.fireStatusChanged();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                handleInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleInput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleInput();
            }
        });
    }

    /**
     * Shorthand for requiring the field to be non-empty. Replicates Vaadin's
     * behavior by adding an automatic non-empty string validator.
     *
     * @param errorMessage the message to display if the field is left empty
     * @return the current builder instance for fluent chaining
     */
    public BindingBuilder<B, P> asRequired(String errorMessage) {
        return withValidator(value -> {
            if (value == null) {
                return false;
            }
            if (value instanceof String) {
                return !((String) value).trim().isEmpty();
            }
            // If it was already converted to a number/object, check if it is not null
            return true;
        }, errorMessage);
    }

    @Override
    public void clear() {
        isRefreshing = true;
        field.setText("");
        field.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextField.border"));
        field.setToolTipText(null);
        isRefreshing = false;
    }

}
