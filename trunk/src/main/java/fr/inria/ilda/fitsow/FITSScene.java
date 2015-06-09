/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.awt.Color;

import java.util.HashMap;
import java.util.LinkedHashMap;

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;

import fr.inria.zuist.engine.SceneManager;
import fr.inria.zuist.event.ProgressListener;
import fr.inria.zvtm.glyphs.JSkyFitsImage;
import fr.inria.zuist.engine.ObjectDescription;
import fr.inria.zuist.engine.JSkyFitsImageDescription;

class FITSScene {

    File SCENE_FILE, SCENE_FILE_DIR;

    FITSOW app;
    SceneManager sm;

    String zuistColorMapping = Config.COLOR_MAPPING_LIST[0];
    String zuistScale = Config.SCALE_LINEAR;

    FITSScene(FITSOW app){
        this.app = app;
        this.sm = app.sm;
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

    void loadImage(File f){
        if (f != null){
            String path = f.getAbsolutePath();
            if (File.separatorChar != '/'){
                path = path.replace(File.separatorChar, '/');
            }
            if (!path.startsWith("/")){
                path = "/" + path;
            }
            try {
                loadImage(new URL("file:" + path));
            }
            catch (MalformedURLException mue){
                System.out.println("Error loading FITS image from " + path);
            }
        }
    }

    void addImage(JSkyFitsImage img){
        if (img != null){
            img.setColorLookupTable("Standard", false);
            img.setScaleAlgorithm(JSkyFitsImage.ScaleAlgorithm.LINEAR, false);
            img.updateDisplayedImage();
            app.dSpace.addGlyph(img);
            // menu.buildHistogram();
        }
    }

    /* ---------------- Scale ---------------------- */

    void setScale(JSkyFitsImage img, String scale){
        if (img != null){
            img.setScaleAlgorithm(Config.SCALES.get(scale), true);
        }
        else {
            JSkyFitsImage.ScaleAlgorithm sa = Config.SCALES.get(scale);
            for (ObjectDescription desc:app.sm.getObjectDescriptions()){
                if (desc instanceof JSkyFitsImageDescription){
                    ((JSkyFitsImageDescription)desc).setScaleAlgorithm(sa, true);
                }
            }
        }
    }

    /* ---------------- Color mapping ---------------------- */

    void setColorMapping(JSkyFitsImage img, String clt){
        if (img != null){
            img.setColorLookupTable(clt, true);
        }
        else {
            for (ObjectDescription desc:app.sm.getObjectDescriptions()){
                if (desc instanceof JSkyFitsImageDescription){
                    ((JSkyFitsImageDescription)desc).setColorLookupTable(clt, true);
                }
            }
        }
    }

    String selectNextColorMapping(JSkyFitsImage img){
        if (img == null){
            // XXX no specific image selected, do it on ZUIST scene
            return null;
        }
        String currentCLT = img.getColorLookupTable();
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

    String selectPrevColorMapping(JSkyFitsImage img){
        if (img == null){
            // XXX no specific image selected, do it on ZUIST scene
            return null;
        }
        String currentCLT = img.getColorLookupTable();
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

}
