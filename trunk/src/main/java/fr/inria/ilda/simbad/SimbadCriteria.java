package fr.inria.ilda.simbad;

import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.Glyph;

import fr.inria.zvtm.engine.VirtualSpace;

import fr.inria.ilda.fitsow.Config;

import javax.swing.JOptionPane;

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

  private Vector<VSegment> bsplits;

  private SimbadQueryTypeSelector parent;

  VText coordinates = null;
  VText frame = null;
  VText id = null;
  String coordinatesStr = null;
  String frameStr = null;
  String idStr = null;
  VRectangle execute;

  protected static double W = 900;
  protected static double H = 600;

  public static SimbadCriteria lastSimbadCriteria;
  public SimbadCriteria(double x, double y, SimbadQueryTypeSelector parent){
    super(W, H);
    this.parent = parent;
    this.setType(Config.T_ASTRO_OBJ_SC);
    this.background = new VRectangle (x, y, Z, width, height, BACKGROUND_COLOR);
    this.addChild(background);
    double[] bounds = background.getBounds();
    double left = bounds[0];
    double top = bounds[1];
    double right = bounds[2];

    this.basicData = new Composite();
    this.queryType = queryType(top, left, right);
    basicData.addChild(queryType);

    double yOffset = 5*TEXT_SIZE;
    this.objectTypeFilter = new SimbadOTypeFilter(top-yOffset, left, left+W/3);
    basicData.addChild(objectTypeFilter);

    this.fluxesFilter = new SimbadFluxFilter(top-yOffset, left+W/3, left+2*W/3);
    basicData.addChild(fluxesFilter);

    this.spectralTypeFilter = new SimbadSTFilter(top-yOffset, left+2*W/3, left+W);
    basicData.addChild(spectralTypeFilter);

    this.radialVelocityFilter = new SimbadRVFilter(top-yOffset-H/2, left, left+W/3);
    basicData.addChild(radialVelocityFilter);

    this.properMotionFilter = new SimbadPMFilter(top-yOffset-H/2, left+W/3, left+2*W/3);
    basicData.addChild(properMotionFilter);

    this.parallaxesFilter = new SimbadParallaxFilter(top-yOffset-H/2, left+2*W/3, left+W);
    basicData.addChild(parallaxesFilter);

    this.measurements = new SimbadMFilter(top-TEXT_SIZE, left, right, this);

    this.tabs = new Tabs(top, left, H, W, this);
    this.addChild(tabs);
  }
  public Composite queryType(double top, double left, double right){
    Composite c = new Composite();
    int type = parent.getSelected();
    if(type == SimbadQueryTypeSelector.BY_ID){
      id = new VText(left + 2*OFFSET, top-2*TEXT_SIZE-OFFSET, Z, TEXT_COLOR, "Enter Identifier:");
      id.setScale(1.3f);
      c.addChild(id);
    }
    else if(type == SimbadQueryTypeSelector.BY_COORDINATES){
      coordinates = new VText(left + 2*OFFSET, top-2*TEXT_SIZE-OFFSET, Z, TEXT_COLOR, "Coordinates:");
      coordinates.setScale(1.3f);
      c.addChild(coordinates);
      frame = new VText(left + W/3, top-2*TEXT_SIZE-OFFSET, Z, TEXT_COLOR, "Frame:");
      frame.setScale(1.3f);
      c.addChild(frame);
      VText explanation = new VText(left + 2*OFFSET, top-3*TEXT_SIZE-OFFSET,Z, TEXT_COLOR, "(click in text to enter coordinates numerical value or select region in image)");
      c.addChild(explanation);
    }
    execute = new VRectangle(right - 120, top-2*TEXT_SIZE, Z, 110, TEXT_SIZE, SELECTED_COLOR);
    VText executeQuery = new VText(right - 120 - 45, top-2*TEXT_SIZE-OFFSET, Z, TEXT_COLOR, "Execute Query");
    executeQuery.setScale(1.3f);
    c.addChild(execute);
    c.addChild(executeQuery);
    VText optionalFilters = new VText(left + 2*OFFSET, top-4*TEXT_SIZE-OFFSET, Z, TEXT_COLOR, "Select optional filters:");
    c.addChild(optionalFilters);
    return c;
  }

  public void updateQueryParameters(double x, double y){
    JOptionPane optionPane;
    if(coordinates != null && coordinates.coordInsideV(x, y, SQ_CAMERA)){
      optionPane = new JOptionPane("Query coordinates", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
      coordinatesStr = JOptionPane.showInputDialog("Enter query coordinates in the format: ra, dec, radius(m)");
      coordinates.setText("Coordinates: "+coordinatesStr);
    }
    if(frame != null && frame.coordInsideV(x, y, SQ_CAMERA)){
      optionPane = new JOptionPane("Query frame for coordinates", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
      frameStr = JOptionPane.showInputDialog("Enter query frame. If none is entered, J200 is used by default:");
      frame.setText("Frame: "+frameStr);
    }
    if(id != null && id.coordInsideV(x, y, SQ_CAMERA)){
      optionPane = new JOptionPane("Query identifier", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
      idStr = JOptionPane.showInputDialog("Enter object identifier:");
      id.setText("Identifier: "+idStr);
    }
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
  public SimbadParallaxFilter getParallaxFilter(){
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
  public String getIdStr(){
    return idStr;
  }
}
