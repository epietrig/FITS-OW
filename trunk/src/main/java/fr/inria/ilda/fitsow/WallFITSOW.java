/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.util.Vector;

import fr.inria.zvtm.engine.Utils;
import fr.inria.zvtm.engine.VirtualSpaceManager;

import fr.inria.zvtm.cluster.ClusterGeometry;
import fr.inria.zvtm.cluster.ClusteredView;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * @author Emmanuel Pietriga
 */

public class WallFITSOW extends FITSOW {

    ClusteredView cv;
    ClusterGeometry cg;

    public WallFITSOW(FOWOptions options){
        super(options);
    }

    void initGUI(FOWOptions options){
        VirtualSpaceManager.INSTANCE.setMaster("WallFITSOW");
        super.initGUI(options);
        cg = new ClusterGeometry(options.blockWidth, options.blockHeight, options.numCols, options.numRows);
        Vector ccameras = new Vector(2);
        ccameras.add(zfCamera);
        ccameras.add(dCamera);
        ccameras.add(mnCamera);
        cv = new ClusteredView(cg, options.numRows-1, options.numCols, options.numRows, ccameras);
        vsm.addClusteredView(cv);
        cv.setBackgroundColor(Config.BACKGROUND_COLOR);
    }

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
        new WallFITSOW(options);
    }

}
