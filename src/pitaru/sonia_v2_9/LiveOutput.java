/* LiveOutput - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package pitaru.sonia_v2_9;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.softsynth.jsyn.BusWriter;
import com.softsynth.jsyn.LineOut;
import com.softsynth.jsyn.SampleReader_16F1;
import com.softsynth.jsyn.Synth;
import com.softsynth.jsyn.SynthException;
import com.softsynth.jsyn.util.SampleQueueOutputStream;

public class LiveOutput implements Runnable {
  public static SampleReader_16F1 mySampler;
  public static LineOut myOut;
  public static int FRAMES_PER_BLOCK;
  public static int FRAMES_IN_BUFFER;
  public static int NUM_CHANNELS = 1;
  public static int SAMPLES_PER_BLOCK;
  public static short[] directData;
  public static float[] data;
  public static SampleQueueOutputStream outStream;
  public static Thread t1;
  public static int SLEEP = 5;
  public static int state = 0;

  public static void setSleep(int i) {
    SLEEP = i;
  }

  public static void start(int i) {
    start(i, i);
  }

  public static void start(int i, int i_0_) {
    try {
      FRAMES_PER_BLOCK = i;
      FRAMES_IN_BUFFER = i_0_;
      SAMPLES_PER_BLOCK = FRAMES_PER_BLOCK * NUM_CHANNELS;
      directData = new short[SAMPLES_PER_BLOCK];
      data = new float[SAMPLES_PER_BLOCK];
      if (NUM_CHANNELS == 1)
        mySampler = new SampleReader_16F1();
      else if (NUM_CHANNELS != 2)
        throw new RuntimeException("This example only support mono or stereo!");
      myOut = new LineOut();
      mySampler.amplitude.set(1.0);
      outStream = new SampleQueueOutputStream(mySampler.samplePort,
          FRAMES_IN_BUFFER, NUM_CHANNELS);
      mySampler.output.connect(0, myOut.input, 0);
      mySampler.output.connect(NUM_CHANNELS - 1, myOut.input, 1);
      myOut.start();
      mySampler.start();
    } catch (SynthException synthexception) {
      throw new RuntimeException(synthexception);
    }
  }

  public void run() {
    try {
      while (t1 != null) {
        while (outStream.available() >= FRAMES_PER_BLOCK) {
          fireEvent();
          if (!Sonia.directData)
            convertData();
          sendBuffer();
        }
        if (t1 != null) {
          /* empty */
        }
        Thread.sleep((long) SLEEP);
      }
    } catch (SynthException synthexception) {
      System.out.println("run() caught " + synthexception);
    } catch (InterruptedException interruptedexception) {
      System.out.println("run() caught " + interruptedexception);
    }
  }

  public static void sendBuffer() {
    outStream.write(directData, 0, FRAMES_PER_BLOCK);
  }

  public static void convertData() {
    for (int i = 0; i < data.length; i++)
      directData[i] = (short) (int) (data[i] * Sonia.valueMod);
  }

  public static void stop() {
    try {
      stopStream();
      t1 = null;
      mySampler.delete();
      mySampler = null;
      myOut.delete();
      myOut = null;
    } catch (SynthException synthexception) {
      throw new RuntimeException(synthexception);
    }
  }

  public static void startStream() {
    state = 1;
    while (outStream.available() > FRAMES_PER_BLOCK) {
      fireEvent();
      if (!Sonia.directData)
        convertData();
      sendBuffer();
    }
    int i = Synth.getTickCount() + 4;
    mySampler.start(i);
    outStream.start(i);
    LiveOutput liveoutput = new LiveOutput();
    t1 = new Thread(liveoutput);
    t1.start();
  }

  public static void stopStream() {
    state = 0;
    t1 = null;
    int i = Synth.getTickCount();
    mySampler.stop(i);
    outStream.stop(i);
  }

  public static void fireEvent() {
    try {
      Method method = Sonia.host.getClass().getDeclaredMethod(
          "liveOutputEvent", null);
      try {
        method.invoke(Sonia.host, null);
      } catch (InvocationTargetException invocationtargetexception) {
        /* empty */
      } catch (IllegalAccessException illegalaccessexception) {
        /* empty */
      }
    } catch (NoSuchMethodException nosuchmethodexception) {
      /* empty */
    }
  }

  public static void connectLiveInput(boolean bool) {
    int i = LiveInput.maxSamples + 2;
    if (bool) {
      LiveInput.myBusWriter[i] = new BusWriter();
      LiveInput.myBusWriter[i].start();
      mySampler.output.connect(LiveInput.myBusWriter[i].input);
      LiveInput.myBusWriter[i].busOutput
          .connect(LiveInput.myBusReader.busInput);
    } else {
      LiveInput.myBusWriter[i].stop();
      LiveInput.myBusWriter[i].input.disconnect();
      LiveInput.myBusWriter[i].busOutput.disconnect();
    }
  }
}
