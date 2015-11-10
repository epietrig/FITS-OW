package fr.inria.ilda.simbad;

import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.Glyph;

import fr.inria.ilda.fitsow.Config;

import java.awt.Color;
import java.awt.Font;

public class MeasurementsTable extends Composite{
  private int w, h, nRows, nCols;
  private double x, y;
  private VRectangle background;
  private final static int Z = 0;
  private final static Color BACKGROUND_COLOR = Color.white;
  private final static Color TEXT_COLOR = Color.black;
  private final static int OFFSET = 5;

  public MeasurementsTable(Measurement measurement, double x, double y, double topOffset){
    String[][] measurements = measurement.getTable();
    String name = measurement.getName();
    this.setType(Config.T_ASTRO_OBJ_MT);
    this.nCols = measurements[0].length;
    this.nRows = measurements.length;
    // double maxElementSize = maxElementSize(measurements);

    double[] sizeOfColumns = sizeOfColumns(measurements);
    w = 0;
    for(double size: sizeOfColumns){
      w = w+(int)size;
    }
    // w = w + OFFSET*nCols;
    // this.w = (int)maxElementSize*nCols;
    this.h = (nRows)*20;

    background = new VRectangle (x+w/2+OFFSET, y-h/2-topOffset, Z, w, h, BACKGROUND_COLOR);
    background.setVisible(true);
    double[] bounds = background.getBounds();
    double left = bounds[0];
    double top = bounds[1];

    VText title = new VText(OFFSET+left, top+OFFSET, 0, TEXT_COLOR, name);
    title.setScale(1.1f);
    this.addChild(title);
    this.addChild(background);

    double xPosition = 0;
    for(int j = 0; j < nRows; j ++){
      for(int i = 0; i < nCols; i ++){

        VText mText = new VText(OFFSET+left+xPosition, top-15-20*j, 0, TEXT_COLOR, measurements[j][i].trim());
        xPosition = xPosition+sizeOfColumns[i];
        // VText mText = new VText(OFFSET+left+maxElementSize*i, top-15-20*j, 0, TEXT_COLOR, measurements[j][i].trim());
        if(j==0)
          mText.setFont(mText.getFont().deriveFont(Font.BOLD));
        mText.setVisible(true);
        this.addChild(mText);
      }
      xPosition = 0;
    }
  }
  private double maxElementSize(String[][] m){
    double retval = 0;
    double size = 0;
    for(int i = 0; i < nRows; i++){
      for(int j = 0; j < nCols; j++){
        size = m[i][j].trim().length();
        if(size > retval) retval = size;
      }
    }
    return retval*6;
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
