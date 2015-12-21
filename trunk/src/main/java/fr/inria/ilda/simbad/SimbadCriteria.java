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

    //tab tiene h=text_size.
    //después de eso debería dejar algo así como 2 textsizes y 3 offsets
    double yOffset = 3*Config.TEXT_SIZE;
    // double yOffset = 0;
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
}
