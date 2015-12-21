package fr.inria.ilda.simbad;

import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.VRectangle;
import java.awt.Font;
import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.Camera;
import fr.inria.zvtm.glyphs.Glyph;
import java.util.Vector;

public abstract class SimbadQueryGlyph extends Composite{
  protected double width, height;
  protected VRectangle background;
  protected Font bold, notBold;
  protected VirtualSpace vs;
  protected static VirtualSpace SQ_VIRTUAL_SPACE;
  protected static Camera SQ_CAMERA;
  protected final static int Z = 0;
  
  public SimbadQueryGlyph(double width, double height){
    this.width = width;
    this.height = height;
  }

  public double getWidth(){
    return width;
  }
  public double getHeight(){
    return height;
  }
  public boolean coordInsideItem(int jpx, int jpy){
    return background.coordInsideP(jpx, jpy, SQ_CAMERA);
  }
  public static SimbadQueryGlyph getCurrent(String type){
    Vector<Glyph> gv = SQ_VIRTUAL_SPACE.getGlyphsOfType(type);
    if(gv.size()>0){
      SimbadQueryGlyph g = (SimbadQueryGlyph) gv.get(0);
      return g;
    }
    return null;
  }
  public static void setVirtualSpace(VirtualSpace vs){
    SQ_VIRTUAL_SPACE = vs;
  }
  public static void setCamera(Camera c){
    SQ_CAMERA = c;
  }

}
