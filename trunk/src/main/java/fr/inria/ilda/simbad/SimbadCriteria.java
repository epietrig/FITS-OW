/*  (c) COPYRIGHT INRIA (Institut National de Recherche en Informatique et en Automatique) and INRIA Chile, 2015-2016.
 *  Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id:  $
 */

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
  VRectangle cancel;

  protected static double W = 900;
  protected static double H = 600;

  public static SimbadCriteria lastSimbadCriteria;

  private static String queryByIdLabel ="Identifier: ";
  private static String queryByCoordLabel="Coordinates: ";
  private static String executeLabel="Execute";
  private static String cancelLabel="Cancel";
  private static String explanationLabel = "(click in text to enter coordinates numerical value or select region in image)";
  private static String optionalFiltersLabel = "Select optional filters:";
  private static String frameLabel = "Frame: ";

  public SimbadCriteria(double x, double y, SimbadQueryTypeSelector parent){
    super(W, H);
    this.parent = parent;
    this.setType(Config.T_ASTRO_OBJ_SC);
    this.background = new VRectangle (x, y, Z, width, height, CONTAINER_COLOR, CONTAINER_BORDER_COLOR);
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
      id = new VText(left + 2*OFFSET, top-2*TEXT_SIZE-OFFSET, Z, TEXT_COLOR, queryByIdLabel);
      id.setFont(BOLD);
      c.addChild(id);
    }
    else if(type == SimbadQueryTypeSelector.BY_COORDINATES){
      coordinates = new VText(left + 2*OFFSET, top-2*TEXT_SIZE-OFFSET, Z, TEXT_COLOR, queryByCoordLabel);
      coordinates.setFont(BOLD);
      c.addChild(coordinates);
      frame = new VText(left + W/3, top-2*TEXT_SIZE-OFFSET, Z, TEXT_COLOR, frameLabel);
      frame.setFont(BOLD);
      c.addChild(frame);
      VText explanation = new VText(left + 2*OFFSET, top-3*TEXT_SIZE-OFFSET,Z, TEXT_COLOR, explanationLabel);
      explanation.setScale(1.3f);
      c.addChild(explanation);
    }
    execute = new VRectangle(right - 120, top-2*TEXT_SIZE, Z, 110, TEXT_SIZE, EXECUTE_COLOR,EXECUTE_BORDER_COLOR);
    VText executeQuery = new VText(right - 120 - 45, top-2*TEXT_SIZE-OFFSET, Z, TEXT_COLOR_2, executeLabel);
    executeQuery.setScale(1.3f);
    c.addChild(execute);
    c.addChild(executeQuery);

    cancel = new VRectangle(right - 120, top-3*TEXT_SIZE-OFFSET, Z, 110, TEXT_SIZE, CANCEL_COLOR, CANCEL_BORDER_COLOR);
    VText cancelQuery = new VText(right - 120 - 45, top-3*TEXT_SIZE-2*OFFSET, Z, TEXT_COLOR_2, cancelLabel);
    cancelQuery.setScale(1.3f);
    c.addChild(cancel);
    c.addChild(cancelQuery);

    VText optionalFilters = new VText(left + 2*OFFSET, top-4*TEXT_SIZE-OFFSET, Z, TEXT_COLOR, optionalFiltersLabel);
    c.addChild(optionalFilters);
    optionalFilters.setScale(1.3f);

    return c;
  }

  public void updateQueryParameters(double x, double y){
    JOptionPane optionPane;
    if(coordinates != null && coordinates.coordInsideV(x, y, SQ_CAMERA)){
      optionPane = new JOptionPane("Query coordinates", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
      coordinatesStr = JOptionPane.showInputDialog("Enter query coordinates in the format: ra, dec, radius(m)");
      coordinates.setText(queryByCoordLabel+coordinatesStr);
      //do some checking here, maybe use paser?
    }
    if(frame != null && frame.coordInsideV(x, y, SQ_CAMERA)){
      optionPane = new JOptionPane("Query frame for coordinates", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
      frameStr = JOptionPane.showInputDialog("Enter query frame. If none is entered, J200 is used by default:");
      frame.setText(frameLabel+frameStr);
    }
    if(id != null && id.coordInsideV(x, y, SQ_CAMERA)){
      optionPane = new JOptionPane("Query identifier", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
      idStr = JOptionPane.showInputDialog("Enter object identifier:");
      id.setText(queryByIdLabel+idStr);
    }
  }

  public void cleanParameters(){
    coordinatesStr = null;
    coordinates.setText(queryByCoordLabel);
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

  public VRectangle getCancelButton(){
    return cancel;
  }

  public String getIdStr(){
    return idStr;
  }
  public String getCoordinatesStr(){
    return coordinatesStr;
  }
}
