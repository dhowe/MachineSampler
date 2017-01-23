package ms;

import java.awt.Rectangle;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import mkv.MyGUI.*;
import ms.util.RiGUIEvent;
import ms.util.RiTextField;
import oscP5.*;
import osci.OSCiClient;
import pitaru.sonia_v2_9.Sample;
import pitaru.sonia_v2_9.Sonia;
import processing.core.*;

/*
 * TODO:
 *  FADE FROM SET TO SET OVER ONE REP.
 *  ENABLE MANUAL TRACKS WITH OTHER DURATIONS
 *  
 * OLD: 
 *   Add a master volume?
 */
public class MachineSampler extends PApplet
{
  static final String VERSION = "1.5";
  
  static boolean USE_OSC = true;
  static final boolean DBUG_OSC = false;
  static boolean DISABLE_USER_INPUT = USE_OSC;
  static int NUM_PER_COLUMN = USE_OSC ? 13: 15;
  static int NUM_REPEATS = 3;
  static final int SPACING = 40, NUM_COLUMNS = 4;
  
  static boolean LOAD_DEFAULT_SAMPLES = true;
  static String DEFAULT_SAMPLE_DIR = 
    "/Users/dhowe/Documents/eclipse-workspace/GenNarrator/EOE_Generative_Archive";
    //"/Users/dhowe/Documents/eclipse-workspace/GenNarrator/MomGen36_normalized";
  
  static final boolean CONFIRM_ON_QUIT = false && !LOAD_DEFAULT_SAMPLES;
  static final float DEFAULT_PROB = .17f;
  static final int SAMPLE_RATE = 44100;
  
  static final String INSTRUCTIONS = 
    "Usage: cmd-O to load samples, "+
    " space-bar to play/pause, " +
    " arrow-keys to navigate, " +
		" enter to enable/disable tracks, " +
		" delete to remove a track";

  private static final String REQUEST_NEXT = "requestNext";

  OscProxy oscProxy;
  RiTextField repeatsInput;
  WaveSlider soloer, timerSlider, sliders[];
  PImage randButtonOff, randButtonOn, playButton, pauseButton;
  Rectangle randbuttonBounds, playbuttonBounds;
  String names[], sampleDir = "";
  MyGUIPinSlider probSlider;
  Knob adjustingKnob;
  MyGUI gui;
  PFont font; 
  
  boolean started, muted, randomizedLoop, crossFade = true;
  int yOff, xOff = 70, selectedIdx = 3, timesUntilReset = -1;
  int W=1200, H=700, mouseOverIdx = -1, pausedAt = 0;
  float masterVolume = 1f;//.00001f;

  public MachineSampler(int w, int h) { 
    this.W = w;
    this.H = h;
  }
  
  public void setup()
  {
    //size(1200,500);
    size(W, H);
    
    smooth();
    
    background(SamplerFrame.BG[0], SamplerFrame.BG[1], SamplerFrame.BG[2]);
    
    Sonia.start(this, SAMPLE_RATE);
    
    createGui();
    
    Sample[] s = LOAD_DEFAULT_SAMPLES ? loadAllFrom(DEFAULT_SAMPLE_DIR) : null;
     
    createSliders(s, names);
    
    createRepeatsInput();
    
    if (USE_OSC) oscProxy = new OscProxy(this);
  }
  
  public void draw()
  {
    background(SamplerFrame.BG[0], SamplerFrame.BG[1], SamplerFrame.BG[2]);
    
    for (int i = 0; i < sliders.length; i++)
      sliders[i].draw();
    
    if (USE_OSC || randomizedLoop)  // # of repeats
    {
      fill(255);
      textSize(11);
      int disp = (NUM_REPEATS-timesUntilReset);
      if (disp == 0) disp = NUM_REPEATS;
      float yPos = (USE_OSC) ? 13 : 7;
      text("rep="+disp+"/"+NUM_REPEATS, width-42,yPos);
    }
    
    if (!DISABLE_USER_INPUT) {
      
      rectMode(PConstants.CORNER);
      noFill();
      stroke(200);
      strokeWeight(2);
      rect(xOff-50, yOff+10, width-40, 685); // large rect
      stroke(130);
      rect(xOff-26, randbuttonBounds.y-6, width-90, 36); // control rect
      
      // play/pause button
      image(isPaused() ? playButton : pauseButton, playbuttonBounds.x, playbuttonBounds.y-4);
      if (mousePressed && playbuttonBounds.contains(mouseX,mouseY)) {
        fill(0,64);
        noStroke();
        rect(playbuttonBounds.x, playbuttonBounds.y, playbuttonBounds.width, playbuttonBounds.height);
      }
      
      // random button
      image(randomizedLoop ? randButtonOn : randButtonOff, randbuttonBounds.x, randbuttonBounds.y-4);
      if (mousePressed && randbuttonBounds.contains(mouseX,mouseY)) {
        fill(0,64);
        noStroke();
        //stroke(200);
        rect(randbuttonBounds.x, randbuttonBounds.y, randbuttonBounds.width, randbuttonBounds.height);
      }
  
      // prob-slider
      fill(255);
      textSize(12);
      text("prob="+probSlider.getValue()/100f, probSlider._x+62, randbuttonBounds.y+17);
      text(INSTRUCTIONS, xOff+320, randbuttonBounds.y+17);
    }
    
    // sample-name mouse-overs
    if (mouseOverIdx >-1 && sliders[mouseOverIdx] != null) {
      textSize(10);
      String nm = sliders[mouseOverIdx].sampleName;
      //if (frameCount%10 == 9) System.out.println(Medi);
      fill(253,253,201);
      stroke(200);
      strokeWeight(1);
      rect(mouseX, mouseY+20, textWidth(nm)+2, 15);
      fill(50);
      text(nm, mouseX+2, mouseY+32);
    }
  
    if (USE_OSC) handleOsc();
  }

  
  private void createRepeatsInput()
  {
    repeatsInput = new RiTextField(this, 194, 8, 40);
    repeatsInput.setText(""+NUM_REPEATS);
    repeatsInput.setBgColor(20,20,70);
    repeatsInput.setFgColor(255);
  }
  
  public void onRiTaEvent(RiGUIEvent re) 
  {
    String input = (String)re.getMessage();
    try
    {
      int num = Integer.parseInt(input);
      NUM_REPEATS = num;
      repeatsInput.setText(""+num);
    }
    catch (Exception e)
    {
      println("[WARN] Ignoring invalid input: "+input);
    }
  }
  
  private void handleOsc()
  {

    float percentComplete = timerSlider.getPercentComplete();
    
    if (timesUntilReset == 0) {
      if (!oscProxy.oscReadyForOutgoingRequest && percentComplete < .1) {
        oscProxy.oscReadyForOutgoingRequest = true;     
      }
      if (oscProxy.oscReadyForOutgoingRequest && percentComplete > .9) {
        //System.out.println("MachineSampler.REQUEST_NEXT");
        
        oscProxy.oscReadyForOutgoingRequest = false;
        oscProxy.message(REQUEST_NEXT, nextLaunchTime());
      }
    }
  }

  private long nextLaunchTime()
  {
    int numLeft= timerSlider.numFramesRemaining();
    int millisTilNext = (int) ((numLeft / (float)SAMPLE_RATE) * 1000);
    return System.currentTimeMillis() + millisTilNext;
  }

  private void createGui()
  {
    randButtonOff = loadImage("randloop_off.png");
    randButtonOn = loadImage("randloop_on.png");
    playButton = loadImage("playbutton.png");
    pauseButton = loadImage("pausebutton.png");
    font = loadFont("ArialNarrow-48.vlw");
    textFont(font, 32);

    gui = new MyGUI(this, 260);
    gui.getStyle().setFont(font, 10);
    gui.getStyle().buttonFace = color(0);
    gui.getStyle().buttonShadow = color(20);
    gui.getStyle().tintColor(color(255)); // tint color
    gui.getStyle().buttonText = color(255, 255, 255); // text color
    gui.getStyle().face = color(255); // slider color
    
    //triggerBounds = new Rectangle(xOff+20, yOff+height-130, 22, 22);
    playbuttonBounds = new Rectangle(xOff-19, yOff+645, 32, 24);
    randbuttonBounds = new Rectangle(xOff+23, playbuttonBounds.y, 32, 24);
    probSlider = new MyGUIPinSlider(this, randbuttonBounds.x+90, randbuttonBounds.y+11, 100, 12, 0, 100);
    if (!DISABLE_USER_INPUT) {
      gui.add(probSlider);
    }
    else {
      yOff =- 15; // start a bit higher
    }
    probSlider.setValue((int)(DEFAULT_PROB*100));
  }
  
  private int createSliders(Sample[] samples, String[] names)
  {
    sliders = new WaveSlider[NUM_PER_COLUMN * NUM_COLUMNS];
    
    int y = 0;
    int x = xOff;
    for (int i = 0; i < sliders.length; i++) {
      if (i % NUM_PER_COLUMN == 0) {
        if (i>0) x += 280;
        y = yOff;
      }
      
      Sample s = null;
      String name = null;
      if (samples != null && i < samples.length) {
        s = samples[i];
        name = names[i];
      }
      
      sliders[i] = new WaveSlider(this, s, x, y += SPACING, name);
      if (i == 0)
        timerSlider = sliders[i]; 
    }
    
    return y;
  }

  public void keyPressed()
  {
    if (DISABLE_USER_INPUT ) return;
    //System.out.println(key+"="+keyCode);    

    // key codes -------------------
    if (keyCode == 32)
    {
      // space-bar
      togglePause();
    }
    else if (keyCode == 10) 
    {
      // return
      toggleEnabled(sliders[selectedIdx]);
    }
    else if (keyCode == 40)
    { 
      // up-arrow
      incrControl();
    }
    else if (keyCode == 38)
    { 
      // down-arrow
      decrControl();
    }
    else if (keyCode == 37)
    {
      // right-arrow
      for (int i = 0; i < NUM_PER_COLUMN; i++)
        decrControl();
    }
    else if (keyCode == 39)
    {
      // left-arrow
      for (int i = 0; i < NUM_PER_COLUMN; i++)
        incrControl();
    }
    else if (keyCode == 8)
    { 
      // forward-delete key
      sliders[selectedIdx].clear();
    }
    else if (keyCode < 58 && keyCode > 47 || keyCode == 192) // number keys (0-9)
    {
      int pressed = (keyCode-48);
      setMasterVolume( (pressed == 0) ? .001f : pressed*.1f);
    }
  }

  private void resetByProb()
  {
    List<String> l = new ArrayList<String>();
    float prob = probSlider.getValue()/100f;
    for (int i = 0; i < sliders.length; i++)
    {
      if (sliders[i] == null) continue;
        if (Math.random()<prob) {
          l.add(sliders[i].sampleName);
        }
    }
    nextSet(l.toArray(new String[l.size()]));
  }
  
  private void incrControl()
  {
    if (++selectedIdx >= sliders.length)
      selectedIdx %= sliders.length;
  }
  
  private void decrControl()
  {
    if (--selectedIdx < 0)
      selectedIdx += sliders.length;
  }

  public static boolean rightMouseButton(MouseEvent me)
  {
    return (me.getModifiers() == 4 || (me.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK);
  }
  
  public void mousePressed()
  {
    //if (DISABLE_USER_INPUT) return;
    
    if (sliders == null) return;
    
    for (int i = 0; i < Knob.instances.size(); i++)
    {
      Knob knob = (Knob) Knob.instances.get(i);
      if (knob.contains(mouseX, mouseY))
      {
        adjustingKnob = knob;
        adjustingKnob.initAdjust(mouseX, mouseY);
        for (int k = 0; k < sliders.length; k++) {
          if (sliders[k].volumeKnob == adjustingKnob) {
            selectedIdx = k;
            return;
          }
        }
      }
    }
    
    if (DISABLE_USER_INPUT) return;
    

    int mx = mouseX, my = mouseY;
    for (int k = 0; k < sliders.length; k++)
    {
      if (sliders[k] == null) continue;

      if (sliders[k].contains(mx, my))
      {
        sliders[k].mousePressed(mx, my, rightMouseButton(mouseEvent));
        return;
      }
    }
  }

  public void mouseMoved()
  {
    //if (DISABLE_USER_INPUT) return;
    
    
    if (sliders == null) return;
    
    mouseOverIdx = -1;
    for (int k = 0; k < sliders.length; k++)
    {
      if (sliders[k] == null) continue;

      if (sliders[k].contains(mouseX, mouseY))
      {
        if (sliders[k].sample != null)
          mouseOverIdx = k;
        //System.out.println("display: "+k);
      }
    }
  }
  
  public void mouseDragged()
  {
    //if (DISABLE_USER_INPUT) return;
    
    
    if (sliders == null) return;
    
    if (adjustingKnob != null) {
      adjustingKnob.adjust(mouseX, mouseY, pmouseX, pmouseY);
      return;
    }
    
    if (DISABLE_USER_INPUT) return;

    int mx = mouseX, my = mouseY;
    for (int k = 0; k < sliders.length; k++)
    {
      if (sliders[k] == null) continue;

      if (sliders[k].pressed) {
        sliders[k].mouseDragged(mx, my, rightMouseButton(mouseEvent));
        return;
      }
    }
  }
  
  
  // not-used
  private WaveSlider getSliderAt(int mx, int my) {
    for (int k = 0; k < sliders.length; k++){
      if (sliders[k] != null && sliders[k].contains(mx, my))
        return sliders[k];
    }
    return null;
  }

  public void mouseReleased()
  {
    if (sliders == null) return;

    if (adjustingKnob != null)
    {
      adjustingKnob.endAdjust(mouseX, mouseY);
      adjustingKnob = null;
      return;
    }
    
    if (DISABLE_USER_INPUT) return;
    
    
    int mx = mouseX, my = mouseY;
    
    if (randbuttonBounds.contains(mx, my)) 
    {
      randomizedLoop = !randomizedLoop;
      if (randomizedLoop) {
        //timesUntilReset = NUM_REPEATS;
        resetByProb();
        startIfStopped();
      } 
      else
        timesUntilReset = -1;
      
      return;
    }
    
    if (playbuttonBounds.contains(mx, my)) {
      togglePause();
      return;
    }
    
    for (int k = 0; k < sliders.length; k++)
    {
      if (sliders[k] == null) continue;

      if (sliders[k].contains(mx, my))
      {
        selectedIdx = k;
        sliders[k].mouseReleased(mx, my, rightMouseButton(mouseEvent));
        return;
      }
    }
  }

  private void startIfStopped()
  {
    if (!started) {
      started = true;
      for (int k = 0; k < sliders.length; k++)
      {
        if (sliders[k] == null) continue;
        if (!sliders[k].isEnabled())
          sliders[k].setVolume(0);
      }
    }
    if (isPaused()) { // start if stopped
      new Thread() {
        public void run() {
          try {
            Thread.sleep(100);
            togglePause();
          }
          catch (InterruptedException e) {}              
      }}.start();
    }
  }

  public void mouseClicked()
  {
    //if (DISABLE_USER_INPUT) return;
    
    
    if (sliders == null || rightMouseButton(mouseEvent))
      return; // no right-clicks
    
    if (mouseEvent.getClickCount() == 2) {
      for (int k = 0; k < sliders.length; k++)
      {
        if (sliders[k] == null) continue;

        if (sliders[k].contains(mouseX, mouseY))
        {
          selectedIdx = k;
          handleSolo(sliders[k]);
          return;
        }
      }
    }
  }
  

  public void unsoloAll() {
    if (sliders == null) return;
    
    for (int i = 0; i < sliders.length; i++) {
      if (sliders[i]!=null && sliders[i].enabled.isChecked())
        sliders[i].mute(false);
    }
    if (soloer!=null && soloer.sample != null); 
      soloer.sample.setVolume(soloer.lastVol);
      
    soloer = null;
  }

  public void handleSolo(WaveSlider slider)
  {
    if (slider.sample == null) return;

    slider.enabled.checked(true);
            
    if (soloer == null) 
    {
      // start solo: mute all
      for (int i = 0; i < sliders.length; i++)
          sliders[i].mute(true);
      soloer = slider;
      soloer.sample.setVolume(1);
      soloer.mute(false);
    }
    else if (soloer == slider) 
    {
      unsoloAll();
    }
    else 
    {
      // stop old, start new: change soloer
      soloer.mute(true);
      soloer = slider; 
      soloer.sample.setVolume(1);
      soloer.mute(false);
    }
  }

  public Sample[] loadAllFrom(String directory)
  {
    System.out.println("[INFO] Loading samples from: " + directory);
    
    //if (startPaused) pausedAt = 0; // start with everything paused
    
    if (directory == null) return null;
    
    File dir = new File(directory);
    if (!dir.exists())
      throw new RuntimeException("No sampleDir(" + directory
         + ") found; 'user.dir'=" + System.getProperty("user.dir"));

    File[] samps = dir.listFiles(new FileFilter()
    {
      public boolean accept(File pathname)
      {
        // System.out.println("trying "+pathname.getName();
        return pathname.toString().endsWith(".wav")
            || pathname.toString().endsWith(".aiff")
            || pathname.toString().endsWith(".aif");
      }
    });

    List l = Arrays.asList(samps);
    //Collections.shuffle(l);
    samps = (File[]) l.toArray(new File[l.size()]);

    int num = Math.min(samps.length, NUM_COLUMNS * NUM_PER_COLUMN);
    names = new String[num];
    
    Sample[] samples = new Sample[num];
    for (int i = 0; i < samples.length; i++) {
      //System.out.println(i+") "+samps[i].getAbsolutePath());
      samples[i] = new Sample(samps[i].getAbsolutePath());
      names[i] = samps[i].getName();
    }
    
    return samples;
  }
  
  public void actionPerformed(ActionEvent e)
  {
    Object source = e.getSource();
   
    if (source instanceof MyGUICheckBox)
    {
      if (sliders == null) return;

      // check our boxes
      for (int k = 0; k < sliders.length; k++)
      {
        if (source == sliders[k].enabled)
        {
          selectedIdx = k;
          return;
        }
      }
    }
  }

  void toggleEnabled(WaveSlider k)
  {
    k.enabled.checked(!k.isEnabled());
  }

  public void replaceSampleFrom(File f)
  {
    Sample s = new Sample(f.getPath());
    if (s != null)
      sliders[selectedIdx].setSample(s, f.getName());
  }
  
  public void replaceSamplesFrom(String path)
  {
    Sample[] samples = loadAllFrom(path);
    for (int i = 0; i < sliders.length; i++) {
      sliders[i].clear();
      if (samples != null && i < samples.length && samples[i] != null) 
        sliders[i].setSample(samples[i], names[i]);
    }
    if (USE_OSC) 
      oscProxy.message(REQUEST_NEXT, Long.MAX_VALUE);
  }

  public void onStartTimerSlider(WaveSlider ws) 
  {
    --timesUntilReset; // keep this
    
    if (randomizedLoop) 
    {
      if (timesUntilReset == 0) 
      {
        timesUntilReset = NUM_REPEATS;
        
        new Thread() { // TODO: fix
          public void run() { 
            try {
              Thread.sleep(500); // hack: wait .5 sec and then set reset
            }
            catch (InterruptedException e) {
            }
            resetByProb();  
          }
        }.start();   
      }
    }
  }

  public void togglePause()
  {
    if (isPaused()) {
  //  if (!started) timesUntilReset = NUM_REPEATS;
      for (int i = 0; i < sliders.length; i++)
        sliders[i].unpause(pausedAt);
      pausedAt = -1;
    }
    else {
      started = true;
      for (int i = 0; i < sliders.length; i++)
        pausedAt = Math.max(pausedAt, sliders[i].pause());
    }
  } 

  boolean isPaused()
  {
    return (pausedAt > -1);
  }

  public void restart()
  {
    pausedAt = -1;
    for (int i = 0; i < sliders.length; i++)
      sliders[i].restart();
  }
  
  public void toggleMute()
  {
    muted = !muted;
    for (int i = 0; i < sliders.length; i++)
      sliders[i].mute(muted);
  }

  public void fromXml(File f)
  {
    Properties p = new Properties();
    try
    {
      p.loadFromXML(new FileInputStream(f));
      System.out.println("[INFO] Loaded: " + f + "\n       (" + p.getProperty("data.dir")+ ")");
    }
    catch (Exception e)
    {
      System.err.println("[WARN] No config file found: " + f);
      return;
    }
  }

  private Properties toXml(File projDir)
  {
    System.out.println("MS.toXml(" + projDir + ")");
    Properties p = new Properties();
/*    p.setProperty("master.volume", gain.getValue() + "");
    p.setProperty("master.prob", prob.getValue() + "");
    p.setProperty("master.snip", Switch.SNIP.on + "");
    p.setProperty("data.dir", projDir.getAbsolutePath());
    for (int i = 0; i < controlBanks.length; i++)
      controlBanks[i].toXml(p, i);*/
    return p;
  }
  
  public WaveSlider getSelectedSlider() 
  {
    return sliders[selectedIdx]; 
  }
  
  // ========================  interface ===========================
  
  public String[] getPlaying()
  {
    List<String> l = new ArrayList<String>();
    for (int i = 0; i < sliders.length; i++) {
      if (sliders[i] != null && sliders[i].isPlaying()) {
        //System.out.println("adding: "+sliders[i].sampleName);
        l.add(sliders[i].sampleName);
      }
    }
    return l.toArray(new String[l.size()]);
  }
  
  public String[] getAvailable()
  {
    List<String> l = new ArrayList<String>();
    for (int i = 0; i < sliders.length; i++) {
      if (sliders[i] == null || sliders[i].sample == null) 
        continue;
      l.add(sliders[i].sampleName);
    }
    
    System.out.println("[INFO] Loaded "+l.size()+" files...");
    
    return l.toArray(new String[l.size()]);
  }
  
  public void nextSet(Object[] next)
  {
    //System.out.println("MachineSampler.nextSet("+next.length+" of "+sliders.length+") started="+started);
    /*for (int i = 0; i < next.length; i++)
      System.out.println("  "+next[i]);*/

    timesUntilReset = NUM_REPEATS;
    
    if (soloer != null) unsoloAll();
          
    for (int i = 0; i < sliders.length; i++) {
      if (sliders[i] != null)
        sliders[i].enabled.checked(false);
    }
 
    for (int i = 0; i < next.length; i++)
    {
      boolean found = false;
      String name = next[i].toString().trim();

      //System.out.println("checking '"+name+"'");

      for (int j = 0; j < sliders.length; j++)
      {
        WaveSlider slider = sliders[j];
        if (slider==null || slider.sampleName == null) {
          System.out.println("[WARN] Null slider...");
          continue;
        }
        
        if (name.equals(slider.sampleName) || (name+".wav").equals(slider.sampleName)) { // fix
          slider.enabled.checked(true);
          found = true;
          break;
        }
      }
      
      if (!found)
        System.out.println("[WARN] no match for: "+name);
    }
    
    if (!started)  // update immediately if never started
    {
      started = true;
      for (int k = 0; k < sliders.length; k++)
      {
        if (sliders[k] == null) continue;
        if (!sliders[k].isEnabled())
          sliders[k].setVolume(0);
      }
      restart();
    }
  }
  
  // ================================================================
  
  class OscProxy implements OscEventListener {
    
    private static final int OSC_PORT = 13000;

    public boolean oscReadyForPlaying=true, oscReadyForOutgoingRequest;

    OSCiClient client;

    OscProxy(PApplet p) {
      client = new OSCiClient(p, OSC_PORT, this);
      client.connect();
    }
    
    public void message(String name, Object o) {
      if (DBUG_OSC) System.out.println("MachineSample.OscOut: /"+name+" "+o);
      client.message(name, o);
    }
    
    public void oscEvent(OscMessage msg)
    { 
      //System.out.println("MachineSampler.OscIn: "+new String(msg.getAddrPatternAsBytes()));
      Object[] inArgs = msg.arguments();
      if (msg.checkAddrPattern("/setPaused")) 
      {  
        boolean pause = Boolean.parseBoolean(inArgs[0]+"");
        if (!pause) {
          for (int i = 0; i < sliders.length; i++)
            sliders[i].unpause(pausedAt);
          pausedAt = -1;
        }
        else {
          for (int i = 0; i < sliders.length; i++)
            pausedAt = Math.max(pausedAt, sliders[i].pause());
        }
      }
      else if (msg.checkAddrPattern("/autoFire")) 
      {  
        started = false;
      }
      else if (msg.checkAddrPattern("/restart")) 
      {  
        restart();
      }
      else if (msg.checkAddrPattern("/nextSet")) 
      {  
        String s = "";
        for (int i = 0; i < inArgs.length; i++)
          s += inArgs[i]+" ";
        
        if (DBUG_OSC)  System.out.println
          ("MachineSampler.OscIn:  "+new String(msg.getAddrPatternAsBytes())+" ["+s+"]");
        
        if (inArgs != null && inArgs.length > 0) 
        {
          randomizedLoop = false;
          nextSet(inArgs);
        }
      }
    }

    public void oscStatus(OscStatus arg0)
    {
      throw new RuntimeException("Stub only");
    }
  }
  
  public float getMasterVolume()
  {
    return masterVolume;
  }
  
  public void setMasterVolume(float masterVolume)
  {
    this.masterVolume = masterVolume;
    for (int i = 0; i < sliders.length; i++) {
      sliders[i].updateVolumeFromKnob();
    }
  }

  public static void main(String[] args)
  {
    int W = 1200, H = 700;
    USE_OSC = DISABLE_USER_INPUT = false;
    System.out.println(System.getProperty("user.dir")+" v"+VERSION);
    new SamplerFrame(new MachineSampler(W, H), W, H);
  }



}// end
