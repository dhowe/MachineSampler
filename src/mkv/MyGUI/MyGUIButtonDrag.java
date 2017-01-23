/* MyGUIButtonDrag - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package mkv.MyGUI;
import processing.core.PApplet;
import processing.core.PImage;

public class MyGUIButtonDrag extends MyGUIButton
{
    private int xoff;
    private int yoff;
    private int minAreax;
    private int minAreay;
    private int maxAreax;
    private int maxAreay;
    
    public MyGUIButtonDrag(PApplet papplet, int i, int i_0_, int i_1_,
			   int i_2_) {
	super(papplet, i, i_0_, i_1_, i_2_);
    }
    
    public MyGUIButtonDrag(PApplet papplet, int i, int i_3_, PImage pimage) {
	super(papplet, i, i_3_, pimage);
    }
    
    public void clearDragArea() {
	setDragArea(0, 0, 0, 0);
    }
    
    public void setDragArea(int i, int i_4_, int i_5_, int i_6_) {
	minAreax = i;
	minAreay = i_4_;
	maxAreax = i_5_;
	maxAreay = i_6_;
    }
    
    public void mousePressed() {
	if (hasFocus()) {
	    dragged = true;
	    xoff = _parent.tmouseX - _x;
	    yoff = _parent.tmouseY - _y;
	}
    }
    
    public void mouseDragged() {
	if (dragged) {
	    _x = _parent.tmouseX - xoff;
	    _y = _parent.tmouseY - yoff;
	    if (minAreax != 0 || minAreay != 0 || maxAreax != 0
		|| maxAreay != 0) {
		_x = PApplet.constrain(_x, minAreax, maxAreax);
		_y = PApplet.constrain(_y, minAreay, maxAreay);
	    }
	}
    }
}
