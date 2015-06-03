/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.GradientPaint;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.KeyAdapter;
import javax.swing.JComponent;
import javax.swing.JFrame;

import java.util.Vector;
import java.util.HashMap;

import java.io.File;

import fr.inria.zvtm.engine.Camera;
import fr.inria.zvtm.engine.VirtualSpaceManager;
import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.View;
import fr.inria.zvtm.engine.Utils;
import fr.inria.zvtm.engine.PickerVS;
import fr.inria.zvtm.animation.Animation;
import fr.inria.zvtm.animation.EndAction;

import fr.inria.zvtm.widgets.PieMenu;

import fr.inria.zuist.engine.SceneManager;
import fr.inria.zuist.event.ProgressListener;

import fr.inria.zuist.engine.JSkyFitsResourceHandler;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * @author Emmanuel Pietriga
 */

public class FITSOW {

    /* screen dimensions, actual dimensions of windows */
    static int SCREEN_WIDTH =  Toolkit.getDefaultToolkit().getScreenSize().width;
    static int SCREEN_HEIGHT =  Toolkit.getDefaultToolkit().getScreenSize().height;
    static int VIEW_MAX_W = 1280;
    static int VIEW_MAX_H = 1024;
    int VIEW_W, VIEW_H;
    int VIEW_X, VIEW_Y;
    /* dimensions of zoomable panel */
    int panelWidth, panelHeight;

    static final short ZUIST_FITS_LAYER = 0;
    static final short DATA_LAYER = 1;
    static final short MENU_LAYER = 2;

    FITSScene scene;

    /* ZVTM objects */
    VirtualSpaceManager vsm;
    static final String ZUIST_FITS_SPACE_STR = "ZUIST FITS Layer";
    static final String DATA_SPACE_STR = "Data Layer";
    VirtualSpace zfSpace, dSpace;
    Camera zfCamera, dCamera;
    static final String MAIN_VIEW_TITLE = "FITS on a Wall";

    PickerVS dSpacePicker;

    View mView;
    MVEventListener eh;

    SceneManager sm;
    Navigation nav;

    WEGlassPane gp;
    // PieMenu mainPieMenu;

    public FITSOW(FOWOptions options){
        VirtualSpaceManager.INSTANCE.getAnimationManager().setResolution(80);
        initGUI(options);
        nav = new Navigation(this);
        gp = new WEGlassPane(this);
        ((JFrame)mView.getFrame()).setGlassPane(gp);
        gp.setValue(0);
        gp.setVisible(true);
        VirtualSpace[] sceneSpaces = {zfSpace};
        Camera[] sceneCameras = {zfCamera};
        sm = new SceneManager(sceneSpaces, sceneCameras, new HashMap<String,String>(1,1));
        sm.setResourceHandler(JSkyFitsResourceHandler.RESOURCE_TYPE_FITS,
                              new JSkyFitsResourceHandler());
        scene = new FITSScene(this);
        if (options.path_to_zuist_fits != null){
            File xmlSceneFile = new File(options.path_to_zuist_fits);
            loadFITSScene(xmlSceneFile);
		}
        if (options.path_to_fits != null){
            File fitsFile = new File(options.path_to_fits);
            scene.loadImage(fitsFile);
        }
        gp.setVisible(false);
        gp.setLabel(WEGlassPane.EMPTY_STRING);
    }

    void initGUI(FOWOptions options){
        vsm = VirtualSpaceManager.INSTANCE;
        Config.MASTER_ANTIALIASING = !options.noaa;
        windowLayout();
        zfSpace = vsm.addVirtualSpace(ZUIST_FITS_SPACE_STR);
        dSpace = vsm.addVirtualSpace(DATA_SPACE_STR);
        zfCamera = zfSpace.addCamera();
        dCamera = dSpace.addCamera();
        Vector cameras = new Vector(2);
        cameras.add(zfCamera);
        cameras.add(dCamera);
        // XXX add menu camera
        zfCamera.stick(dCamera, true);
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
        zfCamera.addListener(eh);
        mView.setListener(eh, ZUIST_FITS_LAYER);
        mView.setListener(eh, DATA_LAYER);
        // mView.getCursor().getPicker().setListener(eh);
        dSpacePicker = new PickerVS();
        dSpace.registerPicker(dSpacePicker);
        dSpacePicker.setListener(eh);
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
        System.exit(0);
    }

    // void displayMainPieMenu(boolean b){
    //     if (b){
    //         double a = (mCamera.focal+Math.abs(mCamera.altitude)) / mCamera.focal;


    //         PieMenuFactory.setItemFillColor(PIEMENU_FILL_COLOR);
    //         PieMenuFactory.setItemBorderColor(PIEMENU_BORDER_COLOR);
    //         PieMenuFactory.setSelectedItemFillColor(PIEMENU_INSIDE_COLOR);
    //         PieMenuFactory.setSelectedItemBorderColor(null);
    //         PieMenuFactory.setLabelColor(PIEMENU_BORDER_COLOR);
    //         PieMenuFactory.setFont(PIEMENU_FONT);
    //         PieMenuFactory.setTranslucency(0.7f);
    //         PieMenuFactory.setSensitivityRadius(0.5);
    //         PieMenuFactory.setAngle(-Math.PI/2.0);
    //         PieMenuFactory.setRadius((long)(150));

    //         mainPieMenu = PieMenuFactory.createPieMenu(RaildarStore.BRANDS, rdStore.BRANDS_OFFSETS, 0, mView, vsm);

    //         Glyph[] items = mainPieMenu.getItems();
    //         for(Glyph item : items){
    //             item.setType(ExplorerEventHandler.T_PIEMENU);
    //             item.setZindex(Z_DRAW_RECT);
    //         }
    //         for(int i = 0; i < rdStore.BRANDS.length; i++){
    //             short type = Circulation.getTrainType(rdStore.BRANDS[i]);
    //             if(rdStore.hiddenBrand.contains(type) && rdStore.isHiddenBrand(rdStore.BRANDS[i])){
    //                 items[i].setColor(PIEMENU_INSIDE_COLOR_);
    //             }
    //         }
    //     }
    //     else {
    //         mainPieMenu.destroy(0);
    //         mainPieMenu = null;
    //     }
    // }

    // void pieMenuEvent(Glyph menuItem){
    //     int index = mainPieMenu.getItemIndex(menuItem);
    //     if (index != -1){
    //         String label = mainPieMenu.getLabels()[index].getText();
    //         boolean set = rdStore.isHiddenBrand(RaildarStore.BRANDS[index]);
    //         rdStore.hideTrainsForBrand(set, RaildarStore.BRANDS[index]);
    //         smartiesMngr.updateBrands();
    //     }
    // }

    public static void main(String[] args){
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

    static final AlphaComposite GLASS_ALPHA = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f);
    static final Color MSG_COLOR = Color.DARK_GRAY;
    GradientPaint PROGRESS_GRADIENT = new GradientPaint(0, 0, Color.ORANGE, 0, BAR_HEIGHT, Color.BLUE);

    static final String EMPTY_STRING = "";
    String msg = EMPTY_STRING;
    int msgX = 0;
    int msgY = 0;

    int completion = 0;
    int prX = 0;
    int prY = 0;
    int prW = 0;

    FITSOW application;

    static final Font GLASSPANE_FONT = new Font("Arial", Font.PLAIN, 12);

    WEGlassPane(FITSOW app){
        super();
        this.application = app;
        addMouseListener(new MouseAdapter(){});
        addMouseMotionListener(new MouseMotionAdapter(){});
        addKeyListener(new KeyAdapter(){});
    }

    public void setValue(int c){
        completion = c;
        prX = application.panelWidth/2-BAR_WIDTH/2;
        prY = application.panelHeight/2-BAR_HEIGHT/2;
        prW = (int)(BAR_WIDTH * ((float)completion) / 100.0f);
        PROGRESS_GRADIENT = new GradientPaint(0, prY, Color.LIGHT_GRAY, 0, prY+BAR_HEIGHT, Color.DARK_GRAY);
        repaint(prX, prY, BAR_WIDTH, BAR_HEIGHT);
    }

    public void setLabel(String m){
        msg = m;
        msgX = application.panelWidth/2-BAR_WIDTH/2;
        msgY = application.panelHeight/2-BAR_HEIGHT/2 - 10;
        repaint(msgX, msgY-50, 400, 70);
    }

    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        Rectangle clip = g.getClipBounds();
        g2.setComposite(GLASS_ALPHA);
        g2.setColor(Color.WHITE);
        g2.fillRect(clip.x, clip.y, clip.width, clip.height);
        g2.setComposite(AlphaComposite.Src);
        if (msg != EMPTY_STRING){
            g2.setColor(MSG_COLOR);
            g2.setFont(GLASSPANE_FONT);
            g2.drawString(msg, msgX, msgY);
        }
        g2.setPaint(PROGRESS_GRADIENT);
        g2.fillRect(prX, prY, prW, BAR_HEIGHT);
        g2.setColor(MSG_COLOR);
        g2.drawRect(prX, prY, BAR_WIDTH, BAR_HEIGHT);
    }

}
