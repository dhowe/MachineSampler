/* MyGUIObject - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package mkv.MyGUI;
import java.awt.event.KeyEvent;

import processing.core.PApplet;

public abstract class MyGUIObject
{
    public PApplet _root;
    public MyGUIObject _parent;
    public MyGUIStyle _style;
    public int _x;
    public int _y;
    public int _width;
    public int _height;
    public int _id;
    public float _rotation;
    public float _scale = 1.0F;
    public boolean _visible = true;
    public boolean _disabled = false;
    public String _actionCommand = "";
    public int tmouseX;
    public int tmouseY;
    protected boolean dragged = false;
    protected boolean hover = false;
    protected boolean lastHover = false;
    
    public MyGUIObject(PApplet papplet) {
	_root = papplet;
	_x = 0;
	_y = 0;
    }
    
    public MyGUIObject(PApplet papplet, int i, int i_0_) {
	_root = papplet;
	_x = i;
	_y = i_0_;
    }
    
    public boolean hasFocus() {
	if (_id == _parent.getFocusIndex())
	    return true;
	return false;
    }
    
    public boolean isDisabled() {
	if (_disabled)
	    return true;
	if (_parent != null)
	    return _parent.isDisabled();
	return false;
    }
    
    public boolean isDragged() {
	return dragged;
    }
    
    public void mousePressed() {
	if (hasFocus())
	    dragged = true;
    }
    
    public void mouseReleased() {
	dragged = false;
    }
    
    public void mouseDragged() {
	/* empty */
    }
    
    public void keyPressed(KeyEvent keyevent) {
	/* empty */
    }
    
    public void keyReleased(KeyEvent keyevent) {
	/* empty */
    }
    
    public void keyTyped(KeyEvent keyevent) {
	/* empty */
    }
    
    public void draw() {
	if (_visible)
	    drawStates();
    }
    
    public void drawStates() {
	/* empty */
    }
    
    public void setID(int i) {
	_id = i;
    }
    
    public void setParent(MyGUIObject myguiobject_1_) {
	_parent = myguiobject_1_;
	_root = myguiobject_1_._root;
    }
    
    public void setActionCommand(String string) {
	_actionCommand = string;
    }
    
    public void setStyle(MyGUIStyle myguistyle) {
	_style = myguistyle;
    }
    
    public void enable() {
	_disabled = false;
    }
    
    public void disable() {
	_disabled = true;
    }
    
    public void rotate(float f) {
	_rotation += f;
	_rotation = normalize(_rotation, 0.0F, 360.0F);
    }
    
    public void rotateRadians(float f) {
	_rotation += PApplet.degrees(f);
	_rotation = normalize(_rotation, 0.0F, 360.0F);
    }
    
    protected float normalize(float f, float f_2_, float f_3_) {
	float f_4_ = f_3_ - f_2_;
	if (f < f_2_)
	    return normalize(f + f_4_, f_2_, f_3_);
	if (f > f_3_)
	    return normalize(f - f_4_, f_2_, f_3_);
	return f;
    }
    
    public boolean checkForHit() {
	updateLocalMouse();
	if ((float) tmouseX >= (float) (-_width / 2) * _scale
	    && (float) tmouseY >= (float) (-_height / 2) * _scale
	    && (float) tmouseX <= (float) (_width / 2) * _scale
	    && (float) tmouseY <= (float) (_height / 2) * _scale) {
	    lastHover = true;
	    return true;
	}
	lastHover = false;
	return false;
    }
    
    public void updateLocalMouse() {
	if (_rotation != 0.0F) {
	    if (_root != null) {
		/* empty */
	    }
	    float f = PApplet.cos(-PApplet.radians(_rotation));
	    if (_root != null) {
		/* empty */
	    }
	    float f_5_ = PApplet.sin(-PApplet.radians(_rotation));
	    int i = _parent.tmouseX - _x;
	    int i_6_ = _parent.tmouseY - _y;
	    tmouseX = PApplet.round((float) i * f
				    - (float) i_6_ * f_5_ * _parent._scale);
	    tmouseY = PApplet.round((float) i * f_5_
				    + (float) i_6_ * f * _parent._scale);
	} else {
	    tmouseX = PApplet.round((float) (_parent.tmouseX - _x)
				    * _parent._scale);
	    tmouseY = PApplet.round((float) (_parent.tmouseY - _y)
				    * _parent._scale);
	}
    }
    
    public MyGUIStyle getStyle() {
	return _style;
    }
    
    public int getFocusIndex() {
	return -1;
    }
    
    public boolean setFocusIndex(int i) {
	return false;
    }
}
