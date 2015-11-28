package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;

import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.Camera;

import fr.inria.ilda.fitsow.Config;


import java.awt.Color;

public class SimbadOTypeFilter extends SimbadQueryGlyph{
  private VRectangle otsquares[];
  private VText types[];
  private SimbadCriteria parent;
  private VSegment l1, l2;

  public SimbadOTypeFilter(SimbadCriteria parent, double top, double left, double right){
    super(left+(right-left)/2,top-(Config.OFFSET+Config.TEXT_SIZE*9)/2,right-left,Config.OFFSET+Config.TEXT_SIZE*9, parent.getVS());
    this.parent = parent;
    double[] bckg = parent.getBackground().getBounds();
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

  public boolean coordInsideComponent(double x, double y){
    double[] bckgBounds = parent.getBackground().getBounds();
    double left = bckgBounds[0];
    double right = bckgBounds[2];
    return x < right && x > left && y < l1.getLocation().getY() && y > l2.getLocation().getY();
  }

  public int getItemSelected(double x,  double y, Camera sqCamera){
    for(int i = 0; i < otsquares.length; i++){
      if(otsquares[i].coordInsideV(x, y, sqCamera) || types[i].coordInsideV(x, y, sqCamera))
        return i;
    }
    return -1;
  }

  public void select(int t){
    if(t >= 0){
      VRectangle selectedSquare = otsquares[t];
      if(selectedSquare.getColor().equals(Color.red))
        selectedSquare.setColor(color.white);
      else
        selectedSquare.setColor(color.red);
    }
  }

  public int[] getAllSelected(){
    int[] retval = new int[Config.OBJECT_TYPES.length];
    for(int i = 0; i < otsquares.length; i++){
      if(otsquares[i].getColor().equals(Color.red)) retval[i] = 1;
      else retval[i]=0;
    }
    return retval;
  }

  public void setl1(VSegment l1){
    this.l1 = l1;
  }

  public void setl2(VSegment l2){
    this.l2 = l2;
  }
}
