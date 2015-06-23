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
import fr.inria.zuist.engine.ObjectDescription;
import fr.inria.zuist.engine.TextDescription;

class MVEventListener implements ViewListener, CameraListener, ComponentListener, PickerListener {

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

    // cursor inside FITS image
    JSkyFitsImage ciFITSImage = null;

    SimbadQuery sq;

    MVEventListener(FITSOW app){
        this.app = app;
    }

    public void press1(ViewPanel v,int mod,int jpx,int jpy, MouseEvent e){
        lastJPX = jpx;
        lastJPY = jpy;
        if (querying){
            sq = new SimbadQuery(app);
            if (ciFITSImage != null){
                sq.setCenter(v.getVCursor().getVSCoordinates(app.dCamera));
            }
            else {
                sq.setCenter(v.getVCursor().getVSCoordinates(app.zfCamera));
            }
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
        if (draggingFITS){
            app.dSpacePicker.unstickLastGlyph();
            draggingFITS = false;
        }
        if (querying){
            exitQueryMode();
            if (sq != null){
                if (ciFITSImage != null){
                    sq.querySimbad(v.getVCursor().getVSCoordinates(app.dCamera), ciFITSImage);
                }
                else {
                    sq.querySimbad(v.getVCursor().getVSCoordinates(app.zfCamera), (JSkyFitsImage)app.zfSpacePicker.lastGlyphEntered());
                }
                sq = null;
            }
        }
    }

    public void click1(ViewPanel v,int mod,int jpx,int jpy,int clickNumber, MouseEvent e){}

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
        updateZUISTSpacePicker(jpx, jpy);
        updateDataSpacePicker(jpx, jpy);
        if (ciFITSImage != null){
            app.scene.updateWCSCoordinates(dvsCoords.x, dvsCoords.y, ciFITSImage);
        }
        else {
            app.scene.updateWCSCoordinates(zvsCoords.x, zvsCoords.y, (JSkyFitsImage)app.zfSpacePicker.lastGlyphEntered());
        }
    }

    public void mouseDragged(ViewPanel v, int mod, int buttonNumber, int jpx, int jpy, MouseEvent e){
        currentJPX = jpx;
        currentJPY = jpy;
        if (panning){
            app.nav.pan(app.zfCamera, lastJPX-jpx, jpy-lastJPY, 1);
            lastJPX = jpx;
            lastJPY = jpy;
        }
        else if (querying && sq != null){
            sq.setRadius(v.getVCursor().getVSCoordinates((ciFITSImage != null) ? app.dCamera : app.zfCamera));
        }
        else {
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
        app.zfSpacePicker.computePickedGlyphList(app.zfCamera);
    }

    void updateDataSpacePicker(int jpx, int jpy){
        app.mView.fromPanelToVSCoordinates(jpx, jpy, app.dCamera, dvsCoords);
        app.dSpacePicker.setVSCoordinates(dvsCoords.x, dvsCoords.y);
        app.dSpacePicker.computePickedGlyphList(app.dCamera);
    }

    /*------------------ Simbad -------------------------*/

    void enterQueryMode(){
        querying = true;
        app.mView.setActiveLayer(FITSOW.DATA_LAYER);
        app.scene.setStatusBarMessage("Select region to query:");
    }

    void exitQueryMode(){
        app.scene.setStatusBarMessage(null);
        querying = false;
    }

}
