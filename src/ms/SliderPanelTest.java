package ms;
import javax.swing.JFrame;

import processing.core.PApplet;

public class SliderPanelTest extends PApplet
{
  private int background;
  private JFrame sframe;
  public void setup()
  {
    size(800,600);
  }
 
  public void draw()
  {
    background(background);
  }
  
  public void setBackground(Float background)
  {
    background *= 100;
    this.background = (int)background.floatValue();
  }
  
  public void mouseClicked()
  {
    if (sframe == null) {
      sframe = new JFrame();
      sframe.setLocation(400,400);
      sframe.add(new SliderPanel(this, new String[]{ "background", "height", "prob", "speed" }));
      sframe.pack();
    }
    sframe.setVisible(!sframe.isVisible());
      
  }

}
