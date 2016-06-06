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

public class SimbadSTFilter extends SimbadFilter{
  private VText st, lc, pec;
  private String stStr = "";
  private String lcStr = "";
  private String pecStr = "";

  public SimbadSTFilter(double top, double left, double right){
    super(W, H);
    this.background = new VRectangle(left+150,top-150,Z,300,300,SELECTED_BACKGROUND_COLOR,BORDER_COLOR);
    this.addChild(background);
    setFilterLayout("Spectral Type:", top, left, right);
    st = new VText(left+2*OFFSET,top-3*TEXT_SIZE,Z,TEXT_COLOR,"Spectral type:");
    lc = new VText(left+2*OFFSET,top-5*TEXT_SIZE,Z,TEXT_COLOR,"Luminosity class:");
    pec = new VText(left+2*OFFSET,top-7*TEXT_SIZE,Z,TEXT_COLOR,"Peculiarities:");
    st.setScale(1.2f);
    lc.setScale(1.2f);
    pec.setScale(1.2f);
    this.addChild(st);
    this.addChild(lc);
    this.addChild(pec);
    qsquares = qualitySelector(this, left+2*OFFSET, top-OFFSET-TEXT_SIZE*13);
  }

  public int getItemSelected(double x, double y){
    if(st.coordInsideV(x,y,SQ_CAMERA)) return 0;
    else if(lc.coordInsideV(x,y,SQ_CAMERA)) return 1;
    else if(pec.coordInsideV(x,y,SQ_CAMERA)) return 2;
    else{
      for(int i = 0; i < qsquares.length; i++){
        if(qsquares[i].coordInsideV(x,y,SQ_CAMERA)) return i+3;
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
    else if(qsquares != null && i >=3){
      if(qsquares[i-3].getColor().equals(CANCEL_COLOR))
          qsquares[i-3].setColor(BACKGROUND_COLOR);
      else if(qsquares[i-3].getColor().equals(BACKGROUND_COLOR))
        qsquares[i-3].setColor(CANCEL_COLOR);
    }
  }
  public int[] getQualitiesSelected(){
    int[] retval = new int[5];
    for(int i = 0; i < qsquares.length; i++){
      if(qsquares[i].getColor().equals(CANCEL_COLOR)) retval[i] = 1;
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

}
