/* Sonia - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package pitaru.sonia_v2_9;
import com.softsynth.jsyn.AudioDevice;
import com.softsynth.jsyn.Synth;
import com.softsynth.jsyn.SynthException;

import processing.core.PApplet;

public class Sonia
{
    static final boolean DIRECT = true;
    static final int LEFT = 0;
    static final int RIGHT = 1;
    static final boolean NO_MIC = false;
    static final boolean MIC = true;
    static float valueMod = 32767.0F;
    static boolean directData = false;
    static PApplet host;
    static int inDevID;
    static int outDevID;
    static int inDevChNum;
    static int outDevChNum;
    public static final int AVAILABLE = 0;
    public static final int CLASSES_MISSING = -1;
    public static final int NATIVE_LIBRARY_MISSING = -2;
    public static final int OBSOLETE = -3;
    
    public static void start(PApplet papplet, int i, int i_0_) {
	host = papplet;
	initJsyn(i, i_0_);
    }
    
    public static void start(PApplet papplet) {
	host = papplet;
	initJsyn(44100, 0);
    }
    
    public static void start(PApplet papplet, int i) {
	host = papplet;
	initJsyn(i, 0);
    }
    
    public static void initJsyn(int i, int i_1_) {
	if (Synth.openCount == 1) {
	    try {
		Synth.stopEngine();
		for (int i_2_ = 0; i_2_ < 15; i_2_++) {
		    /* empty */
		}
		System.out.println("ENGINE STOPPED count:" + Synth.openCount);
	    } catch (SynthException synthexception) {
		throw new RuntimeException(synthexception);
	    }
	}
	try {
	    Synth.startEngine(i_1_, (double) i);
	    Synth.setTrace(0);
	    inDevID = AudioDevice.getDefaultInputDeviceID();
	    outDevID = AudioDevice.getDefaultOutputDeviceID();
	    inDevChNum = AudioDevice.getMaxInputChannels(inDevID);
	    outDevChNum = AudioDevice.getMaxOutputChannels(outDevID);
	    System.out.println("Input Device #" + inDevID + ": "
			       + AudioDevice.getName(inDevID) + " has "
			       + inDevChNum + " channels");
	    System.out.println("Input Device #" + outDevID + ": "
			       + AudioDevice.getName(outDevID) + " has "
			       + outDevChNum + " channels");
	} catch (SynthException synthexception) {
	    throw new RuntimeException(synthexception);
	}
    }
    
    public static void setValueRange(boolean bool) {
	directData = true;
    }
    
    public static void setValueRange(int i) {
	directData = false;
	valueMod = (float) (32767.0 / (double) i);
    }
    
    public static void stop() {
	if (LiveInput.state == 1)
	    LiveInput.stop();
	if (LiveOutput.state == 1)
	    LiveOutput.stop();
	if (BJSyn.count > 0)
	    BJSyn.stop();
	Synth.stopEngine();
    }
    
    public void dispose() {
	if (LiveInput.state == 1)
	    LiveInput.stop();
	if (LiveOutput.state == 1)
	    LiveOutput.stop();
	if (BJSyn.count > 0)
	    BJSyn.stop();
	Synth.stopEngine();
    }
    
    
    public static void setMaxSamples(int i) {
	BJSyn.maxSamples = i;
    }
    
    public static int getMaxSamples() {
	return BJSyn.maxSamples;
    }
}
