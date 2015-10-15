package fr.inria.ilda.simbad;

import java.util.List;
import java.util.Vector;

import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.Glyph;

import java.awt.Color;
import java.awt.geom.Point2D;

import fr.inria.ilda.fitsow.Config;

public class SimbadInfo extends Composite{
  private Composite basicData;
  private VRectangle background;
  private double h;
  private double w = 200;

  public SimbadInfo(AstroObject obj, double x, double y){
    this.setType("SimbadInfo");
    String[] info = obj.toString().split("\n");
    this.h = info.length*20;
    background = new VRectangle(x, y, 0, w, h, Color.gray);
    background.setVisible(true);
    this.addChild(background);
    double start = background.getBounds()[1];
    for(int i = 0; i < info.length; i++){
      VText text = new VText(x-95,start-20*(i+1),0,Color.white,info[i]);
      text.setVisible(true);
      this.addChild(text);
    }
  }
}
