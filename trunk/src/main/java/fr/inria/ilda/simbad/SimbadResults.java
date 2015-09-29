package fr.inria.ilda.simbad;

import java.util.List;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;
import fr.inria.zvtm.glyphs.Composite;
import java.awt.Color;

public class SimbadResults extends Composite{

  private int size, w, h;
  private VRectangle background;

  public SimbadResults(List<AstroObject> results, double x, double y){
    size = results.size();
    h = size*20;
    w = 200;
    background = new VRectangle (x, y, 0, 200, 200, Color.red);
    background.setVisible(true);
    this.addChild(background);
    this.setVisible(true);
  }
}
