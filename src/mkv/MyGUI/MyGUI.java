/* MyGUI - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package mkv.MyGUI;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import processing.core.PApplet;

public class MyGUI extends MyGUIGroup {
  private PApplet _parent;

  public MyGUI(PApplet papplet) {
    super(papplet, 0, 0);
    papplet.registerMouseEvent(this);
    papplet.registerDraw(this);
    papplet.registerKeyEvent(this);
    _parent = papplet;
  }

  public MyGUI(PApplet papplet, int i) {
    super(papplet, 0, 0, i);
    papplet.registerMouseEvent(this);
    papplet.registerDraw(this);
    papplet.registerKeyEvent(this);
    _parent = papplet;
  }

  public MyGUI(PApplet papplet, int i, MyGUIStyle myguistyle) {
    super(papplet, 0, 0, i, myguistyle);
    papplet.registerMouseEvent(this);
    papplet.registerDraw(this);
    papplet.registerKeyEvent(this);
    _parent = papplet;
  }

  public MyGUI(PApplet papplet, MyGUIStyle myguistyle) {
    super(papplet, 0, 0, myguistyle);
    papplet.registerMouseEvent(this);
    papplet.registerDraw(this);
    papplet.registerKeyEvent(this);
    _parent = papplet;
  }

  public void mouseEvent(MouseEvent mouseevent) {
    if (mouseevent.getButton() != MouseEvent.BUTTON1)
      return; // added: dch (no right-clicks)
    if (mouseevent.getID() == 501)
      mousePressed();
    else if (mouseevent.getID() == 502)
      mouseReleased();
    else if (mouseevent.getID() == 506)
      mouseDragged();
  }

  public void keyEvent(KeyEvent keyevent) {
    if (keyevent.getID() == 401)
      keyPressed(keyevent);
    else if (keyevent.getID() == 402)
      keyReleased(keyevent);
    else if (keyevent.getID() == 400)
      keyTyped(keyevent);
  }

  public boolean isDisabled() {
    return _disabled;
  }

  public void updateLocalMouse() {
    if (customMouse) {
      if (_rotation != 0.0F) {
        if (_root != null) {
          /* empty */
        }
        float f = PApplet.cos(-PApplet.radians(_rotation));
        if (_root != null) {
          /* empty */
        }
        float f_0_ = PApplet.sin(-PApplet.radians(_rotation));
        int i = tmouseX - _x;
        int i_1_ = tmouseY - _y;
        tmouseX = PApplet.round((float) i * f - (float) i_1_ * f_0_ * _scale)
            + _x;
        tmouseY = PApplet.round((float) i * f_0_ + (float) i_1_ * f * _scale)
            + _y;
      }
    } else if (_rotation != 0.0F) {
      if (_root != null) {
        /* empty */
      }
      float f = PApplet.cos(-PApplet.radians(_rotation));
      if (_root != null) {
        /* empty */
      }
      float f_2_ = PApplet.sin(-PApplet.radians(_rotation));
      int i = _parent.mouseX - _x;
      int i_3_ = _parent.mouseY - _y;
      tmouseX = PApplet.round((float) i * f - (float) i_3_ * f_2_ * _scale);
      tmouseY = PApplet.round((float) i * f_2_ + (float) i_3_ * f * _scale);
    } else {
      tmouseX = PApplet.round((float) (_parent.mouseX - _x) * _scale);
      tmouseY = PApplet.round((float) (_parent.mouseY - _y) * _scale);
    }
  }
}
