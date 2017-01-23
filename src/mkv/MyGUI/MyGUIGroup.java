/* MyGUIGroup - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package mkv.MyGUI;

import java.awt.event.KeyEvent;

import processing.core.PApplet;

public class MyGUIGroup extends MyGUIObject {
  private MyGUIObject[] elements;
  private int focusIndex = -1;
  private int numElements = 0;
  protected boolean customMouse = false;

  public MyGUIGroup(PApplet papplet) {
    super(papplet, 0, 0);
    elements = new MyGUIObject[25];
    _style = new MyGUIStyle(_root);
  }

  public MyGUIGroup(PApplet papplet, int i, int i_0_) {
    super(papplet, i, i_0_);
    elements = new MyGUIObject[25];
    _style = new MyGUIStyle(_root);
  }

  public MyGUIGroup(PApplet papplet, int i, int i_1_, int i_2_) {
    super(papplet, i, i_1_);
    elements = new MyGUIObject[i_2_];
    _style = new MyGUIStyle(_root);
  }

  public MyGUIGroup(PApplet papplet, int i, int i_3_, int i_4_,
      MyGUIStyle myguistyle) {
    super(papplet, i, i_3_);
    elements = new MyGUIObject[i_4_];
    _style = myguistyle;
  }

  public MyGUIGroup(PApplet papplet, int i, int i_5_, MyGUIStyle myguistyle) {
    super(papplet, i, i_5_);
    elements = new MyGUIObject[25];
    _style = myguistyle;
  }

  public int add(MyGUIObject myguiobject) {
    for (int i = 0; i < numElements; i++) {
      if (elements[i] == null) {
        elements[i] = myguiobject;
        elements[i].setParent(this);
        elements[i].setID(i);
        return i;
      }
    }
    if (numElements < elements.length) {
      int i = numElements;
      numElements++;
      elements[i] = myguiobject;
      elements[i].setParent(this);
      elements[i].setID(i);
      return i;
    }
    return -1;
  }

  public void remove(int i) {
    if (i > 0 && i <= numElements)
      elements[i] = null;
  }

  public void setStyle(MyGUIStyle myguistyle) {
    _style = myguistyle;
  }

  public void useCustomMouse(boolean bool) {
    customMouse = bool;
  }

  public void setMouseCoords(int i, int i_6_) {
    tmouseX = i;
    tmouseY = i_6_;
    updateLocalMouse();
  }

  public MyGUIObject getFocused() {
    if (focusIndex != -1)
      return elements[focusIndex];
    return null;
  }

  public int getFocusIndex() {
    return focusIndex;
  }

  public boolean setFocusIndex(int i) {
    focusIndex = i;
    return true;
  }

  public MyGUIObject get(int i) {
    if (i < numElements && i > 0)
      return elements[focusIndex];
    return null;
  }

  public boolean isEmpty() {
    if (numElements == 0)
      return true;
    for (int i = 0; i < numElements; i++) {
      if (elements[i] == null)
        return true;
    }
    return false;
  }

  public boolean isFull() {
    if (numElements != elements.length)
      return false;
    for (int i = 0; i < numElements; i++) {
      if (elements[i] == null)
        return false;
    }
    return true;
  }

  public void mousePressed() {
    if (_visible && !_disabled) {
      focusIndex = -1;
      for (int i = numElements - 1; i >= 0; i--) {
        if (elements[i] != null && elements[i].checkForHit()
            && elements[i]._visible && !elements[i]._disabled) {
          focusIndex = i;
          i = -1;
          if (_parent != null)
            _parent.setFocusIndex(_id);
        }
      }
      for (int i = 0; i < numElements; i++) {
        if (elements[i] != null && !elements[i]._disabled
            && elements[i]._visible)
          elements[i].mousePressed();
      }
    }
  }

  public void mouseReleased() {
    if (_visible && !_disabled) {
      for (int i = 0; i < numElements; i++) {
        if (elements[i] != null && !elements[i]._disabled
            && elements[i]._visible)
          elements[i].mouseReleased();
      }
    }
  }

  public void mouseDragged() {
    if (_visible && !_disabled) {
      for (int i = 0; i < numElements; i++) {
        if (elements[i] != null && !elements[i]._disabled
            && elements[i]._visible)
          elements[i].mouseDragged();
      }
    }
  }

  public void keyPressed(KeyEvent keyevent) {
    if (_visible && !_disabled && focusIndex >= 0 && focusIndex < numElements
        && elements[focusIndex] != null && elements[focusIndex]._visible
        && !elements[focusIndex]._disabled)
      elements[focusIndex].keyPressed(keyevent);
  }

  public void keyReleased(KeyEvent keyevent) {
    if (_visible && !_disabled && focusIndex >= 0 && focusIndex < numElements
        && elements[focusIndex] != null && elements[focusIndex]._visible
        && !elements[focusIndex]._disabled)
      elements[focusIndex].keyReleased(keyevent);
  }

  public void keyTyped(KeyEvent keyevent) {
    if (_visible && !_disabled && focusIndex >= 0 && focusIndex < numElements
        && elements[focusIndex] != null && elements[focusIndex]._visible
        && !elements[focusIndex]._disabled)
      elements[focusIndex].keyTyped(keyevent);
  }

  public void draw() {
    if (_visible) {
      _root.pushMatrix();
      _root.translate((float) _x, (float) _y);
      _root.rotate(PApplet.radians(_rotation));
      _root.scale(_scale);
      updateLocalMouse();
      for (int i = 0; i < numElements; i++) {
        if (i != focusIndex && elements[i] != null && elements[i]._visible)
          elements[i].draw();
      }
      if (focusIndex >= 0 && focusIndex < numElements
          && elements[focusIndex] != null && elements[focusIndex]._visible)
        elements[focusIndex].draw();
      _root.popMatrix();
    }
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
        float f_7_ = PApplet.sin(-PApplet.radians(_rotation));
        int i = tmouseX - _x;
        int i_8_ = tmouseY - _y;
        tmouseX = PApplet.round((float) i * f
            - ((float) i_8_ * f_7_ * _parent._scale));
        tmouseY = PApplet.round((float) i * f_7_ + (float) i_8_ * f
            * _parent._scale);
      }
    } else if (_rotation != 0.0F) {
      if (_root != null) {
        /* empty */
      }
      float f = PApplet.cos(-PApplet.radians(_rotation));
      if (_root != null) {
        /* empty */
      }
      float f_9_ = PApplet.sin(-PApplet.radians(_rotation));
      int i = _parent.tmouseX - _x;
      int i_10_ = _parent.tmouseY - _y;
      tmouseX = PApplet.round((float) i * f - (float) i_10_ * f_9_
          * _parent._scale);
      tmouseY = PApplet.round((float) i * f_9_ + (float) i_10_ * f
          * _parent._scale);
    } else {
      tmouseX = PApplet.round((float) (_parent.tmouseX - _x) * _parent._scale);
      tmouseY = PApplet.round((float) (_parent.tmouseY - _y) * _parent._scale);
    }
  }
}
