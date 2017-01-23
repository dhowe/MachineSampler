/* MyGUIButton - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package mkv.MyGUI;

import processing.core.PApplet;
import processing.core.PImage;

public class MyGUIButton extends MyGUIObject
{
  public static boolean SHOW_BUTTON_ARROW = false;

  public String _text;
  public PImage _icon;
  private boolean fixedSize;

  public MyGUIButton(PApplet papplet, int i, int i_0_)
  {
    super(papplet, i, i_0_);
    _width = 10;
    _height = 10;
    _text = "";
    fixedSize = true;
  }

  public MyGUIButton(PApplet papplet, int i, int i_1_, String string)
  {
    super(papplet, i, i_1_);
    _text = string;
    fixedSize = false;
  }

  public MyGUIButton(PApplet papplet, int i, int i_2_, int i_3_, int i_4_)
  {
    super(papplet, i, i_2_);
    _width = i_3_;
    _height = i_4_;
    _text = "";
    fixedSize = true;
  }

  public MyGUIButton(PApplet papplet, int i, int i_5_, String string, int i_6_, int i_7_)
  {
    super(papplet, i, i_5_);
    _text = string;
    _width = i_6_;
    _height = i_7_;
    fixedSize = true;
  }

  public MyGUIButton(PApplet papplet, int i, int i_8_, PImage pimage)
  {
    super(papplet, i, i_8_);
    _icon = pimage;
    _width = pimage.width + 2;
    _height = pimage.height + 2;
    _text = "";
    fixedSize = true;
  }

  public void setLabel(String string)
  {
    _text = string;
  }

  public void setLabel(String string, boolean bool)
  {
    _text = string;
    fixedSize = bool;
  }

  public void mouseReleased()
  {
    hover = checkForHit();
    if (hover && dragged)
    {
      MyGUIActionEvent myguiactionevent = new MyGUIActionEvent(this, _actionCommand);
      myguiactionevent.sendEvent(_root);
    }
    dragged = false;
  }

  public void drawStates()
  {
    MyGUIStyle myguistyle = _style;
    if (_style == null)
      myguistyle = _parent.getStyle();
    else
      myguistyle = _style;
    if (!fixedSize)
    {
      _root.textFont(myguistyle.font, (float) myguistyle.fontSize);
      _width = (PApplet.ceil(_root.textWidth(_text)) + 4 + myguistyle.padding);
      if (_width % 2 == 1)
        _width++;
      _height = myguistyle.fontSize + 2 + myguistyle.padding;
      if (_height % 2 == 1)
        _height++;
    }
    int i = PApplet.round((float) _width / 2.0F + (float) myguistyle.padding);
    int i_9_ = PApplet.round((float) _height / 2.0F + (float) myguistyle.padding);
    int i_10_ = 0;
    int i_11_ = 0;
    if (_icon != null)
    {
      i_10_ = PApplet.constrain(_icon.width, 2, _width - 2) / 2;
      i_11_ = PApplet.constrain(_icon.height, 2, _height - 2) / 2;
    }
    int i_12_ = myguistyle.fontSize / 2 - myguistyle.padding / 2 - 1;
    int i_13_ = 0;
    hover = checkForHit();
    _root.pushMatrix();
    _root.translate((float) _x, (float) _y);
    _root.scale(_scale);
    _root.rotate(PApplet.radians(_rotation));
    _root.rectMode(1);
    _root.strokeWeight((float) myguistyle.strokeWeight);
    _root.strokeJoin(8);
    _root.textFont(myguistyle.font, (float) myguistyle.fontSize);
    _root.textAlign(3);
    _root.imageMode(1);
    if (isDisabled())
    {
      _root.stroke(myguistyle.buttonShadow);
      _root.fill(myguistyle.disabled);
      _root.rect((float) -i, (float) -i_9_, (float) i, (float) i_9_);
      if (_text.length() > 0)
      {
        _root.fill(myguistyle.buttonText);
        _root.text(_text, (float) i_13_, (float) i_12_);
      }
      if (_icon != null)
        _root.image(_icon, (float) -i_10_, (float) -i_11_, (float) i_10_, (float) i_11_);
    }
    else if (dragged)
    {
      _root.stroke(myguistyle.buttonShadow);
      _root.fill(myguistyle.buttonFace);
      _root.rect((float) -i, (float) -i_9_, (float) i, (float) i_9_);
      _root.stroke(myguistyle.buttonShadow);
      _root.noFill();
      _root.strokeWeight((float) ((double) myguistyle.strokeWeight * 1.2));
      _root.quad((float) (-i + 1), (float) (-i_9_ + 1), (float) (i - 1), (float) (-i_9_ + 1), (float) (i - 1), (float) (i_9_ - 1), (float) (-i + 1), (float) (i_9_ - 1));
      if (_text.length() > 0)
      {
        _root.fill(myguistyle.buttonText);
        _root.text(_text, (float) i_13_, (float) i_12_);
      }
      if (_icon != null)
        _root.image(_icon, (float) -i_10_, (float) -i_11_, (float) i_10_, (float) i_11_);
    }
    else if (hover)
    {
      _root.stroke(myguistyle.buttonShadow);
      if (hasFocus())
      {
        _root.fill(myguistyle.buttonHighlight);
        _root.rect((float) -i, (float) -i_9_, (float) i, (float) i_9_);
      }
      else
      {
        _root.fill(myguistyle.buttonFace);
        _root.rect((float) -i, (float) -i_9_, (float) i, (float) i_9_);
        _root.stroke(myguistyle.buttonHighlight);
        _root.noFill();
        _root.strokeWeight((float) ((double) myguistyle.strokeWeight * 1.2));
        _root.quad((float) (-i + 1), (float) (-i_9_ + 1), (float) (i - 1), (float) (-i_9_ + 1), (float) (i - 1), (float) (i_9_ - 1), (float) (-i + 1), (float) (i_9_ - 1));
      }
      if (_text.length() > 0)
      {
        _root.fill(myguistyle.buttonText);
        _root.text(_text, (float) i_13_, (float) i_12_);
      }
      if (_icon != null)
        _root.image(_icon, (float) -i_10_, (float) -i_11_, (float) i_10_, (float) i_11_);
    }
    else if (hasFocus())
    {
      _root.stroke(myguistyle.buttonShadow);
      _root.fill(myguistyle.buttonFace);
      _root.rect((float) -i, (float) -i_9_, (float) i, (float) i_9_);
      _root.stroke(myguistyle.buttonHighlight);
      _root.noFill();
      _root.quad((float) (-i + 1), (float) (-i_9_ + 1), (float) (i - 1), (float) (-i_9_ + 1), (float) (i - 1), (float) (i_9_ - 1), (float) (-i + 1), (float) (i_9_ - 1));
      if (_text.length() > 0)
      {
        _root.fill(myguistyle.buttonText);
        _root.text(_text, (float) i_13_, (float) i_12_);
      }
      if (_icon != null)
        _root.image(_icon, (float) -i_10_, (float) -i_11_, (float) i_10_, (float) i_11_);
    }
    else
    {
      _root.stroke(myguistyle.buttonShadow);
      _root.fill(myguistyle.buttonFace);
      _root.rect((float) -i, (float) -i_9_, (float) i, (float) i_9_);
      if (_text.length() > 0)
      {
        _root.fill(myguistyle.buttonText);
        _root.text(_text, (float) i_13_, (float) i_12_);
      }
      if (_icon != null)
        _root.image(_icon, (float) -i_10_, (float) -i_11_, (float) i_10_, (float) i_11_);
    }

    if (SHOW_BUTTON_ARROW)
    {
      // draw an arrow
      _root.pushMatrix();
      _root.fill(40);
      _root.rotate(PApplet.PI / 2F);
      _root.text('>', 2, 1);
      _root.popMatrix();
    }

    _root.popMatrix();
  }
}
