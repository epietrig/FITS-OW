package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;

import fr.inria.zvtm.engine.VirtualSpace;

import fr.inria.ilda.fitsow.Config;

import java.awt.Color;

public class SimbadOTypeFilter extends SimbadQueryGlyph{
  private VRectangle otsquares[];
  private SimbadCriteria parent;

  public SimbadOTypeFilter(SimbadCriteria parent, double top, double left, double right){
    super(left, top, right-left,0, parent.getVS());
    parent.setFilterLayout("Object Type:", Config.OFFSET+Config.TEXT_SIZE*9, this, top, left, right);
    otsquares = new VRectangle[Config.OBJECT_TYPES.length];
    VText type;
    for(int i = 0; i < Config.OBJECT_TYPES.length; i++){
      if( i < 7){
        otsquares[i] =  new VRectangle (left+2*Config.OFFSET, top-Config.TEXT_SIZE*(i+2), Z, 10, 10, Color.white);
        type = new VText(left+5*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*(i+2), Z, Config.SELECTED_TEXT_COLOR, Config.OBJECT_TYPES[i]);
      }
      else{
        otsquares[i] =  new VRectangle (left+2*Config.OFFSET+width/2, top-Config.TEXT_SIZE*(i+2-7), Z, 10, 10, Color.white);
        type = new VText(left+5*Config.OFFSET+width/2, top-Config.OFFSET-Config.TEXT_SIZE*(i+2-7), Z, Config.SELECTED_TEXT_COLOR,  Config.OBJECT_TYPES[i]);
      }
      this.addChild(otsquares[i]);
      this.addChild(type);
    }
  }

}
