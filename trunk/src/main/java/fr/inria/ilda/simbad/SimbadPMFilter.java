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
import javax.swing.JOptionPane;

import java.awt.Color;

public class SimbadPMFilter extends SimbadFilter{
  private VText ra, dec;
  private String raStr ="";
  private String decStr="";
  private static final String pmLabel = "Proper Motion: ";
  private static final String raLabel = "Right ascension angle: ";
  private static final String decLabel = "Declination angle: ";
  private static final String raInput = "Enter right ascension of proper motion (mas) in the format:\n >=/<= numrical-value";
  private static final String decInput = "Enter declination of proper motion (mas) in the format:\n >=/<= numrical-value";

  public SimbadPMFilter(double top, double left, double right){
    super(W, H-90);
    this.background = new VRectangle(left+150,top-105,Z,300,210,SELECTED_BACKGROUND_COLOR, BORDER_COLOR);
    this.addChild(background);
    setFilterLayout(pmLabel, top, left, right);
    ra = new VText(left+2*OFFSET,top-3*TEXT_SIZE,Z,TEXT_COLOR,raLabel);
    dec = new VText(left+2*OFFSET,top-5*TEXT_SIZE,Z,TEXT_COLOR,decLabel);
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
    JOptionPane optionPane;
    String inputValue ="";
    if(i == 0){
      optionPane = new JOptionPane(raLabel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
      inputValue = JOptionPane.showInputDialog(raInput);
      ra.setText(raLabel+inputValue);
      raStr = inputValue;
   }
    else if(i == 1){
      optionPane = new JOptionPane(decLabel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
      inputValue = JOptionPane.showInputDialog(decInput);
      dec.setText(decLabel+inputValue);
      decStr = inputValue;
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
