package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;

import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.Camera;

import fr.inria.ilda.fitsow.Config;


import java.awt.Color;

public class SimbadPMFilter extends SimbadQueryGlyph{
  private VText ra, dec;
  private String raStr ="";
  private String decStr="";
  private VRectangle[] qsquares = null;
  private SimbadCriteria parent;//sacar
  private VSegment l1, l2;//sacar

  public SimbadPMFilter(SimbadCriteria parent, double top, double left, double right){
    super(right-left, Config.OFFSET+Config.TEXT_SIZE*5, parent.getVS());
    this.parent = parent;
    parent.setFilterLayout("Proper Motion:", Config.OFFSET+Config.TEXT_SIZE*5, this, top, left, right);
    ra = new VText(left+Config.OFFSET,top-2*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Right ascension angle:");
    dec = new VText(left+Config.OFFSET,top-3*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Declination angle:");
    this.addChild(ra);
    this.addChild(dec);
    this.qsquares = parent.qualitySelector(this, left+Config.OFFSET, top-4*Config.TEXT_SIZE);
  }

  public boolean coordInsideComponent(double x, double y){
    double[] bckgBounds = parent.getBackground().getBounds();
    double left = bckgBounds[0];
    double right = bckgBounds[2];
    return x < right && x > left && y < l1.getLocation().getY() && y > l2.getLocation().getY();
  }

  public int getItemSelected(double x,  double y, Camera sqCamera){
    if(ra.coordInsideV(x,y,sqCamera)) return 0;
    else if(dec.coordInsideV(x,y,sqCamera)) return 1;
    else{
      for(int i = 0; i < qsquares.length; i++){
        if(qsquares[i].coordInsideV(x,y,sqCamera)) return i+2;
      }
    }

    return -1;
  }

  public void select(int i, String str){
    if(i == 0){
     ra.setText("Right ascension angle: "+str);
     raStr = str;
   }
    else if(i == 1){
      dec.setText("Declination angle: "+str);
      decStr = str;
    }
    else if(qsquares != null){
      if(qsquares[i-2].getColor().equals(Color.red))
          qsquares[i-2].setColor(Color.white);
      else if(qsquares[i-2].getColor().equals(Color.white))
        qsquares[i-2].setColor(Color.red);
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

  public String getRaStr(){
    return raStr;
  }

  public String getDecStr(){
    return decStr;
  }

  public void setl1(VSegment l1){
    this.l1 = l1;
  }

  public void setl2(VSegment l2){
    this.l2 = l2;
  }
  public void setQualities(VRectangle[] qsquares){
    this.qsquares = qsquares;
  }
}
