/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.awt.Color;

import java.util.HashMap;

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;

import fr.inria.zuist.engine.SceneManager;
import fr.inria.zuist.event.ProgressListener;
import fr.inria.zvtm.glyphs.JSkyFitsImage;

class FITSScene {

    File SCENE_FILE, SCENE_FILE_DIR;

    FITSOW app;
    SceneManager sm;


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

}