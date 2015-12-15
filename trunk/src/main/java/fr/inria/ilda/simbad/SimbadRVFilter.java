package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;

import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.Camera;

import fr.inria.ilda.fitsow.Config;


import java.awt.Color;

public class SimbadRVFilter extends SimbadQueryGlyph{
  private VRectangle qsquares[] = null;
  private SimbadCriteria parent;
  private VSegment l1, l2;
  private VText rv, z, cz;
  private String rvStr="";
  private String zStr="";
  private String czStr = "";

  public SimbadRVFilter(SimbadCriteria parent, double top, double left, double right){
    super(right-left,top-5*Config.TEXT_SIZE,parent.getVS());
    this.parent = parent;
    parent.setFilterLayout("Radial velocity:", Config.OFFSET+Config.TEXT_SIZE*6, this, top, left, right);
    rv = new VText(left+Config.OFFSET,top-2*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Radial velocity (km/s):");
    z  = new VText(left+Config.OFFSET,top-3*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Redshift (z):");
    cz  = new VText(left+Config.OFFSET,top-4*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"cz:");
    this.addChild(rv);
    this.addChild(z);
    this.addChild(cz);
    qsquares = parent.qualitySelector(this, left+Config.OFFSET, top-5*Config.TEXT_SIZE);
  }

  public boolean coordInsideComponent(double x, double y){
    double[] bckgBounds = parent.getBackground().getBounds();
    double left = bckgBounds[0];
    double right = bckgBounds[2];
    return x < right && x > left && y < l1.getLocation().getY() && y > l2.getLocation().getY();
  }

  public int getItemSelected(double x,  double y, Camera sqCamera){
    if(rv.coordInsideV(x,y,sqCamera)) return 0;
    else if(z.coordInsideV(x,y,sqCamera)) return 1;
    else if(cz.coordInsideV(x,y,sqCamera)) return 2;
    else{
      for(int i = 0; i < qsquares.length; i++){
        if(qsquares[i].coordInsideV(x,y,sqCamera)) return i+3;
      }
    }

    return -1;
  }

  public void select(int i, String str){
    if(i == 0){
     rv.setText("Radial velocity (km/s) :"+str);
     rvStr = str;
    }
    else if(i == 1){
      z.setText("Redshift (z) :"+str);
      zStr = str;
    }
    else if(i == 2){
      cz.setText("cz :"+str);
      czStr = str;
    }
    else if(qsquares != null){
      if(qsquares[i-3].getColor().equals(Color.red))
          qsquares[i-3].setColor(Color.white);
      else if(qsquares[i-3].getColor().equals(Color.white))
        qsquares[i-3].setColor(Color.red);
    }
  }

  public int[] getQualitiesSelected(){
    int[] retval = new int[qsquares.length];
    for(int i = 0; i < qsquares.length; i++){
      if(qsquares[i].getColor().equals(Color.red)) retval[i] = 1;
      else retval[i] = 0;
    }
    return retval;
  }

  public String getRVStr(){
    return rvStr;
  }

  public String getZStr(){
    return zStr;
  }

  public String getCZStr(){
    return czStr;
  }

  public void setl1(VSegment l1){
    this.l1 = l1;
  }

  public void setl2(VSegment l2){
    this.l2 = l2;
  }
}
