package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;

import fr.inria.zvtm.engine.VirtualSpace;

import fr.inria.ilda.fitsow.Config;

import java.awt.Color;

public class SimbadMFilter extends SimbadQueryGlyph{
  private static String[] catalogs = {"cel","cl.g","diameter","distance","einstein",
    "fe_h","gcrv","gen","gj","hbet","hbet1","herschel","hgam","iras","irc","iso","iue","jp11", "mk",
    "orv","plx","pm","pos","posa","rot","rvel","sao","td1","ubv","uvby","uvby1",
    "v*","velocities","xmm","z","ze"};
  private VSegment[] msplits;
  private VRectangle[] msquares;
  private SimbadCriteria parent;

  public SimbadMFilter(double top, double left, double right, VirtualSpace vs, SimbadCriteria parent){
    super(left, top, 0, 0, vs);
    this.parent = parent;
    msquares = new VRectangle[catalogs.length+1];
    msplits = new VSegment[catalogs.length+1];
    VText name, options;
    VRectangle square =  new VRectangle (left+2*Config.OFFSET, top-Config.TEXT_SIZE, Z, 10, 10, Color.white);
    name = new VText(left+6*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE, Z, Config.SELECTED_TEXT_COLOR, "All");
    VSegment split = new VSegment(left, top-2*Config.OFFSET-Config.TEXT_SIZE, right, top-2*Config.OFFSET-Config.TEXT_SIZE ,Z, Config.SELECTED_TEXT_COLOR);
    this.addChild(square);
    this.addChild(name);
    this.addChild(split);
    msplits[0] = split;
    msquares[0] = square;
    for(int i = 0; i < catalogs.length; i++){
      square =  new VRectangle (left+3*Config.OFFSET, top-Config.TEXT_SIZE*(i+2), Z, 10, 10, Color.white);
      name = new VText(left+7*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*(i+2), Z, Config.SELECTED_TEXT_COLOR, catalogs[i]);
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

  public void selectMeasurement(int m){
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

  public int getMeasurementSelected(double x, double y){
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

}
