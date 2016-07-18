package fr.inria.ilda.simbad;

import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.ilda.fitsow.FITSOW;
import fr.inria.ilda.fitsow.SimbadQuery;
import fr.inria.ilda.fitsow.MVEventListener;
import java.awt.Color;
import fr.inria.ilda.fitsow.Config;

public class SimbadClearQuery extends SimbadQueryGlyph{
  private VRectangle clearButton;
  private String clear = "Clear query";
  double x;
  double y;

  public SimbadClearQuery(double x, double y){
    super(200, 100);
    this.x = x;
    this.y = y;
    this.background = new VRectangle (x, y, Z, width, height, CONTAINER_COLOR, CONTAINER_BORDER_COLOR);
    background.setVisible(true);
    this.addChild(background);
    clearButton = new VRectangle (x, y, Z, 150, 50, CANCEL_COLOR,CANCEL_BORDER_COLOR);
    double left = clearButton.getBounds()[0];
    VText clearText =  new VText(left+width/5, y-OFFSET, 0, TEXT_COLOR_2, clear);
    clearText.setScale(1.3f);
    this.addChild(clearButton);
    this.addChild(clearText);
    this.setType(Config.T_ASTRO_OBJ_SCQ);
  }

  public VRectangle getClearButton(){
    return clearButton;
  }
  public VRectangle getBackground(){
    return background;
  }

  public void updateSimbadClearQuery(int jpx, int jpy, FITSOW app){
    MVEventListener eh = app.getMVEventListener();
    SimbadQuery sq = eh.getSimbadQuery();
    if(sq == null) sq = new SimbadQuery(app);
    sq.clearQueryResults();
    sq = null;
  }

}
