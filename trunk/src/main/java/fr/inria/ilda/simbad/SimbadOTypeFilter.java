package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;

import fr.inria.zvtm.engine.VirtualSpace;

import fr.inria.ilda.fitsow.Config;

import java.awt.Color;

public class SimbadOTypeFilter extends SimbadQueryGlyph{
  private VRectangle otsquares[];
  private VText types[];
  private SimbadCriteria parent;
  private double top, left, right, bottom;

  public SimbadOTypeFilter(SimbadCriteria parent, double top, double left, double right){
    super(left+(right-left)/2, top-(Config.OFFSET+Config.TEXT_SIZE*9)/2, right-left,Config.OFFSET+Config.TEXT_SIZE*9, parent.getVS());
    this.top = top-Config.OFFSET-Config.TEXT_SIZE;
    this.bottom = top-height;
    this.right = right;
    this.left = left;
    parent.setFilterLayout("Object Type:", height, this, top, left, right);
    otsquares = new VRectangle[Config.OBJECT_TYPES.length];
    types =  new VText[Config.OBJECT_TYPES.length];
    for(int i = 0; i < Config.OBJECT_TYPES.length; i++){
      if( i < 7){
        otsquares[i] =  new VRectangle (left+2*Config.OFFSET, top-Config.TEXT_SIZE*(i+2), Z, 10, 10, Color.white);
        types[i] = new VText(left+5*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*(i+2), Z, Config.SELECTED_TEXT_COLOR, Config.OBJECT_TYPES[i]);
      }
      else{
        otsquares[i] =  new VRectangle (left+2*Config.OFFSET+width/2, top-Config.TEXT_SIZE*(i+2-7), Z, 10, 10, Color.white);
        types[i] = new VText(left+5*Config.OFFSET+width/2, top-Config.OFFSET-Config.TEXT_SIZE*(i+2-7), Z, Config.SELECTED_TEXT_COLOR,  Config.OBJECT_TYPES[i]);
      }
      this.addChild(otsquares[i]);
      this.addChild(types[i]);
    }
  }

  public boolean coordInsideOTFilter(double x, double y){
    return (x > left && x < right) && (y < bottom );
  }

}
