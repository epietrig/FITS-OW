package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;

import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.Camera;

import fr.inria.ilda.fitsow.Config;


import java.awt.Color;

public class SimbadFluxFilter extends SimbadQueryGlyph{
  private SimbadCriteria parent;
  private VSegment l1, l2;
  public SimbadFluxFilter(SimbadCriteria parent, double top, double left, double right){
    super(0, 0, right-left, 0, parent.getVS());
    this.parent = parent;
    parent.setFilterLayout("Flux:", 2*Config.OFFSET+Config.TEXT_SIZE*13, this, top, left, right);
    VRectangle square;
    VText type, range;
    for(int i = 0; i < Config.FLUX_TYPES.length*2; i=i+2){
      if( i < 10){
        square =  new VRectangle (left+2*Config.OFFSET, top-Config.TEXT_SIZE*(i+2), Z, 10, 10, Color.white);
        type = new VText(left+5*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*(i+2), Z, Config.SELECTED_TEXT_COLOR, Config.FLUX_TYPES[i/2]);
        range = new VText(left+5*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*(i+3), Z, Config.SELECTED_TEXT_COLOR, "Range:");
      }
      else if(i<20){
        square =  new VRectangle (left+2*Config.OFFSET+width/3, top-Config.TEXT_SIZE*(i+2-10), Z, 10, 10, Color.white);
        type = new VText(left+5*Config.OFFSET+width/3, top-Config.OFFSET-Config.TEXT_SIZE*(i+2-10), Z, Config.SELECTED_TEXT_COLOR, Config.FLUX_TYPES[i/2]);
        range = new VText(left+5*Config.OFFSET+width/3, top-Config.OFFSET-Config.TEXT_SIZE*(i+3-10), Z, Config.SELECTED_TEXT_COLOR, "Range:");
      }
      else{
        square =  new VRectangle (left+2*Config.OFFSET+2*width/3, top-Config.TEXT_SIZE*(i+2-20), Z, 10, 10, Color.white);
        type = new VText(left+5*Config.OFFSET+2*width/3, top-Config.OFFSET-Config.TEXT_SIZE*(i+2-20), Z, Config.SELECTED_TEXT_COLOR, Config.FLUX_TYPES[i/2]);
        range = new VText(left+5*Config.OFFSET+2*width/3, top-Config.OFFSET-Config.TEXT_SIZE*(i+3-20), Z, Config.SELECTED_TEXT_COLOR, "Range:");
      }
      this.addChild(square);
      this.addChild(type);
      this.addChild(range);
    }
    parent.qualitySelector(this, left+Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*12);
  }

  public boolean coordInsideComponent(double x, double y){
    return false;
  }

  public int getItemSelected(double x,  double y, Camera sqCamera){
    return -1;
  }

  public void select(int t){

  }

  public int[] getAllSelected(){
    return new int[1];
  }

  public void setl1(VSegment l1){
    this.l1 = l1;
  }

  public void setl2(VSegment l2){
    this.l2 = l2;
  }
}
