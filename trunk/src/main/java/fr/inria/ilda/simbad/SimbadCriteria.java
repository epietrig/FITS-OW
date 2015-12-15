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
  private Composite basicData, queryType;
  private SimbadMFilter measurements;
  private SimbadOTypeFilter objectTypeFilter;
  private SimbadPMFilter properMotionFilter;
  private SimbadParallaxFilter parallaxesFilter;
  private SimbadRVFilter radialVelocityFilter;
  private SimbadSTFilter spectralTypeFilter;
  private SimbadFluxFilter fluxesFilter;
  private Tabs tabs;
  private VRectangle background;
  private Font bold;
  private Vector<VSegment> bsplits;
  private double width2, height2;

  public SimbadCriteria(double x, double y, VirtualSpace vs){
    super(300,960,vs);
    this.setType(Config.T_ASTRO_OBJ_SC);
    this.height2 = 500;
    this.width2 = width;

    bsplits = new Vector();

    background = new VRectangle (x, y, Z, width, height, Config.SELECTED_BACKGROUND_COLOR);

    this.addChild(background);

    double[] bounds = background.getBounds();
    double left = bounds[0];
    double top = bounds[1];
    double right = bounds[2];


    this.tabs = new Tabs(top, left, this, height2, width2);
    this.addChild(tabs);

    this.basicData = new Composite();
    this.objectTypeFilter = new SimbadOTypeFilter(this, tabs.getBounds()[3], left, right);
    basicData.addChild(objectTypeFilter);
    objectTypeFilter.setl1(bsplits.get(bsplits.size()-2));
    objectTypeFilter.setl2(bsplits.lastElement());

    this.properMotionFilter = new SimbadPMFilter(this, bsplits.lastElement().getLocation().getY(),left, right);
    basicData.addChild(properMotionFilter);
    properMotionFilter.setl1(bsplits.get(bsplits.size()-2));
    properMotionFilter.setl2(bsplits.lastElement());

    this.parallaxesFilter = new SimbadParallaxFilter(this, bsplits.lastElement().getLocation().getY(),left, right);
    basicData.addChild(parallaxesFilter);
    parallaxesFilter.setl1(bsplits.get(bsplits.size()-2));
    parallaxesFilter.setl2(bsplits.lastElement());

    this.radialVelocityFilter = new SimbadRVFilter(this,bsplits.lastElement().getLocation().getY(),left, right);
    basicData.addChild(radialVelocityFilter);
    radialVelocityFilter.setl1(bsplits.get(bsplits.size()-2));
    radialVelocityFilter.setl2(bsplits.lastElement());

    this.spectralTypeFilter = new SimbadSTFilter(this,bsplits.lastElement().getLocation().getY(),left, right);
    basicData.addChild(spectralTypeFilter);
    spectralTypeFilter.setl1(bsplits.get(bsplits.size()-2));
    spectralTypeFilter.setl2(bsplits.lastElement());

    this.fluxesFilter = new SimbadFluxFilter(this,bsplits.lastElement().getLocation().getY(),left, right);
    basicData.addChild(fluxesFilter);
    fluxesFilter.setl1(bsplits.get(bsplits.size()-2));
    fluxesFilter.setl2(bsplits.lastElement());

    this.measurements = new SimbadMFilter(tabs.getBounds()[3],left,right, vs, this);

  }

  public void setFilterLayout(String titleStr, double size, Composite c, double top, double left, double right){
    VText title = new VText(left+Config.OFFSET,top-Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR, titleStr);
    title.setScale(1.1f);
    c.addChild(title);
    VSegment split1 = new VSegment(left, top-Config.OFFSET-Config.TEXT_SIZE, right, top-Config.OFFSET-Config.TEXT_SIZE,Z, Config.SELECTED_TEXT_COLOR);
    bsplits.add(split1);
    c.addChild(split1);
    VSegment split2 = new VSegment(left, top-size, right, top-size ,Z, Config.SELECTED_TEXT_COLOR);
    bsplits.add(split2);
    c.addChild(split2);
  }
  public VRectangle[] qualitySelector(Composite c, double x, double y){
    VText quality = new VText(x,y,Z,Config.SELECTED_TEXT_COLOR,"Quality:");
    c.addChild(quality);
    VRectangle[] qualities = new VRectangle[5];
    for(char alphabet = 'A'; alphabet <= 'E';alphabet++) {
      VRectangle square =  new VRectangle (x+((int)alphabet-65)*2*Config.TEXT_SIZE+Config.OFFSET, y-Config.TEXT_SIZE+Config.OFFSET, Z, 10, 10, Color.white);
      VText qualityStr = new VText(x+((int)alphabet-65)*2*Config.TEXT_SIZE+3*Config.OFFSET, y-Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR, Character.toString(alphabet));
      qualities[(int)alphabet-65] = square;
      c.addChild(square);
      c.addChild(qualityStr);
    }
    return qualities;
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
}
