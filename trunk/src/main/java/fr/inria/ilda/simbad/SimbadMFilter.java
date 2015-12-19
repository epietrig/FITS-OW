package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;

import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.Camera;

import fr.inria.ilda.fitsow.Config;

import java.awt.Color;

public class SimbadMFilter extends SimbadQueryGlyph{
  private VSegment[] msplits;
  private VRectangle[] msquares;
  private SimbadCriteria parent;//doesnt really needit
  private VSegment l1, l2;//doesnt need them either, once i add bckg
//I should be getting vs from parent class
  public SimbadMFilter(double top, double left, double right, VirtualSpace vs, SimbadCriteria parent){
    super(0, 0, vs);
    this.parent = parent;
    msquares = new VRectangle[Config.CATALOGS.length+1];
    msplits = new VSegment[Config.CATALOGS.length+1];
    VText name, options;
    VRectangle square =  new VRectangle (left+2*Config.OFFSET, top-Config.TEXT_SIZE, Z, 10, 10, Color.white);
    name = new VText(left+6*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE, Z, Config.SELECTED_TEXT_COLOR, "All");
    VSegment split = new VSegment(left, top-2*Config.OFFSET-Config.TEXT_SIZE, right, top-2*Config.OFFSET-Config.TEXT_SIZE ,Z, Config.SELECTED_TEXT_COLOR);
    this.addChild(square);
    this.addChild(name);
    this.addChild(split);
    msplits[0] = split;
    msquares[0] = square;
    for(int i = 0; i < Config.CATALOGS.length; i++){
      square =  new VRectangle (left+3*Config.OFFSET, top-Config.TEXT_SIZE*(i+2), Z, 10, 10, Color.white);
      name = new VText(left+7*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*(i+2), Z, Config.SELECTED_TEXT_COLOR, Config.CATALOGS[i]);
      options = new VText(right-20*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*(i+2), Z, Config.SELECTED_TEXT_COLOR, "Advanced Options");
      split = new VSegment(left, top-2*Config.OFFSET-Config.TEXT_SIZE*(i+2), right, top-2*Config.OFFSET-Config.TEXT_SIZE*(i+2) ,Z, Config.SELECTED_TEXT_COLOR);
      this.addChild(square);
      this.addChild(name);
      this.addChild(options);
      this.addChild(split);
      msquares[i+1] = square;
      msplits[i+1] = split;
    }
  }

  public void select(int m){
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

  public int getItemSelected(double x, double y, Camera c){
    double top = parent.getBackground().getBounds()[1];
    if(x < parent.getBackground().getLocation().getX()){
      for(int i = 0; i < msplits.length; i++){
        if(y < top && y > msplits[i].getLocation().getY()){
          return i;
        }
        top = msplits[i].getLocation().getY();
      }
    }
    return -1;
  }
//rename to getMeasurementsSelected()
  public int[] getAllSelected(){
    int count = 0;
    if(msquares[0].getColor().equals(Color.red)){
      int[] retval = new int[1];
      retval[0] = 1;
      return retval;
    }
    else{
      int[] retval = new int[Config.CATALOGS.length];
      for(int i = 1; i < msquares.length; i++){
        if(msquares[i].getColor().equals(Color.red)){
          count++;
          retval[i-1] = 1;
        }
        else retval[i-1] = 0;
      }
      if(count == 0) retval = null;
      return retval;
    }
  }
public boolean coordInsideComponent(double x, double y){
  return true;
}
public void setl1(VSegment l1){
  this.l1 = l1;
}
public void setl2(VSegment l2){
  this.l2 = l2;
}
}
