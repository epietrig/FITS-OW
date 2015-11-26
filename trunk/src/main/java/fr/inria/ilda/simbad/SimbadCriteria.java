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
  private Composite basicData, properMotionFilter,
  parallaxesFilter, radialVelocityFilter, spectralTypeFilter, fluxesFilter;
  private SimbadMFilter measurements;
  private SimbadOTypeFilter objectTypeFilter;
  private Tabs tabs;
  private VRectangle background, container;
  private Font bold;
  private static String[] objectTypes = {"Star", "Galaxie", "InterStellar Matter",
  "Multiple Object", "Candidates", "Gravitation", "Inexistent", "Radio", "IR", "Red", "Blue", "UV", "X", "gamma"};
  private static String[] fluxTypes = {"U", "V", "B", "R","I","J","K","H","u","g","r","i","z"};
  private Vector<VSegment> bsplits;
  private double width2, height2;

  public SimbadCriteria(double x, double y, VirtualSpace vs){
    super(x,y,300,920,vs);
    this.setType(Config.T_ASTRO_OBJ_SC);
    this.height2 = 500;
    this.width2 = width;

    bsplits = new Vector();

    container = new VRectangle (x, y+25, Z, width+50, height+100, Config.UNSELECTED_BACKGROUND_COLOR);
    background = new VRectangle (x, y, Z, width, height, Config.SELECTED_BACKGROUND_COLOR);
    this.addChild(container);
    this.addChild(background);

    double[] bounds = background.getBounds();
    double left = bounds[0];
    double top = bounds[1];
    double right = bounds[2];

    VText optionalFilters = new VText(left,container.getBounds()[1]-Config.TEXT_SIZE,Z,Config.TEXT_COLOR,"Optional Filters");
    optionalFilters.setScale(1.3f);
    this.addChild(optionalFilters);

    this.tabs = new Tabs(top, left, this, height2, width2);
    this.addChild(tabs);

    this.basicData = new Composite();
    // this.objectTypeFilter = objectTypeFilter(tabs.getBounds()[3], left, right);
    this.objectTypeFilter = new SimbadOTypeFilter(this, tabs.getBounds()[3], left, right);
    basicData.addChild(objectTypeFilter);

    this.properMotionFilter = properMotionFilter(bsplits.lastElement().getLocation().getY(),left, right);
    basicData.addChild(properMotionFilter);

    this.parallaxesFilter = parallaxesFilter(bsplits.lastElement().getLocation().getY(),left, right);
    basicData.addChild(parallaxesFilter);

    this.radialVelocityFilter = radialVelocityFilter(bsplits.lastElement().getLocation().getY(),left, right);
    basicData.addChild(radialVelocityFilter);

    this.spectralTypeFilter = spectralTypeFilter(bsplits.lastElement().getLocation().getY(),left, right);
    basicData.addChild(spectralTypeFilter);

    this.fluxesFilter = fluxesFilter(bsplits.lastElement().getLocation().getY(),left, right);
    basicData.addChild(fluxesFilter);

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
  private void qualitySelector(Composite c, double x, double y){
    VText quality = new VText(x,y,Z,Config.SELECTED_TEXT_COLOR,"Quality:");
    c.addChild(quality);
    for(char alphabet = 'A'; alphabet <= 'E';alphabet++) {
      VRectangle square =  new VRectangle (x+((int)alphabet-65)*2*Config.TEXT_SIZE+Config.OFFSET, y-Config.TEXT_SIZE+Config.OFFSET, Z, 10, 10, Color.white);
      VText qualityStr = new VText(x+((int)alphabet-65)*2*Config.TEXT_SIZE+3*Config.OFFSET, y-Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR, Character.toString(alphabet));
      c.addChild(square);
      c.addChild(qualityStr);
    }
  }

  private Composite objectTypeFilter(double top, double left, double right){
    Composite objectType = new Composite();
    setFilterLayout("Object Type:", Config.OFFSET+Config.TEXT_SIZE*9, objectType, top, left, right);
    VRectangle square;
    VText type;
    for(int i = 0; i < objectTypes.length; i++){
      if( i < 7){
        square =  new VRectangle (left+2*Config.OFFSET, top-Config.TEXT_SIZE*(i+2), Z, 10, 10, Color.white);
        type = new VText(left+5*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*(i+2), Z, Config.SELECTED_TEXT_COLOR, objectTypes[i]);
      }
      else{
        square =  new VRectangle (left+2*Config.OFFSET+width/2, top-Config.TEXT_SIZE*(i+2-7), Z, 10, 10, Color.white);
        type = new VText(left+5*Config.OFFSET+width/2, top-Config.OFFSET-Config.TEXT_SIZE*(i+2-7), Z, Config.SELECTED_TEXT_COLOR, objectTypes[i]);
      }
      objectType.addChild(square);
      objectType.addChild(type);
    }
    return objectType;
  }

  private Composite properMotionFilter(double top, double left, double right){
    Composite properMotion = new Composite();
    setFilterLayout("Proper Motion:", Config.OFFSET+Config.TEXT_SIZE*5, properMotion, top, left, right);
    VText ra = new VText(left+Config.OFFSET,top-2*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Right ascension angle:");
    VText dec = new VText(left+Config.OFFSET,top-3*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Declination angle:");
    properMotion.addChild(ra);
    properMotion.addChild(dec);
    qualitySelector(properMotion, left+Config.OFFSET, top-4*Config.TEXT_SIZE);
    return properMotion;
  }

  private Composite radialVelocityFilter(double top, double left, double right){
    Composite radialVelocity = new Composite();
    setFilterLayout("Radial velocity:", Config.OFFSET+Config.TEXT_SIZE*5, radialVelocity, top, left, right);
    VText type = new VText(left+Config.OFFSET,top-2*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Type:");
    VText rv = new VText(left+Config.OFFSET,top-3*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Range:");
    radialVelocity.addChild(type);
    radialVelocity.addChild(rv);
    qualitySelector(radialVelocity, left+Config.OFFSET, top-4*Config.TEXT_SIZE);
    return radialVelocity;
  }

  private Composite parallaxesFilter(double top, double left, double right){
    Composite parallaxes = new Composite();
    setFilterLayout("Parallax:", Config.OFFSET*2+Config.TEXT_SIZE*3, parallaxes, top, left, right);
    qualitySelector(parallaxes, left+Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*2);
    return parallaxes;
  }

  private Composite spectralTypeFilter(double top, double left, double right){
    Composite spectralType = new Composite();
    setFilterLayout("Spectral Type:", 2*Config.OFFSET+Config.TEXT_SIZE*8, spectralType, top, left, right);
    VText tc = new VText(left+Config.OFFSET,top-2*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Temperature class:");
    VText first = new VText(left+2*Config.OFFSET,top-3*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"1st digit:");
    VText second = new VText(left+2*Config.OFFSET,top-4*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"2nd digit:");
    VText third = new VText(left+2*Config.OFFSET,top-5*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"3rd digit:");
    spectralType.addChild(tc);
    spectralType.addChild(first);
    spectralType.addChild(second);
    spectralType.addChild(third);

    VText lc = new VText(left+Config.OFFSET,top-6*Config.TEXT_SIZE,Z,Config.SELECTED_TEXT_COLOR,"Luminosity class:");
    spectralType.addChild(lc);
    qualitySelector(spectralType, left+Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*7);
    return spectralType;
  }

  private Composite fluxesFilter(double top, double left, double right){
    Composite fluxes = new Composite();
    setFilterLayout("Flux:", 2*Config.OFFSET+Config.TEXT_SIZE*13, fluxes, top, left, right);

    VRectangle square;
    VText type, range;
    for(int i = 0; i < fluxTypes.length*2; i=i+2){
      if( i < 10){
        square =  new VRectangle (left+2*Config.OFFSET, top-Config.TEXT_SIZE*(i+2), Z, 10, 10, Color.white);
        type = new VText(left+5*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*(i+2), Z, Config.SELECTED_TEXT_COLOR, fluxTypes[i/2]);
        range = new VText(left+5*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*(i+3), Z, Config.SELECTED_TEXT_COLOR, "Range:");
      }
      else if(i<20){
        square =  new VRectangle (left+2*Config.OFFSET+width/3, top-Config.TEXT_SIZE*(i+2-10), Z, 10, 10, Color.white);
        type = new VText(left+5*Config.OFFSET+width/3, top-Config.OFFSET-Config.TEXT_SIZE*(i+2-10), Z, Config.SELECTED_TEXT_COLOR, fluxTypes[i/2]);
        range = new VText(left+5*Config.OFFSET+width/3, top-Config.OFFSET-Config.TEXT_SIZE*(i+3-10), Z, Config.SELECTED_TEXT_COLOR, "Range:");
      }
      else{
        square =  new VRectangle (left+2*Config.OFFSET+2*width/3, top-Config.TEXT_SIZE*(i+2-20), Z, 10, 10, Color.white);
        type = new VText(left+5*Config.OFFSET+2*width/3, top-Config.OFFSET-Config.TEXT_SIZE*(i+2-20), Z, Config.SELECTED_TEXT_COLOR, fluxTypes[i/2]);
        range = new VText(left+5*Config.OFFSET+2*width/3, top-Config.OFFSET-Config.TEXT_SIZE*(i+3-20), Z, Config.SELECTED_TEXT_COLOR, "Range:");
      }
      fluxes.addChild(square);
      fluxes.addChild(type);
      fluxes.addChild(range);
    }
    qualitySelector(fluxes, left+Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE*12);
    return fluxes;
  }

  public Tabs getTabs(){
    return tabs;
  }
  public VRectangle getBackground(){
    return background;
  }
  public VRectangle getContainer(){
    return container;
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


}
