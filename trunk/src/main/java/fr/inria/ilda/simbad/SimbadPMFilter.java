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
    this.background = new VRectangle(left+150,top-105,Z,300,210,Config.SELECTED_BACKGROUND_COLOR);
    this.addChild(background);
    setFilterLayout("Proper Motion:", top, left, right);
    ra = new VText(left+2*Config.OFFSET,top-3*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Right ascension angle:");
    dec = new VText(left+2*Config.OFFSET,top-5*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Declination angle:");
    this.addChild(ra);
    this.addChild(dec);
    qsquares = qualitySelector(this, left+2*Config.OFFSET, top-9*Config.TEXT_SIZE);
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
    else if(qsquares != null){
      if(qsquares[i-2].getColor().equals(Color.red))
          qsquares[i-2].setColor(Color.white);
      else if(qsquares[i-2].getColor().equals(Color.white))
        qsquares[i-2].setColor(Color.red);
    }
  }

  public String getRaStr(){
    return raStr;
  }

  public String getDecStr(){
    return decStr;
  }

}
