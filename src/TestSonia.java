import pitaru.sonia_v2_9.*;
import processing.core.PApplet;

public class TestSonia extends PApplet
{
  //String file = "beat.wav";
  //String file = "/Users/dhowe/Documents/eclipse-workspace/GenNarrator/EOE_Generative_Archive/EoE_Vocal1.wav";
  //String file = "/Users/dhowe/Documents/eclipse-workspace/MachineSampler/E0E_Generative_Archive/EoE_ Bass drone2.wav"; 
    //"/Users/dhowe/Documents/eclipse-workspace/GenNarrator/MomGen36/Machine_of_Machines11.wav";
  String file = "/Users/dhowe/Desktop/EoE_Bass_drone.wav";
  
  Sample dingdong;
  public void setup()
  {
    size(200, 200);
    Sonia.start(this);
    dingdong = new Sample(file);
    dingdong.repeat();
  }

  public void draw()
  {
    background(255);
  }

  public void stop()
  {
    Sonia.stop();
    super.stop();
  }
  
  public static void main(String[] args)
  {
    PApplet.main(new String[]{TestSonia.class.getName()});
  }
}
