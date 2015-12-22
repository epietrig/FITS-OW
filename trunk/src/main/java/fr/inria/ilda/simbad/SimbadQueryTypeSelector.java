package fr.inria.ilda.simbad;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VSegment;

import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.Camera;

import fr.inria.ilda.fitsow.Config;

import java.awt.Font;

import java.awt.Color;

public class SimbadQueryTypeSelector extends SimbadQueryGlyph{
  private VRectangle[] buttons;
  private int selected = -1;
  public static int BY_COORDINATES = 0;
  public static int BY_ID = 1;
  public static int BY_SCRIPT = 2;
  protected static double W = 300;
  protected static double H = 150;

  public SimbadQueryTypeSelector(VirtualSpace vs){
    super(W, H);
    this.setType(Config.T_ASTRO_OBJ_SQTS);
    this.background = new VRectangle (0, 0, Z, width, height, CONTAINER_COLOR);
    double[] bounds = background.getBounds();
    double left = bounds[0];
    double top = bounds[1];
    double buttonHeight = (height-2*TEXT_SIZE-3*OFFSET)/3;
    double buttonWidth = width-2*OFFSET;
    double y1 = top-2*TEXT_SIZE-buttonHeight/2;
    double y2 = y1 - buttonHeight - OFFSET;
    double y3 = y2 - buttonHeight - OFFSET;
    double tX = left + width/3;
    buttons = new VRectangle[3];
    buttons[0] = new VRectangle (0, y1, Z, buttonWidth , buttonHeight - OFFSET, BACKGROUND_COLOR);
    buttons[1] = new VRectangle (0, y2 , Z, buttonWidth, buttonHeight - OFFSET, BACKGROUND_COLOR);
    buttons[2] = new VRectangle (0, y3 , Z, buttonWidth, buttonHeight - OFFSET, BACKGROUND_COLOR);
    VText options = new VText(left+2*OFFSET, top-OFFSET-TEXT_SIZE, Z, TEXT_COLOR, "Select query type:");
    options.setScale(1.3f);
    VText byCoordinates = new VText(tX, top-height/4-TEXT_SIZE, Z, TEXT_COLOR, "by Coordinates");
    byCoordinates.setScale(1.2f);
    VText byIdentifier = new VText(tX, top-2*height/4-TEXT_SIZE, Z, TEXT_COLOR, "by Identifier");
    byIdentifier.setScale(1.2f);
    VText byScript = new VText(tX, top-3*height/4-TEXT_SIZE, Z, TEXT_COLOR, "by Script");
    byScript.setScale(1.2f);
    this.addChild(background);
    this.addChild(buttons[0]);
    this.addChild(buttons[1]);
    this.addChild(buttons[2]);
    this.addChild(options);
    this.addChild(byCoordinates);
    this.addChild(byIdentifier);
    this.addChild(byScript);
  }

  public int getSelectedButton(int jpx, int jpy, Camera c){
    for(int i = 0; i<buttons.length; i++){
      if(buttons[i].coordInsideP(jpx, jpy, c)) return i;
    }
    return -1;
  }

  public void select(int index){
    if(selected >= 0 && selected != index ){
      buttons[selected].setColor(BACKGROUND_COLOR);
      buttons[index].setColor(SELECTED_BACKGROUND_COLOR);
      selected = index;
    }
    else if(selected < 0){
      buttons[index].setColor(SELECTED_BACKGROUND_COLOR);
      selected = index;
    }
    else if(selected == index){
      selected = -1;
      buttons[index].setColor(BACKGROUND_COLOR);
    }

  }
//should be in parent class
  public VRectangle getBackground(){
    return background;
  }

  public int getSelected(){
    return selected;
  }

}
