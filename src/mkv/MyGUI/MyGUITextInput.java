/* MyGUITextInput - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package mkv.MyGUI;
import java.awt.event.KeyEvent;

import processing.core.PApplet;

public class MyGUITextInput extends MyGUIObject
{
    public String _value;
    public String _text;
    private boolean fixedSize;
    private boolean cursorVisible;
    private boolean keyPressed;
    private int cursorLetterPos;
    private int textOffsetPos;
    private int cursorPixelPos;
    private int cursorFlash;
    
    public MyGUITextInput(PApplet papplet, int i, int i_0_) {
	super(papplet, i, i_0_);
	_width = 100;
	fixedSize = false;
	_text = "";
	_value = "";
	cursorLetterPos = 0;
	textOffsetPos = 0;
    }
    
    public MyGUITextInput(PApplet papplet, int i, int i_1_, int i_2_,
			  int i_3_) {
	super(papplet, i, i_1_);
	_width = i_2_;
	_height = i_3_;
	fixedSize = true;
	_text = "";
	_value = "";
	cursorLetterPos = 0;
	textOffsetPos = 0;
    }
    
    public void setValue(String string) {
	_value = string;
	updateText();
    }
    
    public String getValue() {
	return _value;
    }
    
    public void mousePressed() {
	if (hasFocus()) {
	    dragged = true;
	    cursorVisible = true;
	    cursorFlash = 1;
	    updateText();
	}
    }
    
    private void updateText() {
	MyGUIStyle myguistyle = _style;
	if (_style == null)
	    myguistyle = _parent.getStyle();
	else
	    myguistyle = _style;
	_root.textFont(myguistyle.font, (float) myguistyle.fontSize);
	int i = (int) _root.textWidth(_value);
	cursorLetterPos
	    = PApplet.constrain(cursorLetterPos, 0, _value.length());
	if (cursorLetterPos < textOffsetPos)
	    textOffsetPos = cursorLetterPos;
	if (i <= _width)
	    textOffsetPos = 0;
	else if (cursorLetterPos > textOffsetPos) {
	    String string = _value.substring(textOffsetPos, cursorLetterPos);
	    while (_root.textWidth(string) > (float) _width) {
		textOffsetPos++;
		if (cursorLetterPos < _value.length())
		    string = _value.substring(textOffsetPos, cursorLetterPos);
		else
		    string = _value.substring(textOffsetPos);
	    }
	}
	if (i > _width) {
	    _text = _value.substring(textOffsetPos);
	    if (PApplet.floor(_root.textWidth(_text)) < _width) {
		do {
		    textOffsetPos--;
		    _text = _value.substring(textOffsetPos);
		} while (PApplet.ceil(_root.textWidth(_text)) < _width);
		textOffsetPos++;
		_text = _value.substring(textOffsetPos);
	    } else if (cursorLetterPos >= _value.length()) {
		while (PApplet.floor(_root.textWidth(_text))
		       >= _width - myguistyle.padding - 1) {
		    _text = _text.substring(1);
		    textOffsetPos++;
		}
	    } else {
		for (/**/;
		     (PApplet.floor(_root.textWidth(_text))
		      >= _width - myguistyle.padding - 1);
		     _text = _text.substring(0, _text.length() - 1)) {
		    /* empty */
		}
	    }
	} else
	    _text = _value.substring(textOffsetPos);
	String string
	    = new StringBuilder().append(_text).append("").toString();
	if (dragged) {
	    cursorPixelPos
		= PApplet.round((float) tmouseX + (float) (_width / 2));
	    cursorLetterPos = textOffsetPos + string.length();
	    for (/**/; PApplet.floor(_root.textWidth(string)) > cursorPixelPos;
		 string = string.substring(0, string.length() - 1))
		cursorLetterPos--;
	} else if (cursorLetterPos > 0)
	    string = _value.substring(textOffsetPos, cursorLetterPos);
	else
	    string = "";
	cursorPixelPos = PApplet.floor(_root.textWidth(string));
	cursorPixelPos = PApplet.constrain(cursorPixelPos, 0,
					   _width - myguistyle.padding - 1);
    }
    
    public void keyPressed(KeyEvent keyevent) {
	int i = keyevent.getKeyCode();
	switch (i) {
	case 36:
	    textOffsetPos = 0;
	    cursorLetterPos = 0;
	    break;
	case 35:
	    cursorLetterPos = _value.length();
	    textOffsetPos = _value.length();
	    break;
	case 37:
	    cursorLetterPos--;
	    break;
	case 39:
	    cursorLetterPos++;
	    break;
	}
	keyPressed = true;
	cursorVisible = true;
	cursorFlash = 1;
	updateText();
    }
    
    public void keyTyped(KeyEvent keyevent) {
	char c = keyevent.getKeyChar();
	switch (c) {
	case '\010':
	    if (_value.length() > 0) {
		if (cursorLetterPos < _value.length()) {
		    if (cursorLetterPos > 0) {
			String string
			    = new StringBuilder().append(_value).append("")
				  .toString();
			_value = new StringBuilder().append
				     (string.substring(0, cursorLetterPos - 1))
				     .append
				     (string.substring(cursorLetterPos))
				     .toString();
		    }
		} else {
		    int i = _value.length();
		    _value = _value.substring(0, i - 1);
		}
		if (cursorLetterPos > 0)
		    cursorLetterPos--;
	    }
	    break;
	case '\u007f':
	    if (_value.length() > 0 && cursorLetterPos < _value.length()) {
		if (cursorLetterPos > 0) {
		    String string = new StringBuilder().append(_value).append
					("").toString();
		    _value = new StringBuilder().append
				 (string.substring(0, cursorLetterPos)).append
				 (string.substring(cursorLetterPos + 1))
				 .toString();
		} else
		    _value = _value.substring(1);
	    }
	    break;
	case '\n': {
	    MyGUIActionEvent myguiactionevent
		= new MyGUIActionEvent(this, _actionCommand);
	    myguiactionevent.sendEvent(_root);
	    break;
	}
	default: {
	    String string
		= new StringBuilder().append(_value).append("").toString();
	    _value = new StringBuilder().append
			 (string.substring(0, cursorLetterPos)).append
			 (keyevent.getKeyChar()).append
			 (string.substring(cursorLetterPos)).toString();
	    cursorLetterPos++;
	}
	}
	updateText();
    }
    
    public void keyReleased(KeyEvent keyevent) {
	keyPressed = false;
    }
    
    public void drawStates() {
	MyGUIStyle myguistyle = _style;
	if (_style == null)
	    myguistyle = _parent.getStyle();
	else
	    myguistyle = _style;
	if (!fixedSize) {
	    _root.textFont(myguistyle.font, (float) myguistyle.fontSize);
	    _height = myguistyle.fontSize + 2 + myguistyle.padding;
	    if (_height % 2 == 1)
		_height++;
	}
	int i = PApplet
		    .round((float) _width / 2.0F + (float) myguistyle.padding);
	int i_4_ = PApplet.round((float) _height / 2.0F
				 + (float) myguistyle.padding);
	int i_5_ = myguistyle.fontSize / 2 - myguistyle.padding / 2;
	boolean bool = false;
	hover = checkForHit();
	_root.pushMatrix();
	_root.translate((float) _x, (float) _y);
	_root.scale(_scale);
	_root.rotate(PApplet.radians(_rotation));
	_root.rectMode(1);
	_root.strokeWeight((float) myguistyle.strokeWeight);
	_root.textFont(myguistyle.font, (float) myguistyle.fontSize);
	_root.textAlign(37);
	if (isDisabled()) {
	    _root.stroke(myguistyle.shadow);
	    _root.fill(myguistyle.disabled);
	    _root.rect((float) -i, (float) -i_4_, (float) i, (float) i_4_);
	} else if (hover) {
	    _root.stroke(myguistyle.highlight);
	    _root.fill(myguistyle.scrollBar);
	    _root.rect((float) -i, (float) -i_4_, (float) i, (float) i_4_);
	} else {
	    _root.stroke(myguistyle.highlight);
	    _root.fill(myguistyle.scrollBar);
	    _root.rect((float) -i, (float) -i_4_, (float) i, (float) i_4_);
	}
	if (hasFocus()) {
	    cursorFlash++;
	    if (cursorFlash % 20 == 0) {
		cursorFlash = 1;
		cursorVisible = !cursorVisible;
	    }
	    if (cursorVisible) {
		_root.stroke(myguistyle.shadow);
		_root.line((float) (cursorPixelPos - i + myguistyle.padding
				    + 1),
			   (float) (-i_4_ + 2),
			   (float) (cursorPixelPos - i + myguistyle.padding
				    + 1),
			   (float) (i_4_ - 2));
	    }
	}
	_root.fill(myguistyle.buttonText);
	_root.text(_text, (float) (-i + myguistyle.padding + 1), (float) -i_5_,
		   (float) _width, (float) _height);
	_root.popMatrix();
    }
}
