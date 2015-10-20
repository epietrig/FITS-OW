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
import java.awt.Font;

import fr.inria.ilda.fitsow.Config;

public class SimbadInfo extends Composite{
  private Composite basicData;
  private VRectangle background;
  private double h;
  private double w = 220;
  private double TEXT_SIZE = 20;
  private double OFFSET = 25;
  private double X_OFFSET = 100;
  private Color BACKGROUND_COLOR = Color.gray;
  private Color TEXT_COLOR = Color.white;
  private int Z = 0;
  private Font bold;


  public SimbadInfo(AstroObject obj, double x, double y){
    this.setType(Config.T_ASTRO_OBJ_BINFO);
    this.h = AstroObject.BASIC_DATA_LENGTH*TEXT_SIZE+OFFSET;
    background = new VRectangle(x, y, Z, w, h, BACKGROUND_COLOR);
    background.setVisible(true);
    this.basicData = basicData(x, y, obj);
    this.addChild(background);
    this.basicData.setVisible(true);
    this.addChild(basicData);
  }

  private Composite basicData(double x, double y, AstroObject obj){
    Composite basicInfo = new Composite();
    String[] info = obj.toString().split("\n");
    double start = background.getBounds()[1];
    VText title = new VText(x-X_OFFSET,start-TEXT_SIZE,Z,TEXT_COLOR,"Basic Data:");
    bold = title.getFont().deriveFont(Font.BOLD);
    title.setFont(bold);
    basicInfo.addChild(title);
    VText identifier = new VText(x-X_OFFSET,start-TEXT_SIZE*2,Z,TEXT_COLOR,obj.getIdentifier());
    identifier.setFont(bold);
    identifier.setScale(1.3f);
    basicInfo.addChild(identifier);
    for(int i = 0; i < info.length; i++){
      VText text = new VText(x-X_OFFSET,start-TEXT_SIZE*(i+3),Z,TEXT_COLOR,info[i]);
      text.setVisible(true);
      basicInfo.addChild(text);
    }

    return basicInfo;
  }

  public boolean insideInfo(double x, double y){
    double[] bounds = this.getBounds();
    if(bounds[0] < x && x < bounds[2] && y < bounds[1] && y > bounds[3])
      return true;
    return false;
  }
}
