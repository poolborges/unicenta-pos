//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.reports;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.*;
import com.openbravo.data.user.EditorCreator;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.BeanFactoryException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author adrianromero
 */
public class PanelReportBean extends JPanelReport {
    
    private String title;
    private String report;
    
    private String resourcebundle = null;
    
    private String sentence;

    
// JG 16 May 12 use diamond inference
    private List<Datas> fielddatas = new ArrayList<>();
    private List<String> fieldnames = new ArrayList<>();
    
    private List<String> paramnames = new ArrayList<>();
    
    private JParamsComposed qbffilter = new JParamsComposed();
    
    /**
     *
     * @param app
     * @throws BeanFactoryException
     */
    @Override
    public void init(AppView app) throws BeanFactoryException {        
        
        qbffilter.init(app);       
        super.init(app);
    }
    
    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {
        
        qbffilter.activate();
        super.activate();
        
        if (qbffilter.isEmpty()) {
            setVisibleFilter(false);
            setVisibleButtonFilter(false);
        }
    }
    
    /**
     *
     * @return
     */
    @Override
    protected EditorCreator getEditorCreator() {
        
        return qbffilter;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     *
     * @param titlekey
     */
    public void setTitleKey(String titlekey) {
        title = AppLocal.getIntString(titlekey);
    }
   
    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param report
     */
    public void setReport(String report) {
        this.report = report;
    }
    
    /**
     *
     * @return
     */
    protected String getReport() {
        return report;
    }

    /**
     *
     * @param resourcebundle
     */
    public void setResourceBundle(String resourcebundle) {
        this.resourcebundle = resourcebundle;
    }
    
    /**
     *
     * @return
     */
    protected String getResourceBundle() {
        return resourcebundle == null 
                ? report 
                : resourcebundle;
    }

    /**
     *
     * @param sentence
     */
    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
    
    /**
     *
     * @param name
     * @param data
     */
    public void addField(String name, Datas data) {
        fieldnames.add(name);
        fielddatas.add(data);
    }
    
    /**
     *
     * @param name
     */
    public void addParameter(String name) {
        paramnames.add(name);        
    }
    
    /**
     *
     * @return
     */
    protected BaseSentence getSentence() {
        return new StaticSentence(m_App.getSession()
            , new QBFBuilder(sentence, paramnames.toArray(new String[paramnames.size()]))
            , qbffilter.getSerializerWrite()
            , new SerializerReadBasic(fielddatas.toArray(new Datas[fielddatas.size()])));
    }

    /**
     *
     * @return
     */
    protected ReportFields getReportFields() {
        return new ReportFieldsArray(fieldnames.toArray(new String[fieldnames.size()]));
    }

    /**
     *
     * @param qbff
     */
    public void addQBFFilter(ReportEditorCreator qbff) {
        qbffilter.addEditor(qbff);
    }    
}
