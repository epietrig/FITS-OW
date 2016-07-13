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

public class SimbadSTFilter extends SimbadFilter{
  private VText st, lc, pec;
  private String stStr = "";
  private String lcStr = "";
  private String pecStr = "";
  private static final String stInput = "Enter spectral type in the format:\n >=/<=/>/</!=/= value ";
  private static final String lcInput = "Enter luminosity value in the format:\n >=/<=/>/</!=/= value ";
  private static final String pecInput = "Enter Peculiarities in the format:\n =/!= numrical-value";
  private static final String stLabel = "Spectral Type: ";
  private static final String lcLabel = "Luminosity Class: ";
  private static final String pecLabel = "Peculiarities: ";

  public SimbadSTFilter(double top, double left, double right){
    super(W, H);
    this.background = new VRectangle(left+150,top-150,Z,300,300,SELECTED_BACKGROUND_COLOR,BORDER_COLOR);
    this.addChild(background);
    setFilterLayout(stLabel, top, left, right);
    st = new VText(left+2*OFFSET,top-3*TEXT_SIZE,Z,TEXT_COLOR,stLabel);
    lc = new VText(left+2*OFFSET,top-5*TEXT_SIZE,Z,TEXT_COLOR,lcLabel);
    pec = new VText(left+2*OFFSET,top-7*TEXT_SIZE,Z,TEXT_COLOR,pecLabel);
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
    String inputValue="";
    JOptionPane optionPane;
    if(i == 0){
      optionPane = new JOptionPane(stLabel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
      inputValue = JOptionPane.showInputDialog(stInput);
      st.setText(stLabel+inputValue);
      stStr = inputValue;
     }
    else if(i == 1){
      optionPane = new JOptionPane(lcLabel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
      inputValue = JOptionPane.showInputDialog(lcInput);
      lc.setText(lcLabel+inputValue);
      lcStr = inputValue;
     }
    else if(i == 2){
      optionPane = new JOptionPane(pecLabel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
      inputValue = JOptionPane.showInputDialog(pecInput);
      pec.setText(pecLabel+inputValue);
      pecStr = inputValue;
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
