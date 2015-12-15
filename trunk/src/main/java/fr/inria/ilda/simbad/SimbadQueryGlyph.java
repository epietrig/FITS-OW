package fr.inria.ilda.simbad;

import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.VRectangle;
import java.awt.Font;
import fr.inria.zvtm.engine.VirtualSpace;

public abstract class SimbadQueryGlyph extends Composite{
  protected double x, y, width, height;
  protected Font bold, notBold;
  protected final static int Z = 0;
  protected VirtualSpace vs;

  public SimbadQueryGlyph(double width, double height, VirtualSpace vs){
    this.width = width;
    this.height = height;
    this.vs = vs;
  }

  public double getWidth(){
    return width;
  }
  public double getHeight(){
    return height;
  }
  public VirtualSpace getVS(){
    return vs;
  }

}
