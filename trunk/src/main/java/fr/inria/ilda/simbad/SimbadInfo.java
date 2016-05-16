/*  (c) COPYRIGHT INRIA (Institut National de Recherche en Informatique et en Automatique) and INRIA Chile, 2015-2016.
 *  Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id:  $
 */

package fr.inria.ilda.simbad;

import java.util.List;
import java.util.Vector;

import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VRoundRect;
import fr.inria.zvtm.glyphs.VSegment;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.Glyph;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.Font;
import fr.inria.zvtm.engine.VirtualSpace;

import fr.inria.ilda.fitsow.Config;

public class SimbadInfo extends SimbadQueryGlyph{
  public Composite basicData;
  private Composite measurements;
  private Tabs tabs;

  public SimbadInfo(AstroObject obj, double x, double y, SimbadResults parent){
    super(0,0);
    this.setType(Config.T_ASTRO_OBJ_BINFO);
    String[] info = obj.basicDataToString().split("\n");
    this.height = (info.length+2)*TEXT_SIZE+OFFSET;
    this.width = calculateWidth(info);
    double bx = x+width/2+parent.getWidth();
    this.background = new VRectangle(bx, y, Z, width, height, CONTAINER_COLOR, CONTAINER_BORDER_COLOR);
    this.addChild(background);

    double[] bounds = background.getBounds();
    double left = bounds[0];
    double top = bounds[1];

    this.tabs = new Tabs(top, left, height, width, this);
    this.addChild(tabs);

    this.measurements =  measurements(top, left, obj);
    this.basicData = basicData(top, left, obj, info);
  }

  private Composite basicData(double top, double left, AstroObject obj, String[] info){
    Composite basicInfo = new Composite();
    VText identifier = new VText(left+OFFSET,top-TEXT_SIZE*2,Z,TEXT_COLOR,obj.getIdentifier());
    // notBold = identifier.getFont();
    // bold = identifier.getFont().deriveFont(Font.BOLD);
    identifier.setFont(BOLD);
    identifier.setScale(1.3f);
    basicInfo.addChild(identifier);
    for(int i = 0; i < info.length; i++){
      VText text = new VText(left+OFFSET,top-TEXT_SIZE*(i+3),Z,TEXT_COLOR,info[i]);
      basicInfo.addChild(text);
    }
    return basicInfo;
  }

  private Composite measurements(double top, double left, AstroObject obj){
    Composite cMeasurements= new Composite();
    Vector<Measurement> vmeasurements = obj.getMeasurements();
    double maxWidth = 0;
    double maxHeight = 0;
    double aux = 0;
    MeasurementsTable table;
    if(vmeasurements.size() > 0){
      for (Measurement m : vmeasurements){
        if(m.equals(vmeasurements.firstElement()))
          table = new MeasurementsTable(m, left, top-TEXT_SIZE, 25, vs);
        else
          table = new MeasurementsTable(m, left, top, 5, vs);
        top = table.getBackground().getBounds()[3]-Config.TEXT_SIZE;
        aux = table.getWidth();
        maxHeight = maxHeight + table.getHeight();
        if(aux > maxWidth) maxWidth = aux;
        cMeasurements.addChild(table);
      }
    maxHeight = maxHeight + (OFFSET+Config.TEXT_SIZE)*vmeasurements.size();
    tabs.setWidth2(maxWidth);
    tabs.setHeight2(maxHeight);
    }
    return cMeasurements;
  }

  private double calculateWidth(String[] info){
    int retval = 0;
    int length = 0;
    for(String str : info){
      length = str.length();
      if(length > retval) retval = length;
    }
    return retval*5.5;
  }

  public Tabs getTabs(){
    return tabs;
  }

  public Composite getBasicData(){
    return basicData;
  }
  public Composite getMeasurements(){
    return measurements;
  }
//should be on parent class
  public VRectangle getBackground(){
    return background;
  }
}
