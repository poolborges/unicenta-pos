package com.openbravo.ui.data.binding;

import javax.swing.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Fluent Builder specialized in binding and validating {@link JSlider} numeric
 * components.
 *
 * @param <B> the bean type
 * @author KriolOS
 * @since 1.0.0
 */
public class SliderBuilder<B> implements SwingBinder.BindingLifecycle<B> {

    private final JSlider slider;
    private final SwingBinder<B> binder;
    private final List<Validator<Integer>> validators = new ArrayList<>();

    private Function<B, Integer> activeGetter;
    private BiConsumer<B, Integer> activeSetter;
    private boolean isRefreshing = false;

    public SliderBuilder(JSlider slider, SwingBinder<B> binder) {
        this.slider = slider;
        this.binder = binder;
    }

    /**
     * Adds a validation rule to the slider value path.
     *
     * @param validator the validator containing business rules
     * @return the current builder instance for fluent chaining
     */
    public SliderBuilder<B> withValidator(Validator<Integer> validator) {
        this.validators.add(validator);
        return this;
    }

    /**
     * Convenient lambda-ready overload to add a validation rule to the slider.
     *
     * @param predicate    the validation logic predicate
     * @param errorMessage the error message to display when validation fails
     * @return the current builder instance for fluent chaining
     */
    public SliderBuilder<B> withValidator(Predicate<Integer> predicate, String errorMessage) {
        return withValidator(Validator.from(predicate, errorMessage));
    }
    
    @Override
    public void readBean(B sourceBean) {
        if (activeGetter != null && sourceBean != null) {
            isRefreshing = true;
            Integer value = activeGetter.apply(sourceBean);
            slider.setValue(value != null ? value : slider.getMinimum());

            // Restaura o aspeto visual padrão do componente caso estivesse em erro
            slider.setBackground(UIManager.getColor("Slider.background"));
            slider.setToolTipText(null);
            isRefreshing = false;
        }
    }

    @Override
    public boolean validateCurrentValue() {
        int currentValue = slider.getValue();
        for (Validator<Integer> validator : validators) {
            if (!validator.isValid(currentValue)) {
                // Como sliders normalmente não têm bordas tradicionais, mudamos o fundo
                slider.setBackground(new Color(255, 230, 230));
                slider.setToolTipText(validator.getMessage());
                binder.setValidationStatus(slider, false);
                return false;
            }
        }
        slider.setBackground(UIManager.getColor("Slider.background"));
        slider.setToolTipText(null);
        binder.setValidationStatus(slider, true);
        return true;
    }

    @Override
    public void writeBean(B targetBean) {
        if (activeSetter != null) {
            activeSetter.accept(targetBean, slider.getValue());
        }
    }

    /**
     * Connects the JSlider to the designated Java property methods.
     */
       public void bind(Function<B, Integer> getter, BiConsumer<B, Integer> setter) {
        this.activeGetter = getter;
        this.activeSetter = setter;
        binder.registerBinding(this);
        
        // 1. Desliga temporariamente o gatilho para evitar disparos falsos no layout
        isRefreshing = true; 
        readBean(binder.getBean());
        isRefreshing = false;

        // 2. Adiciona o listener para capturar as interações do utilizador
        slider.addChangeListener(e -> {
            if (isRefreshing) return;
            
            // Só executa o commit completo e validação ao mexer
            validateCurrentValue();
            binder.fireStatusChanged();
        });
        
        // 3. Força uma notificação geral para garantir que a label "15%" se atualiza no ecrã
        binder.fireStatusChanged(); 
        binder.setDirty(false); // Mantém o formulário limpo na abertura inicial
    }


    @Override
    public void clear() {
        isRefreshing = true;
        slider.setValue(slider.getMinimum());
        slider.setBackground(UIManager.getColor("Slider.background"));
        slider.setToolTipText(null);
        isRefreshing = false;
    }

}
