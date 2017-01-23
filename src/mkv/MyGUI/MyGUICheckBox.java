/* MyGUICheckBox - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package mkv.MyGUI;
import processing.core.PApplet;

public class MyGUICheckBox extends MyGUIObject
{
    private boolean _checked;
    private int _boxsize;
    private String _text;
    private boolean fixedSize;
    
    public MyGUICheckBox(PApplet papplet, int i, int i_0_) {
	super(papplet, i, i_0_);
	_boxsize = 14;
	_width = _boxsize;
	_height = _boxsize;
	_text = "";
	fixedSize = true;
    }
    
    public MyGUICheckBox(PApplet papplet, int i, int i_1_, int i_2_,
			 int i_3_) {
	super(papplet, i, i_1_);
	_width = i_2_;
	_height = i_3_;
	if (i_2_ >= i_3_)
	    _boxsize = i_3_;
	else
	    _boxsize = i_2_;
	_text = "";
	fixedSize = true;
    }
    
    public MyGUICheckBox(PApplet papplet, int i, int i_4_, String string) {
	super(papplet, i, i_4_);
	_text = string;
	fixedSize = false;
    }
    
    public MyGUICheckBox(PApplet papplet, int i, int i_5_, String string,
			 int i_6_, int i_7_) {
	super(papplet, i, i_5_);
	_text = string;
	_width = i_6_;
	_height = i_7_;
	if (i_6_ >= i_7_)
	    _boxsize = i_7_;
	else
	    _boxsize = i_6_;
	fixedSize = true;
    }
    
    public boolean isChecked() {
	return _checked;
    }
    
    public void checked(boolean bool) {
	_checked = bool;
    }
    
    public void setLabel(String string) {
	_text = string;
    }
    
    public void setLabel(String string, boolean bool) {
	_text = string;
	fixedSize = bool;
    }
    
    public void mouseReleased() {
	hover = checkForHit();
	if (hover && dragged) {
	    MyGUIActionEvent myguiactionevent
		= new MyGUIActionEvent(this, _actionCommand);
	    myguiactionevent.sendEvent(_root);
	    _checked = !_checked;
	}
	dragged = false;
    }
    
    public void drawStates() {
	MyGUIStyle myguistyle = _style;
	if (_style == null)
	    myguistyle = _parent.getStyle();
	else
	    myguistyle = _style;
	if (!fixedSize) {
	    _root.textFont(myguistyle.font, (float) myguistyle.fontSize);
	    _width = (PApplet.ceil(_root.textWidth(_text)) + 4
		      + myguistyle.padding);
	    _height = myguistyle.fontSize + 2 + myguistyle.padding;
	    if (_width >= _height)
		_boxsize = myguistyle.fontSize;
	    else
		_boxsize = _width;
	}
	int i = PApplet.round((float) _boxsize / 2.0F
			      + (float) myguistyle.padding);
	int i_8_ = myguistyle.fontSize / 2 - myguistyle.padding / 2 - 1;
	int i_9_ = _boxsize;
	hover = checkForHit();
	_root.pushMatrix();
	_root.translate((float) _x, (float) _y);
	_root.scale(_scale);
	_root.rotate(PApplet.radians(_rotation));
	_root.rectMode(1);
	_root.strokeWeight((float) myguistyle.strokeWeight);
	_root.stroke(myguistyle.shadow);
	_root.textFont(myguistyle.font, (float) myguistyle.fontSize);
	_root.textAlign(37);
	if (isDisabled()) {
	    _root.stroke(myguistyle.shadow);
	    _root.fill(myguistyle.disabled);
	    _root.rect((float) -i, (float) -i, (float) i, (float) i);
	} else if (dragged) {
	    _root.fill(myguistyle.face);
	    _root.rect((float) -i, (float) -i, (float) i, (float) i);
	    _root.noFill();
	    _root.rect((float) (-i + 1), (float) (-i + 1), (float) (i - 1),
		       (float) (i - 1));
	} else if (hover) {
	    _root.fill(myguistyle.scrollBar);
	    _root.rect((float) -i, (float) -i, (float) i, (float) i);
	    _root.stroke(myguistyle.highlight);
	    _root.noFill();
	    _root.rect((float) (-i + 1), (float) (-i + 1), (float) (i - 1),
		       (float) (i - 1));
	} else if (hasFocus()) {
	    _root.fill(myguistyle.scrollBar);
	    _root.rect((float) -i, (float) -i, (float) i, (float) i);
	    _root.stroke(myguistyle.highlight);
	    _root.noFill();
	    _root.rect((float) (-i + 1), (float) (-i + 1), (float) (i - 1),
		       (float) (i - 1));
	} else {
	    _root.fill(myguistyle.scrollBar);
	    _root.rect((float) -i, (float) -i, (float) i, (float) i);
	}
	int i_10_ = 2 + myguistyle.padding;
	_root.strokeWeight((float) PApplet.floor(PApplet.sqrt((float) _boxsize
							      * 0.9F)));
	if (_checked) {
	    _root.stroke(myguistyle.icon);
	    _root.line((float) (-i + i_10_), (float) (-i + i_10_),
		       (float) (i - i_10_), (float) (i - i_10_));
	    _root.line((float) (i - i_10_), (float) (-i + i_10_),
		       (float) (-i + i_10_), (float) (i - i_10_));
	} else if (hover && !_disabled) {
	    _root.stroke(myguistyle.highlight);
	    _root.line((float) (-i + i_10_), (float) (-i + i_10_),
		       (float) (i - i_10_), (float) (i - i_10_));
	    _root.line((float) (i - i_10_), (float) (-i + i_10_),
		       (float) (-i + i_10_), (float) (i - i_10_));
	}
	if (_text.length() > 0) {
	    _root.fill(myguistyle.buttonText);
	    _root.text(_text, (float) i_9_, (float) i_8_);
	}
	_root.popMatrix();
    }
    
    public boolean checkForHit() {
	updateLocalMouse();
	int i = 0;
	if (fixedSize)
	    i = _width - _boxsize;
	else if (_text.length() > 0)
	    i = _width;
	if ((float) tmouseX >= (float) (-_boxsize / 2) * _scale
	    && (float) tmouseY >= (float) (-_height / 2) * _scale
	    && (float) tmouseX <= (float) (_boxsize / 2 + i) * _scale
	    && (float) tmouseY <= (float) (_height / 2) * _scale) {
	    lastHover = true;
	    return true;
	}
	lastHover = false;
	return false;
    }
}
