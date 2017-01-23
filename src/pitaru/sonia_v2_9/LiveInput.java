/* LiveInput - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package pitaru.sonia_v2_9;

import com.softsynth.jsyn.BusReader;
import com.softsynth.jsyn.BusWriter;
import com.softsynth.jsyn.ChannelIn;
import com.softsynth.jsyn.PeakFollower;
import com.softsynth.jsyn.SampleWriter;
import com.softsynth.jsyn.SampleWriter_16F1;
import com.softsynth.jsyn.Synth;
import com.softsynth.jsyn.SynthSample;

public class LiveInput
{
  static ChannelIn[] chIn = new ChannelIn[Sonia.inDevChNum];
  static SynthSample sample;
  static SampleWriter sampleWriter;
  static BusReader myBusReader;
  static int maxSamples = BJSyn.maxSamples;
  static BusWriter[] myBusWriter = new BusWriter[4 + maxSamples];
  static PeakFollower followerL;
  static PeakFollower followerR;
  static SampleWriter[] recorder = new SampleWriter[maxSamples];
  static FFTutils _FFTutils;
  public static float[] spectrum;
  static short[] _signal;
  public static float[] signal;
  static boolean doOnce;
  public static boolean micFlag = true;
  static int state = 0;
  public static int startframe = 0;

  public static void start(int i, int i_0_)
  {
    if (micFlag)
    {
      if (Synth.openCount == 1)
      {
        Synth.stop();
        for (int i_1_ = 0; i_1_ < 5; i_1_++)
        {
          /* empty */
        }
        Synth.start(0, (double) i, Sonia.inDevID, Sonia.inDevChNum, Sonia.outDevID, Sonia.outDevChNum);
        BJSyn.startEngine();
      }
      else if (Synth.openCount == 0)
        Sonia.initJsyn(i, 32);
    }
    doOnce = true;
    state = 1;
    _FFTutils = new FFTutils(i_0_ * 2);
    signal = new float[i_0_ * 2];
    _signal = new short[i_0_ * 2];
    sample = new SynthSample(i_0_ * 2 + 1);
    sampleWriter = new SampleWriter_16F1();
    myBusReader = new BusReader();
    myBusWriter[0 + maxSamples] = new BusWriter();
    myBusWriter[0 + maxSamples].start();
    myBusWriter[0 + maxSamples].busOutput.connect(myBusReader.busInput);
    chIn[0] = new ChannelIn(0);
    chIn[0].output.connect(myBusWriter[maxSamples].input);
    chIn[0].start();
    myBusReader.output.connect(sampleWriter.input);
    followerL = new PeakFollower();
    followerR = new PeakFollower();
    chIn[0].output.connect(followerL.input);
    myBusReader.start();
    followerL.start();
    followerR.start();
    sampleWriter.start();
    sampleWriter.samplePort.queueLoop(sample);
  }

  public static void start()
  {
    start(1024);
  }

  public static void start(int i)
  {
    if (Synth.openCount >= 1)
      start((int) Synth.getFrameRate(), i);
    else if (Synth.openCount == 0)
      start(44100, i);
  }

  public static void start(boolean bool)
  {
    micFlag = bool;
  }

  public static void start(int i, boolean bool)
  {
    micFlag = bool;
    start(i);
  }

  public static void start(boolean bool, int i)
  {
    micFlag = bool;
    start(i);
  }

  public static void stop()
  {
    state = 0;
    sampleWriter.stop();
    sampleWriter.delete();
    sampleWriter = null;
    sample.delete();
    sampleWriter = null;
    followerR.stop();
    followerR.delete();
    followerR = null;
    followerL.stop();
    followerL.delete();
    followerL = null;
  }

  public static float[] getSignal()
  {
    sample.read(0, _signal, 0, _signal.length);
    int i = sampleWriter.samplePort.getNumFramesMoved();
    startframe = i % _signal.length;
    for (int i_2_ = 0; i_2_ < _signal.length; i_2_++)
      signal[i_2_] = (float) _signal[(i_2_ + startframe) % _signal.length];
    spectrum = new float[_signal.length / 2];
    return signal;
  }

  public static float[] getSpectrum()
  {
    spectrum = _FFTutils.computeFFT(getSignal());
    return spectrum;
  }

  public static float[] getSpectrum(boolean bool)
  {
    useEqualizer(bool);
    useEnvelope(bool);
    return getSpectrum();
  }

  public static float[] getSpectrum(boolean bool, float f)
  {
    useEqualizer(bool);
    useEnvelope(bool, f);
    return getSpectrum();
  }

  public static void useEqualizer(boolean bool)
  {
    if (_FFTutils != null)
      _FFTutils.useEqualizer(bool);
  }

  public static void useEnvelope(boolean bool, float f)
  {
    if (bool)
      _FFTutils.useEnvelope(true, f);
    else
      _FFTutils.useEnvelope(false, f);
  }

  public static void useEnvelope(boolean bool)
  {
    useEnvelope(bool, 1.5F);
  }

  public static float getLevel()
  {
    if (followerL == null || followerR==null)
      return 0;
    return (float) ((followerL.output.get() + followerR.output.get()) / 2.0);
  }

  public static float getLevelLeft()
  {
    return (float) followerL.output.get();
  }

  public static float getLevelRight()
  {
    return (float) followerR.output.get();
  }

  public static float getLevel(int i)
  {
    if (i == 0)
      return (float) followerL.output.get();
    if (i == 1)
      return (float) followerR.output.get();
    return 0.0F;
  }

  public static void prepareRecorder(Sample sample, int i)
  {
    recorder[i] = new SampleWriter_16F1();
    // System.out.println("out: "+myBusReader);
    myBusReader.output.connect(recorder[i].input);
  }

  public static void startRec(Sample sample)
  {
    int i = sample.getID();
    for (int i_3_ = 0; i_3_ < sample.getNumChannels(); i_3_++)
    {
      prepareRecorder(sample, i + i_3_);
      recorder[i + i_3_].start();
      recorder[i + i_3_].samplePort.queue(BJSyn.mySamp[i + i_3_]);
    }
  }

  public static void startRec(Sample sample, int i, int i_4_)
  {
    int i_5_ = sample.getID();
    for (int i_6_ = 0; i_6_ < sample.getNumChannels(); i_6_++)
    {
      prepareRecorder(sample, i_5_ + i_6_);
      recorder[i_5_ + i_6_].start();
      recorder[i_5_ + i_6_].samplePort.queue(BJSyn.mySamp[i_5_ + i_6_], i, i_4_ - i);
    }
  }

  public static void startRecLoop(Sample sample)
  {
    int i = sample.getID();
    for (int i_7_ = 0; i_7_ < sample.getNumChannels(); i_7_++)
    {
      prepareRecorder(sample, i + i_7_);
      recorder[i + i_7_].start();
      recorder[i + i_7_].samplePort.queueLoop(BJSyn.mySamp[i + i_7_]);
    }
  }

  public static void startRecLoop(Sample sample, int i)
  {
    int i_8_ = sample.getID();
    for (int i_9_ = 0; i_9_ < sample.getNumChannels(); i_9_++)
    {
      prepareRecorder(sample, i_8_ + i_9_);
      recorder[i_8_ + i_9_].start();
      recorder[i_8_ + i_9_].samplePort.queue(BJSyn.mySamp[i_8_ + i_9_], i, BJSyn.mySamp[i_8_
          + i_9_].getNumFrames()
          - i);
      recorder[i_8_ + i_9_].samplePort.queueLoop(BJSyn.mySamp[i_8_ + i_9_]);
    }
  }

  public static void startRecLoop(Sample sample, int i, int i_10_)
  {
    int i_11_ = sample.getID();
    for (int i_12_ = 0; i_12_ < sample.getNumChannels(); i_12_++)
    {
      prepareRecorder(sample, i_11_ + i_12_);
      recorder[i_11_ + i_12_].start();
      recorder[i_11_ + i_12_].samplePort.queueLoop(BJSyn.mySamp[i_11_ + i_12_], i, i_10_
          - i);
    }
  }

  public static void stopRec(Sample sample)
  {
    int i = sample.getID();
    for (int i_13_ = 0; i_13_ < sample.getNumChannels(); i_13_++)
    {
      recorder[i + i_13_].samplePort.queueOff(BJSyn.mySamp[i + i_13_]);
      recorder[i + i_13_].samplePort.clear();
      recorder[i + i_13_].stop();
    }
  }
}
