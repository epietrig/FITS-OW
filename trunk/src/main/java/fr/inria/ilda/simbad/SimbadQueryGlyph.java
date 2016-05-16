/*  (c) COPYRIGHT INRIA (Institut National de Recherche en Informatique et en Automatique) and INRIA Chile, 2015-2016.
 *  Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id:  $
 */

package fr.inria.ilda.simbad;
import java.awt.Color;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.VRectangle;
import java.awt.Font;
import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.Camera;
import fr.inria.zvtm.glyphs.Glyph;
import java.util.Vector;

public abstract class SimbadQueryGlyph extends Composite{
  protected double width, height;
  protected VRectangle background;
  protected Font bold, notBold;
  protected VirtualSpace vs;
  protected static VirtualSpace SQ_VIRTUAL_SPACE;
  protected static Camera SQ_CAMERA;
  // protected static Color BACKGROUND_COLOR = new Color(217, 218, 222);
  protected static Color BACKGROUND_COLOR = new Color(255, 255, 255);
  protected static Color BORDER_COLOR= new Color(204, 204, 204);
  protected static Color CONTAINER_COLOR = new Color(245, 245, 245);
  protected static Color CONTAINER_BORDER_COLOR = new Color(227,227,227);
  protected static Color UNSELECTED_BACKGROUND_COLOR = new Color(204, 204, 204);
  protected static Color SELECTED_BACKGROUND_COLOR = new Color(230, 230, 230);
  protected static Color EXECUTE_COLOR = new Color(92,184,92);
  protected static Color EXECUTE_BORDER_COLOR = new Color(76,174,76);
  protected static Color TEXT_COLOR = new Color(51, 51, 51);
  protected static Color TEXT_COLOR_2 = Color.white ;
  protected static Color UNSELECTED_COLOR = Color.white;
  protected static Color SELECTED_COLOR = new Color(134,197,67);
  protected static Color CANCEL_COLOR = new Color(217, 83, 79);
  protected static Color CANCEL_BORDER_COLOR = new Color(212, 63, 58);
  protected static Font BOLD = new Font("bold", Font.BOLD, 12);
  protected static Font NOT_BOLD = new Font("plain", Font.PLAIN, 12);
  protected static final int Z = 0;
  protected static final double TEXT_SIZE = 20;
  protected static final double OFFSET = 5;

  public SimbadQueryGlyph(double width, double height){
    this.width = width;
    this.height = height;
  }

  public double getWidth(){
    return width;
  }
  public double getHeight(){
    return height;
  }
  public boolean coordInsideItem(int jpx, int jpy){
    return background.coordInsideP(jpx, jpy, SQ_CAMERA);
  }
  public static SimbadQueryGlyph getCurrent(String type){
    Vector<Glyph> gv = SQ_VIRTUAL_SPACE.getGlyphsOfType(type);
    if(gv.size()>0){
      SimbadQueryGlyph g = (SimbadQueryGlyph) gv.get(0);
      return g;
    }
    return null;
  }
  public static void setVirtualSpace(VirtualSpace vs){
    SQ_VIRTUAL_SPACE = vs;
  }
  public static void setCamera(Camera c){
    SQ_CAMERA = c;
  }
  public void setHeight(double h){
    this.height = h;
  }

}
