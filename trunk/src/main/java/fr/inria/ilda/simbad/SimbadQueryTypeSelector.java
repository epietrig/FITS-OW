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
  VRectangle[] buttons;
  VRectangle background;
  int selected;

  public SimbadQueryTypeSelector(VirtualSpace vs){
    super(300, 150, vs);
    this.setType(Config.T_ASTRO_OBJ_SQTS);
    selected = -1;
    background = new VRectangle (0, 0, Z, width, height, Config.SELECTED_BACKGROUND_COLOR);
    double[] bounds = background.getBounds();
    double left = bounds[0];
    double top = bounds[1];
    double buttonHeight = (height-2*Config.TEXT_SIZE-3*Config.OFFSET)/3;
    double buttonWidth = width-2*Config.OFFSET;
    double y1 = top-2*Config.TEXT_SIZE-buttonHeight/2;
    double y2 = y1 - buttonHeight - Config.OFFSET;
    double y3 = y2 - buttonHeight - Config.OFFSET;
    double tX = left + width/3;
    buttons = new VRectangle[3];
    buttons[0] = new VRectangle (0, y1, Z, buttonWidth , buttonHeight - Config.OFFSET, Config.SELECTED_BACKGROUND_COLOR);
    buttons[1] = new VRectangle (0, y2 , Z, buttonWidth, buttonHeight - Config.OFFSET, Config.SELECTED_BACKGROUND_COLOR);
    buttons[2] = new VRectangle (0, y3 , Z, buttonWidth, buttonHeight - Config.OFFSET, Config.SELECTED_BACKGROUND_COLOR);
    VText options = new VText(left+2*Config.OFFSET, top-Config.OFFSET-Config.TEXT_SIZE, Z, Config.SELECTED_TEXT_COLOR, "Select query type:");
    // options.setFont(options.getFont().deriveFont(Font.BOLD));
    options.setScale(1.3f);
    VText byCoordinates = new VText(tX, top-height/4-Config.TEXT_SIZE, Z, Config.SELECTED_TEXT_COLOR, "by Coordinates");
    byCoordinates.setScale(1.2f);
    VText byIdentifier = new VText(tX, top-2*height/4-Config.TEXT_SIZE, Z, Config.SELECTED_TEXT_COLOR, "by Identifier");
    byIdentifier.setScale(1.2f);
    VText byScript = new VText(tX, top-3*height/4-Config.TEXT_SIZE, Z, Config.SELECTED_TEXT_COLOR, "by Script");
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
      buttons[selected].setColor(Config.SELECTED_BACKGROUND_COLOR);
      buttons[index].setColor(Color.red);
      selected = index;
    }
    else if(selected < 0){
      buttons[index].setColor(Color.red);
      selected = index;
    }
    else if(selected == index){
      selected = -1;
      buttons[index].setColor(Config.SELECTED_BACKGROUND_COLOR);
    }

  }

  public VRectangle getBackground(){
    return background;
  }

}
