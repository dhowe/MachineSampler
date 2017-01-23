/* Sample - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package pitaru.sonia_v2_9;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.softsynth.jsyn.BusWriter;
import com.softsynth.jsyn.Synth;
import com.softsynth.jsyn.SynthException;
import com.softsynth.jsyn.util.WAVFileWriter;

public class Sample
{
  public static boolean THROW_JSYN_ERRORS = true;

  public static List instances = new ArrayList();

  public int id = 0;
  public int channels;
  public int startF;
  public int endF;
  public int state;
  public int movedF_Buff;
  public float rate;
  public int framesWhenDone;
  public static FFTutils fft = new FFTutils(1024);
  public float[] spectrum;
  int lastFrame = 0;

  public Sample(String fileName)
  {
    BJSyn.loadSample(BJSyn.count, fileName);
    id = BJSyn.count;
    channels = BJSyn.channelNum;
    rate = (float) BJSyn.mySamp[id].getSampleRate();
    state = 0;
    startF = 0;
    endF = 0;
    movedF_Buff = 0;
    setVolume(1.0F);
    setRate(rate);
    BJSyn.count += channels;
    if (BJSyn.count == BJSyn.maxSamples)
      BJSyn.count = 0;
    instances.add(this);
  }

  public Sample(int numFrames, int sampleRate)
  {
    this(numFrames, sampleRate, 1);
  }

  public Sample(int numFrames)
  {
    this(numFrames, (int) Synth.getFrameRate(), 1);
  }

  private Sample(int numFrames, int synthFrameRate, int numChannels)
  {
    // public void initEmptySample() {
    rate = (float) synthFrameRate;
    id = BJSyn.count;
    channels = Math.max(Math.min(numChannels, 2), 1);
    for (int i_6_ = 0; i_6_ < channels; i_6_++)
    {
      BJSyn.buildEmptySamp(BJSyn.count, numFrames, synthFrameRate);
      BJSyn.buildCircuit(BJSyn.count);
      BJSyn.count++;
      if (BJSyn.count == BJSyn.maxSamples)
        BJSyn.count = 0;
      setVolume(1.0F, i_6_);
      setRate(rate, i_6_);
      if (channels > 1)
      {
        if (i_6_ == 0)
          setPan(-1.0F, i_6_);
        else
          setPan(1.0F, i_6_);
      }
    }
    state = 0;
    startF = 0;
    endF = 0;
    movedF_Buff = 0;
    instances.add(this);
  }

  public int getID()
  {
    return id;
  }

  public int getNumChannels()
  {
    return channels;
  }

  public float[] getSpectrum(int i, int i_7_)
  {
    int i_8_ = 0;
    if (fft.WS2 != i * 2)
      fft = new FFTutils(i * 2);
    fft.useEnvelope(true, 1.5F);
    fft.useEqualizer(true);
    short[] is = new short[i * 2];
    float[] fs = new float[i * 2];
    if (BJSyn.mySamp[id + i_8_].getNumFrames() - i * 2 >= i_7_)
    {
      BJSyn.mySamp[id + i_8_].read(i_7_, is, 0, i * 2);
      convertData(is, fs);
      spectrum = new float[i / 2];
      spectrum = fft.computeFFT(fs);
    }
    return spectrum;
  }

  public float[] getSpectrum(int i)
  {
    return getSpectrum(i, getCurrentFrame());
  }

  public void setVolume(float f)
  {
    BJSyn.setVolume(id, f);
    if (channels == 2)
      BJSyn.setVolume(id + 1, f);
  }

  public void setVolume(float f, int i)
  {
    if (i < channels && i >= 0)
      BJSyn.setVolume(id + i, f);
  }

  public float getVolume()
  {
    return (float) BJSyn.getVolume(id);
  }

  public float getVolume(int i)
  {
    if (i < channels && i >= 0)
      return (float) BJSyn.getVolume(id + i);
    return 0.0F;
  }

  public int getRate()
  {
    return (int) BJSyn.getRate(id);
  }

  public float getRate(int i)
  {
    if (i < channels && i >= 0)
      return (float) BJSyn.getRate(id + i);
    return 0.0F;
  }

  public void setRate(float f)
  {
    BJSyn.setRate(id, f);
    if (channels == 2)
      BJSyn.setRate(id + 1, f);
  }

  public void setRate(float f, int i)
  {
    if (i < channels && i >= 0)
      BJSyn.setRate(id + i, f);
  }

  public float getSpeed()
  {
    return (float) (BJSyn.getRate(id) / (double) rate);
  }

  public float getSpeed(int i)
  {
    if (i < channels && i >= 0)
      return (float) (BJSyn.getRate(id + i) / (double) rate);
    return 0.0F;
  }

  public void setSpeed(float f)
  {
    BJSyn.setRate(id, f * rate);
    if (channels == 2)
      BJSyn.setRate(id + 1, f * rate);
  }

  public void setSpeed(float f, int i)
  {
    if (i < channels && i >= 0)
      BJSyn.setRate(id + i, f * rate);
  }

  public void setPan(float f)
  {
    setPan(f, 0);
  }

  public void setPan(float f, int i)
  {
    if (i < channels && i >= 0)
      BJSyn.setPan(id + i, f);
  }

  public float getPan()
  {
    return getPan(0);
  }

  public float getPan(int i)
  {
    if (i < channels && i >= 0)
      return (float) BJSyn.getPan(id + i);
    return 0.0F;
  }

  public void repeat()
  {
    repeat(0, getNumFrames());
  }

  public void repeat(int i, int i_9_)
  {
    if (i == i_9_)
    {
      System.out.println("[INFO] Handled SONIA bug: Sample.java(line#198)");
      i_9_ = i + 1;
    }
    state = 2;
    startF = i;
    endF = i_9_;
    movedF_Buff = getFramesMoved();
    try
    {
      BJSyn.loopSample(id, i, i_9_);
    }
    catch (Exception e)
    {
      if (THROW_JSYN_ERRORS)
      throw new RuntimeException(e);
    }
    if (channels == 2)
      BJSyn.loopSample(id + 1, i, i_9_);
  }

  public void repeatNum(int i, int i_10_, int i_11_)
  {
    state = 2;
    startF = i_10_;
    endF = i_11_;
    movedF_Buff = getFramesMoved();
    BJSyn.loopSampleNum(i, id, i_10_, i_11_);
    if (channels == 2)
      BJSyn.loopSampleNum(i, id + 1, i_10_, i_11_);
  }

  public void repeatNum(int i)
  {
    repeatNum(i, 0, getNumFrames());
  }

  public void play(int i, int i_12_)
  {
    state = 1;
    startF = i;
    endF = i_12_;
    movedF_Buff = getFramesMoved();
    framesWhenDone = movedF_Buff + i_12_ - i;
    BJSyn.playSample(id, i, i_12_);
    if (channels == 2)
      BJSyn.playSample(id + 1, i, i_12_);
  }

  public void play()
  {
    play(0, getNumFrames());
  }

  public void stop(boolean bool, int i)
  {
    state = 0;
    BJSyn.stopSample(id, bool, i);
    if (channels == 2)
      BJSyn.stopSample(id + 1, bool, i);
  }

  public void stop(int i)
  {
    stop(true, i);
  }

  public void stop()
  {
    stop(false, 0);
  }

  public void delete()
  {
    instances.remove(this);
    stop(0);
    BJSyn.deleteCircuit(id);
    if (channels == 2)
      BJSyn.deleteCircuit(id + 1);
  }

  public int getNumFrames()
  {
    int i = 0;
    try
    {
      i = BJSyn.getNumFrames(id);
    }
    catch (Throwable synthexception)
    {
      if (THROW_JSYN_ERRORS)
        throw new RuntimeException(synthexception);
    }
    return i;
  }

  int getRangeFrames()
  {
    int i = 0;
    if (getNumFrames() > 0)
      i = endF - startF;
    return i;
  }

  public int getCurrentFrame()
  {
    int i = 0;
    try
    {
      if (getFramesMoved() > 0 && getRangeFrames() > 0)
        i = (startF + (getFramesMoved() - movedF_Buff) % getRangeFrames());
      else
        i = 0;
    }
    catch (Exception e)
    {
      if (THROW_JSYN_ERRORS)
          throw new RuntimeException(e);
    }
    return i;
  }

  public int getFramesMoved()
  {
    int i = 0;
    try
    {
      i = BJSyn.mySampler[id].samplePort.getNumFramesMoved();
    }
    catch (Exception e)
    {      if (THROW_JSYN_ERRORS)
      throw new RuntimeException(e);
    }
    return i;
  }

  public void readChannel(int i, float[] fs, int i_13_, int i_14_, int i_15_)
  {
    short[] is = new short[i_15_];
    BJSyn.mySamp[id + i].read(i_14_, is, 0, i_15_);
    convertData(is, fs, i_15_, i_14_);
  }

  public void readChannel(int i, float[] fs, int i_16_)
  {
    readChannel(i, fs, 0, i_16_, fs.length);
  }

  public void readChannel(int i, float[] fs)
  {
    readChannel(i, fs, 0, 0, fs.length);
  }

  public void read(float[] fs, int i, int i_17_, int i_18_)
  {
    readChannel(0, fs, i, i_17_, i_18_);
  }

  public void read(float[] fs, int i)
  {
    readChannel(0, fs, 0, i, fs.length);
  }

  /** Read data from sample and write it into the array */
  public void read(float[] fs)
  {
    readChannel(0, fs, 0, 0, fs.length);
  }

  public void readChannel(int i, short[] is, int i_19_, int i_20_, int i_21_)
  {
    BJSyn.mySamp[id + i].read(i_20_, is, 0, i_21_);
  }

  public void readChannel(int i, short[] is, int i_22_)
  {
    readChannel(i, is, 0, i_22_, is.length);
  }

  public void readChannel(int i, short[] is)
  {
    readChannel(i, is, 0, 0, is.length);
  }

  public void read(short[] is, int i, int i_23_, int i_24_)
  {
    readChannel(0, is, i, i_23_, i_24_);
  }

  public void read(short[] is, int i)
  {
    readChannel(0, is, 0, i, is.length);
  }

  public void read(short[] is)
  {
    readChannel(0, is, 0, 0, is.length);
  }

  public void writeChannel(int i, float[] fs, int i_25_, int i_26_, int i_27_)
  {
    BJSyn.mySamp[id + i].write(i_26_, convertData(fs), 0, i_27_);
  }

  public void writeChannel(int i, float[] fs, int i_28_)
  {
    writeChannel(i, fs, 0, i_28_, fs.length);
  }

  public void writeChannel(int i, float[] fs)
  {
    writeChannel(i, fs, 0, 0, fs.length);
  }

  public void write(float[] fs, int i, int i_29_, int i_30_)
  {
    writeChannel(0, fs, i, i_29_, i_30_);
  }

  public void write(float[] fs, int i)
  {
    writeChannel(0, fs, 0, i, fs.length);
  }

  /** Write data (from the array) into the sample */
  public void write(float[] fs)
  {
    writeChannel(0, fs, 0, 0, fs.length);
  }

  public void writeChannel(int i, short[] is, int i_31_, int i_32_, int i_33_)
  {
    BJSyn.mySamp[id + i].write(i_32_, is, 0, i_33_);
  }

  public void writeChannel(int i, short[] is, int i_34_)
  {
    writeChannel(i, is, 0, i_34_, is.length);
  }

  public void writeChannel(int i, short[] is)
  {
    writeChannel(i, is, 0, 0, is.length);
  }

  public void write(short[] is, int i, int i_35_, int i_36_)
  {
    writeChannel(0, is, i, i_35_, i_36_);
  }

  public void write(short[] is, int i)
  {
    writeChannel(0, is, 0, i, is.length);
  }

  public void write(short[] is)
  {
    writeChannel(0, is, 0, 0, is.length);
  }

  public static short[] convertData(float[] fs, int i, int i_37_)
  {
    short[] is = new short[i];
    for (int i_38_ = 0; i_38_ < i; i_38_++)
      is[i_38_] = (short) (int) (fs[i_38_ + i_37_] * Sonia.valueMod);
    return is;
  }

  public static short[] convertData(float[] fs)
  {
    short[] is = new short[fs.length];
    for (int i = 0; i < fs.length; i++)
      is[i] = (short) (int) (fs[i] * Sonia.valueMod);
    return is;
  }

  public static void convertData(short[] is, float[] fs, int i, int i_39_)
  {
    for (int i_40_ = 0; i_40_ < i; i_40_++)
      fs[i_40_ + i_39_] = (float) is[i_40_] / Sonia.valueMod;
  }

  public static void convertData(short[] is, float[] fs)
  {
    for (int i = 0; i < is.length; i++)
      fs[i] = (float) is[i] / Sonia.valueMod;
  }

  public void connectLiveInput(boolean bool)
  {
    if (bool)
    {
      LiveInput.myBusWriter[id] = new BusWriter();
      LiveInput.myBusWriter[id].start();
      BJSyn.mySampler[id].output.connect(LiveInput.myBusWriter[id].input);
      LiveInput.myBusWriter[id].busOutput.connect(LiveInput.myBusReader.busInput);
      if (channels == 2)
      {
        LiveInput.myBusWriter[id + 1] = new BusWriter();
        LiveInput.myBusWriter[id + 1].start();
        BJSyn.mySampler[id + 1].output.connect(LiveInput.myBusWriter[id + 1].input);
        LiveInput.myBusWriter[id + 1].busOutput.connect(LiveInput.myBusReader.busInput);
      }
    }
    else
    {
      LiveInput.myBusWriter[id].stop();
      LiveInput.myBusWriter[id].input.disconnect();
      LiveInput.myBusWriter[id].busOutput.disconnect();
      if (channels == 2)
      {
        LiveInput.myBusWriter[id + 1].stop();
        LiveInput.myBusWriter[id + 1].input.disconnect();
        LiveInput.myBusWriter[id + 1].busOutput.disconnect();
      }
    }
  }

  public boolean isPlaying()
  {
    switch (state)
    {
      case 0:
        return false;
      case 1:
      {
        int i = startF + getRangeFrames() - getCurrentFrame();
        if (framesWhenDone - getFramesMoved() <= 1)
        {
          state = 0;
          return false;
        }
        return true;
      }
      case 2:
        return true;
      default:
        return false;
    }
  }

  public void saveFile(String string)
  {
    try
    {
      RandomAccessFile randomaccessfile = new RandomAccessFile(string + ".wav", "rw");
      WAVFileWriter wavfilewriter = new WAVFileWriter(randomaccessfile);
      if (channels == 1)
      {
        short[] is = new short[getNumFrames()];
        read(is);
        wavfilewriter.write(is, 1, getRate());
        wavfilewriter.close();
      }
      else if (channels == 2)
      {
        short[] is = new short[getNumFrames() * 2];
        short[] is_41_ = new short[getNumFrames()];
        short[] is_42_ = new short[getNumFrames()];
        read(is_41_);
        BJSyn.mySamp[id + 1].read(is_42_);
        for (int i = 0; i < getNumFrames() * 2; i += 2)
        {
          is[i] = is_41_[i / 2];
          is[i + 1] = is_42_[i / 2];
        }
        wavfilewriter.write(is, 2, getRate());
        wavfilewriter.close();
      }
    }
    catch (IOException ioexception)
    {
      throw new RuntimeException(ioexception);
    }
  }
}
