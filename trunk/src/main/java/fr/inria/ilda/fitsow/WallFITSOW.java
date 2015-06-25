/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.awt.Image;
import javax.swing.ImageIcon;

import java.util.Vector;
import java.util.Locale;

import fr.inria.zvtm.engine.Location;
import fr.inria.zvtm.engine.Utils;
import fr.inria.zvtm.engine.VirtualSpaceManager;
import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.Camera;
import fr.inria.zvtm.glyphs.VImage;
import fr.inria.zvtm.animation.EndAction;

import fr.inria.zvtm.cluster.ClusterGeometry;
import fr.inria.zvtm.cluster.ClusteredView;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * @author Emmanuel Pietriga
 */

public class WallFITSOW extends FITSOW {

    static final String LOGO_PATH_2100 = "/images/fits-ow-logos_2100.png";
    static final Image LOGOS = (new ImageIcon(WallFITSOW.class.getResource(LOGO_PATH_2100))).getImage();

    ClusteredView cv;
    ClusterGeometry cg;

    VirtualSpace logoSpace;
    Camera logoCam;

    public WallFITSOW(FOWOptions options){
        super(options);
    }

    void initGUI(FOWOptions options){
        VirtualSpaceManager.INSTANCE.setMaster("WallFITSOW");
        super.initGUI(options);
        logoSpace = vsm.addVirtualSpace(VirtualSpace.ANONYMOUS);
        logoCam = logoSpace.addCamera();
        cg = new ClusterGeometry(options.blockWidth, options.blockHeight, options.numCols, options.numRows);
        Vector ccameras = new Vector(2);
        ccameras.add(zfCamera);
        ccameras.add(dCamera);
        ccameras.add(mnCamera);
        ccameras.add(crCamera);
        ccameras.add(logoCam);
        cv = new ClusteredView(cg, options.numRows-1, options.numCols, options.numRows, ccameras);
        vsm.addClusteredView(cv);
        cv.setBackgroundColor(Config.BACKGROUND_COLOR);
        showLogosOnWall();
    }

    void showLogosOnWall(){
        double x = (getDisplayWidth() - LOGOS.getWidth(null)) / 2;
        double y = (getDisplayHeight() - LOGOS.getHeight(null)) / 2;
        VImage logos = new VImage(x, y, 0, LOGOS, 1, .7f);
        logoSpace.addGlyph(logos);
        logos.setSensitivity(false);
    }

    @Override
    public boolean runningOnWall(){
        return true;
    }

    @Override
    int getDisplayWidth(){
        return cg.getWidth();
    }

    @Override
    int getDisplayHeight(){
        return cg.getHeight();
    }

    @Override
    int getColumnCount(){
        return cg.getColumns();
    }

    @Override
    int getRowCount(){
        return cg.getRows();
    }

    @Override
    void getGlobalView(EndAction ea){
        if (sceneBounds == null) {return;}

        Location l =  mView.centerOnRegion(zfCamera,0,sceneBounds[0], sceneBounds[1],sceneBounds[2], sceneBounds[3]);
        zfCamera.setLocation(l);
        if (ea != null) {
             ea.execute(null,null);
        }
    }

    public static void main(String[] args){
        Locale.setDefault(new Locale("en", "US"));
        FOWOptions options = new FOWOptions();
        CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);
        } catch(CmdLineException ex){
            System.err.println(ex.getMessage());
            parser.printUsage(System.err);
            return;
        }
        if (!options.fullscreen && Utils.osIsMacOS()){
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        System.out.println("--help for command line options");
        new WallFITSOW(options);
    }

}
