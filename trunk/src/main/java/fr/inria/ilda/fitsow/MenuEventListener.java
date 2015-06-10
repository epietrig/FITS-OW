/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
//
import java.awt.image.RGBImageFilter;
import java.awt.MultipleGradientPaint;

import java.util.Vector;
import java.util.HashMap;
// import java.util.Timer;
// import java.util.TimerTask;
//
import fr.inria.zvtm.engine.View;
import fr.inria.zvtm.engine.ViewPanel;
import fr.inria.zvtm.event.ViewListener;
import fr.inria.zvtm.event.PickerListener;
import fr.inria.zvtm.glyphs.Glyph;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.PRectangle;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.JSkyFitsImage;
import fr.inria.zvtm.widgets.PieMenuFactory;
import fr.inria.zvtm.widgets.PieMenu;

import fr.inria.zvtm.fits.Utils;

public class MenuEventListener implements ViewListener, PickerListener {

    static final String MPM_GLOBAL_VIEW = "Global View";
    static final String MPM_QUERY = "Query...";
    static final String MPM_COLOR = "Color...";
    static final String MPM_SCALE = "Scale...";
    static final String[] MPM_COMMANDS = {MPM_COLOR, MPM_SCALE, MPM_QUERY, MPM_GLOBAL_VIEW};
    static final Point2D.Double[] MPM_OFFSETS = {new Point2D.Double(0,0), new Point2D.Double(-10,0),
                                                 new Point2D.Double(0,-10), new Point2D.Double(10,0)};

    static final String SCALEPM_LINEAR = "linear";
    static final String SCALEPM_LOG = "log";
    static final String SCALEPM_HISTEQ = "histogram";
    static final String SCALEPM_SQRT = "sqrt";
    static final String[] SCALEPM_COMMANDS = {SCALEPM_HISTEQ, SCALEPM_LINEAR, SCALEPM_SQRT, SCALEPM_LOG};
    static final Point2D.Double[] SCALEPM_OFFSETS = {new Point2D.Double(0,5), new Point2D.Double(-5,0),
                                                     new Point2D.Double(0,-10), new Point2D.Double(5,0)};

    FITSOW app;

    PieMenu mainPieMenu, subPieMenu;

    JSkyFitsImage selectedFITSImage = null;

    MenuEventListener(FITSOW app){
        this.app = app;
    }

    public void press1(ViewPanel v,int mod,int jpx,int jpy, MouseEvent e){
        if (showingCLTmenu){
            hideColorSubMenu();
            app.mView.setActiveLayer(FITSOW.DATA_LAYER);
        }
    }

    public void release1(ViewPanel v,int mod,int jpx,int jpy, MouseEvent e){}

    public void click1(ViewPanel v,int mod,int jpx,int jpy,int clickNumber, MouseEvent e){}

    public void press2(ViewPanel v,int mod,int jpx,int jpy, MouseEvent e){}

    public void release2(ViewPanel v,int mod,int jpx,int jpy, MouseEvent e){}

    public void click2(ViewPanel v,int mod,int jpx,int jpy,int clickNumber, MouseEvent e){}

    public void press3(ViewPanel v,int mod,int jpx,int jpy, MouseEvent e){}

    public void release3(ViewPanel v,int mod,int jpx,int jpy, MouseEvent e){
        Glyph g = v.lastGlyphEntered();
        short layerToActivate = FITSOW.DATA_LAYER;
        if (g != null){
            if (g.getType() == Config.T_MPMI){
                layerToActivate = mainPieMenuEvent(g);
            }
            else if (g.getType() == Config.T_SPMISc){
                // nothing to do, command triggered when cursor entered menu item
            }
        }
        hideSubPieMenu();
        hideMainPieMenu();
        app.mView.setActiveLayer(layerToActivate);
    }

    public void click3(ViewPanel v, int mod, int jpx, int jpy, int clickNumber, MouseEvent e){}

    public void mouseMoved(ViewPanel v, int jpx, int jpy, MouseEvent e){
        updateMenuSpacePicker(jpx, jpy);
    }

    public void mouseDragged(ViewPanel v, int mod, int buttonNumber, int jpx, int jpy, MouseEvent e){
        updateMenuSpacePicker(jpx, jpy);
    }

    public void mouseWheelMoved(ViewPanel v, short wheelDirection, int jpx, int jpy, MouseWheelEvent e){}

    public void enterGlyph(Glyph g){
        if (g.getType() != null){
            if (g.getType().equals(Config.T_MPMI)){
                g.highlight(true, null);
                // app.mnSpace.onTop(g);
                // int i = mainPieMenu.getItemIndex(g);
                // if (i != -1){
                //     app.mnSpace.onTop(mainPieMenu.getLabels()[i]);
                // }
            }
            else if (g.getType().startsWith(Config.T_SPMI)){
                g.highlight(true, null);
                if (g.getType() == Config.T_SPMISc){
                    subPieMenuEvent(g);
                }
                // app.mnSpace.onTop(g);
                // int i = subPieMenu.getItemIndex(g);
                // if (i != -1){
                //     app.mnSpace.onTop(subPieMenu.getLabels()[i]);
                // }
            }
            else if (g.getType().equals(Config.T_CLT_BTN)){
                selectCLT((String)g.getOwner());
            }
        }
        else {
            if (mainPieMenu != null && g == mainPieMenu.getBoundary()){
                mainPieMenu.setSensitivity(true);
            }
        }
    }

    public void exitGlyph(Glyph g){
        if (g.getType() != null){
            if (g.getType().equals(Config.T_MPMI) || g.getType().startsWith(Config.T_SPMI)){
                // exiting a pie menu item
                g.highlight(false, null);
            }
            else if (g.getType().equals(Config.T_CLT_BTN)){
                g.highlight(false, null);
            }
        }
        else {
            if (mainPieMenu != null && g == mainPieMenu.getBoundary()){
                // crossing the main pie menu's trigger
                Glyph lge = app.mnSpacePicker.lastGlyphEntered();
                if (lge != null && lge.getType() == Config.T_MPMI){
                    if (displaySubPieMenu(lge)){
                        mainPieMenu.setSensitivity(false);
                    }
                }
            }
            else if (subPieMenu != null && g == subPieMenu.getBoundary()){
                // crossing a sub pie menu's trigger
                // (takes back to main pie menu)
                hideSubPieMenu();
                mainPieMenu.setSensitivity(true);
            }
        }
    }

    public void Kpress(ViewPanel v, char c, int code, int mod, KeyEvent e){
        if (code==KeyEvent.VK_F1){
            String newCLT = app.scene.selectPrevColorMapping(app.meh.selectedFITSImage);
            updateHighlightedCLT(newCLT);
        }
        else if (code==KeyEvent.VK_F2){
            String newCLT = app.scene.selectNextColorMapping(app.meh.selectedFITSImage);
            updateHighlightedCLT(newCLT);
        }
        else if (code==KeyEvent.VK_ESCAPE){
            if (showingCLTmenu){
                hideColorSubMenu();
                app.mView.setActiveLayer(FITSOW.DATA_LAYER);
            }
        }
    }

    public void Ktype(ViewPanel v, char c, int code, int mod, KeyEvent e){}

    public void Krelease(ViewPanel v, char c, int code, int mod, KeyEvent e){}

    public void viewActivated(View v){}

    public void viewDeactivated(View v){}

    public void viewIconified(View v){}

    public void viewDeiconified(View v){}

    public void viewClosing(View v){
        app.exit();
    }

    /*------------------ Picking in this layer ------------*/

    Point2D.Double vsCoords = new Point2D.Double();

    void updateMenuSpacePicker(int jpx, int jpy){
        app.mView.fromPanelToVSCoordinates(jpx, jpy, app.mnCamera, vsCoords);
        app.mnSpacePicker.setVSCoordinates(vsCoords.x, vsCoords.y);
        app.mnSpacePicker.computePickedGlyphList(app.mnCamera);
    }

    void setSelectedFITSImage(Glyph[] pickedImages){
        selectedFITSImage = (pickedImages.length > 0) ? (JSkyFitsImage)pickedImages[pickedImages.length-1] : null;
    }

    /*------------------ Pie menu -------------------------*/

    void displayMainPieMenu(){
        PieMenuFactory.setSensitivityRadius(0.6);
        PieMenuFactory.setRadius(140);
        PieMenuFactory.setTranslucency(0.7f);
        mainPieMenu = PieMenuFactory.createPieMenu(MPM_COMMANDS, MPM_OFFSETS, 0, app.mView);
        Glyph[] items = mainPieMenu.getItems();
        for (Glyph item:items){
            item.setType(Config.T_MPMI);
        }
    }

    void hideMainPieMenu(){
        if (mainPieMenu == null){return;}
        mainPieMenu.destroy(0);
        mainPieMenu = null;
    }

    // returns true if it did create a sub pie menu
    boolean displaySubPieMenu(Glyph menuItem){
        int index = mainPieMenu.getItemIndex(menuItem);
        if (index != -1){
            String label = mainPieMenu.getLabels()[index].getText();
            PieMenuFactory.setSensitivityRadius(1);
            PieMenuFactory.setRadius(100);
            PieMenuFactory.setTranslucency(0.95f);
            if (label.equals(MPM_SCALE)){
                displayScaleSubMenu();
                return true;
            }
            return false;
        }
        return false;
    }

    void hideSubPieMenu(){
        if (subPieMenu == null){return;}
        subPieMenu.destroy(0);
        subPieMenu = null;
    }

    short mainPieMenuEvent(Glyph menuItem){
        int index = mainPieMenu.getItemIndex(menuItem);
        if (index != -1){
            String label = mainPieMenu.getLabels()[index].getText();
            if (label == MPM_GLOBAL_VIEW){
                app.nav.getGlobalView(null);
            }
            else if (label == MPM_COLOR){
                displayColorSubMenu();
                return FITSOW.MENU_LAYER;
            }
            // else if (label == MPM_QUERY){
            //
            // }
        }
        return FITSOW.DATA_LAYER;
    }

    void subPieMenuEvent(Glyph menuItem){
        int index = subPieMenu.getItemIndex(menuItem);
        if (index != -1){
            String label = subPieMenu.getLabels()[index].getText();
            if (label == SCALEPM_LOG){app.scene.setScale(selectedFITSImage, Config.SCALE_LOG);}
            else if (label == SCALEPM_LINEAR){app.scene.setScale(selectedFITSImage, Config.SCALE_LINEAR);}
            else if (label == SCALEPM_SQRT){app.scene.setScale(selectedFITSImage, Config.SCALE_SQRT);}
            else if (label == SCALEPM_HISTEQ){app.scene.setScale(selectedFITSImage, Config.SCALE_HISTEQ);}
        }
    }

    /*------------------ Color -------------------------*/

    boolean showingCLTmenu = false;

    HashMap<String,CLTButton> clt2button = new HashMap(Config.COLOR_MAPPING_GRADIENTS.size(),1);
    String currentCLT;

    public void displayColorSubMenu(){
        currentCLT = (selectedFITSImage != null) ? selectedFITSImage.getColorLookupTable() : app.scene.zuistColorMapping;
        Vector<Glyph> cltMenuGs = new Vector(2*Config.COLOR_MAPPING_LIST.length+1);
        double gridH = Config.LARGEST_COLOR_MAPPING_CAT;
        double gridW = Config.COLOR_MAPPINGS.length;
        double cellW = (Config.CLT_BTN_W + 2*Config.CLT_BTN_PADDING);
        double cellH = (Config.CLT_BTN_H + 2*Config.CLT_BTN_PADDING);
        double bkgW = gridW * cellW;
        double bkgH = gridH * cellH;
        VRectangle bkg = new VRectangle(0, 0, Config.Z_CLT_BKG, bkgW, 1.05*bkgH, Color.BLACK, Color.BLACK, .8f);
        bkg.setType(Config.T_CLT_BTN);
        bkg.setSensitivity(false);
        cltMenuGs.add(bkg);
        clt2button.clear();
        for (int i=0;i<Config.COLOR_MAPPINGS.length;i++){
            for (int j=0;j<Config.COLOR_MAPPINGS[i].length;j++){
                double x = i * cellW - bkgW/2d + cellW/2d;
                double y = -j * cellH + bkgH/2d - cellH/2d;
                RGBImageFilter f = Config.COLOR_MAPPING_GRADIENTS.get(Config.COLOR_MAPPINGS[i][j]);
                MultipleGradientPaint mgp = Utils.makeGradient(f);
                PRectangle filterG = new PRectangle(x, y, Config.Z_CLT_BTN,
                                                    Config.CLT_BTN_W, Config.CLT_BTN_H,
                                                    mgp, Config.CLT_BTN_BORDER_COLOR);
                filterG.setType(Config.T_CLT_BTN);
                filterG.setOwner(Config.COLOR_MAPPINGS[i][j]);
                VText filterLb = new VText(x-Config.CLT_BTN_W/2d+Config.CLT_BTN_HOFFSET,
                                           y+Config.CLT_BTN_H/2d+Config.CLT_BTN_VOFFSET,
                                           Config.Z_CLT_BTN, Config.CLT_BTN_BORDER_COLOR,
                                           Config.COLOR_MAPPINGS[i][j], VText.TEXT_ANCHOR_START);
                filterLb.setType(Config.T_CLT_BTN);
                cltMenuGs.add(filterG);
                cltMenuGs.add(filterLb);
                clt2button.put(Config.COLOR_MAPPINGS[i][j], new CLTButton(filterG, filterLb));
            }
        }
        app.mnSpace.addGlyphs(cltMenuGs.toArray(new Glyph[cltMenuGs.size()]));
        showingCLTmenu = true;
        clt2button.get(currentCLT).select();
    }

   public void hideColorSubMenu(){
        Vector v = app.mnSpace.getGlyphsOfType(Config.T_CLT_BTN);
        app.mnSpace.removeGlyphs((Glyph[])v.toArray(new Glyph[v.size()]), true);
        showingCLTmenu = false;
    }

    void selectCLT(String clt){
        if (!clt.equals(currentCLT)){
            updateHighlightedCLT(clt);
            app.scene.setColorMapping(selectedFITSImage, clt);
        }
    }

    public void updateHighlightedCLT(String clt){
        clt2button.get(currentCLT).unselect();
        clt2button.get(clt).select();
        currentCLT = clt;
    }

    /*------------------ Scale -------------------------*/

    void displayScaleSubMenu(){
        subPieMenu = PieMenuFactory.createPieMenu(SCALEPM_COMMANDS, SCALEPM_OFFSETS,
                                                  0, app.mView);
        Glyph[] items = subPieMenu.getItems();
        for (int i=0;i<items.length;i++){
            items[i].setType(Config.T_SPMISc);
        }
    }


}

class CLTButton {

    PRectangle button;
    VText label;

    CLTButton(PRectangle r, VText t){
        this.button = r;
        this.label = t;
    }

    void select(){
        button.setStroke(Config.CLT_BTN_SEL_STROKE);
        button.setBorderColor(Config.CLT_BTN_SEL_COLOR);
        label.setColor(Config.CLT_BTN_SEL_COLOR);
    }

    void unselect(){
        button.setStroke(null);
        button.setBorderColor(Config.CLT_BTN_BORDER_COLOR);
        label.setColor(Config.CLT_BTN_BORDER_COLOR);
    }

}
