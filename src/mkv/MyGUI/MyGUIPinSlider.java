/* MyGUIPinSlider - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package mkv.MyGUI;

import processing.core.PApplet;

public class MyGUIPinSlider extends MyGUIObject {
  private int reactionWidth;
  private int minValue;
  private int maxValue;
  public int value = 0;

  public MyGUIPinSlider(PApplet papplet, int i, int i_0_, int i_1_, int i_2_,
      int i_3_, int i_4_) {
    super(papplet, i, i_0_);
    _width = i_1_;
    _height = i_2_;
    minValue = i_3_;
    maxValue = i_4_;
    //reactionHeight = _height;
    reactionWidth = _width;
  }

  public void setValue(int i) {
    value = PApplet.constrain(i, minValue, maxValue);
  }

  public int getValue() {
    return value;
  }

  private void updateValue() {
    updateLocalMouse();
    int i = tmouseX + _width / 2;
    value = (int) ((float) i / (float) reactionWidth * (float) (maxValue - minValue))
        + minValue;
    value = PApplet.constrain(value, minValue, maxValue);
  }

  public void mousePressed() {
    if (hasFocus()) {
      dragged = true;
      hover = true;
      updateValue();
    }
  }

  public void mouseReleased() {
    if (dragged) {
      MyGUIActionEvent myguiactionevent = new MyGUIActionEvent(this,
          _actionCommand);
      myguiactionevent.sendEvent(_root);
      updateValue();
    }
    dragged = false;
  }

  public void mouseDragged() {
    if (dragged)
      updateValue();
  }

  public void draw() {
    if (_visible)
      drawStates();
  }

  public void drawStates() {
    MyGUIStyle myguistyle;
    if (_style == null)
      myguistyle = _parent.getStyle();
    else
      myguistyle = _style;
    if (_width % 2 == 1)
      _width++;
    if (_height % 2 == 1)
      _height++;
    int i = PApplet.round((float) _width / 2.0F + (float) myguistyle.padding);
    int i_5_ = PApplet.round((float) _height / 2.0F
        + (float) myguistyle.padding);
    int i_6_ = PApplet
        .round((float) _width / 4.0F + (float) myguistyle.padding);
    int i_7_ = PApplet.round((float) _height / 4.0F
        + (float) myguistyle.padding);
    int i_8_ = (PApplet.round((float) (value - minValue)
        / (float) (maxValue - minValue) * (float) _width) - i);
    hover = checkForHit();
    _root.pushMatrix();
    _root.translate((float) _x, (float) _y);
    _root.scale(_scale);
    _root.rotate(PApplet.radians(_rotation));
    _root.rectMode(1);
    _root.strokeWeight((float) myguistyle.strokeWeight);
    _root.stroke(myguistyle.shadow);
    int i_9_ = _width / 10;
    for (int i_10_ = 0; i_10_ < 11; i_10_++)
      _root.line((float) (-_width / 2 + i_9_ * i_10_), (float) i_7_,
          (float) (-_width / 2 + i_9_ * i_10_), (float) i_5_);
    if (isDisabled()) {
      _root.stroke(myguistyle.shadow);
      _root.fill(myguistyle.disabled);
      _root
          .rect((float) -i, (float) (-i_7_ / 2), (float) i, (float) (i_7_ / 2));
      _root.stroke(myguistyle.shadow);
      _root.translate((float) i_8_, 0.0F);
      _root.beginShape(20);
      _root.vertex((float) -i_7_, (float) -i_5_);
      _root.vertex((float) -i_7_, (float) i_7_);
      _root.vertex(0.0F, (float) i_5_);
      _root.vertex((float) i_7_, (float) i_7_);
      _root.vertex((float) i_7_, (float) -i_5_);
      _root.endShape();
    } else if (dragged) {
      _root.stroke(myguistyle.shadow);
      _root.fill(myguistyle.face);
      _root
          .rect((float) -i, (float) (-i_7_ / 2), (float) i, (float) (i_7_ / 2));
      _root.stroke(myguistyle.shadow);
      _root.translate((float) i_8_, 0.0F);
      _root.beginShape(20);
      _root.vertex((float) -i_7_, (float) -i_5_);
      _root.vertex((float) -i_7_, (float) i_7_);
      _root.vertex(0.0F, (float) i_5_);
      _root.vertex((float) i_7_, (float) i_7_);
      _root.vertex((float) i_7_, (float) -i_5_);
      _root.endShape();
      _root.stroke(myguistyle.shadow);
      _root.beginShape(20);
      _root.vertex((float) (-i_7_ + 1), (float) (-i_5_ + 1));
      _root.vertex((float) (-i_7_ + 1), (float) i_7_);
      _root.vertex(0.0F, (float) (i_5_ - 1));
      _root.vertex((float) (i_7_ - 1), (float) i_7_);
      _root.vertex((float) (i_7_ - 1), (float) (-i_5_ + 1));
      _root.endShape();
    } else if (hover) {
      _root.stroke(myguistyle.shadow);
      _root.fill(myguistyle.face);
      _root
          .rect((float) -i, (float) (-i_7_ / 2), (float) i, (float) (i_7_ / 2));
      _root.stroke(myguistyle.shadow);
      _root.translate((float) i_8_, 0.0F);
      if (hasFocus()) {
        _root.fill(myguistyle.highlight);
        _root.beginShape(20);
        _root.vertex((float) -i_7_, (float) -i_5_);
        _root.vertex((float) -i_7_, (float) i_7_);
        _root.vertex(0.0F, (float) i_5_);
        _root.vertex((float) i_7_, (float) i_7_);
        _root.vertex((float) i_7_, (float) -i_5_);
        _root.endShape();
      } else {
        _root.beginShape(20);
        _root.vertex((float) -i_7_, (float) -i_5_);
        _root.vertex((float) -i_7_, (float) i_7_);
        _root.vertex(0.0F, (float) i_5_);
        _root.vertex((float) i_7_, (float) i_7_);
        _root.vertex((float) i_7_, (float) -i_5_);
        _root.endShape();
        _root.stroke(myguistyle.highlight);
        _root.beginShape(20);
        _root.vertex((float) (-i_7_ + 1), (float) (-i_5_ + 1));
        _root.vertex((float) (-i_7_ + 1), (float) i_7_);
        _root.vertex(0.0F, (float) (i_5_ - 1));
        _root.vertex((float) (i_7_ - 1), (float) i_7_);
        _root.vertex((float) (i_7_ - 1), (float) (-i_5_ + 1));
        _root.endShape();
      }
    } else if (hasFocus()) {
      _root.stroke(myguistyle.shadow);
      _root.fill(myguistyle.face);
      _root
          .rect((float) -i, (float) (-i_7_ / 2), (float) i, (float) (i_7_ / 2));
      _root.translate((float) i_8_, 0.0F);
      _root.stroke(myguistyle.shadow);
      _root.beginShape(20);
      _root.vertex((float) -i_7_, (float) -i_5_);
      _root.vertex((float) -i_7_, (float) i_7_);
      _root.vertex(0.0F, (float) i_5_);
      _root.vertex((float) i_7_, (float) i_7_);
      _root.vertex((float) i_7_, (float) -i_5_);
      _root.endShape();
      _root.stroke(myguistyle.highlight);
      _root.beginShape(20);
      _root.vertex((float) (-i_7_ + 1), (float) (-i_5_ + 1));
      _root.vertex((float) (-i_7_ + 1), (float) i_7_);
      _root.vertex(0.0F, (float) (i_5_ - 1));
      _root.vertex((float) (i_7_ - 1), (float) i_7_);
      _root.vertex((float) (i_7_ - 1), (float) (-i_5_ + 1));
      _root.endShape();
    } else {
      _root.stroke(myguistyle.shadow);
      _root.fill(myguistyle.face);
      _root
          .rect((float) -i, (float) (-i_7_ / 2), (float) i, (float) (i_7_ / 2));
      _root.translate((float) i_8_, 0.0F);
      _root.beginShape(20);
      _root.vertex((float) -i_7_, (float) -i_5_);
      _root.vertex((float) -i_7_, (float) i_7_);
      _root.vertex(0.0F, (float) i_5_);
      _root.vertex((float) i_7_, (float) i_7_);
      _root.vertex((float) i_7_, (float) -i_5_);
      _root.endShape();
    }
    _root.popMatrix();
  }
}
