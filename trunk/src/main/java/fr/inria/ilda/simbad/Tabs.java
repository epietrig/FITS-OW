package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.Composite;

import fr.inria.ilda.fitsow.Config;
import java.awt.Font;


public class Tabs extends SimbadQueryGlyph{
  public static String basicDataStr = "Basic Data";
  public static String measurementsStr = "Measurements";
  private String tabSelected;
  private VRectangle basicDataTab, measurementsTab;
  private VText basicDataTabStr, measurementsTabStr;
  private SimbadQueryGlyph parent;//doesnt really need it
  private double width2, height2;

  public Tabs(double top, double left, double height2, double width2, SimbadQueryGlyph parent){
    super(parent.getWidth(),parent.getHeight());
    this.width2 = width2;
    this.height2 = height2;
    basicDataTab = new VRectangle(left+width/4, top-Config.TEXT_SIZE/2, Z, width/2, Config.TEXT_SIZE, Config.SELECTED_BACKGROUND_COLOR);
    basicDataTabStr = new VText(left+Config.OFFSET,top-Config.OFFSET*3,Z,Config.SELECTED_TEXT_COLOR,basicDataStr);
    basicDataTabStr.setScale(1.3f);
    tabSelected = basicDataStr;
    bold = basicDataTabStr.getFont().deriveFont(Font.BOLD);
    notBold = basicDataTabStr.getFont();
    basicDataTabStr.setFont(bold);

    measurementsTab = new VRectangle(left+width/4+width/2, top-Config.TEXT_SIZE/2, Z, width/2, Config.TEXT_SIZE, Config.UNSELECTED_BACKGROUND_COLOR);
    measurementsTabStr = new VText(left+width/2+2*Config.OFFSET,top-Config.OFFSET*3,Z,Config.TEXT_COLOR,measurementsStr);
    measurementsTabStr.setScale(1.3f);

    this.addChild(basicDataTab);
    this.addChild(basicDataTabStr);
    this.addChild(measurementsTab);
    this.addChild(measurementsTabStr);
  }

  @Override
  public double[] getBounds(){
    double[] bounds = new double[4];
    bounds[0] = basicDataTab.getBounds()[0];
    bounds[1] = basicDataTab.getBounds()[1];
    bounds[2] = measurementsTab.getBounds()[2];
    bounds[3] = basicDataTab.getBounds()[3];
    return bounds;
  }//sacar
  public void activateBasicDataTab(VRectangle background, Composite measurements, Composite basicData){
    if(!tabSelected.equals(basicDataStr)){
      tabSelected = basicDataStr;
      basicDataTab.setColor(Config.SELECTED_BACKGROUND_COLOR);
      measurementsTab.setColor(Config.UNSELECTED_BACKGROUND_COLOR);
      basicDataTabStr.setFont(bold);
      measurementsTabStr.setFont(notBold);
      if(width2 > width){
        background.setWidth(width);
        basicDataTab.setWidth(width/2);
        measurementsTab.setWidth(width/2);
        background.move((width-width2-20)/2,0);
        basicDataTab.move((width-width2-20)/4,0);
        double[] bounds = basicDataTab.getBounds();
        measurementsTab.moveTo(bounds[2]+width/4,measurementsTab.getLocation().getY());
        basicDataTabStr.moveTo(bounds[0]+Config.OFFSET,basicDataTabStr.getLocation().getY());
        measurementsTabStr.moveTo(bounds[2]+Config.OFFSET, measurementsTabStr.getLocation().getY());
      }
      if(height2 > height){
        background.setHeight(height);
        background.move(0,(height2+basicDataTab.getHeight()+20-height)/2);
      }
      SQ_VIRTUAL_SPACE.removeGlyph(measurements);
      SQ_VIRTUAL_SPACE.addGlyph(basicData);
    }
  }

  public void activateMeasurementsTab(VRectangle background, Composite measurements, Composite basicData){
    if(!tabSelected.equals(measurementsStr)){
      tabSelected = measurementsStr;
      basicDataTab.setColor(Config.UNSELECTED_BACKGROUND_COLOR);
      measurementsTab.setColor(Config.SELECTED_BACKGROUND_COLOR);
      measurementsTabStr.setFont(bold);
      basicDataTabStr.setFont(notBold);
      if(width2 > width){
        background.setWidth(width2+20);
        basicDataTab.setWidth((width2+20)/2);
        measurementsTab.setWidth((width2+20)/2);
        background.move((width2-width+20)/2,0);
        basicDataTab.move((width2+20-width)/4,0);
        double[] bounds = basicDataTab.getBounds();
        measurementsTab.moveTo(bounds[2]+(width2+20)/4,measurementsTab.getLocation().getY());
        basicDataTabStr.moveTo(bounds[0]+Config.OFFSET,basicDataTabStr.getLocation().getY());
        measurementsTabStr.moveTo(bounds[2]+Config.OFFSET, measurementsTabStr.getLocation().getY());
      }
      if(height2 > height){
        background.setHeight(height2+basicDataTab.getHeight()+20);
        background.move(0,-(height2+basicDataTab.getHeight()+20-height)/2);
      }
      SQ_VIRTUAL_SPACE.removeGlyph(basicData);
      SQ_VIRTUAL_SPACE.addGlyph(measurements);
    }
  }

  public VRectangle getBasicDataTab(){
    return basicDataTab;
  }
  public VRectangle getMeasurementsTab(){
    return measurementsTab;
  }
  public String getTabSelected(){
    return tabSelected;
  }
  public void setWidth2(double width2){
    this.width2 = width2;
  }
  public void setHeight2(double height2){
    this.height2 = height2;
  }
  public String getMeasurementsStr(){
    return measurementsStr;
  }
  public String getBasicDataStr(){
    return basicDataStr;
  }
}
