package fr.inria.ilda.simbad;

import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.Glyph;

import fr.inria.ilda.fitsow.Config;

import java.awt.Color;
import java.awt.Font;

public class SimbadCriteria extends Composite{
  private int w, h;
  private double x, y;
  private final static int Z = 0;
  private Composite tabs;
  private Composite objectTypeSelection;
  public static String basicDataStr = "Basic Data";
  public static String measurementsStr = "Measurements";
  private String tabSelected;
  private VRectangle background;
  private VRectangle basicDataTab;
  private VRectangle measurementsTab;
  private Font bold;
  private static String[] objectTypes = {"Star", "Galaxie", "InterStellar Matter",
  "Multiple Object", "Candidates", "Gravitation", "Inexistent", "Radio", "IR", "Red", "Blue", "UV", "X", "gamma"};


  public SimbadCriteria(double x, double y){
    this.w = 300;
    this.h = 200;
    this.x = x;
    this.y = y;
    background = new VRectangle (x, y, Z, w, h, Config.SELECTED_BACKGROUND_COLOR);
    background.setVisible(true);
    this.addChild(background);
    double[] bounds = background.getBounds();
    double left = bounds[0];
    double top = bounds[1];
    double right = bounds[2];

    VText optionalFilters = new VText(left+Config.OFFSET,top-Config.OFFSET*3,Z,Config.TEXT_COLOR,"Optional Filters");
    optionalFilters.setScale(1.3f);
    this.addChild(optionalFilters);

    this.tabs = tabs(top, left);
    this.tabs.setVisible(true);
    this.addChild(tabs);

    this.objectTypeSelection = objectTypeSelection(basicDataTab.getBounds()[3], left, right);
    this.addChild(objectTypeSelection);
  }

  private Composite tabs(double top, double left){
    Composite tabs = new Composite();
    basicDataTab = new VRectangle(left+w/4, top-6*Config.OFFSET, Z, w/2, Config.TEXT_SIZE, Config.SELECTED_BACKGROUND_COLOR);
    VText basicDataTabStr = new VText(left+Config.OFFSET,top-Config.OFFSET*7,Z,Config.SELECTED_TEXT_COLOR,basicDataStr);
    basicDataTabStr.setScale(1.3f);
    tabSelected = basicDataStr;
    bold = basicDataTabStr.getFont().deriveFont(Font.BOLD);
    basicDataTabStr.setFont(bold);

    measurementsTab = new VRectangle(left+w/4+w/2, top-6*Config.OFFSET, Z, w/2, Config.TEXT_SIZE, Config.UNSELECTED_BACKGROUND_COLOR);
    VText measurementsTabStr = new VText(left+w/2+2*Config.OFFSET,top-Config.OFFSET*7,Z,Config.TEXT_COLOR,measurementsStr);
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

  private Composite objectTypeSelection(double top, double left, double right){
    Composite objectType = new Composite();
    VText objectTypeStr = new VText(left+Config.OFFSET,top-Config.OFFSET,Z,Config.SELECTED_TEXT_COLOR,"Object type:");
    objectType.addChild(objectTypeStr);
    VSegment split = new VSegment(left, top-Config.OFFSET-Config.TEXT_SIZE, right, top-Config.OFFSET-Config.TEXT_SIZE,Z, Config.SELECTED_TEXT_COLOR);
    objectType.addChild(split);
    for(int i = 0; i < objectTypes.length; i++){
      if( i < 7){
        VText type = new VText(left+Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*(i+1), Z, Config.SELECTED_TEXT_COLOR, objectTypes[i]);
        objectType.addChild(type);
      }
    }
    return objectType;
  }



}
