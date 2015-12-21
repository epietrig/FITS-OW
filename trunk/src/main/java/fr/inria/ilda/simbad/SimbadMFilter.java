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
    msquares[0] =  new VRectangle (left+2*Config.OFFSET, top-Config.TEXT_SIZE, Z, 10, 10, Color.white);
    mnames[0] = new VText(left+6*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE, Z, Config.SELECTED_TEXT_COLOR, "All");
    // msplits[0] = new VSegment(left, top-2*Config.OFFSET-Config.TEXT_SIZE, right, top-2*Config.OFFSET-Config.TEXT_SIZE ,Z, Config.SELECTED_TEXT_COLOR);
    this.addChild(msquares[0]);
    this.addChild(mnames[0]);
    // this.addChild(msplits[0]);
    for(int i = 1; i < Config.CATALOGS.length; i++){
      if(i <= (Config.CATALOGS.length)/2){
        msquares[i] =  new VRectangle (left+3*Config.OFFSET, top-Config.TEXT_SIZE*(i+1)-i*10, Z, 10, 10, Color.white);
        mnames[i] = new VText(left+7*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*(i+1)-i*10, Z, Config.SELECTED_TEXT_COLOR, Config.CATALOGS[i]);
        msplits[i-1] = new VSegment(left, top-2*Config.OFFSET-Config.TEXT_SIZE*i-i*10, right, top-2*Config.OFFSET-Config.TEXT_SIZE*i-i*10 ,Z, Config.SELECTED_TEXT_COLOR);
        this.addChild(msplits[i-1]);
      }
      else{
        msquares[i] =  new VRectangle (left+3*Config.OFFSET+width/2, top-Config.TEXT_SIZE*(i+1-Config.CATALOGS.length/2)-((i-Config.CATALOGS.length/2)*10), Z, 10, 10, Color.white);
        mnames[i] = new VText(left+7*Config.OFFSET+width/2, top-Config.OFFSET-Config.TEXT_SIZE*(i+1-Config.CATALOGS.length/2)-((i-Config.CATALOGS.length/2)*10), Z, Config.SELECTED_TEXT_COLOR, Config.CATALOGS[i]);
      }
      this.addChild(msquares[i]);
      this.addChild(mnames[i]);
    }
    VSegment vertical = new VSegment(left+ width/2, msplits[0].getLocation().getY(), left+width/2, background.getBounds()[3],Z,Config.SELECTED_TEXT_COLOR);
    this.addChild(vertical);
  }

  public void select(int m, String str){
    if(m == 0){
      if(msquares[0].getColor().equals(Color.red)){
        for(VRectangle square : msquares){
          square.setColor(Color.white);
        }
      }
      else{
        for(VRectangle square : msquares){
          square.setColor(Color.red);
        }
      }
    }
    else if(m > 0){
      VRectangle selectedSquare = msquares[m];
      if(selectedSquare.getColor().equals(Color.red))
        selectedSquare.setColor(Color.white);
      else
        selectedSquare.setColor(Color.red);
    }
  }

  public int getItemSelected(double x, double y){
    // double top = parent.getBackground().getBounds()[1];
    // if(x < parent.getBackground().getLocation().getX()){
    //   for(int i = 0; i < msplits.length; i++){
    //     if(y < top && y > msplits[i].getLocation().getY()){
    //       return i;
    //     }
    //     top = msplits[i].getLocation().getY();
    //   }
    // }
    // return -1;
    for(int i = 0; i < msquares.length; i++){
      if(msquares[i].coordInsideV(x, y, SQ_CAMERA) || mnames[i].coordInsideV(x, y, SQ_CAMERA)) return i;
    }
    return -1;
  }
//rename to getMeasurementsSelected()
  public int[] getMeasurementsSelected(){
    int count = 0;
    if(msquares[0].getColor().equals(Color.red)){
      int[] retval = new int[1];
      retval[0] = 1;
      return retval;
    }
    else{
      int[] retval = new int[Config.CATALOGS.length];
      for(int i = 1; i < msquares.length; i++){
        if(msquares[i]!= null && msquares[i].getColor().equals(Color.red)){
          count++;
          retval[i-1] = 1;
        }
        else retval[i-1] = 0;
        System.out.println(i);
      }
      if(count == 0) retval = null;
      return retval;
    }
  }
// public boolean coordInsideComponent(double x, double y){
//   return true;
// }
// public void setl1(VSegment l1){
//   this.l1 = l1;
// }
// public void setl2(VSegment l2){
//   this.l2 = l2;
// }
}
