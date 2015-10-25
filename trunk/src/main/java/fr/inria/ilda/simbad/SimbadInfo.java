package fr.inria.ilda.simbad;

import java.util.List;
import java.util.Vector;

import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VRoundRect;
import fr.inria.zvtm.glyphs.VSegment;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.Glyph;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.Font;

import fr.inria.ilda.fitsow.Config;

public class SimbadInfo extends Composite{
  private Composite basicData;
  private Composite tabs;
  private String basicDataStr = "Basic Data";
  private String measurementsStr = "Measurements";
  private String tabSelected;
  private VRectangle background;
  private VRectangle basicDataTab;
  private VRectangle measurementsTab;
  private double h;
  private double w;
  private double TEXT_SIZE = 20;
  private double OFFSET = 5;
  private Color BACKGROUND_COLOR = Color.gray;
  private Color SELECTED_BACKGROUND_COLOR = new Color(195, 195, 195);
  private Color TEXT_COLOR = Color.black;
  private Color SELECTED_TEXT_COLOR = new Color(34,34,34);
  private int Z = 0;
  private Font bold;


  public SimbadInfo(AstroObject obj, double x, double y, SimbadResults stick){
    this.setType(Config.T_ASTRO_OBJ_BINFO);
    String[] info = obj.basicDataToString().split("\n");
    this.h = (info.length+2)*TEXT_SIZE+OFFSET;
    this.w = getWidth(info);

    background = new VRectangle(x+w/2+stick.getW(), y, Z, w, h, SELECTED_BACKGROUND_COLOR);
    background.setVisible(true);
    this.addChild(background);

    double[] bounds = background.getBounds();
    double left = bounds[0];
    double top = bounds[1];
    double right = bounds[2];

    this.tabs = tabs(top, left);
    this.tabs.setVisible(true);
    this.addChild(tabs);

    this.basicData = basicData(top, left, obj, info);
    this.basicData.setVisible(true);
    this.addChild(basicData);


  }

  private int getHeight(String[] strs){
    int length = strs.length;
    System.out.println(strs[length-1]);
    return length;
  }
  private Composite tabs(double top, double left){
    Composite tabs = new Composite();

    basicDataTab = new VRectangle(left+w/4, top-2*OFFSET, Z, w/2, TEXT_SIZE, SELECTED_BACKGROUND_COLOR);
    VText basicDataTabStr = new VText(left+OFFSET,top-18,Z,SELECTED_TEXT_COLOR,basicDataStr);
    basicDataTabStr.setScale(1.3f);
    tabSelected = basicDataStr;
    bold = basicDataTabStr.getFont().deriveFont(Font.BOLD);
    basicDataTabStr.setFont(bold);

    measurementsTab = new VRectangle(left+w/4+w/2, top-2*OFFSET, Z, w/2, TEXT_SIZE, BACKGROUND_COLOR);
    VText measurementsTabStr = new VText(left+w/2+2*OFFSET,top-18,Z,TEXT_COLOR,measurementsStr);
    measurementsTabStr.setScale(1.3f);

    basicDataTab.setVisible(true);
    tabs.addChild(basicDataTab);
    basicDataTabStr.setVisible(true);
    tabs.addChild(basicDataTabStr);
    measurementsTab.setVisible(true);
    tabs.addChild(measurementsTab);
    measurementsTabStr.setVisible(true);
    tabs.addChild(measurementsTabStr);
    return tabs;
  }
  private Composite basicData(double top, double left, AstroObject obj, String[] info){
    Composite basicInfo = new Composite();
    VText identifier = new VText(left+OFFSET,top-TEXT_SIZE*2,Z,SELECTED_TEXT_COLOR,obj.getIdentifier());
    bold = identifier.getFont().deriveFont(Font.BOLD);
    identifier.setFont(bold);
    identifier.setScale(1.3f);
    basicInfo.addChild(identifier);
    for(int i = 0; i < info.length; i++){
      VText text = new VText(left+OFFSET,top-TEXT_SIZE*(i+3),Z,SELECTED_TEXT_COLOR,info[i]);
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
  public void activateBasicDataTab(){
    if(!tabSelected.equals(basicDataStr)){
      tabSelected = basicDataStr;
      basicDataTab.setColor(SELECTED_BACKGROUND_COLOR);
      measurementsTab.setColor(BACKGROUND_COLOR);
      this.addChild(basicData);
      // this.removeChild(measurementsTab);
    }
  }
  public void activateMeasurementsTab(){
    if(!tabSelected.equals(measurementsStr)){
      tabSelected = measurementsStr;
      basicDataTab.setColor(BACKGROUND_COLOR);
      measurementsTab.setColor(SELECTED_BACKGROUND_COLOR);
      this.removeChild(basicData);
    }
  }
  // public boolean insideInfo(double x, double y){
  //   double[] bounds = this.getBounds();
  //   if(bounds[0] < x && x < bounds[2] && y < bounds[1] && y > bounds[3])
  //     return true;
  //   return false;
  // }

  public VRectangle getBasicDataTab(){
    return basicDataTab;
  }
  public VRectangle getMeasurementsTab(){
    return measurementsTab;
  }
  public VRectangle getBackground(){
    return background;
  }
}
