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

public class SimbadRVFilter extends SimbadFilter{
  // private VRectangle qsquares[] = null;
  private SimbadCriteria parent;//sacar
  // private VSegment l1, l2;//sacar
  private VText rv, z, cz;
  private String rvStr="";
  private String zStr="";
  private String czStr = "";
  private static final String rvLabel = "Radial velocity: ";
  private static final String redshiftLabel = "Redshift: ";
  private static final String czLabel = "cz: ";
  private static final String rvInput = "Enter radial velocity (km/s) in the format:\n >=/<=/=/!= numrical-value";
  private static final String redshiftInput = "Enter redshift (km/s) in the format:\n >=/<=/=/!= numrical-value";
  private static final String czInput = "Enter cz in the format:\n >=/<=/=/!= numrical-value";

  public SimbadRVFilter(double top, double left, double right){
    super(W,H-90);
    this.background = new VRectangle(left+150,top-105,Z,300,210,SELECTED_BACKGROUND_COLOR, BORDER_COLOR);
    this.addChild(background);
    setFilterLayout(rvLabel, top, left, right);
    rv = new VText(left+2*OFFSET,top-3*TEXT_SIZE,Z,TEXT_COLOR,rvLabel);
    z  = new VText(left+2*OFFSET,top-5*TEXT_SIZE,Z,TEXT_COLOR,"Redshift (z):");
    cz  = new VText(left+2*OFFSET,top-7*TEXT_SIZE,Z,TEXT_COLOR,"cz:");
    rv.setScale(1.2f);
    z.setScale(1.2f);
    cz.setScale(1.2f);
    this.addChild(rv);
    this.addChild(z);
    this.addChild(cz);
    qsquares = qualitySelector(this, left+2*Config.OFFSET, top-9*TEXT_SIZE);
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
    String inputValue="";
    JOptionPane optionPane;
    if(i == 0){
      optionPane = new JOptionPane(rvLabel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
      inputValue = JOptionPane.showInputDialog(rvInput);
      rv.setText(rvLabel+inputValue);
      rvStr = inputValue;
    }
    else if(i == 1){
      optionPane = new JOptionPane(redshiftLabel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
      inputValue = JOptionPane.showInputDialog(redshiftInput);
      z.setText(redshiftLabel+inputValue);
      zStr = inputValue;
    }
    else if(i == 2){
      optionPane = new JOptionPane(czLabel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
      inputValue = JOptionPane.showInputDialog(czInput);
      cz.setText(czLabel+inputValue);
      czStr = inputValue;
    }
    else if(qsquares != null && i >= 3){
      if(qsquares[i-3].getColor().equals(CANCEL_COLOR))
          qsquares[i-3].setColor(BACKGROUND_COLOR);
      else if(qsquares[i-3].getColor().equals(BACKGROUND_COLOR))
        qsquares[i-3].setColor(CANCEL_COLOR);
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
