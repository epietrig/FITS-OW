/*  (c) COPYRIGHT INRIA (Institut National de Recherche en Informatique et en Automatique) and INRIA Chile, 2015-2016.
 *  Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id:  $
 */

 package fr.inria.ilda.simbad;

import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.ilda.fitsow.Config;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;
import fr.inria.zvtm.glyphs.VRectangle;
import java.awt.Color;

public abstract class SimbadFilter extends SimbadQueryGlyph{
  protected VRectangle[] qsquares = null;
  protected static final double W = 300;
  protected static final double H = 300;
  public SimbadFilter(double width, double height){
    super(width, height);
  }
    public void setFilterLayout(String titleStr, double top, double left, double right){
      VText title = new VText(left+Config.OFFSET,top-Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR, titleStr);
      title.setScale(1.1f);
      this.addChild(title);
      VSegment split = new VSegment(left, top-Config.OFFSET-Config.TEXT_SIZE, right, top-Config.OFFSET-Config.TEXT_SIZE,Z, Config.SELECTED_TEXT_COLOR);

      // bsplits.add(split1);
      this.addChild(split);
      // VSegment split2 = new VSegment(left, top-size, right, top-size ,Z, Config.SELECTED_TEXT_COLOR);
      // bsplits.add(split2);
      // c.addChild(split2);
    }
    public VRectangle[] qualitySelector(Composite c, double x, double y){
      VText quality = new VText(x,y,Z,Config.SELECTED_TEXT_COLOR,"Quality:");
      c.addChild(quality);
      VRectangle[] qualities = new VRectangle[5];
      for(char alphabet = 'A'; alphabet <= 'E';alphabet++) {
        VRectangle square =  new VRectangle (x+((int)alphabet-65)*2*Config.TEXT_SIZE+Config.OFFSET, y-Config.TEXT_SIZE+Config.OFFSET, Z, 10, 10, Color.white);
        VText qualityStr = new VText(x+((int)alphabet-65)*2*Config.TEXT_SIZE+3*Config.OFFSET, y-Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR, Character.toString(alphabet));
        qualities[(int)alphabet-65] = square;
        c.addChild(square);
        c.addChild(qualityStr);
      }
      return qualities;
    }
    public int[] getQualitiesSelected(){
      int[] retval = new int[qsquares.length];
      for(int i = 0; i<qsquares.length; i++){
        if(qsquares[i].getColor().equals(Color.red))
          retval[i] = 1;
        else
          retval[i] = 0;
      }
      return retval;
    }
    public abstract void select(int i, String str);
    public abstract int getItemSelected(double x, double y);

}
