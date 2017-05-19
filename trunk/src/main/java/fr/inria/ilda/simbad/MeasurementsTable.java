/*  (c) COPYRIGHT INRIA (Institut National de Recherche en Informatique et en Automatique) and INRIA Chile, 2015-2016.
 *  Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id:  $
 */

 package fr.inria.ilda.simbad;

import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;
import fr.inria.zvtm.engine.VirtualSpace;

import fr.inria.ilda.fitsow.Config;

import java.awt.Color;

public class MeasurementsTable extends SimbadQueryGlyph{
  private int nRows, nCols;
  private VRectangle background;
  private final static int Z = 0;
  private final static Color BACKGROUND_COLOR = Color.white;
  private final static Color TEXT_COLOR = Color.black;
  private final static int OFFSET = 5;

  public MeasurementsTable(Measurement measurement, double x, double y, double topOffset, VirtualSpace vs){
    super(0, 0);
    String[][] measurements = measurement.getTable();
    String name = measurement.getName();
    this.setType(Config.T_ASTRO_OBJ_MT);
    this.nCols = measurements[0].length;
    this.nRows = measurements.length;

    double[] sizeOfColumns = sizeOfColumns(measurements);
    double w = 0;
    for(double size: sizeOfColumns){
      w = w+size;
    }

    this.width = w;
    this.height =(nRows)*20;

    background = new VRectangle (x+width/2+OFFSET, y-height/2-topOffset, Z, width, height, BACKGROUND_COLOR, BORDER_COLOR);
    background.setVisible(true);
    double[] bounds = background.getBounds();
    double left = bounds[0];
    double top = bounds[1];
    double right = bounds[2];
    double bottom = bounds[3];

    VText title = new VText(OFFSET+left, top+OFFSET, 0, TEXT_COLOR, name);
    title.setScale(1.1f);

    this.addChild(background);
    this.addChild(title);

    double xPosition = 0;
    for(int j = 0; j < nRows; j ++){
      for(int i = 0; i < nCols; i ++){

        VText mText = new VText(OFFSET+left+xPosition, top-15-20*j, 0, TEXT_COLOR, measurements[j][i].trim());
        if(j==0){
          double xSplit = mText.getBounds()[0]-Config.OFFSET;
          VSegment splits = new VSegment(xSplit,top,xSplit,bottom,Z,BORDER_COLOR);
          this.addChild(splits);
        }
        xPosition = xPosition+sizeOfColumns[i];
        mText.setVisible(true);
        this.addChild(mText);
      }
      xPosition = 0;
    }
  }
  private double[] sizeOfColumns(String[][] m){
    double[] retval = new double[nCols];
    double colSize = 0;
    double aux = 0;
    for(int j = 0; j < nCols; j++){
      for(int i = 0; i < nRows; i++){
        aux = m[i][j].trim().length()*8;
        if(aux > colSize) colSize = aux;
      }
      retval[j] = colSize;
      colSize = 0;
    }
    return retval;
  }

  public VRectangle getBackground(){
    return background;
  }
}
