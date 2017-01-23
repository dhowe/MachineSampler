/* BJSyn - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package pitaru.sonia_v2_9;

import java.io.IOException;
import java.io.InputStream;

import com.softsynth.jsyn.AddUnit;
import com.softsynth.jsyn.LineOut;
import com.softsynth.jsyn.LinearLag;
import com.softsynth.jsyn.MultiplyUnit;
import com.softsynth.jsyn.PanUnit;
import com.softsynth.jsyn.SampleReader_16V1;
import com.softsynth.jsyn.Synth;
import com.softsynth.jsyn.SynthAlert;
import com.softsynth.jsyn.SynthException;
import com.softsynth.jsyn.SynthSample;
import com.softsynth.jsyn.SynthSampleAIFF;
import com.softsynth.jsyn.SynthSampleWAV;

import processing.core.PApplet;

public class BJSyn
{
  public static PApplet parent;
  public static int maxSamples = 1000;
  public static SynthSample[] mySamp = new SynthSample[maxSamples];
  public static SampleReader_16V1[] mySampler = new SampleReader_16V1[maxSamples];
  public static LineOut[] myOut = new LineOut[maxSamples];
  public static PanUnit[] myPan = new PanUnit[maxSamples];
  public static double basePitch;
  public static boolean ifApplication = false;
  public static InputStream stream;
  public static final int NUM_FRAMES = 64;
  public static int count = 0;
  public static LinearLag[] myLinearLag = new LinearLag[maxSamples];
  public static LinearLag[] myLinearLag2 = new LinearLag[maxSamples];
  public static LinearLag[] myLinearLag3 = new LinearLag[maxSamples];
  public static int sampleNum = 0;
  public static int channelNum;
  public static MultiplyUnit[] multiplier = new MultiplyUnit[maxSamples];

  public static int getChannels(int i)
  {
    return mySamp[i].getChannelsPerFrame();
  }

  public static void loopSample(int i, int start, int mid)
  {
    int stop = mid - start;
    try
    {
      startCircuit(i);
      int i_2_ = Synth.getTickCount() + 1;
      mySampler[i].samplePort.clear(i_2_);
      myLinearLag2[i].time.set(i_2_, 0.0010);
      myLinearLag2[i].input.set(i_2_, 1.0);

      mySampler[i].samplePort.queueLoop(i_2_, mySamp[i], start, stop);
    }
    catch (SynthException synthexception)
    {if (Sample.THROW_JSYN_ERRORS)
      throw new RuntimeException("VALUES: start=" + start + ", stop=" + stop + ", mid="
          + mid + "\n" + synthexception);
    }
  }

  public static void loopSample(int i)
  {
    loopSample(i, 0, mySamp[i].getNumFrames());
  }

  public static void playSample(int i)
  {
    playSample(i, 0, mySamp[i].getNumFrames());
  }

  public static void playSample(int i, int i_3_, int i_4_)
  {
    try
    {
      startCircuit(i);
      int i_5_ = Synth.getTickCount() + 1;
      mySampler[i].samplePort.clear(i_5_);
      myLinearLag2[i].time.set(i_5_, 0.0010);
      myLinearLag2[i].input.set(i_5_, 1.0);
      mySampler[i].samplePort.queue(i_5_, mySamp[i], i_3_, i_4_ - i_3_);
    }
    catch (SynthException synthexception)
    {if (Sample.THROW_JSYN_ERRORS)
      throw new RuntimeException(synthexception);
    }
  }

  public static void loopSampleNum(int i, int i_6_, int i_7_, int i_8_)
  {
    try
    {
      startCircuit(i_6_);
      int i_9_ = Synth.getTickCount() + 1;
      mySampler[i_6_].samplePort.clear(i_9_);
      myLinearLag2[i_6_].time.set(i_9_, 0.0010);
      myLinearLag2[i_6_].input.set(i_9_, 1.0);
      for (int i_10_ = 0; i_10_ < i; i_10_++)
        mySampler[i_6_].samplePort.queue(i_9_, mySamp[i_6_], i_7_, i_8_ - i_7_);
    }
    catch (SynthException synthexception)
    {      if (Sample.THROW_JSYN_ERRORS)
      throw new RuntimeException(synthexception);
    }
  }

  public static void stopSample(int i, boolean bool, int i_11_)
  {
    try
    {
      if (bool)
        stopCircuit(i, i_11_);
      myLinearLag2[i].time.set(0.0050);
      myLinearLag2[i].input.set(0.0);
    }
    catch (SynthException synthexception)
    {
      if (Sample.THROW_JSYN_ERRORS)
        throw new RuntimeException(synthexception);
    }
  }

  public static void startCircuit(int i)
  {
    try
    {
      myPan[i].start();
      myOut[i].start();
      mySampler[i].start();
      multiplier[i].start();
      myLinearLag[i].start();
      myLinearLag2[i].start();
      myLinearLag3[i].start();
    }
    catch (SynthException synthexception)
    {
      if (Sample.THROW_JSYN_ERRORS)
      throw new RuntimeException(synthexception);
    }
  }

  public static void stopCircuit(int i, int i_12_)
  {
    try
    {
      int i_13_ = Synth.getTickCount() + i_12_;
      myPan[i].stop(i_13_);
      myOut[i].stop(i_13_);
      mySampler[i].stop(i_13_);
      multiplier[i].stop(i_13_);
      myLinearLag[i].stop(i_13_);
      myLinearLag2[i].stop(i_13_);
      myLinearLag3[i].stop(i_13_);
    }
    catch (SynthException synthexception)
    {if (Sample.THROW_JSYN_ERRORS)
      throw new RuntimeException(synthexception);
    }
  }

  public static void setRate(int i, float f)
  {
    mySampler[i].rate.set((double) f);
  }

  public static double getRate(int i)
  {
    return mySampler[i].rate.get();
  }

  public static void setVolume(int i, float f)
  {
    if (myLinearLag[i] == null)  return;
    myLinearLag[i].input.set((double) f);
    myLinearLag[i].time.set(0.03);
  }

  public static void setVolume(int i, double d)
  {
    setVolume(i, (float) d);
  }

  public static double getVolume(int i)
  {
    if (myLinearLag[i] == null)  return 0;
    return myLinearLag[i].input.get();
  }

  public static void setPan(int i, float f)
  {
    myLinearLag3[i].time.set(0.03);
    myLinearLag3[i].input.set((double) f);
  }

  public static double getPan(int i)
  {
    if (myLinearLag[i] == null)  return 0;
    return myLinearLag3[i].input.get();
  }

  public static int getNumFrames(int i)
  {
    return mySamp[i].getNumFrames();
  }

  public static void buildSamp(int i, String string)
  {
    switch (SynthSample.getFileType(string))
    {
      case 1:
        mySamp[i] = new SynthSampleAIFF();
        break;
      case 2:
        mySamp[i] = new SynthSampleWAV();
        break;
      default:
        System.err.println("Sonia SAYS: Sample must be a 'Wav' or 'Aiff' file format.");
    }
  }

  public static void buildEmptySamp(int i, int i_14_, int i_15_)
  {
    mySamp[i] = new SynthSample(i_14_);
    mySamp[i].setSampleRate((double) i_15_);
  }

  public static void loadSample(int i, String string)
  {
    //System.out.println("BJSyn.loadSample("+string+")");
    try
    {
      InputStream inputstream = Sonia.host.openStream(string);
      buildSamp(i, string);
      if (mySamp[i] != null)
        mySamp[i].load(inputstream);
    }
    catch (NullPointerException nullpointerexception)
    {
      System.err.println("Sonia: Please make sure you have entered the correct Sample file name to load");
    }
    catch (Exception e)
    {
      System.err.println("FILE: "+string);
      throw new RuntimeException(e);
    }

    if (mySamp[i].getChannelsPerFrame() == 1)
    {
      buildCircuit(i);
      channelNum = 1;
    }
    else if (mySamp[i].getChannelsPerFrame() == 2)
    {
      channelNum = 2;
      int i_16_ = mySamp[i].getNumFrames() * mySamp[i].getChannelsPerFrame();
      short[] is = new short[i_16_];
      short[] is_17_ = new short[i_16_ / 2];
      short[] is_18_ = new short[i_16_ / 2];
      mySamp[i].read(is);
      int i_19_ = 0;
      int i_20_ = 0;
      int i_21_ = 0;
      while (i_21_ < i_16_)
      {
        is_17_[i_19_++] = is[i_21_++];
        is_18_[i_20_++] = is[i_21_++];
      }
      buildSamp(i + 1, string);
      mySamp[i].clear(0, i_16_ / 2);
      mySamp[i].allocate(i_16_ / 2, 1);
      mySamp[i].write(0, is_17_, 0, i_16_ / 2);
      buildCircuit(i);
      setPan(i, -1.0F);
      mySamp[i + 1].allocate(i_16_ / 2, 1);
      mySamp[i + 1].write(0, is_18_, 0, i_16_ / 2);
      buildCircuit(i + 1);
      setPan(i + 1, 1.0F);
    }
  }

  public static void loadSample(SynthSample synthsample, InputStream inputstream) throws IOException
  {
    synthsample.load(inputstream);
  }

  //public static AddUnit mixer;
  
/*  public static void buildCircuitWithMixer(int i)
  {
    mySampler[i] = new SampleReader_16V1();
    myOut[i] = new LineOut();
    myPan[i] = new PanUnit();
    multiplier[i] = new MultiplyUnit();
    myLinearLag[i] = new LinearLag();
    myLinearLag2[i] = new LinearLag();
    myLinearLag3[i] = new LinearLag();
    myLinearLag3[i].output.connect(myPan[i].pan);
    mySampler[i].output.connect(myPan[i].input);
    myPan[i].output.connect(mixer.inputA);
    myPan[i].output.connect(mixer.inputB);
    myLinearLag[i].output.connect(multiplier[i].inputB);
    myLinearLag2[i].output.connect(multiplier[i].inputA);
    multiplier[i].output.connect(mySampler[i].amplitude);
    
    mixer.output.connect( 0, myOut[i].input, 0 );
    mixer.output.connect( 0, myOut[i].input, 1 );
    
    startCircuit(i);
  }*/
  
  public static void buildCircuit(int i)
  {
    mySampler[i] = new SampleReader_16V1();
    myOut[i] = new LineOut();
    myPan[i] = new PanUnit();
    multiplier[i] = new MultiplyUnit();
    myLinearLag[i] = new LinearLag();
    myLinearLag2[i] = new LinearLag();
    myLinearLag3[i] = new LinearLag();
    myLinearLag3[i].output.connect(myPan[i].pan);
    mySampler[i].output.connect(myPan[i].input);
    myPan[i].output.connect(0, myOut[i].input, 0);
    myPan[i].output.connect(1, myOut[i].input, 1);
    myLinearLag[i].output.connect(multiplier[i].inputB);
    myLinearLag2[i].output.connect(multiplier[i].inputA);
    multiplier[i].output.connect(mySampler[i].amplitude);
    startCircuit(i);
  }

  public static void deleteCircuit(int i)
  {
    mySampler[i].delete();
    mySampler[i] = null;
    myOut[i].delete();
    myOut[i] = null;
    myPan[i].delete();
    myPan[i] = null;
    mySamp[i].delete();
    mySamp[i] = null;
    multiplier[i].delete();
    multiplier[i] = null;
    myLinearLag[i].delete();
    myLinearLag[i] = null;
    myLinearLag2[i].delete();
    myLinearLag2[i] = null;
    myLinearLag3[i].delete();
    myLinearLag3[i] = null;
  }

  public static void startEngine()
  {
    for (int i = 0; i < count; i++)
      startCircuit(i);
/*    if (mixer == null) 
      mixer = new AddUnit();
    mixer.start();*/
  }

  public static void stopEngine()
  {
    for (int i = 0; i < count; i++)
    {
      stopCircuit(i, 0);
      deleteCircuit(i);
    }
   // mixer.delete();
  }

  public static void stop()
  {
    try
    {
      stopEngine();
      count = 0;
      Synth.setTrace(0);
    }
    catch (SynthException synthexception)
    {
      System.err.println(synthexception);
    }
  }
}
