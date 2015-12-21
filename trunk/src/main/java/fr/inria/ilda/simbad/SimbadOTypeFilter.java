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
  // private SimbadCriteria parent;//doenst need it
  // private VSegment l1, l2;//doesnt need it
//la responsabilidad de saber donde poner cada uno de estos mini objetos es de simbad criteria
  public SimbadOTypeFilter(double top, double left, double right){
    super(right-left,300);
    // this.parent = parent;
    // double[] bckg = parent.getBackground().getBounds();
    // parent.setFilterLayout("Object Type:", height, this, top, left, right);
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

  // public boolean coordInsideComponent(double x, double y){
    // double[] bckgBounds = parent.getBackground().getBounds();
    // double left = bckgBounds[0];
    // double right = bckgBounds[2];
    // return x < right && x > left && y < l1.getLocation().getY() && y > l2.getLocation().getY();
    // return true;
  // }

  // public int getItemSelected(double x,  double y, Camera sqCamera){
  //   for(int i = 0; i < otsquares.length; i++){
  //     if(otsquares[i].coordInsideV(x, y, sqCamera) || types[i].coordInsideV(x, y, sqCamera))
  //       return i;
  //   }
  //   return -1;
  // }
  //
  // public int getItemSelected(int jpx,int jpy){
  //   for(int i = 0; i < otsquares.length; i++){
  //     if(otsquares[i].coordInsideP(jpx, jpy, SQ_CAMERA) || types[i].coordInsideP(jpx, jpy, SQ_CAMERA))
  //       return i;
  //   }
  //   return -1;
  // }
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
