package ms;
import java.awt.Rectangle;
import java.io.*;
import java.util.Properties;
import java.util.Random;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mkv.MyGUI.MyGUICheckBox;
import pitaru.sonia_v2_9.Sample;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

/*
 * 
 * Add fade-lerp  ****
 *
 */
public class WaveSlider implements ChangeListener
{
  static final int W = 194, H = 24, WAVEFORM_COL = 220;
  static final float DEFAULT_VOLUME = .5f; // reset start slider pos if changed
  private static final int REPEAT_FOREVER = -1;
  static Random rand = new Random();
  private static int ID = 0;

  int r, g, b, alpha;
  int id, x, y, w, h, stopFrame;
  int sliderX, startFrame, waveformH, lastFrame;
  
  float startX = 0, stopX = 0, frames[];
  float lastVol, percentComplete, targetVolume;

  boolean muted, pressed, on = true;
  
  String sampleName = "";
  PGraphics waveform;
  Sample sample;
  MachineSampler app;
  Rectangle bounds;
  MyGUICheckBox enabled;
  Knob volumeKnob;
  long setVolumeTs=-1;
  int volumeFadeMs=8000;

  public WaveSlider(MachineSampler p, Sample s, int x, int y, int w, int h, String sampleName)
  {
    this.w = w;
    this.h = h;
    this.y = y;
    this.app = p;
    this.id = ID++;
    this.waveformH = h + 6;
    this.x = sliderX = x;
    this.sample = s;
    if (sampleName != null)
      this.sampleName = sampleName;

    randomColor(p);

    this.lastVol = DEFAULT_VOLUME;
    this.bounds = new Rectangle(x, y - h / 2, w, h);

    setSample(s, sampleName);

    boolean on = s != null;
    p.gui.add(enabled = new MyGUICheckBox(p, x - 12, y, 10, 10));
    enabled.checked(on);

    this.volumeKnob = new Knob(p, "vol", x + W + 17, y - 2);
    this.volumeKnob.addListener(this);
  }

  public WaveSlider(MachineSampler ms, Sample s, int x, int y, String sampleName)
  {
    this(ms, s, x, y, W, H, sampleName);
  }

  public void draw()
  {
    updateScrubPos(sample);
    
    doVolumeFade(sample);
    
    app.fill(WAVEFORM_COL);
    app.stroke(WAVEFORM_COL);
    app.strokeWeight(1);
    app.rectMode(PConstants.CENTER);

    // horiz line
    app.stroke(WAVEFORM_COL);
    app.line(x, y, x + w + 2, y);

    // waveform
    if (waveform == null)
      computeWaveForm(sample, w + 4, waveformH);
    if (waveform != null)
      app.image(waveform, bounds.x, y - waveformH / 2f);

    // slider rect
    app.fill(255);
    app.noStroke();
    app.rect(x + sliderX + 1, y, 4, 20);

    // bounding box
    app.fill(SamplerFrame.BG[0], SamplerFrame.BG[1], SamplerFrame.BG[2], 220);
    app.stroke(0);
    if (sample != null && sample.getVolume() > 0)
    {
      app.fill(r, g, b, 180); // color fill if playing
      app.stroke(200);
    }

    app.rectMode(PConstants.CORNER);
    app.rect(bounds.x - 25, bounds.y - 3, bounds.width + 56, bounds.height + 6);
    app.noFill();
    app.line(x, y - 15, x, y + 15);
    app.line(x + W + 3, y - 15, x + W + 3, y + 15);

    // red outline for selected
    if (MachineSampler.DISABLE_USER_INPUT) return;
    
    if (isSelected())
    {
      app.stroke(200, 50, 50);
      app.strokeWeight(2);
      app.noFill();
      app.rect(bounds.x - 26, bounds.y - 4, bounds.width + 57, bounds.height + 8);
    }
  }

  private void doVolumeFade(Sample s)
  {
    if (s == null || SamplerFrame.exiting)
      return;
    
    if (!isPlaying()) return;

    if (testIdx >= 0 && id != testIdx)
      return;
   
    testIdx = id;
    
    
    float curr = s.getVolume();
    System.out.println("testIdx="+id+" targetVol="+targetVolume+ " curr="+curr);
    if (curr != targetVolume)
    {
      float ntime = (System.currentTimeMillis() - setVolumeTs) / volumeFadeMs;
      System.out.print(id+".lerp("+curr+", "+targetVolume+", "+ntime+")");
      curr = PApplet.lerp(curr, targetVolume, ntime);
      System.out.println(" -> "+curr);
      //s.setVolume(curr);
    }
  }
  static int testIdx = -1;

  boolean isSelected()
  {
    return this == app.sliders[app.selectedIdx];
  }

  void randomColor(PApplet p)
  {
    r = (int) p.random(100, 200);
    g = (int) p.random(100, 200);
    b = (int) p.random(100, 200);
  }

  public void computeWaveForm(Sample s, int w, int h)
  {
    if (s == null)
      return;

    // System.out.println("SampleScrubber."+id+".computeWaveForm()");

    float[] pts = new float[w]; // 1 pt per pixel
    boolean partial = isPartial(s);
    int mod = s.getNumFrames() / w;

    float max = 0;
    try
    {
      if (frames == null)
        return;
      for (int i = 0; i < pts.length; i++)
      {
        float f = Math.abs(frames[i * mod]);
        if (f > max)
          max = f;
      }
      if (frames == null)
        return;
      for (int i = 0; i < pts.length; i++)
        pts[i] = (frames[i * mod] / max);
    }
    catch (Throwable e)
    {
      System.out.println("[WARN] Error in computeWaveForm() : " + e);
      return;
    }

    pts[0] = pts[pts.length - 1] = 0; // first and last

    waveform = app.createGraphics(w, h, PApplet.JAVA2D);
    float center = waveform.height / 2f;

    waveform.beginDraw();

    waveform.noStroke();// 255,0,0);
    waveform.fill(0, 0);
    waveform.rect(0, 0, waveform.width - 1, waveform.height - 1);

    waveform.smooth();
    waveform.noFill();

    for (int i = 0; i < pts.length - 1; i++)
    {
      float y1 = center + (pts[i] * h / 2f);
      float y2 = center + (pts[i + 1] * h / 2f);

      int alpha = 255;

      if (partial && (i < startX || i + 1 > stopX))
        alpha = 32;

      waveform.stroke(WAVEFORM_COL, alpha);

      waveform.line(i, y1, i + 1, y2);
      if (y1 > center && y2 > center)
      {
        waveform.stroke(WAVEFORM_COL * .75f, alpha);
        waveform.line(i, center + 1, i, y1 - 1);
      }
      if (y1 < center && y2 < center)
      {
        waveform.stroke(WAVEFORM_COL * .75f, alpha);
        waveform.line(i, center - 1, i, y1 + 1);
      }
    }

    waveform.endDraw();
  }

  public float getPercentComplete()
  {
    return percentComplete;
  }
  
  public float getDurationMs()
  {
    return sample.getNumFrames() / (float) MachineSampler.SAMPLE_RATE;
  }
  
  public int numFramesRemaining()
  {
    return (sample.getNumFrames() - sample.getCurrentFrame());
  }
  
  public boolean contains(int mx, int my)
  {
    return bounds.contains(mx, my);
  }

  void updateScrubPos(Sample s)
  {
    if (s == null || SamplerFrame.exiting)
      return;

    int cur = 0;
    float tot = Float.MAX_VALUE;
    try
    {
      cur = s.getCurrentFrame();
      tot = s.getNumFrames();
    }
    catch (Exception e1)
    {
      if (!SamplerFrame.exiting)
        System.out.println("[WARN] updateScrubPos-0: "+
            "Error getting currentFrame!\n"+ stackToString(e1));
      return;
    }

    boolean paused = app.isPaused();
    
    if (!paused) {
      
      percentComplete = cur / tot;
      
      sliderX = (int) PApplet.lerp(0, w, percentComplete);
      
      if (!SamplerFrame.exiting && !s.isPlaying()) // start looping if stopped
      {
          try
          {
            resetVolume();
            playSample();
          }
          catch (Exception e) {
            System.out.println("[WARN] updateScrubPos-1:\n" + stackToString(e));
          }
    
          // ok, back at start =======================
          try
          {
            cur = s.getCurrentFrame(); // needed?
            percentComplete = cur / (float) tot;
          }
          catch (Exception e) {
            System.out.println("[WARN] updateScrubPos-2:\n" + stackToString(e));
          }
      }
    }
  }

  private void resetVolume()
  {
    //setVolume(isEnabled() && !muted ? lastVol : 0); 
    setVolume(0);
    targetVolume = (isEnabled() && !muted) ? lastVol : 0; 
  }

  public void reset()
  {
    startFrame = 0;
    stopFrame = 0;
    startX = 0;
    stopX = 0;
    muted = false;
  }

  static float[] multiples = { 1, 16, 12, 8, 6, 4, 3, 2, 3 / 2f, 4 / 3f };

  public void playPartial(int oneToTen)
  {

    Sample s = sample;
    if (s == null || oneToTen > 9 || oneToTen < 0)
      return;

    float mult = multiples[oneToTen];

    // we're playing a sub-section
    startX = w * (.5f - ((1 / mult) * .5f));

    stopX = w - startX;

    System.err.println("WaveSlider.playPartial: " + (stopX - startX) / w);

    doPartialPlayback(s);
  }

  void doPartialPlayback(Sample s)
  {
    if (s == null)
      return;
    // System.out.println("start: "+startX+" stop: "+stopX);
    int len = s.getNumFrames();
    startFrame = (int) ((startX / (float) w) * len);
    stopFrame = (int) ((stopX / (float) w) * len);
    if (startFrame >= stopFrame)
      throw new RuntimeException("startFrame=" + startFrame + " >= stopFrame="
          + stopFrame + "!");
    if (s != null)
      sample.stop();
  }

  public void resetStartStopFrames()
  {
    if (sample == null)
      return;
    startX = stopX = 0;
    startFrame = stopFrame = 0;
    try
    {
      stopFrame = sample.getNumFrames();
    }
    catch (RuntimeException e)
    {
      System.err.println("[WARN] SampleScrubber.resetStartStopFrames() : "
          + e.getMessage());
    }
  }

  public void mousePressed(int mx, int my, boolean right)
  {

    if (right)
    {
      rightClicked(mx, my);
      return;
    }

    pressed = true;
    startX = mx - x;
  }

  public void mouseDragged(int mx, int my, boolean right)
  {
    if (!pressed || sample == null)
      return;
    stopX = Math.min(w, mx - x);
  }

  public void mouseReleased(int mx, int my, boolean right)
  {
    /*
     * Sample s = sample; if (!pressed || s == null) return;
     * 
     * pressed = false; stopX = Math.min(w, mx - x);
     * 
     * if (stopX < startX) {// swap float tmp = startX; startX = stopX; stopX =
     * tmp; }
     * 
     * if (stopX - startX > 2) doPartialPlayback(s); else resetSample();
     */
  }

  private void rightClicked(int mx, int my)
  {
    /*
     * Sample s = sample; if (s == null) return; sliderX = mx - x; try {
     * s.play((int) ((sliderX / (float) w) * s.getNumFrames()),
     * s.getNumFrames()); } catch (RuntimeException e) {
     * System.err.println("rightClicked.CAUGHT: " + e+e.getMessage()); }
     */
  }

  private boolean isPartial(Sample s)
  {
    if (s == null)
      return false;
    return (startX > 0 || (stopX > 0 && stopX < s.getNumFrames()));
  }

  public boolean isEnabled()
  {
    return enabled.isChecked();
  }

  public void setSample(Sample s, String name)
  {
    Sample last = sample;
    sample = s;
    if (sample != null)
    {
      this.sampleName = name;
      this.frames = new float[s.getNumFrames()];
      s.read(frames);
      //setVolume(on ? DEFAULT_VOLUME : 0f);
      targetVolume = (on ? DEFAULT_VOLUME : 0f);
      //setVolume(0);
      this.stopFrame = s.getNumFrames();
    }
    if (last != null)
      last.stop();
    if (enabled != null)
      enabled.checked(true);
  }

  public void restart()
  {
    resetVolume();
    
    if (sample != null)
    {
      sample.stop();
      playSample();
    }
  }

  private void playSample()
  {
    if (sample == null) return;
    
    setVolumeTs = System.currentTimeMillis();
    sample.play();
    
    if (this == app.timerSlider)
      app.onStartTimerSlider(this);
  }

  public boolean isMuted()
  {
    return muted;
  }

  public void mute(boolean b)
  {
    if (sample == null)
      return;

    muted = b;
    float v = sample.getVolume();
    if (b)
    {
      if (v > 0)
      {
        lastVol = v;
        sample.setVolume(0);
      }
    }
    else
    {
      if (v == 0)
        setVolume(lastVol);
    }
  }

  public void clear()
  {
    if (sample != null)
    {
      sample.stop();
      sample.delete();
    }
    sample = null;
    frames = null;
    waveform = null;
    if (enabled != null)
      enabled.checked(false);
    reset();
  }

  public boolean isPlaying() {
    if (muted || sample == null || !sample.isPlaying())
      return false;
    return sample.getVolume() > 0;
  }
  
  public int pause()
  {
    if (sample == null || !sample.isPlaying())
      return -1;

    int lastFramePlayed = sample.getCurrentFrame();
    sample.stop();

    return lastFramePlayed;
  }

  public void unpause(int frame)
  {
    if (sample == null) return;
    sample.play(Math.max(0,frame), sample.getNumFrames());
  }

  private void setColor(String[] rgb)
  {
    r = Integer.parseInt(rgb[0]);
    g = Integer.parseInt(rgb[1]);
    b = Integer.parseInt(rgb[2]);
  }

  public void toXml(Properties p)
  {
    boolean debugOnly = true;

    String tag = "track" + id;
    p.setProperty(tag + "color", (r + "," + g + "," + b));

    if (sample == null)
      return;

    p.setProperty(tag + "sliderX", sliderX + "");
    p.setProperty(tag + "startFrame", startFrame + "");
    p.setProperty(tag + "stopFrame", stopFrame + "");
    p.setProperty(tag + "startX", startX + "");
    p.setProperty(tag + "stopX", stopX + "");

    // gain, rate, pan =========================
    p.setProperty(tag + "rate", sample.getRate() + "");
    p.setProperty(tag + "pan", sample.getPan() + "");
    p.setProperty(tag + "gain", sample.getVolume() + "");

    // write sample to file ====================
    String dataDir = p.getProperty("data.dir");
    if (!debugOnly && dataDir == null)
      throw new RuntimeException("SampleUIControl.toXml() :: Null data dir!");

    // if its a new sample, assign a name (no ext)
    sampleName = dataDir + "/" + tag + "sample";
    p.setProperty(tag + "sample", sampleName);

    sample.saveFile(sampleName);
    sample.delete();
  }

  public void fromXml(Properties p)
  {
    String tag = "track" + id;

    try
    {
      setColor(p.getProperty(tag + "color").split(","));
    }
    catch (Exception e)
    {
      randomColor(app);
    }

    sliderX = Integer.parseInt(p.getProperty(tag + "sliderX"));
    startFrame = Integer.parseInt(p.getProperty(tag + "startFrame"));
    stopFrame = Integer.parseInt(p.getProperty(tag + "stopFrame"));
    startX = Float.parseFloat(p.getProperty(tag + "startX"));
    stopX = Float.parseFloat(p.getProperty(tag + "stopX"));

    // gain, rate, pan =========================
    sample.setPan(Float.parseFloat(p.getProperty(tag + "pan")));
    sample.setVolume(Float.parseFloat(p.getProperty(tag + "gain")));
    sample.setRate(Float.parseFloat(p.getProperty(tag + "rate")));

    // read sample from file ====================
    Sample s = new Sample(sampleName + ".wav");
    setSample(s, sampleName);
  }

  public void stateChanged(ChangeEvent e)
  {
    Knob k = (Knob) e.getSource();
    if (k == volumeKnob)
      setVolume(k.getValue());
  }
 
  void updateVolumeFromKnob()
  {
    if (sample != null)
      setVolume(volumeKnob.getValue());
  }
  
  void setVolume(float v)
  {
    if (sample == null) return;
    if(1==1 && v>0) 
      throw new RuntimeException("Stack");
    sample.setVolume(v * app.getMasterVolume());
  }
  
  public static String stackToString(Throwable t)
  {
    final Writer result = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(result);
    t.printStackTrace(printWriter);
    return result.toString();
  }
  
}// end
