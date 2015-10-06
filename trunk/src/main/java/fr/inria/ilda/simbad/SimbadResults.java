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
  private VText[] ids;
  private String glyphType = "SimbadResults";

  public SimbadResults(List<AstroObject> results, double x, double y){
    this.setType(glyphType);
    size = results.size();
    h = size*20+5;
    w = 200;
    background = new VRectangle (x, y, 0, w, h, Color.gray);
    background.setVisible(true);
    this.addChild(background);
    double start = background.getBounds()[1];
    ids = new VText[size];
    VSegment[] splits = new VSegment[size];
    for(int i = 0; i <size; i++){
      ids[i] = new VText(x-x/2+5, start-20*(i+1), 0, Color.white, results.get(i).getIdentifier());
      ids[i].setVisible(true);
      this.addChild(ids[i]);
      splits[i] = new VSegment(x-x/2, start-20*(i+1)-5, x+x/2, start-20*(i+1)-5, 0, Color.black);
      this.addChild(splits[i]);
    }
    this.setVisible(true);
  }
}
