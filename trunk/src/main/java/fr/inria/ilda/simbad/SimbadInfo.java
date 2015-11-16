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

public class SimbadInfo extends SimbadQueryGlyph{
  public Composite basicData;
  private Composite measurements;
  private Composite tabs;
  public static String basicDataStr = "Basic Data";
  public static String measurementsStr = "Measurements";
  private String tabSelected;
  private VRectangle background;
  private VRectangle basicDataTab;
  private VRectangle measurementsTab;
  private VText basicDataTabStr, measurementsTabStr;
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

    this.tabs = tabs(top, left);
    this.tabs.setVisible(true);
    this.addChild(tabs);

    this.measurements =  measurements(top, left, obj);
    this.basicData = basicData(top, left, obj, info);

    this.vs = parent.getVirtualSpace();
  }

  private Composite tabs(double top, double left){
    Composite tabs = new Composite();

    basicDataTab = new VRectangle(left+width/4, top-2*Config.OFFSET, Z, width/2, Config.TEXT_SIZE, Config.SELECTED_BACKGROUND_COLOR);
    basicDataTabStr = new VText(left+Config.OFFSET,top-18,Z,Config.SELECTED_TEXT_COLOR,basicDataStr);
    basicDataTabStr.setScale(1.3f);
    tabSelected = basicDataStr;
    bold = basicDataTabStr.getFont().deriveFont(Font.BOLD);
    basicDataTabStr.setFont(bold);

    measurementsTab = new VRectangle(left+width/4+width/2, top-2*Config.OFFSET, Z, width/2, Config.TEXT_SIZE, Config.UNSELECTED_BACKGROUND_COLOR);
    measurementsTabStr = new VText(left+width/2+2*Config.OFFSET,top-18,Z,Config.TEXT_COLOR,measurementsStr);
    measurementsTabStr.setScale(1.3f);

    tabs.addChild(basicDataTab);
    tabs.addChild(basicDataTabStr);
    tabs.addChild(measurementsTab);
    tabs.addChild(measurementsTabStr);
    return tabs;
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
    int maxWidth = 0;
    int aux = 0;
    MeasurementsTable table;
    if(vmeasurements.size() > 0){
      for (Measurement m : vmeasurements){
        if(m.equals(vmeasurements.firstElement()))
          table = new MeasurementsTable(m, left, top-Config.TEXT_SIZE, 25);
        else
          table = new MeasurementsTable(m, left, top, 5);
        top = table.getBackground().getBounds()[3]-Config.TEXT_SIZE;
        aux = table.getW();
        hm = hm + table.getH();
        if(aux > maxWidth) maxWidth = aux;
        cMeasurements.addChild(table);
      }
    hm = hm + (Config.OFFSET+Config.TEXT_SIZE)*vmeasurements.size();
    wm = maxWidth;
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
      basicDataTabStr.setFont(bold);
      measurementsTabStr.setFont(notBold);
      if(wm > width){
        background.setWidth(width);
        basicDataTab.setWidth(width/2);
        measurementsTab.setWidth(width/2);
        background.move((width-wm-20)/2,0);
        basicDataTab.move((width-wm-20)/4,0);
        double[] bounds = basicDataTab.getBounds();
        measurementsTab.moveTo(bounds[2]+width/4,measurementsTab.getLocation().getY());
        basicDataTabStr.moveTo(bounds[0]+Config.OFFSET,basicDataTabStr.getLocation().getY());
        measurementsTabStr.moveTo(bounds[2]+Config.OFFSET, measurementsTabStr.getLocation().getY());
      }
      if(hm > height){
        background.setHeight(height);
        background.move(0,(hm+basicDataTab.getHeight()+20-height)/2);
      }
      vs.removeGlyph(measurements);
      vs.addGlyph(basicData);

    }
  }

  public void activateMeasurementsTab(){
    if(!tabSelected.equals(measurementsStr)){
      tabSelected = measurementsStr;
      basicDataTab.setColor(Config.UNSELECTED_BACKGROUND_COLOR);
      measurementsTab.setColor(Config.SELECTED_BACKGROUND_COLOR);
      measurementsTabStr.setFont(bold);
      basicDataTabStr.setFont(notBold);
      if(wm > width){
        background.setWidth(wm+20);
        basicDataTab.setWidth((wm+20)/2);
        measurementsTab.setWidth((wm+20)/2);
        background.move((wm-width+20)/2,0);
        basicDataTab.move((wm+20-width)/4,0);
        double[] bounds = basicDataTab.getBounds();
        measurementsTab.moveTo(bounds[2]+(wm+20)/4,measurementsTab.getLocation().getY());
        basicDataTabStr.moveTo(bounds[0]+Config.OFFSET,basicDataTabStr.getLocation().getY());
        measurementsTabStr.moveTo(bounds[2]+Config.OFFSET, measurementsTabStr.getLocation().getY());
      }
      if(hm > height){
        background.setHeight(hm+basicDataTab.getHeight()+20);
        background.move(0,-(hm+basicDataTab.getHeight()+20-height)/2);
      }
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
