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
  private double w;
  private double TEXT_SIZE = 20;
  private double OFFSET = 5;
  private Color BACKGROUND_COLOR = Color.gray;
  private Color TEXT_COLOR = Color.black;
  private int Z = 0;
  private Font bold;


  public SimbadInfo(AstroObject obj, double x, double y, SimbadResults stick){
    this.setType(Config.T_ASTRO_OBJ_BINFO);
    String[] info = obj.basicDataToString().split("\n");
    this.h = (info.length+2)*TEXT_SIZE+OFFSET;
    this.w = getWidth(info);
    stick.stick(this);
    background = new VRectangle(x+w/2+stick.getW(), y, Z, w, h, BACKGROUND_COLOR);
    background.setVisible(true);
    this.basicData = basicData(x, y, obj, info);
    this.addChild(background);
    this.basicData.setVisible(true);
    this.addChild(basicData);
  }

  private int getHeight(String[] strs){
    int length = strs.length;
    System.out.println(strs[length-1]);
    return length;
  }

  private Composite basicData(double x, double y, AstroObject obj, String[] info){
    Composite basicInfo = new Composite();
    double[] bounds = background.getBounds();
    double top = bounds[1];
    double left = bounds[0];
    VText title = new VText(left+OFFSET,top-TEXT_SIZE,Z,TEXT_COLOR,"Basic Data:");
    bold = title.getFont().deriveFont(Font.BOLD);
    title.setFont(bold);
    basicInfo.addChild(title);
    VText identifier = new VText(left+OFFSET,top-TEXT_SIZE*2,Z,TEXT_COLOR,obj.getIdentifier());
    identifier.setFont(bold);
    identifier.setScale(1.3f);
    basicInfo.addChild(identifier);
    for(int i = 0; i < info.length; i++){
      VText text = new VText(left+OFFSET,top-TEXT_SIZE*(i+3),Z,TEXT_COLOR,info[i]);
      text.setVisible(true);
      basicInfo.addChild(text);
    }

    return basicInfo;
  }

  private double getWidth(String[] info){
    int retval = 0;
    int length = 0;
    for(String str : info){
      length = str.length();
      if(length > retval) retval = length;
    }
    return retval*5.5;
  }

  public boolean insideInfo(double x, double y){
    double[] bounds = this.getBounds();
    if(bounds[0] < x && x < bounds[2] && y < bounds[1] && y > bounds[3])
      return true;
    return false;
  }
}
