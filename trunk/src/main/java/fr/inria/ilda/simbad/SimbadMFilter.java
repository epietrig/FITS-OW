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

public class SimbadMFilter extends SimbadFilter{
  private VRectangle[] msquares;
  private VText[] mnames;
  // private SimbadCriteria parent;//doesnt really needit
  // private VSegment l1, l2;//doesnt need them either, once i add bckg
//I should be getting vs from parent class
  public SimbadMFilter(double top, double left, double right, SimbadCriteria parent){
    super(900, 600);
    background = parent.background;
    // this.parent = parent;
    msquares = new VRectangle[Config.CATALOGS.length+1];
    mnames = new VText[Config.CATALOGS.length+1];
    VSegment[] msplits = new VSegment[(Config.CATALOGS.length)/2+1];
    msquares[0] =  new VRectangle (left+2*OFFSET, top-TEXT_SIZE, Z, 10, 10, SELECTED_BACKGROUND_COLOR,BORDER_COLOR);
    mnames[0] = new VText(left+6*OFFSET, top-OFFSET-TEXT_SIZE, Z, TEXT_COLOR, "All");
    // msplits[0] = new VSegment(left, top-2*Config.OFFSET-Config.TEXT_SIZE, right, top-2*Config.OFFSET-Config.TEXT_SIZE ,Z, Config.SELECTED_TEXT_COLOR);
    this.addChild(msquares[0]);
    this.addChild(mnames[0]);
    // this.addChild(msplits[0]);
    for(int i = 1; i < Config.CATALOGS.length; i++){
      if(i <= (Config.CATALOGS.length)/2){
        msquares[i] =  new VRectangle (left+3*OFFSET, top-TEXT_SIZE*(i+1)-i*10, Z, 10, 10, BACKGROUND_COLOR, BORDER_COLOR);
        mnames[i] = new VText(left+7*OFFSET, top-OFFSET-TEXT_SIZE*(i+1)-i*10, Z, TEXT_COLOR, Config.CATALOGS[i]);
        msplits[i-1] = new VSegment(left, top-2*OFFSET-TEXT_SIZE*i-i*10, right, top-2*OFFSET-TEXT_SIZE*i-i*10 ,Z, BORDER_COLOR);
        this.addChild(msplits[i-1]);
      }
      else{
        msquares[i] =  new VRectangle (left+3*OFFSET+width/2, top-TEXT_SIZE*(i+1-Config.CATALOGS.length/2)-((i-Config.CATALOGS.length/2)*10), Z, 10, 10, BACKGROUND_COLOR, BORDER_COLOR);
        mnames[i] = new VText(left+7*OFFSET+width/2, top-OFFSET-TEXT_SIZE*(i+1-Config.CATALOGS.length/2)-((i-Config.CATALOGS.length/2)*10), Z, TEXT_COLOR, Config.CATALOGS[i]);
      }
      this.addChild(msquares[i]);
      this.addChild(mnames[i]);
    }
    VSegment vertical = new VSegment(left+ width/2, msplits[0].getLocation().getY(), left+width/2, background.getBounds()[3],Z,BORDER_COLOR);
    this.addChild(vertical);
  }

  public void select(int m, String str){
    if(m == 0){
      if(msquares[0].getColor().equals(CANCEL_COLOR)){
        for(VRectangle square : msquares){
          square.setColor(BACKGROUND_COLOR);
        }
      }
      else{
        for(VRectangle square : msquares){
          square.setColor(CANCEL_COLOR);
        }
      }
    }
    else if(m > 0){
      VRectangle selectedSquare = msquares[m];
      if(selectedSquare.getColor().equals(CANCEL_COLOR))
        selectedSquare.setColor(BACKGROUND_COLOR);
      else
        selectedSquare.setColor(CANCEL_COLOR);
    }
  }

  public int getItemSelected(double x, double y){
    for(int i = 0; i < msquares.length; i++){
      if(msquares[i]!= null && mnames[i]!=null &&
      (msquares[i].coordInsideV(x, y, SQ_CAMERA) || mnames[i].coordInsideV(x, y, SQ_CAMERA))) return i;
    }
    return -1;
  }

  public int[] getMeasurementsSelected(){
    int count = 0;
    if(msquares[0].getColor().equals(CANCEL_COLOR)){
      int[] retval = new int[1];
      retval[0] = 1;
      return retval;
    }
    else{
      int[] retval = new int[Config.CATALOGS.length];
      for(int i = 1; i < msquares.length; i++){
        if(msquares[i]!= null && msquares[i].getColor().equals(CANCEL_COLOR)){
          count++;
          retval[i-1] = 1;
        }
        else retval[i-1] = 0;
      }
      if(count == 0) retval = null;
      return retval;
    }
  }

  // public void reselect(int[] m){
  //   if(m != null && m.length > 0){
  //     if(m.length == 1 && m[0] == 1){
  //       for(VRectangle square : msquares){
  //         square.setColor(CANCEL_COLOR);
  //       }
  //     }
  //     else if(m.length == Config.CATALOGS.length){
  //       for(int i = 0; i < m.length; i++){
  //         if(m[i]==1 && msquares[i+1]!= null) msquares[i+1].setColor(CANCEL_COLOR);
  //       }
  //     }
  //   }
  // }

}
