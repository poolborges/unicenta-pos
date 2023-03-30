/*
 * Copyright (C) 2022 Paulo Borges
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openbravo.pos.panels;

import javax.swing.JPanel;
import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationGroupProvider;
import org.netbeans.validation.api.ui.ValidationUI;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;

/**
 *
 * @author poolborges
 */
public abstract class ValidationPanel extends JPanel implements ValidationGroupProvider{

    private static final long serialVersionUID = 1L;
    private ValidationGroup group;
    private Problem problem;
    private VUI vui = new VUI();
    
    public ValidationPanel(){
        group = SwingValidationGroup.create(vui);
    }

    /**
     * Default Implementation do nothing
     * @param problem 
     */
    protected void onShowProblem(Problem problem){}
    
    /**
     * Default Implementation do nothing
     */
    protected void onClearProblem(){}

    @Override
    public ValidationGroup getValidationGroup() {
        return group;
    }
    
    
    protected boolean isFatalProblem(){
        return problem != null && problem.isFatal();
    }
    
    protected Problem getProblem(){
        return problem;
    }
            
    private class VUI implements ValidationUI {

        @Override
        public void showProblem(Problem problem) {
            Problem old = ValidationPanel.this.problem;
            ValidationPanel.this.problem = problem;
            if(old != null && !old.equals(problem)){
                onShowProblem(problem);
            }
        }

        @Override
        public void clearProblem() {
            ValidationPanel.this.problem = null;
            onClearProblem();
        }
    
    }
}
