/*  (c) COPYRIGHT INRIA (Institut National de Recherche en Informatique et en Automatique) and INRIA Chile, 2015-2016.
 *  Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id:  $
 */

package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;

import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.Camera;

import fr.inria.ilda.fitsow.Config;


import java.awt.Color;

public class SimbadRVFilter extends SimbadFilter{
  // private VRectangle qsquares[] = null;
  private SimbadCriteria parent;//sacar
  // private VSegment l1, l2;//sacar
  private VText rv, z, cz;
  private String rvStr="";
  private String zStr="";
  private String czStr = "";

  public SimbadRVFilter(double top, double left, double right){
    super(W,H-90);
    this.background = new VRectangle(left+150,top-105,Z,300,210,Config.SELECTED_BACKGROUND_COLOR);
    this.addChild(background);
    setFilterLayout("Radial velocity:", top, left, right);
    rv = new VText(left+2*Config.OFFSET,top-3*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Radial velocity (km/s):");
    z  = new VText(left+2*Config.OFFSET,top-5*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Redshift (z):");
    cz  = new VText(left+2*Config.OFFSET,top-7*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"cz:");
    this.addChild(rv);
    this.addChild(z);
    this.addChild(cz);
    qsquares = qualitySelector(this, left+2*Config.OFFSET, top-9*Config.TEXT_SIZE);
  }
  public int getItemSelected(double x,  double y){
    if(rv.coordInsideV(x,y,SQ_CAMERA)) return 0;
    else if(z.coordInsideV(x,y,SQ_CAMERA)) return 1;
    else if(cz.coordInsideV(x,y,SQ_CAMERA)) return 2;
    else{
      for(int i = 0; i < qsquares.length; i++){
        if(qsquares[i].coordInsideV(x,y,SQ_CAMERA)) return i+3;
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
  public String getRVStr(){
    return rvStr;
  }

  public String getZStr(){
    return zStr;
  }

  public String getCZStr(){
    return czStr;
  }
}
