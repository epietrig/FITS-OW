package fr.inria.ilda.simbad;

import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.Glyph;

import fr.inria.zvtm.engine.VirtualSpace;

import fr.inria.ilda.fitsow.Config;

import java.awt.Color;
import java.awt.Font;

import java.util.Vector;

public class SimbadCriteria extends SimbadQueryGlyph{
  private Composite basicData = null;
  private Composite queryType;
  private SimbadMFilter measurements = null;
  private SimbadOTypeFilter objectTypeFilter;
  private SimbadPMFilter properMotionFilter;
  private SimbadParallaxFilter parallaxesFilter;
  private SimbadRVFilter radialVelocityFilter;
  private SimbadSTFilter spectralTypeFilter;
  private SimbadFluxFilter fluxesFilter;
  private Tabs tabs;
  private Font bold;//idem
  private Vector<VSegment> bsplits;
  private SimbadQueryTypeSelector parent;//shouldnt exist, going to change
  VText coordinates, frame;
  VText id;
  String coordinatesStr = null;
  String frameStr = null;
  String idStr = null;
  VRectangle execute;
  // private double width2, height2;//should be on tabs

  public static SimbadCriteria lastSimbadCriteria;
  public SimbadCriteria(double x, double y, SimbadQueryTypeSelector parent){
    super(900, 600);
    this.parent = parent;
    this.setType(Config.T_ASTRO_OBJ_SC);
    this.background = new VRectangle (x, y, Z, width, height, Config.SELECTED_BACKGROUND_COLOR);
    this.addChild(background);
    double[] bounds = background.getBounds();
    double left = bounds[0];
    double top = bounds[1];
    double right = bounds[2];

    this.basicData = new Composite();
    this.queryType = queryType(top, left, right);
    basicData.addChild(queryType);

    double yOffset = 5*Config.TEXT_SIZE;
    this.objectTypeFilter = new SimbadOTypeFilter(top-yOffset, left, left+300);
    basicData.addChild(objectTypeFilter);

    this.fluxesFilter = new SimbadFluxFilter(top-yOffset, left+300, left+600);
    basicData.addChild(fluxesFilter);

    this.spectralTypeFilter = new SimbadSTFilter(top-yOffset, left+600, left+900);
    basicData.addChild(spectralTypeFilter);

    this.radialVelocityFilter = new SimbadRVFilter(top-yOffset-300, left, left+300);
    basicData.addChild(radialVelocityFilter);

    this.properMotionFilter = new SimbadPMFilter(top-yOffset-300, left+300, left+600);
    basicData.addChild(properMotionFilter);

    this.parallaxesFilter = new SimbadParallaxFilter(top-yOffset-300, left+600, left+900);
    basicData.addChild(parallaxesFilter);

    this.measurements = new SimbadMFilter(top-Config.TEXT_SIZE, left, right, this);

    this.tabs = new Tabs(top, left, 600, 900, this);
    this.addChild(tabs);
  }
  public Composite queryType(double top, double left, double right){
    Composite c = new Composite();
    int type = parent.getSelected();
    if(type == SimbadQueryTypeSelector.BY_ID){
      id = new VText(left + 2*Config.OFFSET, top-2*Config.TEXT_SIZE-Config.OFFSET, Z, Config.SELECTED_TEXT_COLOR, "Enter Identifier:");
      id.setScale(1.3f);
      c.addChild(id);
    }
    else if(type == SimbadQueryTypeSelector.BY_COORDINATES){
      coordinates = new VText(left + 2*Config.OFFSET, top-2*Config.TEXT_SIZE-Config.OFFSET, Z, Config.SELECTED_TEXT_COLOR, "Coordinates:");
      coordinates.setScale(1.3f);
      c.addChild(coordinates);
      frame = new VText(left + 300, top-2*Config.TEXT_SIZE-Config.OFFSET, Z, Config.SELECTED_TEXT_COLOR, "Frame:");
      frame.setScale(1.3f);
      c.addChild(frame);
      VText explanation = new VText(left + 2*Config.OFFSET, top-3*Config.TEXT_SIZE-Config.OFFSET, Z, Config.SELECTED_TEXT_COLOR, "(click in text to enter coordinates numerical value or select region in image)");
      c.addChild(explanation);
    }
    execute = new VRectangle(right - 120, top-2*Config.TEXT_SIZE, Z, 110, Config.TEXT_SIZE, Color.red);
    VText executeQuery = new VText(right - 120 - 45, top-2*Config.TEXT_SIZE-Config.OFFSET, Z, Config.SELECTED_TEXT_COLOR, "Execute Query");
    executeQuery.setScale(1.3f);
    c.addChild(execute);
    c.addChild(executeQuery);
    VText optionalFilters = new VText(left + 2*Config.OFFSET, top-4*Config.TEXT_SIZE-Config.OFFSET, Z, Config.SELECTED_TEXT_COLOR, "Select optional filters:");
    c.addChild(optionalFilters);
    return c;
  }
  public void setQueryParameters(String parameters){

  }
  public String fromRegionToString(){
    //acá se supone que tome el circulo y lo convirta en string que desplegar
    return "";
  }

  public Tabs getTabs(){
    return tabs;
  }
  public VRectangle getBackground(){
    return background;
  }

  public Composite getBasicData(){
    return basicData;
  }
  public SimbadMFilter getMeasurements(){
    return measurements;
  }
  public SimbadOTypeFilter getObjectTypeFilter(){
    return objectTypeFilter;
  }
  public SimbadPMFilter getPMFilter(){
    return properMotionFilter;
  }
  public SimbadParallaxFilter getParallaxFiler(){
    return parallaxesFilter;
  }
  public SimbadRVFilter getRVFilter(){
    return radialVelocityFilter;
  }
  public SimbadSTFilter getSTFilter(){
    return spectralTypeFilter;
  }
  public SimbadFluxFilter getFluxFilter(){
    return fluxesFilter;
  }
  public static SimbadCriteria getLastSimbadCriteria(){
    return lastSimbadCriteria;
  }
  public VRectangle getExecuteButton(){
    return execute;
  }
}
