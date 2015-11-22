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
import fr.inria.zvtm.engine.VirtualSpace;

import fr.inria.ilda.fitsow.Config;

public class SimbadInfo extends SimbadQueryGlyph{
  public Composite basicData;
  private Composite measurements;
  // private Composite tabs;
  private Tabs tabs;
  // public static String basicDataStr = "Basic Data";
  // public static String measurementsStr = "Measurements";
  // private String tabSelected;
  private VRectangle background;
  // private VRectangle basicDataTab;
  // private VRectangle measurementsTab;
  // private VText basicDataTabStr, measurementsTabStr;
  private double wm, hm;

  public SimbadInfo(AstroObject obj, double x, double y, SimbadResults parent){
    super(x,y,parent.getVS());
    this.setType(Config.T_ASTRO_OBJ_BINFO);
    String[] info = obj.basicDataToString().split("\n");
    this.height = (info.length+2)*Config.TEXT_SIZE+Config.OFFSET;
    this.width = getWidth(info);
    this.x = x+width/2+parent.getWidth();
    background = new VRectangle(this.x, y, Z, width, height, Config.SELECTED_BACKGROUND_COLOR);
    background.setVisible(true);
    this.addChild(background);

    double[] bounds = background.getBounds();
    double left = bounds[0];
    double top = bounds[1];

    // this.tabs = tabs(top, left);
    this.tabs = new Tabs(top, left, this, height, width);
    // this.tabs.setVisible(true);
    this.addChild(tabs);

    this.measurements =  measurements(top, left, obj);
    this.basicData = basicData(top, left, obj, info);

    this.vs = parent.getVirtualSpace();
  }

  private Composite basicData(double top, double left, AstroObject obj, String[] info){
    Composite basicInfo = new Composite();
    VText identifier = new VText(left+Config.OFFSET,top-Config.TEXT_SIZE*2,Z,Config.SELECTED_TEXT_COLOR,obj.getIdentifier());
    notBold = identifier.getFont();
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
    double maxWidth = 0;
    double maxHeight = 0;
    int aux = 0;
    MeasurementsTable table;
    if(vmeasurements.size() > 0){
      System.out.println("this should display measrements");
      for (Measurement m : vmeasurements){
        if(m.equals(vmeasurements.firstElement()))
          table = new MeasurementsTable(m, left, top-Config.TEXT_SIZE, 25);
        else
          table = new MeasurementsTable(m, left, top, 5);
        top = table.getBackground().getBounds()[3]-Config.TEXT_SIZE;
        aux = table.getW();
        maxHeight = maxHeight + table.getH();
        if(aux > maxWidth) maxWidth = aux;
        cMeasurements.addChild(table);
      }
    maxHeight = maxHeight + (Config.OFFSET+Config.TEXT_SIZE)*vmeasurements.size();
    tabs.setWidth2(maxWidth);
    tabs.setHeight2(maxHeight);
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

  public Tabs getTabs(){
    return tabs;
  }

  public Composite getBasicData(){
    return basicData;
  }
  public Composite getMeasurements(){
    return measurements;
  }

  public VRectangle getBackground(){
    return background;
  }
}
