/* MyGUIImage - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package mkv.MyGUI;
import processing.core.PApplet;
import processing.core.PImage;

public class MyGUIImage extends MyGUIObject
{
    PImage _icon;
    
    public MyGUIImage(PApplet papplet, int i, int i_0_, PImage pimage) {
	super(papplet, i, i_0_);
	_root = papplet;
	_icon = pimage;
	_width = pimage.width;
	_height = pimage.height;
    }
    
    public void drawStates() {
	_root.pushMatrix();
	_root.translate((float) _x, (float) _y);
	_root.scale(_scale);
	_root.rotate(PApplet.radians(_rotation));
	_root.imageMode(1);
	_root.image(_icon, (float) (-_width / 2), (float) (-_height / 2),
		    (float) (_width / 2), (float) (_height / 2));
	_root.popMatrix();
    }
}
