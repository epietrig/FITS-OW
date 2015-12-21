package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;

import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.Camera;

import fr.inria.ilda.fitsow.Config;


import java.awt.Color;

public class SimbadFluxFilter extends SimbadFilter{
  // private SimbadCriteria parent;//this shouldn't be here.
  // private VSegment l1, l2; //this should be off and I should add a backgorund
  private VRectangle[] squares = null;
  // private VRectangle[] qsquares = null;
  private VText[]type;
  private VText[]range;
  private String[] rangeStr;
  //this doesn't really need a father
  public SimbadFluxFilter(double top, double left, double right){
    super(W, H);
    // this.parent = parent;
    this.background = new VRectangle(left+150,top-150,Z,300,300,Config.SELECTED_BACKGROUND_COLOR);
    this.addChild(background);
    squares = new VRectangle[13];
    type = new VText[13];
    range = new VText[13];
    rangeStr = new String[13];
    for(int i = 0; i<rangeStr.length; i++) rangeStr[i] = "";
    setFilterLayout("Flux:", top, left, right);
    for(int i = 0; i < Config.FLUX_TYPES.length*2; i=i+2){
      if( i < 10){
        squares[i/2] =  new VRectangle (left+2*Config.OFFSET, top-Config.TEXT_SIZE*(i+2), Z, 10, 10, Color.white);
        type[i/2] = new VText(left+5*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*(i+2), Z, Config.SELECTED_TEXT_COLOR, Config.FLUX_TYPES[i/2]);
        range[i/2] = new VText(left+5*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*(i+3), Z, Config.SELECTED_TEXT_COLOR, "Range:");
      }
      else if(i<20){
        squares[i/2] =  new VRectangle (left+2*Config.OFFSET+width/3, top-Config.TEXT_SIZE*(i+2-10), Z, 10, 10, Color.white);
        type[i/2] = new VText(left+5*Config.OFFSET+width/3, top-Config.OFFSET-Config.TEXT_SIZE*(i+2-10), Z, Config.SELECTED_TEXT_COLOR, Config.FLUX_TYPES[i/2]);
        range[i/2] = new VText(left+5*Config.OFFSET+width/3, top-Config.OFFSET-Config.TEXT_SIZE*(i+3-10), Z, Config.SELECTED_TEXT_COLOR, "Range:");
      }
      else{
        squares[i/2] =  new VRectangle (left+2*Config.OFFSET+2*width/3, top-Config.TEXT_SIZE*(i+2-20), Z, 10, 10, Color.white);
        type[i/2] = new VText(left+5*Config.OFFSET+2*width/3, top-Config.OFFSET-Config.TEXT_SIZE*(i+2-20), Z, Config.SELECTED_TEXT_COLOR, Config.FLUX_TYPES[i/2]);
        range[i/2] = new VText(left+5*Config.OFFSET+2*width/3, top-Config.OFFSET-Config.TEXT_SIZE*(i+3-20), Z, Config.SELECTED_TEXT_COLOR, "Range:");
      }
      this.addChild(squares[i/2]);
      this.addChild(type[i/2]);
      this.addChild(range[i/2]);
    }
    qsquares = qualitySelector(this, left+2*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*13);
  }
//this should be on parent class
  // public boolean coordInsideComponent(double x, double y){
    // double[] bckgBounds = parent.getBackground().getBounds();
    // double left = bckgBounds[0];
    // double right = bckgBounds[2];
    // return x < right && x > left && y < l1.getLocation().getY() && y > l2.getLocation().getY();
    // return true;
  // }
//this should be on parent class
  public int getItemSelected(double x, double y){
    for(int i = 0; i < squares.length; i++){
      if(squares[i].coordInsideV(x,y, SQ_CAMERA) || type[i].coordInsideV(x,y, SQ_CAMERA)) return i*2;
      else if(range[i].coordInsideV(x,y, SQ_CAMERA)) return i*2 + 1;
    }
    for(int i = 0; i<qsquares.length; i++){
      if(qsquares[i].coordInsideV(x,y, SQ_CAMERA)) return i+26;
    }
    return -1;
  }

  public void select(int i, String str){
    if(i >= 0 && i<26){
      if(i%2 == 0){
        if(squares[i/2].getColor().equals(Color.red))
          squares[i/2].setColor(Color.white);
        else squares[i/2].setColor(Color.red);
      }
      else if(i%2 != 0){
        range[i/2].setText("Range: "+str);
        rangeStr[i/2] = str;
      }
    }
    else if(i >= 0 && qsquares!= null){
      if(qsquares[i-26].getColor().equals(Color.red))
          qsquares[i-26].setColor(Color.white);
      else if(qsquares[i-26].getColor().equals(Color.white))
        qsquares[i-26].setColor(Color.red);
    }
  }

  public int[] getFluxesSelected(){
    int[] retval = new int[squares.length];
    for(int i = 0; i < squares.length; i++){
      if(squares[i].getColor().equals(Color.red)) retval[i] = 1;
      else retval[i] = 0;
    }
    return retval;
  }

  public String[] getRangeStrs(){
    return rangeStr;
  }
// this should be on parent class
  // public int[] getQualitiesSelected(){
  //   int[] retval = new int[5];
  //   for(int i = 0; i < qsquares.length; i++){
  //     if(qsquares[i].getColor().equals(Color.red)) retval[i] = 1;
  //     else retval[i] = 0;
  //   }
  //   return retval;
  // }

  // public void setl1(VSegment l1){
  //   this.l1 = l1;
  // }
  //
  // public void setl2(VSegment l2){
  //   this.l2 = l2;
  // }
}
