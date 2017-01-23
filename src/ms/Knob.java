package ms;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import processing.core.PApplet;
import processing.core.PVector;

public class Knob 
{
  // added -- DCH 8/20
  private static final float SCALE = 1.5f;
  private static final float MIN_SIZE = 8;
  
  public static List instances = new ArrayList();
  
  PApplet p;
  String label = "";
  float knobFontSize = 11;
  float labelFontSize = 9;
  float knobOffset = .5f;
  boolean showRevs = false;
  private List listeners;
  
  public float maxRevs = .5f; // This many revolutions allowed

  public float knobSize = MIN_SIZE; // Radius of knob body
  
  // -----------------
  
  public PVector position;
  
  public boolean selected = false;

  float angle;  // Current angle
  private float adjustAngle = angle; // Angle attempting to be set by mouse movement
  
  float angleSmallStep = PApplet.TWO_PI * 0.1f;
  float angleLargeStep = PApplet.TWO_PI;

  boolean adjusting; // Set true when adjusting (not automating);
  
  boolean automate;          // Set true when automating
  float automateTargetAngle; // Target angle of automation
  float automateEase = 0.1f; // Easing factor for automation
  float automateEpsilon = 0.02f; // Automation will "finish" if this close or closer
  
  boolean snap = true;     // Snapping on?
  boolean snapped = false; // Set true when snapped (at snap point or min/max)
  float snapTolerance = 0.06f; // Snap to full revolution if within this angle (radians)
  
  public Knob(PApplet p, float posX, float posY)
  {
    this(p, null, posX, posY);
  }
  
  public Knob(PApplet p, String label, float posX, float posY)
  {
    this.p = p;
    if (label != null)
      this.label = label;
    position = new PVector(posX, posY);
    setAngle(0);
    instances.add(this);
    p.registerDraw(this);
  }

  public void update()
  {
    if (automate)
    {
      adjustAngle = PApplet.lerp(adjustAngle, automateTargetAngle, automateEase);
      
      // Complete automation if close to target
      if (PApplet.abs(adjustAngle - automateTargetAngle) < automateEpsilon)
      {
        adjustAngle = automateTargetAngle;
        automate = false;
      }
      
      setAngle(adjustAngle);
    }
  }

  public void draw()
  {
    this.update();
    
    p.ellipseMode(PApplet.RADIUS);
    p.textAlign(PApplet.CENTER, PApplet.CENTER);
    
    p.pushStyle();
    p.pushMatrix();
    
    // LABEL and VALUE
    
    //  p.textSize(knobFontSize);
    
    if (knobSize>MIN_SIZE) {
      
      // add a bg rect?
      p.textSize(knobFontSize);
      p.fill(textColor);
      p.text(PApplet.nfs(getValue(), 0, 2), 
        position.x, position.y + knobSize + knobFontSize - 4);
    }
    else {
      p.fill(textColor);
      p.textSize(labelFontSize);
      p.text(label, position.x, position.y + knobSize + labelFontSize - 3);
    }
  
    p.translate(position.x, position.y);
    p.rotate(-PApplet.PI/2); // Make "up" the zero angle (like "12 oclock")
  
    float maxAngle = maxRevs * PApplet.TWO_PI;
    
    float cuffSize = knobSize * cuffRelativeSize;
    float cuffAngle = PApplet.TWO_PI / 3.0f;

    // Fill the cuff based on revolutions away from zero (cuff)
    int maxColor = angle < 0 ? cuffColorNeg : cuffColorPos; 
    //p.fill(p.lerpColor(cuffColorZero, maxColor, PApplet.abs(angle) / maxAngle)); // innerColor
   //p.fill(0,0,40);
    p.noFill();
    p.stroke(cuffOutlineColor); // outerColor
    p.strokeWeight(2);  
    if (knobSize > MIN_SIZE)
      p.arc(0, 0, cuffSize, cuffSize, PApplet.TWO_PI - cuffAngle, PApplet.TWO_PI + cuffAngle);

    // (half-way marker)
    float a, ca, sa, r1, r2; // angle, cos, sin, inner/outer radius
    if (knobSize>MIN_SIZE)
      for (int i = 0; i <= maxRevs; i++)
      {
        a = (float) i / maxRevs * cuffAngle;
        ca = PApplet.cos(a);
        sa = PApplet.sin(a);
        
        r1 = (i == maxRevs) ? knobSize : cuffSize + 3;
        r2 = cuffSize + 6;
  
        p.line(r1 * ca, r1 * sa, r2 * ca, r2 * sa);
        p.line(r1 * ca, -r1 * sa, r2 * ca, -r2 * sa);
      }


    a = angle / maxAngle * cuffAngle;
    ca = PApplet.cos(a);
    sa = PApplet.sin(a);
    
    float x1 = knobSize * ca;
    float y1 = knobSize * sa;
    float x2 = cuffSize * ca;
    float y2 = cuffSize * sa;

    // position marker outline
    p.strokeWeight(3);
    if (knobSize>MIN_SIZE) //dch 
      p.strokeWeight(7);
    p.stroke(revsMarkerColor);
    p.line(x1, y1, x2, y2);

    // position marker inside
    
/*    if (snapped) // dch
      p.stroke(revsMarkerSnapColor);
    else
      p.stroke(0,0,40); // dch
*/    
    p.stroke(100);//200,50,50);
    //p.strokeWeight(2);
    if (knobSize>MIN_SIZE) {//dch 
      p.strokeWeight(3);
      //p.stroke(0xffeeee66);
      //p.stroke(revsMarkerHilightColor);
      p.line(x1, y1, x2, y2);
    }


    // Knob body
    
    /*    p.fill(adjusting ? knobAdjustingColor :
               automate  ? knobAutomateColor  :
               selected  ? knobSelectedColor : knobBodyColor)*/;
        //p.fill(0,0,40);
               p.noFill();        
    if (knobSize>MIN_SIZE) //dch 
      p.fill(100);
    p.stroke(knobOutlineColor);
    p.strokeWeight(2);
    p.ellipse(0, 0, knobSize, knobSize);

    // Knob angle marker
    ca = PApplet.cos(angle);
    sa = PApplet.sin(angle);
    r1 = knobSize * 0.35f;
    r2 = knobSize * 0.85f;
  
    p.stroke(snapped   ? knobMarkerSnapColor :
           adjusting   ? knobMarkerAdjustingColor
                       : knobMarkerColor);
    p.strokeWeight(4);
    if (showRevs) // false
      p.line(r1 * ca, r1 * sa, r2 * ca, r2 * sa);
    
    p.popMatrix();
    p.popStyle();
    
    p.textAlign(PApplet.LEFT);
  }
  
  // Determine if coordinates are close enough to knob for adjustment
  public boolean contains(int x, int y)
  {
    return PApplet.dist(x, y, position.x, position.y) <= knobSize * cuffRelativeSize * 1.2 + 15;
  }

  // Call to initiate value adjustment (eg on mouse click)
  public void initAdjust(int mx, int my)
  {
    selected = true;
    automate = false;
    adjusting = true;
    adjustAngle = angle;
    knobSize *= SCALE;
  }

  // Call to enact adjustment (eg on mouse move)
  // Takes current mouse position and previous mouse position  
  public void adjust(int mx, int my, int px, int py)
  {
    float offx = mx - position.x;
    float offy = my - position.y;
    if (PApplet.abs(offx) + PApplet.abs(offy) < 3)
      return;
    
    float thisAngle = PApplet.atan2(offy, offx);
    float prevAngle = PApplet.atan2(py - position.y, px - position.x);
    float diff = thisAngle - prevAngle;
  
    if (diff >= PApplet.PI) diff -= PApplet.TWO_PI;
    else if (diff <= -PApplet.PI) diff += PApplet.TWO_PI;
  
    adjustAngle += diff;
    
    setAngle(adjustAngle);
    
    if (listeners != null) {
      for (int i = 0; i < listeners.size(); i++)
      {
        ChangeListener cl = (ChangeListener)listeners.get(i);
        cl.stateChanged(new ChangeEvent(this));
      }
    }
  }
  
  // Call to complete adjustment (e.g., on mouse release)
  public void endAdjust(int mx, int my)
  {
    knobSize /= SCALE;
    adjusting = false;
  }
  
  public float maxAngle()
  {
    return maxRevs * PApplet.TWO_PI;
  }
  
  public void automate(float targetAngle)
  {
    if (!adjusting)
    {
      float maxAngle = maxAngle();
      adjustAngle = angle;
      automateTargetAngle = PApplet.constrain(targetAngle, -maxAngle, maxAngle);
      automate = true;
    }
  }
  
  public void setAngle(float setAngle)
  {
    float absAngle = PApplet.abs(setAngle);
    int sign = setAngle < 0 ? -1 : 1;
    int revs = PApplet.round(absAngle / PApplet.TWO_PI);
    float off = absAngle - revs * PApplet.TWO_PI;
  
    if (snap && PApplet.abs(off) < snapTolerance)
    {
      angle = sign * revs * PApplet.TWO_PI;
      snapped = true;
    }
    else
    {
      angle = setAngle;
      snapped = false;
    }

    // Make sure we don't rotate past the max rotations
    float maxAngle = maxRevs * PApplet.TWO_PI;
    if (angle < -maxAngle)
    {
      angle = -maxAngle;
      snapped = true;
    }
    else if (angle > maxAngle)
    {
      angle = maxAngle;
      snapped = true;
    }
  }
  
  public float getValue()
  {
    return angle / PApplet.TWO_PI + knobOffset;
  }

  public void keyPressed(int key, int keyCode)
  {
    float step = 0;
    if (key == PApplet.CODED) switch(keyCode)
    {
      case PApplet.UP:    step = +angleSmallStep; break;
      case PApplet.DOWN:  step = -angleSmallStep; break;
      case PApplet.LEFT:  step = -angleLargeStep; break;
      case PApplet.RIGHT: step = +angleLargeStep; break;
    }
    else switch(key)
    {
      case 'r':
        automate(maxRevs * p.random(-PApplet.TWO_PI, PApplet.TWO_PI));
        break;
    }
    
    if (step != 0) automate((automate ? automateTargetAngle : adjustAngle) + step);
  }

  public void addListener(ChangeListener cl)
  {
    if (listeners == null)
      listeners = new ArrayList();
    listeners.add(cl);
  }
  
  int knobBodyColor       = 0xffffffff;  // White
  //int knobOutlineColor  = 0xff000000;  // Black
  int knobOutlineColor    = 0xffffffff;  // ????
  int knobMarkerColor     = 0xffcc9900;  // Orange
  int knobMarkerSnapColor = 0xffff0000;  // Red

  //int knobSelectedColor   = 0xffffff66;      // Pale yellow
  int knobSelectedColor   =  0xffdddddd;      // ????
  //int knobAdjustingColor  = 0xffffff00;      // Yellow
  int knobAdjustingColor  = 0xffcccccc;      // ???
  //int knobMarkerAdjustingColor = 0xff000000; // Black
  int knobMarkerAdjustingColor = 0xff000000; // ????
  int knobAutomateColor   = 0xff6666ff;      // Blue

  float cuffRelativeSize = 1.3f;      // Size of cuff in proportion to knob (should be >1 !)
  int cuffColorNeg     = 0xff00aaff;  // Blue-cyan
  int cuffColorPos     = 0xffffaa00;  // Orange
  int cuffColorZero    = 0xff336633;  // Dark green
  
  //int cuffOutlineColor = 0xff000000;  // Black
  int cuffOutlineColor = 0xffffffff;  // ????
  int cuffDivsColor    = 0xff333333;  // Dark grey
  
  //int revsMarkerColor      = 0xff000000; // Black
  int revsMarkerColor        = 0xffffffff; // ????
  //int revsMarkerHilightColor = 0xffffffff; // White
  int revsMarkerHilightColor = 0xffff0000; // ????
  int revsMarkerSnapColor    = 0xffffff00; // Yellow
  
  //int textColor = 0xff000000; // Black
  int textColor = 0xffffffff; //????
  
}