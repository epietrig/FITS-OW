package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;

import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.Camera;

import fr.inria.ilda.fitsow.Config;


import java.awt.Color;

public class SimbadParallaxFilter extends SimbadFilter{
  // private SimbadCriteria parent;//doesnt need it
  // private VSegment l1, l2;//doesnt need it
  private VText parallax;
  private String parallaxStr ="";
  // private VRectangle[] qsquares = null;

  public SimbadParallaxFilter(double top, double left, double right){
    super(W, H-90);
    this.background = new VRectangle(left+150,top-105,Z,300,210,Config.SELECTED_BACKGROUND_COLOR);
    this.addChild(background);
    setFilterLayout("Parallax:", top, left, right);
    parallax = new VText(left+2*Config.OFFSET,top-3*Config.TEXT_SIZE-Config.OFFSET,Z,Config.SELECTED_TEXT_COLOR,"Parallax:");
    this.addChild(parallax);
    this.qsquares = qualitySelector(this, left+2*Config.OFFSET, top-Config.TEXT_SIZE*9);
  }
  public int getItemSelected(double x,  double y){
    if(parallax.coordInsideV(x,y,SQ_CAMERA)) return 0;
    for(int i = 0; i < qsquares.length; i++){
      if(qsquares[i].coordInsideV(x,y,SQ_CAMERA)) return i+1;
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

  public String getParallaxStr(){
    return parallaxStr;
  }

}
