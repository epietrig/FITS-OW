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
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.JSkyFitsImage;
import fr.inria.zvtm.glyphs.BrowsableDocument;

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
import fr.inria.ilda.simbad.SimbadQueryTypeSelector;
import fr.inria.ilda.simbad.SimbadQueryGlyph;
import fr.inria.ilda.simbad.SimbadClearQuery;
import fr.inria.ilda.simbad.Tabs;
import fr.inria.ilda.simbad.AstroObject;

public class MVEventListener implements ViewListener, CameraListener, ComponentListener, PickerListener {

    static final float MAIN_SPEED_FACTOR = 50.0f;

    static final float WHEEL_ZOOMIN_FACTOR = 21.0f;
    static final float WHEEL_ZOOMOUT_FACTOR = 22.0f;

    static float WHEEL_MM_STEP = 1.0f;

    //remember last mouse coords to compute translation  (dragging)
    int lastJPX,lastJPY;
    double lastVX, lastVY;
    Point2D.Double circleCoords;
    int currentJPX, currentJPY;

    FITSOW app;

    // last glyph entered
    Glyph lge;

    boolean panning = false;
    boolean querying = false;
    boolean queryingByCoordinates = false;
    boolean draggingFITS = false;
    boolean draggingSimbadResults = false;
    boolean draggingSimbadInfo = false;
    boolean draggingSimbadCriteria = false;
    // query type selector
    boolean draggingSimbadQTS = false;
    // clear query
    boolean draggingSimbadCQ = false;

    // cursor inside FITS image
    JSkyFitsImage img = null;

    // cursor inside PDF page
    BrowsableDocument ciPDF = null;

    SimbadQuery sq;

    // SimbadCriteria lastSimbadCriteria;

    MVEventListener(FITSOW app){
        this.app = app;
    }

    public void press1(ViewPanel v,int mod,int jpx,int jpy, MouseEvent e){
        lastJPX = jpx;
        lastJPY = jpy;

        if (querying && !insideSimbadCriteria(jpx, jpy) && !insideSimbadQueryTypeSelector(jpx,jpy)){
            SimbadQueryTypeSelector sqts = (SimbadQueryTypeSelector) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SQTS);
            if(sqts.getSelected() == 0){
              if(sq != null)sq.clearQueryRegion();
              sq = new SimbadQuery(app);
              if (app.scene.getActiveFITSImage() != null){
                  sq.setCenter(v.getVCursor().getVSCoordinates(app.dCamera), app.scene.getActiveFITSImage());
              }
              else {
                  sq.setCenter(v.getVCursor().getVSCoordinates(app.zfCamera),
                               (JSkyFitsImage)app.zfSpacePicker.lastGlyphEntered());
              }
          }
        }
        else if(insideSimbadInfo(jpx, jpy)){
          draggingSimbadInfo = true;
          draggingFITS = false;
          draggingSimbadResults = false;
          draggingSimbadQTS = false;
          draggingSimbadCriteria = false;
          draggingSimbadCQ = false;
        }
        else if(insideSimbadClearQuery(jpx, jpy)){
          draggingSimbadInfo = false;
          draggingSimbadCQ = true;
          draggingFITS = false;
          draggingSimbadResults = false;
          draggingSimbadQTS = false;
          draggingSimbadCriteria = false;
        }
        else if(insideSimbadResults(jpx, jpy)){
          draggingSimbadResults = true;
          draggingFITS = false;
          draggingSimbadQTS = false;
          draggingSimbadInfo = false;
          draggingSimbadCriteria = false;
          draggingSimbadCQ = false;
        }
        else if(insideSimbadCriteria(jpx, jpy)){
          draggingSimbadCriteria = true;
          draggingSimbadInfo = false;
          draggingFITS = false;
          draggingSimbadResults = false;
          draggingSimbadQTS = false;
          draggingSimbadCQ = false;

        }
        else if(insideSimbadQueryTypeSelector(jpx, jpy)){
          draggingSimbadCriteria = false;
          draggingSimbadInfo = false;
          draggingFITS = false;
          draggingSimbadResults = false;
          draggingSimbadQTS = true;
          draggingSimbadCQ = false;

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
        draggingSimbadQTS = false;
        draggingSimbadResults = false;
        draggingSimbadInfo = false;
        draggingSimbadCriteria = false;
        draggingSimbadCQ = false;
        if (draggingFITS){
            app.dSpacePicker.unstickLastGlyph();
            draggingFITS = false;
        }
        if (querying && !insideSimbadQueryTypeSelector(jpx, jpy) &&!insideSimbadCriteria(jpx, jpy)){
          SimbadQueryTypeSelector sqts = (SimbadQueryTypeSelector) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SQTS);
          if(sqts.getSelected() == 0){
          //   exitQueryMode();
          //   if (sq != null){
                if (app.scene.getActiveFITSImage() != null){
                  circleCoords = v.getVCursor().getVSCoordinates(app.dCamera);
                }
          //           sq.querySimbad(v.getVCursor().getVSCoordinates(app.dCamera), app.scene.getActiveFITSImage());
                else{
                  circleCoords = v.getVCursor().getVSCoordinates(app.zfCamera);
                  img = (JSkyFitsImage) app.zfSpacePicker.lastGlyphEntered();
                }
                SimbadCriteria criteria = (SimbadCriteria)SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SC);
                if(criteria.getCoordinatesStr() != null){
                  criteria.cleanParameters();
                }
          //           sq.querySimbad(v.getVCursor().getVSCoordinates(app.zfCamera),
          //                          (JSkyFitsImage)app.zfSpacePicker.lastGlyphEntered());
          //       sq = null;
          //   }
          }
        }
    }

    public void click1(ViewPanel v,int mod,int jpx,int jpy,int clickNumber, MouseEvent e){
      SimbadQueryTypeSelector sqts = (SimbadQueryTypeSelector) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SQTS);
      SimbadCriteria criteria = (SimbadCriteria)SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SC);
      SimbadResults list = (SimbadResults) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SR);
      SimbadInfo info = (SimbadInfo) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_BINFO);
      SimbadClearQuery cq = (SimbadClearQuery) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SCQ);
      if(criteria != null && criteria.coordInsideItem(jpx,jpy)){
        updateSimbadCriteriaTabs(jpx, jpy, criteria);
        updateSimbadCriteria(jpx, jpy, criteria);
      }
      else if(sqts != null && sqts.coordInsideItem(jpx, jpy)){
        updateSimbadQueryTypeSelector(jpx, jpy, sqts);
      }
      else if(info != null && info.coordInsideItem(jpx, jpy)){
        updateSimbadInfoTabs(jpx, jpy, info);
      }
      else if(list!=null && list.coordInsideItem(jpx, jpy)){
        updateSimbadResults(jpx, jpy, list);
      }
      else if(cq!= null && cq.getClearButton().coordInsideP(jpx, jpy, app.sqCamera)){
        updateSimbadClearQuery(jpx, jpy);
      }
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
        if (app.scene.getActiveFITSImage() != null){
            app.scene.updateWCSCoordinates(dvsCoords.x, dvsCoords.y);
        }
        else {
            try {
                app.scene.updateWCSCoordinates(zvsCoords.x, zvsCoords.y);
                //XXX have to make this one the active img: (JSkyFitsImage)app.zfSpacePicker.lastGlyphEntered()
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
        else if(draggingSimbadCQ){
          SimbadClearQuery cq = (SimbadClearQuery) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SCQ);
          cq.move(jpx-lastJPX, lastJPY-jpy);
          lastJPX = jpx;
          lastJPY = jpy;
        }
        else if(draggingSimbadResults){
          SimbadResults list = (SimbadResults) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SR);
          list.move(jpx-lastJPX, lastJPY-jpy);
          lastJPX = jpx;
          lastJPY = jpy;
        }
        else if(draggingSimbadQTS){
          SimbadQueryTypeSelector qts = (SimbadQueryTypeSelector) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SQTS);
          qts.move(jpx-lastJPX, lastJPY-jpy);
          lastJPX = jpx;
          lastJPY = jpy;
        }
        else if(draggingSimbadInfo){
          SimbadInfo info = (SimbadInfo) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_BINFO);
          info.move(jpx-lastJPX, lastJPY-jpy);
          info.getBasicData().move(jpx-lastJPX, lastJPY-jpy);
          info.getMeasurements().move(jpx-lastJPX, lastJPY-jpy);

          lastJPX = jpx;
          lastJPY = jpy;
        }
        else if(draggingSimbadCriteria){
          SimbadCriteria criteria = (SimbadCriteria)SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SC);
          criteria.move(jpx-lastJPX, lastJPY-jpy);
          criteria.getBasicData().move(jpx-lastJPX, lastJPY-jpy);
          criteria.getMeasurements().move(jpx-lastJPX, lastJPY-jpy);

          lastJPX = jpx;
          lastJPY = jpy;
        }
        else if (querying && sq != null){
          app.mView.setActiveLayer(FITSOW.DATA_LAYER);
          sq.setRadius(v.getVCursor().getVSCoordinates((app.scene.getActiveFITSImage() != null) ? app.dCamera : app.zfCamera));
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
            app.scene.setActiveFITSImage((JSkyFitsImage)g);
        }
        else if (g.getType().equals(Config.T_PDF)){
            ciPDF = (BrowsableDocument)g;
        }
    }

    public void exitGlyph(Glyph g){
        g.highlight(false, null);
        if (g.getType().equals(Config.T_FITS)){
            Glyph[] insideOtherFITS = app.dSpacePicker.getPickedGlyphList(Config.T_FITS);
            if (insideOtherFITS.length > 0){
                app.scene.setActiveFITSImage((JSkyFitsImage)insideOtherFITS[insideOtherFITS.length-1]);
            }
            // else {
            //     app.scene.setActiveFITSImage(null);
            // }
        }
        else if (g.getType().equals(Config.T_PDF)){
            Glyph[] insideOtherPDF = app.dSpacePicker.getPickedGlyphList(Config.T_PDF);
            if (insideOtherPDF.length > 0){
                ciPDF = (BrowsableDocument)insideOtherPDF[insideOtherPDF.length-1];
            }
        }
    }

    public void Kpress(ViewPanel v,char c,int code,int mod, KeyEvent e){
        if (code==KeyEvent.VK_PAGE_UP){
            if (ciPDF != null){
                app.pdfL.goToPreviousPage(ciPDF);
            }
            else {
                app.nav.getHigherView(app.zfCamera);
            }
        }
        else if (code==KeyEvent.VK_PAGE_DOWN){
            if (ciPDF != null){
                app.pdfL.goToNextPage(ciPDF);
            }
            else {
                app.nav.getLowerView(app.zfCamera);
            }
        }
        else if (code==KeyEvent.VK_HOME){app.nav.getGlobalView(null);}
        else if (code==KeyEvent.VK_UP){app.nav.translateView(app.zfCamera, Navigation.MOVE_UP);}
        else if (code==KeyEvent.VK_DOWN){app.nav.translateView(app.zfCamera, Navigation.MOVE_DOWN);}
        else if (code==KeyEvent.VK_LEFT){app.nav.translateView(app.zfCamera, Navigation.MOVE_LEFT);}
        else if (code==KeyEvent.VK_RIGHT){app.nav.translateView(app.zfCamera, Navigation.MOVE_RIGHT);}
        else if (code==KeyEvent.VK_F1){
            app.meh.setSelectedFITSImage(app.dSpacePicker.getPickedGlyphList(Config.T_FITS));
            app.scene.selectPrevColorMapping(app.meh.selectedFITSImage);
        }
        else if (code==KeyEvent.VK_F2){
            app.meh.setSelectedFITSImage(app.dSpacePicker.getPickedGlyphList(Config.T_FITS));
            app.scene.selectNextColorMapping(app.meh.selectedFITSImage);
        }
        // else if (code==KeyEvent.VK_D){
        //     Glyph[] aos = app.dSpacePicker.getPickedGlyphList(Config.T_ASTRO_OBJ_LB);
        //     if (aos.length > 0){
        //         AstroObject ao = (AstroObject)aos[0].getOwner();
        //         ao.displayBibRefs();
        //     }
        // }
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
        SimbadQueryTypeSelector ts = new SimbadQueryTypeSelector(app.sqSpace);
        app.sqSpace.addGlyph(ts);
        app.mView.setActiveLayer(FITSOW.DATA_LAYER);
        app.scene.setStatusBarMessage("Select query parameters");
    }

    void exitQueryMode(){
      SimbadQueryTypeSelector qts = (SimbadQueryTypeSelector) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SQTS);
      app.scene.setStatusBarMessage("");
      app.sqSpace.removeGlyph(qts);
      SimbadCriteria criteria = (SimbadCriteria) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SC);
      if(criteria != null){
        SimbadCriteria.lastSimbadCriteria = criteria;
        app.sqSpace.removeGlyph(criteria.getBasicData());
        app.sqSpace.removeGlyph(criteria.getMeasurements());
        app.sqSpace.removeGlyph(criteria);
      }
      app.scene.setStatusBarMessage(null);
      querying = false;
      if(sq != null)sq.clearQueryRegion();
    }

    void updateSimbadClearQuery(int jpx, int jpy){
      sq = new SimbadQuery(app);
      sq.clearQueryResults();
      sq = null;
    }

    void updateSimbadQueryTypeSelector(int jpx, int jpy, SimbadQueryTypeSelector sqts){
      int selectedButtonIndex = sqts.getSelectedButton(jpx, jpy, app.sqCamera);
        sqts.select(selectedButtonIndex);
        SimbadCriteria current = (SimbadCriteria) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SC);
        if(current!=null){
          if(current.getBasicData()!= null) app.sqSpace.removeGlyph(current.getBasicData());
          if(current.getMeasurements()!= null) app.sqSpace.removeGlyph(current.getMeasurements());
          app.sqSpace.removeGlyph(current);
        }
        if(sqts.getSelected() == sqts.BY_COORDINATES || sqts.getSelected() == sqts.BY_ID){
          SimbadCriteria sc = new SimbadCriteria(150+455,0,sqts);
          app.sqSpace.addGlyph(sc);
          app.sqSpace.addGlyph(sc.getBasicData());
          SimbadCriteria.lastSimbadCriteria = sc;
        }
        else if(sqts.getSelected() == sqts.BY_SCRIPT){
          JFrame parent = new JFrame();
          JOptionPane optionPane = new JOptionPane("Query by script", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
          String inputValue = JOptionPane.showInputDialog("Enter script for query:");
          sq = new SimbadQuery(app);
          exitQueryMode();
          sq.querySimbadbyScript(inputValue, app.scene.getActiveFITSImage());
          sq = null;

        }
        else if(sqts.getSelected() == sqts.CANCEL){
          exitQueryMode();
        }
    }
    void updateSimbadResults(int jpx, int jpy, SimbadResults list){
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

    void updateSimbadInfoTabs(int jpx, int jpy, SimbadInfo info){
      Tabs tabs = info.getTabs();
      if(tabs.getBasicDataTab().coordInsideP(jpx,jpy,app.sqCamera)){
        tabs.activateBasicDataTab(info.getBackground(), info.getMeasurements(), info.getBasicData());
      }
      else if(tabs.getMeasurementsTab().coordInsideP(jpx,jpy,app.sqCamera)){
        tabs.activateMeasurementsTab(info.getBackground(), info.getMeasurements(), info.getBasicData());
      }
    }

    void updateSimbadCriteriaTabs(int jpx, int jpy, SimbadCriteria criteria){
      Tabs tabs = criteria.getTabs();
      if(tabs.getBasicDataTab().coordInsideP(jpx,jpy,app.sqCamera)){
        tabs.activateBasicDataTab(criteria.getBackground(), criteria.getMeasurements(), criteria.getBasicData());
      }
      else if(tabs.getMeasurementsTab().coordInsideP(jpx,jpy,app.sqCamera)){
        tabs.activateMeasurementsTab(criteria.getBackground(), criteria.getMeasurements(), criteria.getBasicData());
      }
    }

    void updateSimbadCriteria(int jpx, int jpy, SimbadCriteria criteria){
        Tabs tabs = criteria.getTabs();
        Point2D.Double coords = new Point2D.Double();
        app.mView.fromPanelToVSCoordinates(jpx,jpy,app.sqCamera,coords);
        double x = coords.getX();
        double y = coords.getY();
        if(tabs.getTabSelected().equals(tabs.getMeasurementsStr())){
          criteria.getMeasurements().select(criteria.getMeasurements().getItemSelected(x,y),"");
        }
        else if(tabs.getTabSelected().equals(tabs.getBasicDataStr())){
          criteria.updateQueryParameters(x, y);
          if(criteria.getCoordinatesStr() != null){
            if(sq!=null){
              app.dSpace.removeGlyph(sq.getQueryRegion());
            }
            circleCoords = null;
            sq = null;
          }

          if(criteria.getExecuteButton().coordInsideP(jpx,jpy,app.sqCamera)){
            SimbadQueryTypeSelector ts = (SimbadQueryTypeSelector) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SQTS);
            if(ts.getSelected() == ts.BY_ID){
              sq = new SimbadQuery(app);
              exitQueryMode();
            //   sq.querySimbadbyId("2MASS J18184834-1349146", app.scene.getActiveFITSImage());
              sq.querySimbadbyId(criteria.getIdStr(), app.scene.getActiveFITSImage());
              sq = null;
              return;
            }
            else if(ts.getSelected() == ts.BY_COORDINATES){
              exitQueryMode();
              if (sq != null && circleCoords!= null){
                if (app.scene.getActiveFITSImage() != null){
                    sq.querySimbad(circleCoords, app.scene.getActiveFITSImage());
                }
                else {
                    sq.querySimbad(circleCoords, img);
                }
                sq = null;
                return;
              }
              else if(sq == null && app.scene.getActiveFITSImage()!=null){
                sq = new SimbadQuery(app);
                sq.querySimbadbyCoordinates(criteria.getCoordinatesStr(), app.scene.getActiveFITSImage());
                sq = null;
                return;
              }
            }
          }

          if(criteria.getCancelButton().coordInsideP(jpx,jpy,app.sqCamera)){
            exitQueryMode();
          }

          if(criteria.getObjectTypeFilter().coordInsideItem(jpx, jpy)){
          criteria.getObjectTypeFilter().select(criteria.getObjectTypeFilter().getItemSelected(x,y),"");
          }
          else if(criteria.getPMFilter().coordInsideItem(jpx, jpy)){
            int angle = criteria.getPMFilter().getItemSelected(x,y);
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
          else if(criteria.getParallaxFilter().coordInsideItem(jpx,jpy)){
            int parallax = criteria.getParallaxFilter().getItemSelected(x,y);
            String inputValue ="";
            if(parallax == 0){
              JFrame parent = new JFrame();
              JOptionPane optionPane = new JOptionPane("parallax (mas)", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
              inputValue = JOptionPane.showInputDialog("Enter parallax (mas) in the format:\n!=/=/>=/<= numrical-value");
            }
            criteria.getParallaxFilter().select(parallax, inputValue);
          }
          else if(criteria.getRVFilter().coordInsideItem(jpx,jpy)){
            int value = criteria.getRVFilter().getItemSelected(x,y);
            String inputValue="";
            JFrame parent = new JFrame();
            JOptionPane optionPane;
            if(value == 0){
              optionPane = new JOptionPane("Radial velocity (km/s) ", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
              inputValue = JOptionPane.showInputDialog("Enter radial velocity (km/s) in the format:\n >=/<=/=/!= numrical-value");
            }
            else if(value == 1){
              optionPane = new JOptionPane("Redshift ", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
              inputValue = JOptionPane.showInputDialog("Enter redshift (km/s) in the format:\n >=/<=/=/!= numrical-value");
            }
            else if(value ==2){
              optionPane = new JOptionPane("cz ", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
              inputValue = JOptionPane.showInputDialog("Enter cz in the format:\n >=/<=/=/!= numrical-value");
            }
            criteria.getRVFilter().select(value,inputValue);
          }
          else if(criteria.getSTFilter().coordInsideItem(jpx,jpy)){
            int value = criteria.getSTFilter().getItemSelected(x,y);
            String inputValue="";
            JFrame parent = new JFrame();
            JOptionPane optionPane;
            if(value == 0){
              optionPane = new JOptionPane("Spectral type ", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
              inputValue = JOptionPane.showInputDialog("Enter spectral type in the format:\n >=/<=/>/</!=/= value ");
            }
            else if(value == 1){
              optionPane = new JOptionPane("Luminosity class ", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
              inputValue = JOptionPane.showInputDialog("Enter luminosity value in the format:\n >=/<=/>/</!=/= value ");
            }
            else if(value ==2){
              optionPane = new JOptionPane("Peculiarities ", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
              inputValue = JOptionPane.showInputDialog("Enter Peculiarities in the format:\n =/!= numrical-value");
            }
            criteria.getSTFilter().select(value,inputValue);
          }
          else if(criteria.getFluxFilter().coordInsideItem(jpx,jpy)){
            int value = criteria.getFluxFilter().getItemSelected(x,y);
            String inputValue="";
            JFrame parent = new JFrame();
            JOptionPane optionPane;
            if(value%2!=0 && value<25 && value >0){
              optionPane = new JOptionPane(Config.FLUX_TYPES[value/2]+" Range", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
              inputValue = JOptionPane.showInputDialog("Enter range of "+Config.FLUX_TYPES[value/2]+" magnitude in the format:\n =/!= value");
            }
            criteria.getFluxFilter().select(value, inputValue);
          }
        }
    }

    boolean insideSimbadResults(int jpx, int jpy){
      SimbadResults list = (SimbadResults) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SR);
      if(list!= null) return list.getBackground().coordInsideP(jpx, jpy, app.sqCamera);
      return false;
    }

    boolean insideSimbadClearQuery(int jpx, int jpy){
      SimbadClearQuery cq = (SimbadClearQuery) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SCQ);
      if(cq!= null) return cq.getBackground().coordInsideP(jpx, jpy, app.sqCamera);
      return false;
    }

    boolean insideSimbadInfo(int jpx, int jpy){
      SimbadInfo info = (SimbadInfo) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_BINFO);
      if(info != null)
        return info.coordInsideItem(jpx, jpy);
      return false;
    }

    boolean insideSimbadCriteria(int jpx, int jpy){
      SimbadCriteria criteria = (SimbadCriteria) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SC);
      if(criteria != null)
        return criteria.coordInsideItem(jpx, jpy);
      return false;
    }

    boolean insideSimbadQueryTypeSelector(int jpx, int jpy){
      SimbadQueryTypeSelector ts = (SimbadQueryTypeSelector) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SQTS);
      if(ts != null)
        return ts.coordInsideItem(jpx, jpy);
      return false;
    }
}
