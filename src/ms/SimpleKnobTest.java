package ms;
import java.awt.Color;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import processing.core.PApplet;


public class SimpleKnobTest extends PApplet implements ChangeListener
{
  Knob knob;
  Color purple = new Color(40);
  float[] BG = new float[] { 128, purple.getGreen(), purple.getBlue(), 255 };
  private Knob adjustingKnob;
  
  public void setup()
  {
    size(400,400);
    smooth();
    knob = new Knob(this, "vol", 200, 200); 
    this.knob.addListener(this);
  }
  
  public void draw()
  {
    fill(BG[0], BG[1], BG[2]);
    rect(0,0,width,height);
  }
  
  public void mousePressed()
  {
    for (int i = 0; i < Knob.instances.size(); i++)
    {
      Knob knob = (Knob) Knob.instances.get(i);
      if (knob.contains(mouseX, mouseY))
      {
        adjustingKnob = knob;
        adjustingKnob.initAdjust(mouseX, mouseY);
        break;
      }
    }
  }
   
  public void mouseDragged()
  {
    if (adjustingKnob != null)
      adjustingKnob.adjust(mouseX, mouseY, pmouseX, pmouseY);
  }
     
  public void mouseReleased()
  {
    if (adjustingKnob != null)
    {
      adjustingKnob.endAdjust(mouseX, mouseY);
      adjustingKnob = null;
    }
  }

  public void stateChanged(ChangeEvent e)
  {
    Knob k = (Knob) e.getSource();
    BG[0] = k.getValue()*256;
    System.out.println(BG[0]);
  }
}
