package fr.inria.ilda.simbad;

import java.util.List;
import java.util.Vector;

import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VCross;
import fr.inria.zvtm.glyphs.VSegment;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.Glyph;

import java.awt.Color;
import java.awt.geom.Point2D;

import fr.inria.ilda.fitsow.Config;



public class SimbadResults extends Composite{

  private int size, w, h;
  private double x, y;
  private VRectangle background;
  private String glyphType = "SimbadResults";
  private VText[] ids;
  private VSegment[] splits;
  private Color SELECTED_COLOR = Color.black;
  private Color UNSELECTED_COLOR = Color.white;
  private int selected, glyphSelected;

  public SimbadResults(List<AstroObject> results, double x, double y){
    this.setType(glyphType);
    size = results.size();
    h = size*20+5;
    w = 200;
    this.x = x ;
    this.y = y;
    selected = -1;
    glyphSelected = -1;
    background = new VRectangle (x, y, 0, w, h, Color.gray);
    background.setVisible(true);
    this.addChild(background);
    double start = background.getBounds()[1];
    ids = new VText[size];
    splits = new VSegment[size];
    for(int i = 0; i <size; i++){
      ids[i] = new VText(x-x/2+5, start-20*(i+1), 0, Color.white, results.get(i).getIdentifier());
      ids[i].setVisible(true);
      this.addChild(ids[i]);
      splits[i] = new VSegment(x-x/2, start-20*(i+1)-5, x+x/2, start-20*(i+1)-5, 0, Color.black);
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
  public boolean insideList(double x, double y){
    double[] bounds = this.getBounds();
    if(bounds[0] < x && x < bounds[2] && y < bounds[1] && y > bounds[3]) return true;
    return false;
  }
  public int insideWhichObject(double x, double y){
    double start = background.getBounds()[1];
    double locationY = start;
    Point2D.Double location = new Point2D.Double();
    System.out.println("y: "+y);
    for(int i = 0; i < ids.length; i++){
      locationY = splits[i].getLocation().getY();
      System.out.println("yi: "+locationY);
      if(y < start && y > locationY){
        start = locationY;
        return i;
      }
    }
    return -1;
  }
  public void highlight(int i){
    if(selected !=  i){
      if(selected >= 0)ids[selected].setColor(UNSELECTED_COLOR);
      ids[i].setColor(SELECTED_COLOR);
      selected = i;
    }
    else if(selected == i){
      ids[i].setColor(UNSELECTED_COLOR);
      selected = -1;
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
        gs.get(glyphSelected).setColor(Color.white);
        gs.get(glyphSelected+1).setColor(Color.red);
      }
      gs.get(i).setColor(Color.green);
      gs.get(i+1).setColor(Color.green);
      glyphSelected = i;
    }
    else{
      System.out.println("unselect");
      gs.get(i).setColor(Color.white);
      gs.get(i+1).setColor(Color.red);
      glyphSelected = -1;
    }
  }

}
