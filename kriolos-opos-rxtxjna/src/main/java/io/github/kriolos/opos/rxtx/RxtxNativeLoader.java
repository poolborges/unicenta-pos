/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.kriolos.opos.rxtx;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 *
 * @author bellah
 */
public class RxtxNativeLoader {
    
    public static boolean LOADED = false;
    
    public static void load() {
        if (!LOADED) {
            
            Native.load("rxtxSerial", Library.class);
            Native.load("rxtxParallel", Library.class);
            System.out.println("===== LOADED: rxtxSerial, rxtxParallel  =====");
            LOADED = true;
        }
    }
    
    public static void main(String[] args) {
        
        //java -Djna.debug_load=true -Djna.debug_load.jna=true
        System.setProperty("jna.encoding", "UTF-8");
        System.setProperty("jna.debug_load", "true");
        System.setProperty("jna.debug_load.jna", "true");
        RxtxNativeLoader.load();
    }
}
