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

public class SimbadPMFilter extends SimbadFilter{
  private VText ra, dec;
  private String raStr ="";
  private String decStr="";

  public SimbadPMFilter(double top, double left, double right){
    super(W, H-90);
    this.background = new VRectangle(left+150,top-105,Z,300,210,SELECTED_BACKGROUND_COLOR, BORDER_COLOR);
    this.addChild(background);
    setFilterLayout("Proper Motion:", top, left, right);
    ra = new VText(left+2*OFFSET,top-3*TEXT_SIZE,Z,TEXT_COLOR,"Right ascension angle:");
    dec = new VText(left+2*OFFSET,top-5*TEXT_SIZE,Z,TEXT_COLOR,"Declination angle:");
    ra.setScale(1.2f);
    dec.setScale(1.2f);
    this.addChild(ra);
    this.addChild(dec);
    qsquares = qualitySelector(this, left+2*OFFSET, top-9*TEXT_SIZE);
  }

  public int getItemSelected(double x, double y){
    if(ra.coordInsideV(x,y,SQ_CAMERA)) return 0;
    else if(dec.coordInsideV(x,y,SQ_CAMERA)) return 1;
    else{
      for(int i = 0; i < qsquares.length; i++){
        if(qsquares[i].coordInsideV(x,y,SQ_CAMERA)) return i+2;
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
    else if(qsquares != null && i >= 2){
      if(qsquares[i-2].getColor().equals(CANCEL_COLOR))
          qsquares[i-2].setColor(BACKGROUND_COLOR);
      else if(qsquares[i-2].getColor().equals(BACKGROUND_COLOR))
        qsquares[i-2].setColor(CANCEL_COLOR);
    }
  }

  public String getRaStr(){
    return raStr;
  }

  public String getDecStr(){
    return decStr;
  }

}
