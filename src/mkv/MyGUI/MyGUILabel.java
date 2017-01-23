/* MyGUILabel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package mkv.MyGUI;
import processing.core.PApplet;

public class MyGUILabel extends MyGUIObject
{
    public String _text;
    public int alignment;
    
    public MyGUILabel(PApplet papplet, String string, int i, int i_0_) {
	super(papplet, i, i_0_);
	_text = string;
	alignment = 37;
    }
    
    public MyGUILabel(PApplet papplet, String string, int i, int i_1_,
		      int i_2_, int i_3_) {
	super(papplet, i, i_1_);
	_text = string;
	_width = i_2_;
	_height = i_3_;
	alignment = 37;
    }
    
    public void align(int i) {
	alignment = i;
    }
    
    public void setAlignment(int i) {
	alignment = i;
    }
    
    public void setText(String string) {
	_text = string;
    }
    
    public void drawStates() {
	MyGUIStyle myguistyle = _style;
	if (_style == null)
	    myguistyle = _parent.getStyle();
	else
	    myguistyle = _style;
	int i = myguistyle.fontSize / 2 - myguistyle.padding / 2 - 1;
	boolean bool = false;
	_root.pushMatrix();
	_root.translate((float) _x, (float) _y);
	_root.scale(_scale);
	_root.rotate(PApplet.radians(_rotation));
	_root.textFont(myguistyle.font, (float) myguistyle.fontSize);
	_root.textAlign(alignment);
	_root.fill(myguistyle.buttonText);
	if (_width > 0 && _height > 0)
	    _root.text(_text, 0.0F, (float) i, (float) _width,
		       (float) _height);
	else
	    _root.text(_text, 0.0F, (float) i);
	_root.popMatrix();
    }
}
