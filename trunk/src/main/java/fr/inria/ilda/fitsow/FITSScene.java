/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import javax.swing.SwingUtilities;

import java.util.HashMap;
import java.util.LinkedHashMap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import fr.inria.zvtm.engine.Java2DPainter;
import fr.inria.zvtm.engine.SwingWorker;
import fr.inria.zvtm.glyphs.Glyph;
import fr.inria.zvtm.glyphs.JSkyFitsImage;
import fr.inria.zvtm.glyphs.Translucent;
import fr.inria.zvtm.widgets.TranslucentWidget;
import fr.inria.zuist.engine.SceneManager;
import fr.inria.zuist.engine.SceneBuilder;
import fr.inria.zuist.od.ObjectDescription;
import fr.inria.zuist.od.JSkyFitsImageDescription;
import fr.inria.zuist.engine.JSkyFitsResourceHandler;
import fr.inria.zuist.engine.Level;
import fr.inria.zuist.event.ProgressListener;
import fr.inria.zvtm.event.PickerListener;

import fr.inria.zvtm.fits.CoordConv;

public class FITSScene implements Java2DPainter, PickerListener {

    File SCENE_FILE, SCENE_FILE_DIR;

    FITSOW app;
    SceneManager sm;

    String zuistCLT = Config.DEFAULT_COLOR_LOOKUP_TABLE;
    JSkyFitsImage.ScaleAlgorithm zuistScale = Config.DEFAULT_SCALE;
    double[] globalMinMax = {0,0};

    FITSServer server;

    static CoordConv cc;

    static final String EMPTY_STRING = "";
    String wcsStr = EMPTY_STRING;
    String sbMsg = null;

    // used to show previews of what a change in scale algo or color lookup table will be like
    // on representative images from the ZUIST scene
    JSkyFitsImage[] fitsThumbs;
    String previewedCLT = Config.DEFAULT_COLOR_LOOKUP_TABLE;
    JSkyFitsImage.ScaleAlgorithm previewedScale = Config.DEFAULT_SCALE;

    double thCumulatedWidth = 0;
    double thMaxHeight = 0;

    JSkyFitsImage activeFITSimg = null;

    FITSScene(FITSOW app, String fitsDir, String ip, int port){
        this.app = app;
        this.sm = app.sm;
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                cc = new CoordConv();
            }
        });
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
        sm.loadScene(SceneBuilder.parseXML(SCENE_FILE), SCENE_FILE_DIR, true, pl);
        HashMap sceneAttributes = sm.getSceneAttributes();
        if (sceneAttributes.containsKey(SceneBuilder._background)){
            app.mView.setBackgroundColor((Color)sceneAttributes.get(SceneBuilder._background));
            // clusteredView.setBackgroundColor((Color)sceneAttributes.get(SceneBuilder._background));
        }
        // mCamera.setAltitude(0.0f);
        setupThumbnails();
    }

    void setupThumbnails(){
        // root tile
        ObjectDescription od = sm.getRegionsAtLevel(0)[0].getObjectsInRegion()[0];
        URL overviewTileURL = null;
        if (od.getType().equals(JSkyFitsResourceHandler.RESOURCE_TYPE_FITS)){
            JSkyFitsImageDescription jsid = ((JSkyFitsImageDescription)od);
            overviewTileURL = jsid.getSrc();
            globalMinMax = jsid.getGlobalScaleParams();
        }
        // tile at deepest level, close to the center of the full image
        Level deepestLevel = sm.getLevel(sm.getLevelCount()-1);
        double[] wnes = deepestLevel.getBounds();
        // XXX 512 is a hack to prevent selecting root region when it spans all levels
        od = deepestLevel.getClosestRegion(
                            new Point2D.Double((wnes[0]+wnes[2])/2d+512,
                                               (wnes[1]+wnes[3])/2d+512)).getObjectsInRegion()[0];
        URL detailTileURL = null;
        if (od.getType().equals(JSkyFitsResourceHandler.RESOURCE_TYPE_FITS)){
            detailTileURL = ((JSkyFitsImageDescription)od).getSrc();
        }
        loadThumbnails(new URL[]{overviewTileURL, detailTileURL});
    }

    void loadThumbnails(URL[] thumbURLs){
        fitsThumbs = new JSkyFitsImage[thumbURLs.length];
        for (int i=0;i<thumbURLs.length;i++){
            fitsThumbs[i] = new JSkyFitsImage(0, 0, Config.Z_FITS_IMG, thumbURLs[i]);
            fitsThumbs[i].setType(Config.T_FITS);
            app.mnSpace.addGlyph(fitsThumbs[i]);
            fitsThumbs[i].setCutLevels(globalMinMax[0], globalMinMax[1], false);
            fitsThumbs[i].setVisible(false);
            fitsThumbs[i].setColorLookupTable(zuistCLT, false);
            fitsThumbs[i].setScaleAlgorithm(zuistScale, false);
            fitsThumbs[i].updateDisplayedImage();
            fitsThumbs[i].setDrawBorder(true);
            fitsThumbs[i].setBorderColor(Config.FITS_IMG_BORDER_COLOR);
            thCumulatedWidth += fitsThumbs[i].getWidth()+Config.FITS_THUMB_MARGIN;
            if (fitsThumbs[i].getHeight() > thMaxHeight){
                thMaxHeight = fitsThumbs[i].getHeight();
            }
        }
    }

    void moveThumbnails(double ox, double oy){
        // adjust layout of thumbnails on top of color menu
        double vx = ox -thCumulatedWidth /2d + Config.FITS_THUMB_MARGIN/2d;
        for (int i=0;i<fitsThumbs.length;i++){
            fitsThumbs[i].moveTo(vx+fitsThumbs[i].getWidth()/2d,
                                 oy + thMaxHeight/2d);
            vx += fitsThumbs[i].getWidth() + Config.FITS_THUMB_MARGIN;
        }
    }

    void showThumbnails(double ox, double oy){
        if (fitsThumbs != null){
            moveThumbnails(ox, oy);
            for (JSkyFitsImage img:fitsThumbs){
                img.setVisible(true);
            }
        }
    }

    void hideThumbnails(){
        if (fitsThumbs != null){
            for (JSkyFitsImage img:fitsThumbs){
                img.setVisible(false);
            }
        }
    }

    void loadImage(URL url, double vx, double vy){
        JSkyFitsImage img = new JSkyFitsImage(vx, vy, Config.Z_FITS_IMG, url);
        img.setType(Config.T_FITS);
        addImage(img);
    }

    void loadImages(String fitsFileNames){
        // expects a String of file names, comma-separated
        String[] ffns = fitsFileNames.split(",");
        for (String ffn:ffns){
            loadImage(ffn);
        }
    }

    void loadImage(String fitsFileName){
        // assumes that the file is in the local data dir served with NanoHTTPD
        String urlS = "http://" + Config.HTTPD_IP + ":" + Config.HTTPD_PORT + "/" + fitsFileName;
        try {
            URL fitsURL = new URL(urlS);
            //System.out.println("Fetching "+fitsURL);
            loadImage(fitsURL, 0, 0);
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

    public void setScale(JSkyFitsImage img, JSkyFitsImage.ScaleAlgorithm sa){
        if (img != null){
            // doing it on a specific FITS image
            img.setScaleAlgorithm(sa, true);
        }
        else {
            // doing it on the background ZUIST scene
            // do it on the thumbnail previews only,
            // the new scale will be applied to the tiles only when the change is confirmed
            previewedScale = sa;
            if (fitsThumbs != null){
                for (JSkyFitsImage timg:fitsThumbs){
                    timg.setScaleAlgorithm(sa, true);
                }
            }
        }
    }

    public void applyScaleToZuistTiles(){
        new SwingWorker(){
            @Override public Object construct(){
                for (ObjectDescription desc:sm.getObjectDescriptions()){
                    if (desc.getType() == JSkyFitsResourceHandler.RESOURCE_TYPE_FITS){
                        ((JSkyFitsImageDescription)desc).setScaleAlgorithm(previewedScale, true);
                    }
                }
                return null;
            }
        }.start();
    }

    public JSkyFitsImage.ScaleAlgorithm getCurrentScale(JSkyFitsImage img){
        JSkyFitsImage.ScaleAlgorithm currentSA = Config.DEFAULT_SCALE;
        if (img != null){
            currentSA = img.getScaleAlgorithm();
        }
        else {
            // for (ObjectDescription desc:sm.getObjectDescriptions()){
            //     if (desc.getType() == JSkyFitsResourceHandler.RESOURCE_TYPE_FITS){
            //         currentCLT = ((JSkyFitsImageDescription)desc).getColorLookupTable();
            //         break;
            //     }
            // }
            currentSA = previewedScale;
        }
        return currentSA;
    }

    /* ---------------- Color mapping ---------------------- */

    public void setColorMapping(JSkyFitsImage img, String clt){
        if (img != null){
            // doing it on a specific FITS image
            img.setColorLookupTable(clt, true);
        }
        else {
            // doing it on the background ZUIST scene
            // do it on the thumbnail previews only,
            // the new CLT will be applied to the tiles only when the change is confirmed
            previewedCLT = clt;
            if (fitsThumbs != null){
                for (JSkyFitsImage timg:fitsThumbs){
                    timg.setColorLookupTable(clt, true);
                }
            }
        }
    }

    public void applyCLTToZuistTiles(){
        new SwingWorker(){
            @Override public Object construct(){
                for (ObjectDescription desc:sm.getObjectDescriptions()){
                    if (desc.getType() == JSkyFitsResourceHandler.RESOURCE_TYPE_FITS){
                        ((JSkyFitsImageDescription)desc).setColorLookupTable(previewedCLT, true);
                    }
                }
                return null;
            }
        }.start();
    }

    public String getCurrentCLT(JSkyFitsImage img){
        String currentCLT = Config.DEFAULT_COLOR_LOOKUP_TABLE;
        if (img != null){
            currentCLT = img.getColorLookupTable();
        }
        else {
            // for (ObjectDescription desc:sm.getObjectDescriptions()){
            //     if (desc.getType() == JSkyFitsResourceHandler.RESOURCE_TYPE_FITS){
            //         currentCLT = ((JSkyFitsImageDescription)desc).getColorLookupTable();
            //         break;
            //     }
            // }
            currentCLT = previewedCLT;
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

    /* ----------------------- FITS image under cursor --------------------- */
    // this is the image fed to astropy to compute WCS coordinates
    // and conversely to plot simbad query result sets in VirtualSpace

    void setActiveFITSImage(JSkyFitsImage img){
        this.activeFITSimg = img;
        if (this.activeFITSimg != null){
            URL fitsURL = this.activeFITSimg.getFITSImageURL();
            String fitsPath;
            if (fitsURL.getProtocol().equals(FITSServer.HTTP_PROTOCOL)){
                // image served by FITSOW's own NanoHTTPD
                fitsPath = FITSServer.FITS_DIR + fitsURL.getFile();
            }
            else {
                // assume file:// protocol
                // a zuist fits tile
                // System.out.println(fitsURL.getPath());
                fitsPath = fitsURL.getPath();
            }
            cc.setFITSFile(this.activeFITSimg, fitsPath);
        }
    }

    JSkyFitsImage getActiveFITSImage(){
        return this.activeFITSimg;
    }

    /* ----------------------- WCS coordinates --------------------- */

    void updateWCSCoordinates(double vx, double vy){
        if (activeFITSimg != null){
            Point2D.Double wcs = cc.vs2wcs(vx, vy);
            if (wcs != null) wcsStr = wcs.x + " " + wcs.y;
            else wcsStr = "";
            app.mView.repaint();
        }
        else {
            wcsStr = EMPTY_STRING;
        }
    }

    /* ----------------------- Zuist scene picker --------------------- */

    public void enterGlyph(Glyph g){}

    public void exitGlyph(Glyph g){}

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
        g2d.setFont(Config.DEFAULT_FONT);
        g2d.drawString(wcsStr, 4, 10);
        if (sbMsg != null){
            g2d.drawString(sbMsg, viewWidth/2, 10);
        }
    }

}
