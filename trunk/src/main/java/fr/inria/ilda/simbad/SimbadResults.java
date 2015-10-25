package fr.inria.ilda.simbad;

import java.util.List;
import java.util.Vector;

import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VCross;
import fr.inria.zvtm.glyphs.VSegment;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.Glyph;

import fr.inria.ilda.fitsow.FITSOW;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import fr.inria.ilda.fitsow.Config;



public class SimbadResults extends Composite{
  private int size, w, h;
  private double x, y;
  private VRectangle background;
  private VText[] ids;
  private VSegment[] splits;

  private int selected, glyphSelected;
  private List<AstroObject> results;
  private static final int OFFSET = 5;
  private static final int TEXT_SIZE = 20;
  private static final Color SEGMENT_COLOR = Color.black;
  private static final Color BACKGROUND_COLOR = Color.gray;
  private static final Color TEXT_SELECTED_COLOR = Color.black;
  private static final Color TEXT_UNSELECTED_COLOR = Color.white;
  private static final Color GLYPH_SELECTED_COLOR = Color.green;
  private static final Color GLYPH_UNSELECTED_COLOR = Color.red;
  private static final int Z = 0;

  public SimbadResults(List<AstroObject> results, double x, double y){
    this.results = results;
    this.setType(Config.T_ASTRO_OBJ_SR);
    size = results.size();
    h = size*TEXT_SIZE+OFFSET;
    w = 200;
    this.x = y ;
    this.y = x;
    selected = -1;
    glyphSelected = -1;
    background = new VRectangle (x, y, Z, w, h, BACKGROUND_COLOR);
    background.setVisible(true);
    this.addChild(background);
    double[] bounds = background.getBounds();
    double top = bounds[1];
    double left = bounds[0];
    double right = bounds[2];
    ids = new VText[size];
    splits = new VSegment[size];
    for(int i = 0; i <size; i++){
      ids[i] = new VText(left+OFFSET, top-TEXT_SIZE*(i+1), 0, TEXT_UNSELECTED_COLOR, results.get(i).getIdentifier());
      ids[i].setVisible(true);
      this.addChild(ids[i]);
      splits[i] = new VSegment(left, top-TEXT_SIZE*(i+1)-OFFSET, right, top-TEXT_SIZE*(i+1)-OFFSET, 0, SEGMENT_COLOR);
      this.addChild(splits[i]);
    }
    this.setVisible(true);
  }

  public double getX(){
    return x;
  }
  public double getY(){
    return y;
  }
  public double getW(){
    return w;
  }
  public VRectangle getBackground(){
    return background;
  }
  // public boolean insideList(double x, double y){
  //   double[] bounds = this.getBounds();
  //   if(bounds[0] < x && x < bounds[2] && y < bounds[1] && y > bounds[3]) return true;
  //   return false;
  // }
  public int insideWhichObject(double x, double y){
    double start = background.getBounds()[1];
    double locationY = start;
    Point2D.Double location = new Point2D.Double();
    for(int i = 0; i < ids.length; i++){
      locationY = splits[i].getLocation().getY();
      if(y < start && y > locationY){
        start = locationY;
        return i;
      }
    }
    return -1;
  }
  public boolean highlight(int i){
    if(selected !=  i){
      if(selected >= 0)ids[selected].setColor(TEXT_UNSELECTED_COLOR);
      ids[i].setColor(TEXT_SELECTED_COLOR);
      selected = i;
      return true;
    }
    else{
      ids[i].setColor(TEXT_UNSELECTED_COLOR);
      selected = -1;
      return false;
    }
  }
  public int getCorrespondingGlyph(Vector<Glyph> gs){
    try{
      String selectedLabel = ids[selected].getText();
      for(int i = 0; i < gs.size(); i++){
        Glyph g = gs.get(i);
        if(g.getType().equals(Config.T_ASTRO_OBJ_LB)){
          VText label = (VText) g;
          if(label.getText().equals(selectedLabel)) return i;
        }
      } return -1;
    }catch(ArrayIndexOutOfBoundsException e){
      return glyphSelected;
    }
  }

  public void highlightCorrespondingGlyph(Vector<Glyph> gs, int i){
    if(i != glyphSelected){
      if(glyphSelected >= 0){
        gs.get(glyphSelected).setColor(TEXT_UNSELECTED_COLOR);
        gs.get(glyphSelected+1).setColor(GLYPH_UNSELECTED_COLOR);
      }
      gs.get(i).setColor(GLYPH_SELECTED_COLOR);
      gs.get(i+1).setColor(GLYPH_SELECTED_COLOR);
      glyphSelected = i;
    }
    else{
      gs.get(i).setColor(TEXT_UNSELECTED_COLOR);
      gs.get(i+1).setColor(GLYPH_UNSELECTED_COLOR);
      glyphSelected = -1;
    }
  }

  public SimbadInfo getBasicInfo(int i){
    AstroObject obj = results.get(i);
    Point2D.Double location = ids[i].getLocation();
    return new SimbadInfo(obj, location.getX(), location.getY(), this);
  }

}
