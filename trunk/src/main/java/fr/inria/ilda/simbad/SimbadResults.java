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

  public SimbadResults(List<AstroObject> results, double x, double y){
    size = results.size();
    h = size*20+5;
    w = 200;
    background = new VRectangle (x, y, 0, w, h, Color.gray);
    background.setVisible(true);
    this.addChild(background);
    double start = background.getBounds()[1];
    ids = new VText[size];
    VSegment[] splits = new VSegment[size];
    VRectangle[] button = new VRectangle[size];
    for(int i = 0; i <size; i++){
      ids[i] = new VText(x-x/2+5, start-20*(i+1), 0, Color.white, results.get(i).getIdentifier());
      ids[i].setVisible(true);
      this.addChild(ids[i]);
      button[i] = new VRectangle(x+w/2 -20, start-20*(i+1)+5,0, 15, 15,Color.red);
      VText plus = new VText(x+w/2 -25, start-20*(i+1), 0, Color.black, "+");
      this.addChild(button[i]);
      this.addChild(plus);
      splits[i] = new VSegment(x-x/2, start-20*(i+1)-5, x+x/2, start-20*(i+1)-5, 0, Color.black);
      this.addChild(splits[i]);
    }
    this.setVisible(true);
  }
}
