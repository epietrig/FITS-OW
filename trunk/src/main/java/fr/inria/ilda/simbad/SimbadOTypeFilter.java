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

public class SimbadOTypeFilter extends SimbadFilter{
  private VRectangle otsquares[];
  private VText types[];
  public SimbadOTypeFilter(double top, double left, double right){
    super(right-left,300);
    this.background = new VRectangle(left+150,top-150,Z,300,300,Config.SELECTED_BACKGROUND_COLOR);
    this.addChild(background);
    setFilterLayout("Object Type:", top, left, right);
    otsquares = new VRectangle[Config.OBJECT_TYPES.length];
    types =  new VText[Config.OBJECT_TYPES.length];
    for(int i = 0; i < Config.OBJECT_TYPES.length; i++){
      if( i < 7){
        otsquares[i] =  new VRectangle (left+2*Config.OFFSET, top-Config.TEXT_SIZE*(i+2)-20*i, Z, 10, 10, Color.white);
        types[i] = new VText(left+5*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*(i+2)-20*i, Z, Config.SELECTED_TEXT_COLOR, Config.OBJECT_TYPES[i]);
      }
      else{
        otsquares[i] =  new VRectangle (left+2*Config.OFFSET+width/2, top-Config.TEXT_SIZE*(i+2-7)-20*(i-7), Z, 10, 10, Color.white);
        types[i] = new VText(left+5*Config.OFFSET+width/2, top-Config.OFFSET-Config.TEXT_SIZE*(i+2-7)-20*(i-7), Z, Config.SELECTED_TEXT_COLOR,  Config.OBJECT_TYPES[i]);
      }
    this.addChild(otsquares[i]);
    this.addChild(types[i]);
    }
  }
  public int getItemSelected(double x,double y){
    for(int i = 0; i < otsquares.length; i++){
      if(otsquares[i].coordInsideV(x, y, SQ_CAMERA) || types[i].coordInsideV(x, y, SQ_CAMERA))
        return i;
    }
    return -1;
  }
  public void select(int t, String str){
    if(t >= 0){
      VRectangle selectedSquare = otsquares[t];
      if(selectedSquare.getColor().equals(Color.red))
        selectedSquare.setColor(color.white);
      else
        selectedSquare.setColor(color.red);
    }
  }
  public int[] getOTSelected(){
    int[] retval = new int[Config.OBJECT_TYPES.length];
    for(int i = 0; i < otsquares.length; i++){
      if(otsquares[i].getColor().equals(Color.red)) retval[i] = 1;
      else retval[i]=0;
    }
    return retval;
  }
}
