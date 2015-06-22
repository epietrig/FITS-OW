/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import fr.inria.zvtm.engine.Java2DPainter;
import fr.inria.zvtm.engine.SwingWorker;
import fr.inria.zvtm.glyphs.JSkyFitsImage;
import fr.inria.zvtm.glyphs.VCircle;
import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VCross;
import fr.inria.zvtm.glyphs.Translucent;
import fr.inria.zvtm.widgets.TranslucentWidget;
import fr.inria.zuist.engine.SceneManager;
import fr.inria.zuist.engine.ObjectDescription;
import fr.inria.zuist.engine.JSkyFitsImageDescription;
import fr.inria.zuist.engine.JSkyFitsResourceHandler;
import fr.inria.zuist.event.ProgressListener;

import fr.inria.ilda.simbad.AstroObject;
import fr.inria.ilda.simbad.SimbadCatQuery;

import jsky.coords.WorldCoords;

public class FITSScene implements Java2DPainter {

    File SCENE_FILE, SCENE_FILE_DIR;

    FITSOW app;
    SceneManager sm;

    String zuistColorMapping = Config.COLOR_MAPPING_LIST[0];
    String zuistScale = Config.SCALE_LINEAR;

    FITSServer server;

    static final String EMPTY_STRING = "";
    String wcsStr = EMPTY_STRING;
    String sbMsg = null;

    FITSScene(FITSOW app, String fitsDir, String ip, int port){
        this.app = app;
        this.sm = app.sm;
        System.out.println("Initializing NanoHTTPD Server ("+ip+":"+port+")...");
        server = new FITSServer(app, fitsDir, ip, port);
        try {
            server.start();
            System.out.println("OK");
        }
        catch (IOException ex){
            System.out.println("Failed");
        }
    }

    void loadScene(File xmlSceneFile, ProgressListener pl){
        SCENE_FILE = xmlSceneFile;
        SCENE_FILE_DIR = SCENE_FILE.getParentFile();
        sm.loadScene(SceneManager.parseXML(SCENE_FILE), SCENE_FILE_DIR, true, pl);
        HashMap sceneAttributes = sm.getSceneAttributes();
        if (sceneAttributes.containsKey(SceneManager._background)){
            app.mView.setBackgroundColor((Color)sceneAttributes.get(SceneManager._background));
            // clusteredView.setBackgroundColor((Color)sceneAttributes.get(SceneManager._background));
        }
        // mCamera.setAltitude(0.0f);
    }

    void loadImage(URL url){
        JSkyFitsImage img = new JSkyFitsImage(url);
        img.setType(Config.T_FITS);
        addImage(img);
    }

    void loadImage(String fitsFileName){
        // assumes that the file is in the FITS dir served with NanoHTTPD
        String urlS = "http://" + Config.HTTPD_IP + ":" + Config.HTTPD_PORT + "/" + fitsFileName;
        try {
            URL fitsURL = new URL(urlS);
            //System.out.println("Fetching "+fitsURL);
            loadImage(fitsURL);
        }
        catch (MalformedURLException mue){
            System.out.println("Error loading FITS image from " + urlS);
        }
    }

    void addImage(JSkyFitsImage img){
        if (img != null){
            app.dSpace.addGlyph(img);
            img.setColorLookupTable(Config.DEFAULT_COLOR_LOOKUP_TABLE, false);
            img.setScaleAlgorithm(Config.DEFAULT_SCALE, false);
            img.updateDisplayedImage();
            img.setDrawBorder(true);
            img.setBorderColor(Config.FITS_IMG_BORDER_COLOR);
            img.setCursorInsideHighlightColor(Config.FITS_IMG_BORDER_COLOR_CI);
            // menu.buildHistogram();
        }
    }

    /* ---------------- Scale ---------------------- */

    public void setScale(JSkyFitsImage img, String scale){
        if (img != null){
            img.setScaleAlgorithm(Config.SCALES.get(scale), true);
        }
        else {
            JSkyFitsImage.ScaleAlgorithm sa = Config.SCALES.get(scale);
            for (ObjectDescription desc:app.sm.getObjectDescriptions()){
                if (desc.getType() == JSkyFitsResourceHandler.RESOURCE_TYPE_FITS){
                    ((JSkyFitsImageDescription)desc).setScaleAlgorithm(sa, true);
                }
            }
        }
    }

    /* ---------------- Color mapping ---------------------- */

    public void setColorMapping(JSkyFitsImage img, String clt){
        if (img != null){
            img.setColorLookupTable(clt, true);
        }
        else {
            for (ObjectDescription desc:app.sm.getObjectDescriptions()){
                if (desc.getType() == JSkyFitsResourceHandler.RESOURCE_TYPE_FITS){
                    ((JSkyFitsImageDescription)desc).setColorLookupTable(clt, true);
                }
            }
        }
    }

    String getCurrentCLT(JSkyFitsImage img){
        String currentCLT = Config.DEFAULT_COLOR_LOOKUP_TABLE;
        if (img != null){
            currentCLT = img.getColorLookupTable();
        }
        else {
            for (ObjectDescription desc:app.sm.getObjectDescriptions()){
                if (desc.getType() == JSkyFitsResourceHandler.RESOURCE_TYPE_FITS){
                    currentCLT = ((JSkyFitsImageDescription)desc).getColorLookupTable();
                    break;
                }
            }
        }
        return currentCLT;
    }

    public String selectNextColorMapping(JSkyFitsImage img){
        String currentCLT = getCurrentCLT(img);
        int ci = 0;
        for (int i=0;i<Config.COLOR_MAPPING_LIST.length;i++){
            if (Config.COLOR_MAPPING_LIST[i].equals(currentCLT)){
                ci = i;
                break;
            }
        }
        ci = (ci >= Config.COLOR_MAPPING_LIST.length-1) ? 0 : ci + 1;
        setColorMapping(img, Config.COLOR_MAPPING_LIST[ci]);
        return Config.COLOR_MAPPING_LIST[ci];
    }

    public String selectPrevColorMapping(JSkyFitsImage img){
        String currentCLT = getCurrentCLT(img);
        int ci = 0;
        for (int i=0;i<Config.COLOR_MAPPING_LIST.length;i++){
            if (Config.COLOR_MAPPING_LIST[i].equals(currentCLT)){
                ci = i;
                break;
            }
        }
        ci = (ci <= 0) ? Config.COLOR_MAPPING_LIST.length-1 : ci - 1;
        setColorMapping(img, Config.COLOR_MAPPING_LIST[ci]);
        return Config.COLOR_MAPPING_LIST[ci];
    }

    /* ----------------------- WCS coordinates --------------------- */

    void updateWCSCoordinates(double vx, double vy, JSkyFitsImage img){
        if (img != null){
            Point2D.Double wcs = img.vs2wcs(vx, vy);
            wcsStr = wcs.x + " " + wcs.y;
            app.mView.repaint();
        }
        else {
            wcsStr = EMPTY_STRING;
        }
    }

    /* ----------------------- Status bar --------------------- */

    void querySimbad(Point2D.Double center, Point2D.Double onCircle, final JSkyFitsImage img){
        if (img == null){return;}
        Point2D.Double centerWCS = img.vs2wcs(center.x, center.y);
        Point2D.Double onCircleWCS = img.vs2wcs(onCircle.x, onCircle.y);
        //compute radius in arcmin
        final WorldCoords wc = new WorldCoords(centerWCS.getX(), centerWCS.getY());
        WorldCoords wcDummy = new WorldCoords(onCircleWCS.getX(), onCircleWCS.getY());
        final double distArcMin = wc.dist(wcDummy);
        //perform catalog query
        String queryInfo = "Querying Simbad at " + wc + " with a radius of " + Config.ARCMIN_FORMATTER.format(distArcMin) + " arcminutes";
        app.scene.setStatusBarMessage(queryInfo);
        // symbolSpace.removeAllGlyphs();
        new SwingWorker(){
            @Override public List<AstroObject> construct(){
                List<AstroObject> objs = null;
                try{
                    objs = SimbadCatQuery.makeSimbadCoordQuery(wc.getRaDeg(), wc.getDecDeg(), distArcMin);
                } catch(IOException ioe){
                    ioe.printStackTrace();
                } finally {
                    return objs;
                }
            }
            @Override public void finished(){
                List<AstroObject> objs = (List<AstroObject>)get();
                displayQueryResults(objs, img);
                app.eh.fadeOutQueryRegion();
            }
        }.start();
    }

    void displayQueryResults(List<AstroObject> objs, JSkyFitsImage img){
        for(AstroObject obj: objs){
            Point2D.Double p = img.wcs2vs(obj.getRa(), obj.getDec());
            VCross cr = new VCross(p.x, p.y, 100, 10, 10, Config.SIMBAD_AO_COLOR, Color.WHITE, .8f);
            VText lb = new VText(p.x+10, p.y+10, 101, Config.SIMBAD_AO_COLOR, obj.getIdentifier(), VText.TEXT_ANCHOR_START);
            app.dSpace.addGlyph(cr);
            app.dSpace.addGlyph(lb);
            cr.setStroke(Config.SIMBAD_AO_STROKE);
            lb.setBorderColor(Config.SIMBAD_AO_BACKGROUND);
            lb.setTranslucencyValue(Config.SIMBAD_AO_ALPHA);
            cr.setOwner(obj);
            lb.setOwner(obj);
            img.stick(cr);
            img.stick(lb);
            cr.setType(Config.T_ASTRO_OBJ_CR);
            lb.setType(Config.T_ASTRO_OBJ_LB);
        }
    }

    /* ----------------------- Status bar --------------------- */

    public void setStatusBarMessage(String s){
        sbMsg = s;
        app.mView.repaint();
    }

    public void	paint(Graphics2D g2d, int viewWidth, int viewHeight){
        g2d.setColor(Config.INFO_BAR_BACKGROUND);
        g2d.setComposite(TranslucentWidget.AB_08);
        g2d.fillRect(0, 0, viewWidth, Config.INFO_BAR_HEIGHT);
        g2d.setColor(Config.INFO_BAR_FOREGROUND);
        g2d.setComposite(Translucent.acO);
        g2d.drawString(wcsStr, 4, 10);
        if (sbMsg != null){
            g2d.drawString(sbMsg, viewWidth/2, 10);
        }
    }

}
