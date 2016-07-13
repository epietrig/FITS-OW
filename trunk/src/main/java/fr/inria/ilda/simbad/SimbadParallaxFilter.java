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

public class SimbadParallaxFilter extends SimbadFilter{
  private VText parallax;
  private String parallaxStr ="";
  private static final String parallaxLabel = "Parallax: ";
  private static final String parallaxInput = "Enter parallax (mas) in the format:\n!=/=/>=/<= numrical-value";

  public SimbadParallaxFilter(double top, double left, double right){
    super(W, H-90);
    this.background = new VRectangle(left+150,top-105,Z,300,210,SELECTED_BACKGROUND_COLOR, BORDER_COLOR);
    this.addChild(background);
    setFilterLayout(parallaxLabel, top, left, right);
    parallax = new VText(left+2*OFFSET,top-3*TEXT_SIZE-OFFSET,Z,TEXT_COLOR,parallaxLabel);
    parallax.setScale(1.2f);
    this.addChild(parallax);
    this.qsquares = qualitySelector(this, left+2*OFFSET, top-TEXT_SIZE*9);
  }
  public int getItemSelected(double x,  double y){
    if(parallax.coordInsideV(x,y,SQ_CAMERA)) return 0;
    for(int i = 0; i < qsquares.length; i++){
      if(qsquares[i].coordInsideV(x,y,SQ_CAMERA)) return i+1;
    }
    return -1;
  }

  public void select(int i, String str){
    if(i == 0){
      JOptionPane optionPane = new JOptionPane(parallaxLabel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
      String inputValue = JOptionPane.showInputDialog(parallaxInput);
      parallax.setText(parallaxLabel+inputValue);
      parallaxStr = inputValue;
    }
    else if(qsquares!=null && i >= 0){
      if(qsquares[i-1].getColor().equals(CANCEL_COLOR))
        qsquares[i-1].setColor(BACKGROUND_COLOR);
      else qsquares[i-1].setColor(CANCEL_COLOR);
    }
  }

  public String getParallaxStr(){
    return parallaxStr;
  }

}
