/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import javax.swing.ImageIcon;

import java.io.File;

import java.util.HashMap;
import java.util.Vector;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import fr.inria.ilda.TUIO.TUIOInputDevice;
import fr.inria.ilda.gesture.GestureManager;
import fr.inria.ilda.gesture.SegmenterTouch;
import fr.inria.ilda.gestures.GestureLayer;
import fr.inria.ilda.gestures.MTRecognitionEngine;
import fr.inria.zuist.engine.JSkyFitsResourceHandler;
import fr.inria.zuist.engine.SceneManager;
import fr.inria.zuist.event.ProgressListener;
import fr.inria.zvtm.animation.Animation;
import fr.inria.zvtm.animation.EndAction;
import fr.inria.zvtm.engine.Camera;
import fr.inria.zvtm.engine.Java2DPainter;
import fr.inria.zvtm.engine.PickerVS;
import fr.inria.zvtm.engine.Utils;
import fr.inria.zvtm.engine.View;
import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.VirtualSpaceManager;

/**
 * @author Emmanuel Pietriga
 */

public class FITSOW {

    /* screen dimensions, actual dimensions of windows */
    static int SCREEN_WIDTH =  Toolkit.getDefaultToolkit().getScreenSize().width;
    static int SCREEN_HEIGHT =  Toolkit.getDefaultToolkit().getScreenSize().height;
    static int VIEW_MAX_W = 1200;
    static int VIEW_MAX_H = 800;
    int VIEW_W, VIEW_H;
    int VIEW_X, VIEW_Y;
    /* dimensions of zoomable panel */
    int panelWidth, panelHeight;

    static final short ZUIST_FITS_LAYER = 0;
    static final short DATA_LAYER = 1;
    static final short MENU_LAYER = 2;
    static final short CURSOR_LAYER = 3;

    FITSScene scene;

    /* ZVTM objects */
    VirtualSpaceManager vsm;
    static final String ZUIST_FITS_SPACE_STR = "ZUIST FITS Layer";
    static final String DATA_SPACE_STR = "Data Layer";
    static final String MENU_SPACE_STR = "Command/Menu Layer";
    static final String CURSOR_SPACE_STR = "Cursor Layer";
    VirtualSpace zfSpace, dSpace, mnSpace, crSpace;
    Camera zfCamera, dCamera, mnCamera, crCamera;
    static final String MAIN_VIEW_TITLE = "FITS on a Wall";

    PickerVS zfSpacePicker, dSpacePicker, mnSpacePicker;

    View mView;
    MVEventListener eh;
    MenuEventListener meh;

    SceneManager sm;
    Navigation nav;
    CursorManager cm;

    WEGlassPane gp;
    // PieMenu mainPieMenu;

    public FITSOW(FOWOptions options){
        VirtualSpaceManager.INSTANCE.getAnimationManager().setResolution(80);
        initGUI(options);
        nav = new Navigation(this);
        cm = new CursorManager(this);
        gp = new WEGlassPane(this);
        ((JFrame)mView.getFrame()).setGlassPane(gp);
        gp.setValue(0);
        gp.setVisible(true);
        VirtualSpace[] sceneSpaces = {zfSpace};
        Camera[] sceneCameras = {zfCamera};
        sm = new SceneManager(sceneSpaces, sceneCameras, new HashMap<String,String>(1,1));
        sm.setResourceHandler(JSkyFitsResourceHandler.RESOURCE_TYPE_FITS,
                              new JSkyFitsResourceHandler());
        scene = new FITSScene(this, options.path_to_fits_dir, options.httpdIP, options.httpdPort);
        mView.setJava2DPainter(scene, Java2DPainter.FOREGROUND);
        zfSpacePicker.setListener(scene);
        if (options.path_to_zuist_fits != null){
            File xmlSceneFile = new File(options.path_to_zuist_fits);
            loadFITSScene(xmlSceneFile);
		}
        if (options.fits_file_name != null){
            scene.loadImage(options.fits_file_name);
        }
        gp.setVisible(false);
        gp.setLabel(WEGlassPane.EMPTY_STRING);

        new WallTouchManager(this, cm);
        
        if(options.smarties) {
    		GestureManager gestureManager = GestureManager.getInstance();
    		SmartiesManager msmarties = new SmartiesManager(
                this, gestureManager, options.blockWidth, options.blockHeight,
                options.numCols, options.numRows);
            cm.registerDevice(msmarties,"Smarties");

            // tablet screen size in pixels 1280 x 800
            // tablet screen size in mms 217.94 x 136.21
            TUIOInputDevice tuioDevice = new TUIOInputDevice(3334, 217.94f, 136.21f);
            cm.registerDevice(tuioDevice,"tuioDevice");

    		gestureManager.registerDevice(tuioDevice);
    		tuioDevice.connect();

    		SegmenterTouch segmenter = new SegmenterTouch();
    		gestureManager.registerSegmenter(segmenter);
    		MTRecognitionEngine mtRecognizer = new MTRecognitionEngine("MTG");
    		segmenter.registerListener(mtRecognizer);
    		GestureLayer recognitionLayer = new GestureLayer(this);
    		mtRecognizer.registerListener(recognitionLayer);
    		gestureManager.start();
    		mView.setJava2DPainter(recognitionLayer, Java2DPainter.AFTER_PORTALS);
        }
    }

    void initGUI(FOWOptions options){
        vsm = VirtualSpaceManager.INSTANCE;
        Config.MASTER_ANTIALIASING = !options.noaa;
        windowLayout();
        zfSpace = vsm.addVirtualSpace(ZUIST_FITS_SPACE_STR);
        dSpace = vsm.addVirtualSpace(DATA_SPACE_STR);
        mnSpace = vsm.addVirtualSpace(MENU_SPACE_STR);
        crSpace = vsm.addVirtualSpace(CURSOR_SPACE_STR);
        zfCamera = zfSpace.addCamera();
        dCamera = dSpace.addCamera();
        mnCamera = mnSpace.addCamera();
        crCamera = crSpace.addCamera();
        Vector cameras = new Vector(4);
        cameras.add(zfCamera);
        cameras.add(dCamera);
        cameras.add(mnCamera);
        cameras.add(crCamera);
        zfCamera.stick(dCamera, true);
//        zfCamera.stick(crCamera, true);
        mView = vsm.addFrameView(cameras, MAIN_VIEW_TITLE, View.STD_VIEW, VIEW_W, VIEW_H, false, false, !options.fullscreen, null);
        if (options.fullscreen &&
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().isFullScreenSupported()){
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow((JFrame)mView.getFrame());
        }
        else {
            mView.setVisible(true);
        }
        mView.setAntialiasing(Config.MASTER_ANTIALIASING);
        eh = new MVEventListener(this);
        meh = new MenuEventListener(this);
        zfCamera.addListener(eh);
        mView.setListener(eh, ZUIST_FITS_LAYER);
        mView.setListener(eh, DATA_LAYER);
        mView.setListener(meh, MENU_LAYER);
        // mView.getCursor().getPicker().setListener(eh);
        zfSpacePicker = new PickerVS();
        zfSpace.registerPicker(zfSpacePicker);
        dSpacePicker = new PickerVS();
        dSpace.registerPicker(dSpacePicker);
        dSpacePicker.setListener(eh);
        mnSpacePicker = new PickerVS();
        mnSpace.registerPicker(mnSpacePicker);
        mnSpacePicker.setListener(meh);
        mView.setBackgroundColor(Config.BACKGROUND_COLOR);
        mView.getCursor().setColor(Config.CURSOR_COLOR);
        mView.getCursor().setHintColor(Config.CURSOR_COLOR);
        updatePanelSize();
        mView.getPanel().getComponent().addComponentListener(eh);
        mView.setActiveLayer(DATA_LAYER);
    }

    void windowLayout(){
        VIEW_W = (SCREEN_WIDTH <= VIEW_MAX_W) ? SCREEN_WIDTH : VIEW_MAX_W;
        VIEW_H = (SCREEN_HEIGHT <= VIEW_MAX_H) ? SCREEN_HEIGHT : VIEW_MAX_H;
    }

    void updatePanelSize(){
        Dimension d = mView.getPanel().getComponent().getSize();
        panelWidth = d.width;
        panelHeight = d.height;
    }

    int getDisplayWidth() { return panelWidth; }
    int getDisplayHeight() { return panelHeight; }

    void loadFITSScene(File zuistSceneFile){
        sm.enableRegionUpdater(false);
        gp.setLabel("Loading " + zuistSceneFile.getName());
        scene.loadScene(zuistSceneFile, gp);
        EndAction ea  = new EndAction(){
            public void execute(Object subject, Animation.Dimension dimension){
                sm.setUpdateLevel(true);
                sm.enableRegionUpdater(true);
            }
        };
        nav.getGlobalView(ea);
        // eh.cameraMoved(mCamera, null, 0);
    }

    void loadFITSImage(){
        // scene.loadImage();
        // nav.getGlobalView(null);
    }


    void gc(){
        System.gc();
    }

    void exit(){
        scene.server.stop();
        System.exit(0);
    }

	public View getView() {
		return mView;
	}

	public boolean runningOnWall() { return false; }
    int getColumnCount(){ return 1; }
    int getRowCount(){ return 1; }

	public Navigation getNavigation() {
		return nav;
	}

    public VirtualSpace getCursorSpace(){
        return mnSpace;
    }

    public CursorManager getCursorManager(){
        return cm;
    }

	public Camera getZFCamera() {
		return zfCamera;
	}

	public FITSScene getScene() {
		return scene;
	}

	public MenuEventListener getMenuEventHandler() {
		return meh;
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
        new FITSOW(options);
    }

}

class WEGlassPane extends JComponent implements ProgressListener {

    static final int BAR_WIDTH = 200;
    static final int BAR_HEIGHT = 10;

    static final String TITLE = "FITS-OW";
    static final Font TITLE_FONT = new Font("Arial", Font.PLAIN, 36);
    static FontMetrics TITLE_FONT_FM = null;
    static int TITLE_WIDTH = 0;

    static final String CONTRIBUTORS = "Contributors: Emmanuel Pietriga, Fernando del Campo, Caroline Appert, Olivier Chapuis, Roberto Muñoz, Romain Primet";
    static final Font CONTRIBUTORS_FONT = new Font("Arial", Font.PLAIN, 10);
    static FontMetrics CONTRIBUTORS_FONT_FM = null;
    static int CONTRIBUTORS_WIDTH = 0;

    static final Color MSG_COLOR = Color.WHITE;
    static final Color BKG_COLOR = Color.BLACK;

    static final String LOGO_PATH_600 = "/images/fits-ow-logos_600.png";

    static final Image LOGOS = new ImageIcon(WEGlassPane.class.getResource(LOGO_PATH_600)).getImage();

    static final String EMPTY_STRING = "";
    // String msg = EMPTY_STRING;
    // int msgX = 0;
    // int msgY = 0;

    int completion = 0;
    int prX = 0;
    int prY = 0;
    int prW = 0;

    FITSOW app;

    WEGlassPane(FITSOW app){
        super();
        this.app = app;
        addMouseListener(new MouseAdapter(){});
        addMouseMotionListener(new MouseMotionAdapter(){});
        addKeyListener(new KeyAdapter(){});
    }

    public void setValue(int c){
        completion = c;
        prX = app.panelWidth/2-BAR_WIDTH/2;
        prY = app.panelHeight/2-BAR_HEIGHT/2;
        prW = (int)(BAR_WIDTH * ((float)completion) / 100.0f);
        repaint(prX, prY, BAR_WIDTH, BAR_HEIGHT);
    }

    public void setLabel(String m){
        // msg = m;
        // msgX = app.panelWidth/2-BAR_WIDTH/2;
        // msgY = app.panelHeight/2-BAR_HEIGHT/2 - 10;
        // repaint(msgX, msgY-50, 400, 70);
    }

    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (TITLE_FONT_FM == null){
            TITLE_FONT_FM = g2.getFontMetrics(TITLE_FONT);
            TITLE_WIDTH = TITLE_FONT_FM.stringWidth(TITLE);
        }
        if (CONTRIBUTORS_FONT_FM == null){
            CONTRIBUTORS_FONT_FM = g2.getFontMetrics(CONTRIBUTORS_FONT);
            CONTRIBUTORS_WIDTH = CONTRIBUTORS_FONT_FM.stringWidth(CONTRIBUTORS);
        }
        Rectangle clip = g.getClipBounds();
        g2.setColor(BKG_COLOR);
        g2.fillRect(clip.x, clip.y, clip.width, clip.height);
        g2.setColor(MSG_COLOR);
        g2.setFont(TITLE_FONT);
        g2.drawString(TITLE, app.panelWidth/2-TITLE_WIDTH/2, app.panelHeight/2-100);
        g2.setFont(CONTRIBUTORS_FONT);
        g2.drawString(CONTRIBUTORS, app.panelWidth/2-CONTRIBUTORS_WIDTH/2, app.panelHeight/2+200);
        // if (msg != EMPTY_STRING){
        //     g2.drawString(msg, msgX, msgY);
        // }
        g2.fillRect(prX, prY, prW, BAR_HEIGHT);
        g2.drawRect(prX, prY, BAR_WIDTH, BAR_HEIGHT);

        g2.drawImage(LOGOS, app.panelWidth/2-LOGOS.getWidth(null)/2, app.panelHeight-4*LOGOS.getHeight(null)/2, null);
    }

}
