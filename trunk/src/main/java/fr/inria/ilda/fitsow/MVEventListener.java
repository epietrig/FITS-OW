/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.awt.Cursor;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;

import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JDialog;

import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;

import fr.inria.zvtm.engine.VCursor;
import fr.inria.zvtm.engine.Camera;
import fr.inria.zvtm.engine.View;
import fr.inria.zvtm.engine.ViewPanel;
import fr.inria.zvtm.engine.Location;
import fr.inria.zvtm.engine.VirtualSpaceManager;
import fr.inria.zvtm.engine.portals.Portal;
import fr.inria.zvtm.engine.portals.OverviewPortal;
import fr.inria.zvtm.engine.Utils;

import fr.inria.zvtm.glyphs.Glyph;
import fr.inria.zvtm.glyphs.VCircle;
import fr.inria.zvtm.glyphs.JSkyFitsImage;

import fr.inria.zvtm.event.ViewListener;
import fr.inria.zvtm.event.CameraListener;
import fr.inria.zvtm.event.PortalListener;
import fr.inria.zvtm.event.PickerListener;

import fr.inria.zuist.engine.Region;
import fr.inria.zuist.od.ObjectDescription;
import fr.inria.zuist.od.TextDescription;
import java.awt.geom.Point2D.Double;
import fr.inria.ilda.simbad.SimbadResults;
import fr.inria.ilda.simbad.SimbadInfo;
import fr.inria.ilda.simbad.SimbadCriteria;
import fr.inria.ilda.simbad.Tabs;

public class MVEventListener implements ViewListener, CameraListener, ComponentListener, PickerListener {

    static final float MAIN_SPEED_FACTOR = 50.0f;

    static final float WHEEL_ZOOMIN_FACTOR = 21.0f;
    static final float WHEEL_ZOOMOUT_FACTOR = 22.0f;

    static float WHEEL_MM_STEP = 1.0f;

    //remember last mouse coords to compute translation  (dragging)
    int lastJPX,lastJPY;
    double lastVX, lastVY;

    int currentJPX, currentJPY;

    FITSOW app;

    // last glyph entered
    Glyph lge;

    boolean panning = false;
    boolean querying = false;
    boolean draggingFITS = false;
    boolean draggingSimbadResults = false;
    boolean draggingSimbadInfo = false;
    boolean draggingSimbadCriteria = false;

    // cursor inside FITS image
    JSkyFitsImage ciFITSImage = null;

    SimbadQuery sq;

    SimbadCriteria lastSimbadCriteria;

    MVEventListener(FITSOW app){
        this.app = app;
    }

    public void press1(ViewPanel v,int mod,int jpx,int jpy, MouseEvent e){
        lastJPX = jpx;
        lastJPY = jpy;

        if (querying && !insideSimbadCriteria(jpx, jpy)){
            sq = new SimbadQuery(app);
            if (ciFITSImage != null){
                sq.setCenter(v.getVCursor().getVSCoordinates(app.dCamera), ciFITSImage);
            }
            else {
                sq.setCenter(v.getVCursor().getVSCoordinates(app.zfCamera),
                             (JSkyFitsImage)app.zfSpacePicker.lastGlyphEntered());
            }
        }
        else if(insideSimbadResults(jpx, jpy)){
          draggingSimbadResults = true;
          draggingFITS = false;
        }
        else if(insideSimbadInfo(jpx, jpy)){
          draggingSimbadInfo = true;
          draggingFITS = false;
          draggingSimbadResults = false;
        }
        else if(insideSimbadCriteria(jpx, jpy)){
          draggingSimbadCriteria = true;
          draggingSimbadInfo = false;
          draggingFITS = false;
          draggingSimbadResults = false;
        }
        else {
            lge = app.dSpacePicker.lastGlyphEntered();
            if (lge != null){
                // interacting with a Glyph in data space (could be a FITS image, a PDF page, etc.)
                draggingFITS = true;
                app.dSpacePicker.stickGlyph(lge);
            }
            else {
                // pressed button in empty space (or background ZUIST image)
                panning = true;
            }
        }
    }

    public void release1(ViewPanel v,int mod,int jpx,int jpy, MouseEvent e){
        panning = false;
        if(draggingSimbadResults){
          draggingSimbadResults = false;
        }
        if(draggingSimbadInfo){
          draggingSimbadInfo = false;
        }
        if(draggingSimbadCriteria){
          draggingSimbadCriteria = false;
        }
        if (draggingFITS){
            app.dSpacePicker.unstickLastGlyph();
            draggingFITS = false;
        }
        if (querying && !insideSimbadCriteria(jpx, jpy)){
            exitQueryMode();
            if (sq != null){
                if (ciFITSImage != null){
                    sq.querySimbad(v.getVCursor().getVSCoordinates(app.dCamera), ciFITSImage);
                }
                else {
                    sq.querySimbad(v.getVCursor().getVSCoordinates(app.zfCamera),
                                   (JSkyFitsImage)app.zfSpacePicker.lastGlyphEntered());
                }
                sq = null;
            }
        }

    }

    public void click1(ViewPanel v,int mod,int jpx,int jpy,int clickNumber, MouseEvent e){
      if(insideSimbadResults(jpx, jpy)){
        updateSimbadResults(jpx, jpy);
      }
      if(insideSimbadCriteria(jpx, jpy)){
        SimbadCriteria criteria = getCurrentSimbadCriteria();
        Tabs tabs = criteria.getTabs();
        Point2D.Double coords = new Point2D.Double();
        app.mView.fromPanelToVSCoordinates(jpx,jpy,app.sqCamera,coords);
        double x = coords.getX();
        double y = coords.getY();
        if(tabs.getTabSelected().equals(tabs.getMeasurementsStr())){
          criteria.getMeasurements().select(criteria.getMeasurements().getItemSelected(x, y, app.sqCamera));
        }
        else if(tabs.getTabSelected().equals(tabs.getBasicDataStr())){
          if(criteria.getObjectTypeFilter().coordInsideComponent(x, y)){
          criteria.getObjectTypeFilter().select(criteria.getObjectTypeFilter().getItemSelected(x, y, app.sqCamera));
          }
          else if(criteria.getPMFilter().coordInsideComponent(x, y)){
            int angle = criteria.getPMFilter().getItemSelected(x, y, app.sqCamera);
            JFrame parent = new JFrame();
            JOptionPane optionPane;
            String inputValue ="";
            if(angle == 0){
              optionPane = new JOptionPane("right ascension of proper motion (mas)", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
              inputValue = JOptionPane.showInputDialog("Enter right ascension of proper motion (mas) in the format:\n >=/<= numrical-value");
            }else if(angle ==1){
              optionPane = new JOptionPane("declination of proper motion (mas) ", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
              inputValue = JOptionPane.showInputDialog("Enter declination of proper motion (mas) in the format:\n >=/<= numrical-value");
            }
            criteria.getPMFilter().select(angle, inputValue);
          }
        }
      }
      updateSimbadInfoTabs(jpx, jpy);
      updateSimbadCriteriaTabs(jpx, jpy); //if I'm clicking tabs, update them
    }

    public void press2(ViewPanel v,int mod,int jpx,int jpy, MouseEvent e){}

    public void release2(ViewPanel v,int mod,int jpx,int jpy, MouseEvent e){}

    public void click2(ViewPanel v,int mod,int jpx,int jpy,int clickNumber, MouseEvent e){}

    public void press3(ViewPanel v,int mod,int jpx,int jpy, MouseEvent e){
        app.mView.setActiveLayer(FITSOW.MENU_LAYER);
        app.meh.setSelectedFITSImage(app.dSpacePicker.getPickedGlyphList(Config.T_FITS));
        app.meh.displayMainPieMenu(currentJPX, currentJPY);
    }

    public void release3(ViewPanel v,int mod,int jpx,int jpy, MouseEvent e){}

    public void click3(ViewPanel v, int mod, int jpx, int jpy, int clickNumber, MouseEvent e){}

    public void mouseMoved(ViewPanel v, int jpx, int jpy, MouseEvent e){
        currentJPX = jpx;
        currentJPY = jpy;
        lastVX = v.getVCursor().getVSXCoordinate();
        lastVY = v.getVCursor().getVSYCoordinate();
        updateZUISTSpacePicker(jpx, jpy);
        updateDataSpacePicker(jpx, jpy);
        if (ciFITSImage != null){
            app.scene.updateWCSCoordinates(dvsCoords.x, dvsCoords.y, ciFITSImage);
        }
        else {
            try {
                app.scene.updateWCSCoordinates(zvsCoords.x, zvsCoords.y, (JSkyFitsImage)app.zfSpacePicker.lastGlyphEntered());
            }
            catch (Exception ex){
                // be silent about it, only happens at init time when getting
                // mouse moved events before pickers have been created.
            }
        }
    }

    public void mouseDragged(ViewPanel v, int mod, int buttonNumber, int jpx, int jpy, MouseEvent e){

        currentJPX = jpx;
        currentJPY = jpy;
        if (panning){
          app.mView.setActiveLayer(FITSOW.DATA_LAYER);

            app.nav.pan(app.zfCamera, lastJPX-jpx, jpy-lastJPY, 1);
            lastJPX = jpx;
            lastJPY = jpy;
        }
        else if(draggingSimbadResults){
          SimbadResults list = getCurrentSimbadResults();
          list.move(jpx-lastJPX, lastJPY-jpy);
          lastJPX = jpx;
          lastJPY = jpy;
        }
        else if(draggingSimbadInfo){
          SimbadInfo info = getCurrentSimbadInfo();
          info.move(jpx-lastJPX, lastJPY-jpy);
          info.getBasicData().move(jpx-lastJPX, lastJPY-jpy);
          info.getMeasurements().move(jpx-lastJPX, lastJPY-jpy);

          lastJPX = jpx;
          lastJPY = jpy;
        }
        else if(draggingSimbadCriteria){
          SimbadCriteria criteria = getCurrentSimbadCriteria();
          criteria.move(jpx-lastJPX, lastJPY-jpy);
          criteria.getBasicData().move(jpx-lastJPX, lastJPY-jpy);
          criteria.getMeasurements().move(jpx-lastJPX, lastJPY-jpy);

          lastJPX = jpx;
          lastJPY = jpy;
        }
        else if (querying && sq != null){
          app.mView.setActiveLayer(FITSOW.DATA_LAYER);

            sq.setRadius(v.getVCursor().getVSCoordinates((ciFITSImage != null) ? app.dCamera : app.zfCamera));
            updateZUISTSpacePicker(jpx, jpy);
            updateDataSpacePicker(jpx, jpy);
        }
        else if(!draggingSimbadResults){
          app.mView.setActiveLayer(FITSOW.DATA_LAYER);
          updateZUISTSpacePicker(jpx, jpy);
          updateDataSpacePicker(jpx, jpy);
        }
    }

    public void mouseWheelMoved(ViewPanel v, short wheelDirection, int jpx, int jpy, MouseWheelEvent e){
        double mvx = v.getVCursor().getVSXCoordinate();
        double mvy = v.getVCursor().getVSYCoordinate();
        if (wheelDirection  == WHEEL_UP){
            app.nav.czoomOut(app.zfCamera, WHEEL_ZOOMOUT_FACTOR, mvx, mvy);
        }
        else {
            //wheelDirection == WHEEL_DOWN, zooming in
            app.nav.czoomIn(app.zfCamera, WHEEL_ZOOMIN_FACTOR, mvx, mvy);
        }
    }

    public void enterGlyph(Glyph g){
        g.highlight(true, null);
        if (g.getType().equals(Config.T_FITS)){
            ciFITSImage = (JSkyFitsImage)g;
        }
    }

    public void exitGlyph(Glyph g){
        g.highlight(false, null);
        if (g.getType().equals(Config.T_FITS)){
            Glyph[] insideOtherFITS = app.dSpacePicker.getPickedGlyphList(Config.T_FITS);
            if (insideOtherFITS.length > 0){
                ciFITSImage = (JSkyFitsImage)insideOtherFITS[insideOtherFITS.length-1];
            }
            else {
                ciFITSImage = null;
            }
        }
    }

    public void Kpress(ViewPanel v,char c,int code,int mod, KeyEvent e){
        if (code==KeyEvent.VK_PAGE_UP){app.nav.getHigherView();}
        else if (code==KeyEvent.VK_PAGE_DOWN){app.nav.getLowerView();}
        else if (code==KeyEvent.VK_HOME){app.nav.getGlobalView(null);}
        else if (code==KeyEvent.VK_UP){app.nav.translateView(Navigation.MOVE_UP);}
        else if (code==KeyEvent.VK_DOWN){app.nav.translateView(Navigation.MOVE_DOWN);}
        else if (code==KeyEvent.VK_LEFT){app.nav.translateView(Navigation.MOVE_LEFT);}
        else if (code==KeyEvent.VK_RIGHT){app.nav.translateView(Navigation.MOVE_RIGHT);}
        else if (code==KeyEvent.VK_F1){
            app.meh.setSelectedFITSImage(app.dSpacePicker.getPickedGlyphList(Config.T_FITS));
            app.scene.selectPrevColorMapping(app.meh.selectedFITSImage);
        }
        else if (code==KeyEvent.VK_F2){
            app.meh.setSelectedFITSImage(app.dSpacePicker.getPickedGlyphList(Config.T_FITS));
            app.scene.selectNextColorMapping(app.meh.selectedFITSImage);
        }
    }

    public void Ktype(ViewPanel v,char c,int code,int mod, KeyEvent e){}

    public void Krelease(ViewPanel v,char c,int code,int mod, KeyEvent e){}

    public void viewActivated(View v){}

    public void viewDeactivated(View v){}

    public void viewIconified(View v){}

    public void viewDeiconified(View v){}

    public void viewClosing(View v){
        app.exit();
    }

    /*ComponentListener*/
    public void componentHidden(ComponentEvent e){}
    public void componentMoved(ComponentEvent e){}
    public void componentResized(ComponentEvent e){
        app.updatePanelSize();
    }
    public void componentShown(ComponentEvent e){}

    public void cameraMoved(Camera cam, Point2D.Double coord, double a){}

    Point2D.Double zvsCoords = new Point2D.Double();
    Point2D.Double dvsCoords = new Point2D.Double();

    void updateZUISTSpacePicker(int jpx, int jpy){
        app.mView.fromPanelToVSCoordinates(jpx, jpy, app.zfCamera, zvsCoords);
        app.zfSpacePicker.setVSCoordinates(zvsCoords.x, zvsCoords.y);
        app.zfSpacePicker.computePickedGlyphList(app.zfCamera, false);
    }

    void updateDataSpacePicker(int jpx, int jpy){
        app.mView.fromPanelToVSCoordinates(jpx, jpy, app.dCamera, dvsCoords);
        app.dSpacePicker.setVSCoordinates(dvsCoords.x, dvsCoords.y);
        app.dSpacePicker.computePickedGlyphList(app.dCamera, false);
    }

    /*------------------ Simbad -------------------------*/

    void enterQueryMode(){
        querying = true;
        SimbadCriteria sc = new SimbadCriteria(0,0,app.sqSpace);
        app.sqSpace.addGlyph(sc);
        app.sqSpace.addGlyph(sc.getBasicData());
        app.mView.setActiveLayer(FITSOW.DATA_LAYER);
        app.scene.setStatusBarMessage("Select region to query:");
    }

    void exitQueryMode(){
        SimbadCriteria criteria = getCurrentSimbadCriteria();
        lastSimbadCriteria = criteria;
        app.sqSpace.removeGlyph(criteria.getBasicData());
        app.sqSpace.removeGlyph(criteria.getMeasurements());
        app.sqSpace.removeGlyph(criteria);
        app.scene.setStatusBarMessage(null);
        querying = false;
    }

    void updateSimbadResults(int jpx, int jpy){
      SimbadResults list = getCurrentSimbadResults();
      Point2D.Double coords = new Point2D.Double();
      app.mView.fromPanelToVSCoordinates(jpx,jpy,app.sqCamera,coords);
      Vector<Glyph> gsd = app.dSpace.getAllGlyphs();
      updateSimbadInfo(coords.getX(),coords.getY(), list);
      list.highlightCorrespondingGlyph(gsd, list.getCorrespondingGlyph(gsd));
    }

    void updateSimbadInfo(double x, double y, SimbadResults list){
      int index = list.insideWhichObject(x,y);
      boolean selecting = list.highlight(index);
      Vector<Glyph> simbadInfoG = app.sqSpace.getGlyphsOfType(Config.T_ASTRO_OBJ_BINFO);
      if(simbadInfoG.size() > 0){
        SimbadInfo info = (SimbadInfo) simbadInfoG.get(0);
        app.sqSpace.removeGlyph(info);
        if(info.getTabs().getTabSelected().equals(info.getTabs().basicDataStr))
          app.sqSpace.removeGlyph(info.getBasicData());
        else if(info.getTabs().getTabSelected().equals(info.getTabs().measurementsStr))
          app.sqSpace.removeGlyph(info.getMeasurements());
      }
      if(selecting){
        SimbadInfo info = list.getBasicInfo(index);
        app.sqSpace.addGlyph(info);
        app.sqSpace.addGlyph(info.getBasicData());
      }
    }

    void updateSimbadInfoTabs(int jpx, int jpy){
      SimbadInfo info = getCurrentSimbadInfo();
      if(info != null){
        Tabs tabs = info.getTabs();
        if(tabs.getBasicDataTab().coordInsideP(jpx,jpy,app.sqCamera)){
          tabs.activateBasicDataTab(info.getBackground(), info.getMeasurements(), info.getBasicData());
        }
        else if(tabs.getMeasurementsTab().coordInsideP(jpx,jpy,app.sqCamera)){
          tabs.activateMeasurementsTab(info.getBackground(), info.getMeasurements(), info.getBasicData());
        }
      }
    }

    void updateSimbadCriteriaTabs(int jpx, int jpy){
      SimbadCriteria criteria = getCurrentSimbadCriteria();
      if(criteria!= null){
        Tabs tabs = criteria.getTabs();
        if(tabs.getBasicDataTab().coordInsideP(jpx,jpy,app.sqCamera)){
          tabs.activateBasicDataTab(criteria.getBackground(), criteria.getMeasurements(), criteria.getBasicData());
        }
        else if(tabs.getMeasurementsTab().coordInsideP(jpx,jpy,app.sqCamera)){
          tabs.activateMeasurementsTab(criteria.getBackground(), criteria.getMeasurements(), criteria.getBasicData());
        }
      }
    }

    boolean insideSimbadResults(int jpx, int jpy){
      SimbadResults list = getCurrentSimbadResults();
      if(list!= null) return list.getBackground().coordInsideP(jpx, jpy, app.sqCamera);
      return false;
    }

    boolean insideSimbadInfo(int jpx, int jpy){
      SimbadInfo info = getCurrentSimbadInfo();
      if(info != null)
        return info.getBackground().coordInsideP(jpx, jpy, app.sqCamera);
      return false;
    }

    boolean insideSimbadCriteria(int jpx, int jpy){
      SimbadCriteria criteria = getCurrentSimbadCriteria();
      if(criteria != null)
        return criteria.getContainer().coordInsideP(jpx, jpy, app.sqCamera);
      return false;
    }

    SimbadResults getCurrentSimbadResults(){
      Vector <Glyph> simbadResults = app.sqSpace.getGlyphsOfType(Config.T_ASTRO_OBJ_SR);
      if(simbadResults.size()>0){
        SimbadResults list = (SimbadResults) simbadResults.get(0);
        return list;
      }
      return null;
    }
    SimbadInfo getCurrentSimbadInfo(){
      Vector <Glyph> simbadInfo = app.sqSpace.getGlyphsOfType(Config.T_ASTRO_OBJ_BINFO);
      if(simbadInfo.size()>0){
        SimbadInfo info = (SimbadInfo) simbadInfo.get(0);
        return info;
      }
      return null;
    }
    public SimbadCriteria getCurrentSimbadCriteria(){
      Vector<Glyph> simbadCriteria = app.sqSpace.getGlyphsOfType(Config.T_ASTRO_OBJ_SC);
      if(simbadCriteria.size()>0){
        SimbadCriteria criteria = (SimbadCriteria) simbadCriteria.get(0);
        return criteria;
      }
      return null;
    }
    public SimbadCriteria getLastSimbadCriteria(){
      return lastSimbadCriteria;
    }
}
