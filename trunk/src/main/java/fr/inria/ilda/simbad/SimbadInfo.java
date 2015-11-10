package fr.inria.ilda.simbad;

import java.util.List;
import java.util.Vector;

import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VRoundRect;
import fr.inria.zvtm.glyphs.VSegment;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.Glyph;

// import fr.inria.zvtm.engine.VirtualSpace;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.Font;
import fr.inria.zvtm.engine.VirtualSpace;

import fr.inria.ilda.fitsow.Config;

public class SimbadInfo extends Composite{
  public Composite basicData;
  private Composite measurements;
  private Composite tabs;
  public static String basicDataStr = "Basic Data";
  public static String measurementsStr = "Measurements";
  private String tabSelected;
  private VRectangle background;
  private VRectangle basicDataTab;
  private VRectangle measurementsTab;
  private double h;
  private double w;
  private int Z = 0;
  private Font bold;
  private VirtualSpace vs;


  public SimbadInfo(AstroObject obj, double x, double y, SimbadResults parent){
    this.setType(Config.T_ASTRO_OBJ_BINFO);
    String[] info = obj.basicDataToString().split("\n");
    this.h = (info.length+2)*Config.TEXT_SIZE+Config.OFFSET;
    this.w = getWidth(info);

    background = new VRectangle(x+w/2+parent.getW(), y, Z, w, h, Config.SELECTED_BACKGROUND_COLOR);
    background.setVisible(true);
    this.addChild(background);

    double[] bounds = background.getBounds();
    double left = bounds[0];
    double top = bounds[1];


    this.tabs = tabs(top, left);
    this.tabs.setVisible(true);
    this.addChild(tabs);

    this.measurements =  measurements(top, left, obj);
    this.basicData = basicData(top, left, obj, info);

    this.vs = parent.getVirtualSpace();
  }

  // private int getHeight(String[] strs){
  //   int length = strs.length;
  //   System.out.println(strs[length-1]);
  //   return length;
  // }
  private Composite tabs(double top, double left){
    Composite tabs = new Composite();

    basicDataTab = new VRectangle(left+w/4, top-2*Config.OFFSET, Z, w/2, Config.TEXT_SIZE, Config.SELECTED_BACKGROUND_COLOR);
    VText basicDataTabStr = new VText(left+Config.OFFSET,top-18,Z,Config.SELECTED_TEXT_COLOR,basicDataStr);
    basicDataTabStr.setScale(1.3f);
    tabSelected = basicDataStr;
    bold = basicDataTabStr.getFont().deriveFont(Font.BOLD);
    basicDataTabStr.setFont(bold);

    measurementsTab = new VRectangle(left+w/4+w/2, top-2*Config.OFFSET, Z, w/2, Config.TEXT_SIZE, Config.UNSELECTED_BACKGROUND_COLOR);
    VText measurementsTabStr = new VText(left+w/2+2*Config.OFFSET,top-18,Z,Config.TEXT_COLOR,measurementsStr);
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
    VText identifier = new VText(left+Config.OFFSET,top-Config.TEXT_SIZE*2,Z,Config.SELECTED_TEXT_COLOR,obj.getIdentifier());
    bold = identifier.getFont().deriveFont(Font.BOLD);
    identifier.setFont(bold);
    identifier.setScale(1.3f);
    basicInfo.addChild(identifier);
    for(int i = 0; i < info.length; i++){
      VText text = new VText(left+Config.OFFSET,top-Config.TEXT_SIZE*(i+3),Z,Config.SELECTED_TEXT_COLOR,info[i]);
      text.setVisible(true);
      basicInfo.addChild(text);
    }

    return basicInfo;
  }

  private Composite measurements(double top, double left, AstroObject obj){
    Composite cMeasurements= new Composite();
    Vector<Measurement> vmeasurements = obj.getMeasurements();
    MeasurementsTable table;
    if(vmeasurements.size() > 0){
      for (Measurement m : vmeasurements){
        if(m.equals(vmeasurements.firstElement()))
          table = new MeasurementsTable(m, left, top-Config.TEXT_SIZE, 25);
        else
          table = new MeasurementsTable(m, left, top, 5);
        top = table.getBackground().getBounds()[3]-Config.TEXT_SIZE;
        cMeasurements.addChild(table);
      }
    }
    return cMeasurements;
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
      basicDataTab.setColor(Config.SELECTED_BACKGROUND_COLOR);
      measurementsTab.setColor(Config.UNSELECTED_BACKGROUND_COLOR);
      vs.removeGlyph(measurements);
      vs.addGlyph(basicData);

    }
  }

  public void activateMeasurementsTab(){
    if(!tabSelected.equals(measurementsStr)){
      tabSelected = measurementsStr;
      basicDataTab.setColor(Config.UNSELECTED_BACKGROUND_COLOR);
      measurementsTab.setColor(Config.SELECTED_BACKGROUND_COLOR);
      vs.removeGlyph(basicData);
      vs.addGlyph(measurements);
    }
  }

  public VRectangle getBasicDataTab(){
    return basicDataTab;
  }
  public VRectangle getMeasurementsTab(){
    return measurementsTab;
  }
  public Composite getBasicData(){
    return basicData;
  }
  public Composite getMeasurements(){
    return measurements;
  }
  public String getTabSelected(){
    return tabSelected;
  }
  public VRectangle getBackground(){
    return background;
  }
}
