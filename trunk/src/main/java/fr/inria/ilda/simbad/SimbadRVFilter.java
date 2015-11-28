package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;

import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.Camera;

import fr.inria.ilda.fitsow.Config;


import java.awt.Color;

public class SimbadRVFilter extends SimbadQueryGlyph{
  private VRectangle otsquares[];
  private VText types[];
  private SimbadCriteria parent;
  private VSegment l1, l2;

  public SimbadRVFilter(SimbadCriteria parent, double top, double left, double right){
    super(0,0,0,0,parent.getVS());
    parent.setFilterLayout("Radial velocity:", Config.OFFSET+Config.TEXT_SIZE*5, this, top, left, right);
    VText type = new VText(left+Config.OFFSET,top-2*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Type:");
    VText rv = new VText(left+Config.OFFSET,top-3*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Range:");
    this.addChild(type);
    this.addChild(rv);
    parent.qualitySelector(this, left+Config.OFFSET, top-4*Config.TEXT_SIZE);
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
