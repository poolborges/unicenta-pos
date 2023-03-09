/*
FreeTTS is a speech synthesis engine written entirely in the
Java(tm) programming language. FreeTTS was written by the Sun Microsystems Laboratories Speech Team and is based on CMU's
Flite engine. FreeTTS also includes a partial JSAPI 1.0
 */
package io.github.kriolos.krpos.sample;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

/**
 *
 * @author poolb
 */
public class TestToSpeech {
    public static void main(String[] args) {
        String text = "Ola Mundo"; //txttext.getText();
        
        Voice vc;
        
        VoiceManager vm = VoiceManager.getInstance();
        vc = vm.getVoice("kevin16");
        vc.allocate();
        vc.speak(text);
    }
}
