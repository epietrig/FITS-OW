/*  (c) COPYRIGHT INRIA (Institut National de Recherche en Informatique et en Automatique) and INRIA Chile, 2015-2016.
 *  Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id:  $
 */

package fr.inria.ilda.simbad;

import java.util.List;
import java.util.Vector;

import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VCross;
import fr.inria.zvtm.glyphs.VSegment;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.Glyph;
import fr.inria.zvtm.engine.VirtualSpace;

import fr.inria.ilda.fitsow.FITSOW;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import fr.inria.ilda.fitsow.Config;

public class SimbadResults extends SimbadQueryGlyph{
  private VText[] ids;
  private VSegment[] splits;

  private int selected, glyphSelected;
  private List<AstroObject> results;
  private static final Color GLYPH_SELECTED_COLOR = Color.green;
  private static final Color GLYPH_UNSELECTED_COLOR = Color.red;
  double x;
  double y;

  public SimbadResults(double x, double y){
    super(300, 0*TEXT_SIZE+OFFSET);
    this.x = x;
    this.y = y;
    this.setType(Config.T_ASTRO_OBJ_SR);
    selected = -1;
    glyphSelected = -1;
  }

  public void setResults(List<AstroObject> results){
    if(results!=null){
      this.results = results;
      this.setHeight(results.size()*TEXT_SIZE+OFFSET);
      this.background = new VRectangle (x, y, Z, width, height, CONTAINER_COLOR, CONTAINER_BORDER_COLOR);
      background.setVisible(true);
      this.addChild(background);
      double[] bounds = background.getBounds();
      double top = bounds[1];
      double left = bounds[0];
      double right = bounds[2];
      int size = results.size();
      ids = new VText[size];
      splits = new VSegment[size];
      for(int i = 0; i <size; i++){
        ids[i] = new VText(left+OFFSET, top-TEXT_SIZE*(i+1), 0, TEXT_COLOR, results.get(i).getIdentifier());
        ids[i].setFont(NOT_BOLD);
        ids[i].setVisible(true);
        this.addChild(ids[i]);
        splits[i] = new VSegment(left, top-TEXT_SIZE*(i+1)-OFFSET, right, top-TEXT_SIZE*(i+1)-OFFSET, 0, BORDER_COLOR);
        this.addChild(splits[i]);
      }
    }
  }

  public VRectangle getBackground(){
    return background;
  }

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
      if(selected >= 0){
        ids[selected].setColor(TEXT_COLOR);
        ids[selected].setFont(NOT_BOLD);
      }
      ids[i].setColor(EXECUTE_COLOR);
      ids[i].setFont(BOLD);
      selected = i;
      return true;
    }
    else{
      ids[i].setColor(TEXT_COLOR);
      ids[i].setFont(NOT_BOLD);
      selected = -1;
      return false;
    }
  }

  public int getCorrespondingGlyph(Vector<Glyph> gs){
    try{
      String selectedLabel = ids[selected].getText();
      for(int i = 0; i < gs.size(); i++){
        Glyph g = gs.get(i);
        if(g.getType()!= null && g.getType().equals(Config.T_ASTRO_OBJ_LB)){
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
        gs.get(glyphSelected).setColor(TEXT_COLOR_2);
        gs.get(glyphSelected+1).setColor(GLYPH_UNSELECTED_COLOR);
      }
      gs.get(i).setColor(GLYPH_SELECTED_COLOR);
      gs.get(i+1).setColor(GLYPH_SELECTED_COLOR);
      glyphSelected = i;
    }
    else{
      gs.get(i).setColor(TEXT_COLOR_2);
      gs.get(i+1).setColor(GLYPH_UNSELECTED_COLOR);
      glyphSelected = -1;
    }
  }

  public SimbadInfo getBasicInfo(int i){
    AstroObject obj = results.get(i);
    Point2D.Double location = ids[i].getLocation();
    return new SimbadInfo(obj, location.getX(), location.getY(), this);
  }

  public VText[] getIds(){
    return ids;
  }

  public int getSelected(){
    return selected;
  }

  public int getGlyphSelected(){
    return glyphSelected;
  }
  public List<AstroObject> getResults(){
    return results;
  }

  public void addToVs(VirtualSpace vs){
    if( vs != null){
      vs.addGlyph(this);
    }
  }
}
