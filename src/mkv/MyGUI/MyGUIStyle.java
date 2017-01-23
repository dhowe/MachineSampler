/* MyGUIStyle - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package mkv.MyGUI;

import processing.core.PApplet;
import processing.core.PFont;

public class MyGUIStyle
{
  private PApplet _root;
  public PFont font;
  public int fontSize = 12;
  public int strokeWeight = 1;
  public int padding = 1;
  private float transparency = 255.0F;
  public int background;
  public int buttonFace;
  public int buttonHighlight;
  public int buttonShadow;
  public int buttonText;
  public int icon;
  public int highlight;
  public int face;
  public int shadow;
  public int disabled;
  public int highlightText;
  public int scrollBar;

  public MyGUIStyle(PApplet papplet)
  {
    _root = papplet;
    font = _root.loadFont("ArialNarrow-48.vlw");
    setDefaultColors();
  }

  public MyGUIStyle(PApplet papplet, int i)
  {
    _root = papplet;
    font = _root.loadFont("ArialNarrow-48.vlw");
    setDefaultColors();
    tintColor(i);
  }

  public MyGUIStyle(PApplet papplet, PFont pfont, int i, int i_0_, int i_1_, int i_2_, int i_3_, int i_4_, int i_5_, int i_6_, int i_7_, int i_8_, int i_9_, int i_10_, int i_11_)
  {
    _root = papplet;
    font = pfont;
    fontSize = i;
    background = i_0_;
    buttonFace = i_1_;
    buttonHighlight = i_2_;
    buttonShadow = i_3_;
    buttonText = i_4_;
    icon = i_5_;
    face = i_6_;
    highlight = i_7_;
    shadow = i_8_;
    disabled = i_9_;
    highlightText = i_10_;
    scrollBar = i_11_;
  }

  public MyGUIStyle(PApplet papplet, PFont pfont, int i, int i_12_, int i_13_, int i_14_, int i_15_)
  {
    _root = papplet;
    font = _root.loadFont("ArialNarrow-48.vlw");
    font = pfont;
    fontSize = i;
    background = _root.color(204.0F, 204.0F, 204.0F, transparency);
    buttonFace = i_12_;
    buttonHighlight = i_13_;
    buttonShadow = i_14_;
    buttonText = i_15_;
    icon = i_15_;
    face = i_12_;
    highlight = i_13_;
    shadow = i_14_;
    disabled = tintColor(i_12_, background);
    highlightText = i_13_;
    scrollBar = background;
  }

  public void setDefaultColors()
  {
    background = _root.color(204.0F, 204.0F, 204.0F, transparency);
    buttonFace = _root.color(102.0F, 124.0F, 196.0F, transparency);
    buttonHighlight = _root.color(137.0F, 157.0F, 221.0F, transparency);
    buttonShadow = _root.color(78.0F, 93.0F, 140.0F, transparency);
    buttonText = _root.color(0.0F, 0.0F, 0.0F, transparency);
    icon = _root.color(0.0F, 0.0F, 0.0F, transparency);
    face = _root.color(102.0F, 124.0F, 196.0F, transparency);
    highlight = _root.color(137.0F, 157.0F, 221.0F, transparency);
    shadow = _root.color(78.0F, 93.0F, 140.0F, transparency);
    disabled = tintColor(_root.color(200), highlight, 0.2F);
    highlightText = _root.color(78.0F, 93.0F, 140.0F, transparency);
    scrollBar = _root.color(207.0F, 217.0F, 251.0F, transparency);
  }

  public void setFont(PFont pfont)
  {
    font = pfont;
  }

  public void setFont(PFont pfont, int i)
  {
    font = pfont;
    fontSize = i;
  }

  public void setStrokeWeight(int i)
  {
    strokeWeight = i;
  }

  public void setPadding(int i)
  {
    padding = i;
  }

  public void tintColor(int i)
  {
    background = tintColor(background, i);
    buttonFace = tintColor(buttonFace, i);
    buttonHighlight = tintColor(buttonHighlight, i);
    buttonShadow = tintColor(buttonShadow, i);
    buttonText = tintColor(buttonText, i);
    icon = tintColor(icon, i);
    face = tintColor(face, i);
    highlight = tintColor(highlight, i);
    shadow = tintColor(shadow, i);
    highlightText = tintColor(highlightText, i);
    disabled = tintColor(_root.color(200), highlight, 0.2F);
    scrollBar = tintColor(scrollBar, i);
  }

  public void tintDefault(int i)
  {
    setDefaultColors();
    tintColor(i);
  }

  public int tintColor(int i, int i_16_)
  {
    return tintColor(i, i_16_, 1.0F);
  }

  public int tintColor(int i, int i_17_, float f)
  {
    float f_18_ = (_root.brightness(i) + _root.brightness(i_17_)) / 2.0F / 255.0F;
    float f_19_ = (_root.red(i_17_) * f + _root.red(i) * (1.0F - f)) * f_18_;
    float f_20_ = (_root.green(i_17_) * f + _root.green(i) * (1.0F - f)) * f_18_;
    float f_21_ = (_root.blue(i_17_) * f + _root.blue(i) * (1.0F - f)) * f_18_;
    return _root.color((float) PApplet.round(f_19_), (float) PApplet.round(f_20_), (float) PApplet.round(f_21_), _root.alpha(i));
  }

  public void setTransparency(float f)
  {
    transparency = f;
    background = updateColorTransparency(background, f);
    buttonFace = updateColorTransparency(buttonFace, f);
    buttonHighlight = updateColorTransparency(buttonHighlight, f);
    buttonShadow = updateColorTransparency(buttonShadow, f);
    buttonText = updateColorTransparency(buttonText, f);
    icon = updateColorTransparency(icon, f);
    face = updateColorTransparency(face, f);
    highlight = updateColorTransparency(highlight, f);
    shadow = updateColorTransparency(shadow, f);
    disabled = updateColorTransparency(disabled, f);
    highlightText = updateColorTransparency(highlightText, f);
    scrollBar = updateColorTransparency(scrollBar, f);
  }

  public void setTransparency(int i)
  {
    setTransparency((float) i);
  }

  private int updateColorTransparency(int i, float f)
  {
    return _root.color(_root.red(i), _root.green(i), _root.blue(i), f);
  }

  public MyGUIStyle copy()
  {
    MyGUIStyle myguistyle_22_ = new MyGUIStyle(_root, font, fontSize, background, buttonFace, buttonHighlight, buttonShadow, buttonText, icon, face, highlight, shadow, disabled, highlightText, scrollBar);
    myguistyle_22_.setStrokeWeight(strokeWeight);
    myguistyle_22_.setPadding(padding);
    return myguistyle_22_;
  }
}
