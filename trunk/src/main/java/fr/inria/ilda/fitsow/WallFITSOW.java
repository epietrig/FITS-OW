/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.ImageIcon;

import java.util.Vector;
import java.util.Locale;
import java.util.Arrays;

import fr.inria.zvtm.engine.Location;
import fr.inria.zvtm.engine.Utils;
import fr.inria.zvtm.engine.VirtualSpaceManager;
import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.ViewPanel;
import fr.inria.zvtm.engine.View;
import fr.inria.zvtm.engine.EView;
import fr.inria.zvtm.engine.Camera;
import fr.inria.zvtm.glyphs.Glyph;
import fr.inria.zvtm.glyphs.VImage;
import fr.inria.zvtm.animation.EndAction;

import fr.inria.zvtm.event.ViewListener;

import fr.inria.zvtm.cluster.ClusterGeometry;
import fr.inria.zvtm.cluster.ClusteredView;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * @author Emmanuel Pietriga
 */

public class WallFITSOW extends FITSOW {

    static final String OV_VIEW_TITLE = "Wall Overview";

    static final String LOGO_PATH_2100 = "/images/fits-ow-logos_2100.png";
    static final Image LOGOS = (new ImageIcon(WallFITSOW.class.getResource(LOGO_PATH_2100))).getImage();

    ClusteredView cv;
    ClusterGeometry cg;

    EView wallOverview;
    WOVL ol;

    VirtualSpace logoSpace;
    Camera logoCam;

    public WallFITSOW(FOWOptions options){
        super(options);
    }

    void initGUI(FOWOptions options){
        VirtualSpaceManager.INSTANCE.setMaster("WallFITSOW");
        super.initGUI(options);
        initWallOverview();
        logoSpace = vsm.addVirtualSpace(VirtualSpace.ANONYMOUS);
        logoCam = logoSpace.addCamera();
        cg = new ClusterGeometry(options.blockWidth, options.blockHeight, options.numCols, options.numRows);
        Vector ccameras = new Vector();
        ccameras.add(zfCamera);
        ccameras.add(dCamera);
        ccameras.add(mnCamera);
        ccameras.add(sqCamera);
        ccameras.add(crCamera);
        ccameras.add(logoCam);
        cv = new ClusteredView(cg, options.numRows-1, options.numCols, options.numRows, ccameras);
        vsm.addClusteredView(cv);
        cv.setBackgroundColor(Config.BACKGROUND_COLOR);
        showLogosOnWall();
    }

    void showLogosOnWall(){
        double x = (getDisplayWidth() - LOGOS.getWidth(null)) / 2;
        if (x < 0){
            // if display too small (rough estimate), don't show the logos
            return;
        }
        double y = (getDisplayHeight() - LOGOS.getHeight(null)) / 2;
        VImage logos = new VImage(x, y, 0, LOGOS, 1, .7f);
        logoSpace.addGlyph(logos);
        logos.setSensitivity(false);
    }

    void initWallOverview(){
        ol = new WOVL(this);
        // ZUIST layer, data layer, simbad query layer
        ol.cams = new Camera[]{zfSpace.addCamera(), dSpace.addCamera(), sqSpace.addCamera()};
        ol.cams[0].stick(ol.cams[1], true);
        wallOverview = vsm.addFrameView(Arrays.asList(ol.cams), OV_VIEW_TITLE, View.STD_VIEW, 1440, 480, false, true, true, null);
        for (int i=0;i<ol.cams.length;i++){
            wallOverview.setListener(ol, i);
        }
        wallOverview.setAntialiasing(Config.MASTER_ANTIALIASING);
        wallOverview.setBackgroundColor(Config.BACKGROUND_COLOR);
        wallOverview.getCursor().setColor(Config.CURSOR_COLOR);
        wallOverview.getCursor().setHintColor(Config.CURSOR_COLOR);
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

    // @Override
    // void getGlobalView(EndAction ea){
    //     if (sceneBounds == null) {return;}

    //     Location l =  mView.centerOnRegion(zfCamera,0,sceneBounds[0], sceneBounds[1],sceneBounds[2], sceneBounds[3]);
    //     zfCamera.setLocation(l);
    //     if (ea != null) {
    //          ea.execute(null,null);
    //     }
    // }

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

class WOVL implements ViewListener {

    WallFITSOW app;
    Camera[] cams;

    int activeCam = 0;

    WOVL(WallFITSOW app){
        this.app = app;
    }

    public void press1(ViewPanel v, int mod, int jpx, int jpy, MouseEvent e){}
    public void release1(ViewPanel v, int mod, int jpx, int jpy, MouseEvent e){}
    public void click1(ViewPanel v, int mod, int jpx, int jpy, int clickNumber, MouseEvent e){}

    public void press2(ViewPanel v, int mod, int jpx, int jpy, MouseEvent e){}
    public void release2(ViewPanel v, int mod, int jpx, int jpy, MouseEvent e){}
    public void click2(ViewPanel v, int mod, int jpx, int jpy, int clickNumber, MouseEvent e){}

    public void press3(ViewPanel v, int mod, int jpx, int jpy, MouseEvent e){}
    public void release3(ViewPanel v, int mod, int jpx, int jpy, MouseEvent e){}
    public void click3(ViewPanel v, int mod, int jpx, int jpy, int clickNumber, MouseEvent e){}

    public void mouseMoved(ViewPanel v, int jpx, int jpy, MouseEvent e){}

    public void mouseDragged(ViewPanel v, int mod, int buttonNumber, int jpx, int jpy, MouseEvent e){}

    public void mouseWheelMoved(ViewPanel v, short wheelDirection, int jpx, int jpy, MouseWheelEvent e){}

    public void enterGlyph(Glyph g){}
    public void exitGlyph(Glyph g){}

    public void Kpress(ViewPanel v,char c, int code, int mod, KeyEvent e){
        if (code==KeyEvent.VK_PAGE_UP){app.nav.getHigherView(cams[activeCam]);}
        else if (code==KeyEvent.VK_PAGE_DOWN){app.nav.getLowerView(cams[activeCam]);}
        else if (code==KeyEvent.VK_UP){app.nav.translateView(cams[activeCam], Navigation.MOVE_UP);}
        else if (code==KeyEvent.VK_DOWN){app.nav.translateView(cams[activeCam], Navigation.MOVE_DOWN);}
        else if (code==KeyEvent.VK_LEFT){app.nav.translateView(cams[activeCam], Navigation.MOVE_LEFT);}
        else if (code==KeyEvent.VK_RIGHT){app.nav.translateView(cams[activeCam], Navigation.MOVE_RIGHT);}
    }

    public void Krelease(ViewPanel v,char c, int code, int mod, KeyEvent e){}
    public void Ktype(ViewPanel v,char c, int code, int mod, KeyEvent e){}

    public void viewActivated(View v){}
    public void viewDeactivated(View v){}
    public void viewIconified(View v){}
    public void viewDeiconified(View v){}
    public void viewClosing(View v){}

}
