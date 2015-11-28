package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;

import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.Camera;

import fr.inria.ilda.fitsow.Config;


import java.awt.Color;

public class SimbadSTFilter extends SimbadQueryGlyph{
  private SimbadCriteria parent;
  private VSegment l1, l2;
  public SimbadSTFilter(SimbadCriteria parent, double top, double left, double right){
    super(0, 0, 0, 0, parent.getVS());
    this.parent = parent;
    parent.setFilterLayout("Spectral Type:", 2*Config.OFFSET+Config.TEXT_SIZE*8, this, top, left, right);
    VText tc = new VText(left+Config.OFFSET,top-2*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Temperature class:");
    VText first = new VText(left+2*Config.OFFSET,top-3*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"1st digit:");
    VText second = new VText(left+2*Config.OFFSET,top-4*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"2nd digit:");
    VText third = new VText(left+2*Config.OFFSET,top-5*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"3rd digit:");
    this.addChild(tc);
    this.addChild(first);
    this.addChild(second);
    this.addChild(third);
    VText lc = new VText(left+Config.OFFSET,top-6*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Luminosity class:");
    this.addChild(lc);
    parent.qualitySelector(this, left+Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*7);
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
