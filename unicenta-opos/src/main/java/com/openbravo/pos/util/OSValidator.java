/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.util;

/**
 *
 * @author JG uniCenta
 */
public class OSValidator {
   
    private String OS = System.getProperty("os.name").toLowerCase();

    /**
     *
     */
    public OSValidator() {
            
        }
        
    /**
     *
     * @return
     */
    public String getOS(){
      if (isWindows()) {
                    return("w");
		} else if (isMac()) {
                    return("m");
		} else if (isUnix()) {
                    return("l");
		} else if (isSolaris()) {
                    return("s");
		} else {
                    return("x");
		}
    }

    /**
     *
     * @return
     */
    public boolean isWindows() {
		return (OS.contains("win"));
	}

    /**
     *
     * @return
     */
    public  boolean isMac() {
		return (OS.contains("mac")); 
	}

    /**
     *
     * @return
     */
    public  boolean isUnix() {
		return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0 );
	}

    /**
     *
     * @return
     */
    public  boolean isSolaris() {
		return (OS.contains("sunos")); 
	}

        
        
        
        
}
