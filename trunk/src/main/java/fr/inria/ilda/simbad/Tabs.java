/*  (c) COPYRIGHT INRIA (Institut National de Recherche en Informatique et en Automatique) and INRIA Chile, 2015-2016.
 *  Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id:  $
 */

package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.Glyph;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.ilda.simbad.SimbadInfo;
import fr.inria.ilda.fitsow.Config;
import java.awt.Font;
import java.util.Vector;

public class Tabs extends SimbadQueryGlyph{
  public static String basicDataStr = "Basic Data";
  public static String measurementsStr = "Measurements";
  private String tabSelected;
  private VRectangle basicDataTab, measurementsTab;
  private VText basicDataTabStr, measurementsTabStr;
  private SimbadQueryGlyph parent;
  private double width2, height2;

  // public Tabs(double top, double left, double height2, double width2, SimbadQueryGlyph parent){
  //   super(parent.getWidth(),parent.getHeight());
  //   this.width2 = width2;
  //   this.height2 = height2;
  //   this.parent = parent;
  //   basicDataTab = new VRectangle(left+width/4, top-TEXT_SIZE/2, Z, width/2, TEXT_SIZE, BACKGROUND_COLOR, BACKGROUND_COLOR);
  //   basicDataTabStr = new VText(left+OFFSET,top-OFFSET*3,Z,TEXT_COLOR,basicDataStr);
  //   basicDataTabStr.setScale(1.3f);
  //   tabSelected = basicDataStr;
  //   basicDataTabStr.setFont(BOLD);
  //
  //   measurementsTab = new VRectangle(left+width/4+width/2, top-TEXT_SIZE/2, Z, width/2, TEXT_SIZE, BACKGROUND_COLOR, BACKGROUND_COLOR);
  //   measurementsTabStr = new VText(left+width/2+2*OFFSET,top-OFFSET*3,Z,TEXT_COLOR,measurementsStr);
  //   measurementsTabStr.setScale(1.3f);
  //
  //   this.addChild(basicDataTab);
  //   this.addChild(basicDataTabStr);
  //   this.addChild(measurementsTab);
  //   this.addChild(measurementsTabStr);
  // }

  public Tabs(double top, double left, double height2, double width2, SimbadQueryGlyph parent, String selected){
    super(parent.getWidth(),parent.getHeight());
    this.width2 = width2;
    this.height2 = height2;
    this.parent = parent;
    basicDataTab = new VRectangle(left+width/4, top-TEXT_SIZE/2, Z, width/2, TEXT_SIZE, BACKGROUND_COLOR, BORDER_COLOR);
    basicDataTabStr = new VText(left+OFFSET,top-OFFSET*3,Z,TEXT_COLOR,basicDataStr);
    basicDataTabStr.setScale(1.3f);
    measurementsTab = new VRectangle(left+width/4+width/2, top-TEXT_SIZE/2, Z, width/2, TEXT_SIZE, BACKGROUND_COLOR, BORDER_COLOR);
    measurementsTabStr = new VText(left+width/2+2*OFFSET,top-OFFSET*3,Z,TEXT_COLOR,measurementsStr);
    measurementsTabStr.setScale(1.3f);

    tabSelected = selected;
    if(tabSelected.equals(basicDataStr)) basicDataTabStr.setFont(BOLD);
    else measurementsTabStr.setFont(BOLD);

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
      if(this.parent.getType().equals(Config.T_ASTRO_OBJ_BINFO)){
        SimbadInfo former = (SimbadInfo) parent;
        VRectangle formerBackground = former.getBackground();
        SimbadInfo newSimbadInfo;
        if(width2>width && height2>height){
          newSimbadInfo = new SimbadInfo(former.getObj(), former.getInfo() , formerBackground.getLocation().getX()-(width-width2-20)/2,
          formerBackground.getLocation().getY()+(height2+basicDataTab.getHeight()+20-height)/2,width,height,this);
        }
        else if(width2>width){
          newSimbadInfo = new SimbadInfo(former.getObj(), former.getInfo() , formerBackground.getLocation().getX()-(width-width2-20)/2,
          formerBackground.getLocation().getY(),width,height,this);
        }
        else if(height2>height){
          newSimbadInfo = new SimbadInfo(former.getObj(), former.getInfo() , formerBackground.getLocation().getX(),
          formerBackground.getLocation().getY()+(height2+basicDataTab.getHeight()+20-height)/2,width,height,this);
        }
        else{
          newSimbadInfo = new SimbadInfo(former.getObj(), former.getInfo() , formerBackground.getLocation().getX(),
          formerBackground.getLocation().getY(),width,height,this);
        }
          SQ_VIRTUAL_SPACE.removeGlyph(parent);
          SQ_VIRTUAL_SPACE.addGlyph(newSimbadInfo);
      }
      if(this.parent.getType().equals(Config.T_ASTRO_OBJ_SC)){
        SimbadCriteria former = (SimbadCriteria) parent;
        VRectangle formerBackground = former.getBackground();
        SimbadCriteria newSimbadCriteria = new SimbadCriteria(formerBackground.getLocation().getX(),
        formerBackground.getLocation().getY(), former.getParent(), this);
        SQ_VIRTUAL_SPACE.removeGlyph(parent);
        SQ_VIRTUAL_SPACE.addGlyph(newSimbadCriteria);
      }
    }
  }

  public void activateMeasurementsTab(VRectangle background, Composite measurements, Composite basicData){
    if(!tabSelected.equals(measurementsStr)){
      tabSelected = measurementsStr;
      if(this.parent.getType().equals(Config.T_ASTRO_OBJ_BINFO)){
        SimbadInfo former = (SimbadInfo) parent;
        VRectangle formerBackground = former.getBackground();
        SimbadInfo newSimbadInfo;
        if(width2>width && height2>height){
          newSimbadInfo = new SimbadInfo(former.getObj(), former.getInfo() , formerBackground.getLocation().getX()+(width-width2-20)/2,
          formerBackground.getLocation().getY()-(height2+basicDataTab.getHeight()+20-height)/2,width2+20,height2+20,this);
        }
        else if(width2>width){
          newSimbadInfo = new SimbadInfo(former.getObj(), former.getInfo() , formerBackground.getLocation().getX()+(width-width2-20)/2,
          formerBackground.getLocation().getY(),width2+20,height,this);
        }
        else if(height2>height){
          newSimbadInfo = new SimbadInfo(former.getObj(), former.getInfo() , formerBackground.getLocation().getX(),
          formerBackground.getLocation().getY()-(height2+basicDataTab.getHeight()+20-height)/2,width,height2+basicDataTab.getHeight()+20,this);
        }
        else{
          newSimbadInfo = new SimbadInfo(former.getObj(), former.getInfo() , formerBackground.getLocation().getX(),
          formerBackground.getLocation().getY(),width,height,this);
        }
          SQ_VIRTUAL_SPACE.removeGlyph(parent);
          SQ_VIRTUAL_SPACE.addGlyph(newSimbadInfo);
      }
      if(this.parent.getType().equals(Config.T_ASTRO_OBJ_SC)){
        SimbadCriteria former = (SimbadCriteria) parent;
        VRectangle formerBackground = former.getBackground();
        SimbadCriteria newSimbadCriteria = new SimbadCriteria(formerBackground.getLocation().getX(),
        formerBackground.getLocation().getY(), former.getParent(), this);
        SQ_VIRTUAL_SPACE.removeGlyph(parent);
        SQ_VIRTUAL_SPACE.addGlyph(newSimbadCriteria);
      }
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
  public double getWidth2(){
    return width2;
  }
  public double getHeight2(){
    return height2;
  }
  public String getMeasurementsStr(){
    return measurementsStr;
  }
  public String getBasicDataStr(){
    return basicDataStr;
  }
}
