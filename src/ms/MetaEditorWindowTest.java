package ms;
import processing.core.PApplet;

public class MetaEditorWindowTest extends PApplet
{
  @Override
  public void setup()
  {
    size(200,200);
    MetaEditorWindow mew = new MetaEditorWindow("x", this, 10, 10, 400, 400);
  }

  @Override
  public void draw()
  {
    background(255);
  }
}
