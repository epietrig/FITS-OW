package fr.inria.ilda.simbad;

import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.Glyph;

import java.awt.Color;

public class MeasurementsTable extends Composite{
  private int w, h, nRows, nCols;
  private double x, y;
  private VRectangle background;
  private final static int Z = 0;
  private final static Color BACKGROUND_COLOR = Color.white;
  private final static Color TEXT_COLOR = Color.black;

  public MeasurementsTable(String[][] measurements){
    this.w = 200;
    this.nCols = measurements[0].length;
    this.nRows = measurements.length;
    this.h = (nRows)*20;
    background = new VRectangle (x, y, Z, w, h, BACKGROUND_COLOR);
    background.setVisible(true);
    double[] bounds = background.getBounds();
    double left = bounds[0];
    double top = bounds[1];
    this.addChild(background);
    double maxHeaderSize = getMaxHeaderSize(measurements);
    for(int j = 0; j < nRows; j ++){
      for(int i = 0; i < nCols; i ++){
        VText mText = new VText(left+5+maxHeaderSize*i, top-5-20*j, 0, TEXT_COLOR, measurements[j][i]);
        mText.setVisible(true);
        this.addChild(mText);
      }
    }
  }
  private double getMaxHeaderSize(String[][] m){
    double retval = 0;
    double size = 0;
    for(int i = 0; i < nCols; i++){
      size = m[0][i].length();
      if(size > retval) retval = size;
    }
    return retval*5;
  }
}
