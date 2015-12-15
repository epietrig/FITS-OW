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
  private VText st, lc, pec;
  private String stStr = "";
  private String lcStr = "";
  private String pecStr = "";
  private VRectangle[] qsquares = null;
  public SimbadSTFilter(SimbadCriteria parent, double top, double left, double right){
    super(right-left, Config.OFFSET+Config.TEXT_SIZE*5, parent.getVS());
    this.parent = parent;
    parent.setFilterLayout("Spectral Type:", 2*Config.OFFSET+Config.TEXT_SIZE*6, this, top, left, right);
    st = new VText(left+Config.OFFSET,top-2*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Spectral type:");
    lc = new VText(left+Config.OFFSET,top-3*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Luminosity class:");
    pec = new VText(left+Config.OFFSET,top-4*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Peculiarities:");
    this.addChild(st);
    this.addChild(lc);
    this.addChild(pec);
    qsquares = parent.qualitySelector(this, left+Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*5);
  }

  public boolean coordInsideComponent(double x, double y){
    double[] bckgBounds = parent.getBackground().getBounds();
    double left = bckgBounds[0];
    double right = bckgBounds[2];
    return x < right && x > left && y < l1.getLocation().getY() && y > l2.getLocation().getY();
  }

  public int getItemSelected(double x,  double y, Camera sqCamera){
    if(st.coordInsideV(x,y,sqCamera)) return 0;
    else if(lc.coordInsideV(x,y,sqCamera)) return 1;
    else if(pec.coordInsideV(x,y,sqCamera)) return 2;
    else{
      for(int i = 0; i < qsquares.length; i++){
        if(qsquares[i].coordInsideV(x,y,sqCamera)) return i+3;
      }
    }
    return -1;
  }

  public void select(int i  , String str){
    if(i == 0){
      st.setText("Spectral type: "+str);
      stStr = str;
     }
    else if(i == 1){
      lc.setText("Luminosity class: "+str);
      lcStr = str;
     }
    else if(i == 2){
      pec.setText("Peculiarities: "+str);
      pecStr = str;
    }
    else if(qsquares != null){
      if(qsquares[i-3].getColor().equals(Color.red))
          qsquares[i-3].setColor(Color.white);
      else if(qsquares[i-3].getColor().equals(Color.white))
        qsquares[i-3].setColor(Color.red);
    }
  }
  public int[] getQualitiesSelected(){
    int[] retval = new int[5];
    for(int i = 0; i < qsquares.length; i++){
      if(qsquares[i].getColor().equals(Color.red)) retval[i] = 1;
      else retval[i] = 0;
    }
    return retval;
  }
  public String getSTStr(){
    return stStr;
  }

  public String getLCStr(){
    return lcStr;
  }

  public String getPecStr(){
    return pecStr;
  }

  public void setl1(VSegment l1){
    this.l1 = l1;
  }

  public void setl2(VSegment l2){
    this.l2 = l2;
  }
}
