//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.beans;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 * @author adrian
 */
public class LocaleResources {
  
    private List<ResourceBundle> m_resources;
    private ClassLoader m_localeloader;
    
    /** Creates a new instance of LocaleResources */
    public LocaleResources() {
//        m_resources = new LinkedList<ResourceBundle>();
        m_resources = new LinkedList<>();        
        
        File fuserdir = new File(System.getProperty("user.dir"));
        File fresources = new File(fuserdir, "locales");
        try {
            m_localeloader = URLClassLoader.newInstance(
                    new URL[] { fresources.toURI().toURL() },
                    Thread.currentThread().getContextClassLoader());
        } catch (MalformedURLException e) {
            m_localeloader = Thread.currentThread().getContextClassLoader();
        }        
    }
    
    public ResourceBundle getBundle(String bundlename) {
        return ResourceBundle.getBundle(bundlename, Locale.getDefault(), m_localeloader);
    }
    
    /**
     *
     * @param bundlename
     */
        
    public void addBundleName(String bundlename) {
//        m_resources.add(getBundle(bundlename));
        m_resources.add(ResourceBundle.getBundle(bundlename));
    }

    /**
     *
     * @param sKey
     * @return
     */
    public String getString(String sKey) {
        
        if (sKey == null) {
            return null;
        } else  {            
            for (ResourceBundle r : m_resources) {
                try {
                    return r.getString(sKey);
                } catch (MissingResourceException e) {
                    // Next
                }
            }
            
            // MissingResourceException in all ResourceBundle
            return "** " + sKey + " **";
        }
    }

    /**
     *
     * @param sKey
     * @param sValues
     * @return
     */
    public String getString(String sKey, Object ... sValues) {
        
        if (sKey == null) {
            return null;
        } else  {
            for (ResourceBundle r : m_resources) {
                try {
                    return MessageFormat.format(r.getString(sKey), sValues);
                } catch (MissingResourceException e) {
                    // Next
                }
            }
            
            // MissingResourceException in all ResourceBundle
            StringBuilder sreturn = new StringBuilder();
            sreturn.append("** ");
            sreturn.append(sKey);            
            for (Object value : sValues) {
                sreturn.append(" < ");
                sreturn.append(value.toString());
            }
            sreturn.append("** ");
            
            return sreturn.toString();
        }
    }
}
