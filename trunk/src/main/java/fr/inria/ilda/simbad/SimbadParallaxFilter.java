package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;

import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.Camera;

import fr.inria.ilda.fitsow.Config;


import java.awt.Color;

public class SimbadParallaxFilter extends SimbadQueryGlyph{
  private SimbadCriteria parent;
  private VSegment l1, l2;
  public SimbadParallaxFilter(SimbadCriteria parent, double top, double left, double right){
    super(0, 0, 0, 0, parent.getVS());
    this.parent = parent;
    parent.setFilterLayout("Parallax:", Config.OFFSET*2+Config.TEXT_SIZE*3, this, top, left, right);
    parent.qualitySelector(this, left+Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*2);
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
