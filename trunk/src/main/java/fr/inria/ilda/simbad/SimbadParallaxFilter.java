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
  private VText parallax;
  private String parallaxStr ="";
  private VRectangle[] qsquares = null;

  public SimbadParallaxFilter(SimbadCriteria parent, double top, double left, double right){
    super(Config.OFFSET*2+Config.TEXT_SIZE*4,right-left, parent.getVS());
    this.parent = parent;
    parent.setFilterLayout("Parallax:", Config.OFFSET*2+Config.TEXT_SIZE*4, this, top, left, right);
    parallax = new VText(left+Config.OFFSET,top-2*Config.TEXT_SIZE-Config.OFFSET,Z,Config.SELECTED_TEXT_COLOR,"Parallax:");
    this.addChild(parallax);
    this.qsquares = parent.qualitySelector(this, left+Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*3);
  }

  public boolean coordInsideComponent(double x, double y){
    double[] bckgBounds = parent.getBackground().getBounds();
    double left = bckgBounds[0];
    double right = bckgBounds[2];
    return x < right && x > left && y < l1.getLocation().getY() && y > l2.getLocation().getY();
  }

  public int getItemSelected(double x,  double y, Camera sqCamera){
    if(parallax.coordInsideV(x,y,sqCamera)) return 0;
    for(int i = 0; i < qsquares.length; i++){
      if(qsquares[i].coordInsideV(x,y,sqCamera)) return i+1;
    }
    return -1;
  }

  public void select(int i, String str){
    if(i == 0){
      parallax.setText("Parallax: "+str);
      parallaxStr = str;
    }
    else if(qsquares!=null && i >= 0){
      if(qsquares[i-1].getColor().equals(Color.red))
        qsquares[i-1].setColor(Color.white);
      else qsquares[i-1].setColor(Color.red);
    }
  }

  public int[] getQualitiesSelected(){
    int[] retval = new int[qsquares.length];
    for(int i = 0; i<qsquares.length; i++){
      if(qsquares[i].getColor().equals(Color.red))
        retval[i] = 1;
      else
        retval[i] = 0;
    }
    return retval;
  }
  public String getParallaxStr(){
    return parallaxStr;
  }

  public void setl1(VSegment l1){
    this.l1 = l1;
  }

  public void setl2(VSegment l2){
    this.l2 = l2;
  }
}
