/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import fi.iki.elonen.NanoHTTPD;

class FITSServer extends NanoHTTPD {

    static final String FITS_MIME_TYPE = "image/fits";

    static String FITS_DIR = "/tmp";

    FITSOW app;

    FITSServer(FITSOW app, String fitsDir){
        super(Config.HTTPD_PORT);
        this.app = app;
        initFitsDir(fitsDir);
    }

    void initFitsDir(String dir){
        File f = new File(dir);
        if (!f.exists()){
            System.out.println("Creating temporary FITS dir at:\n" + f.getAbsolutePath());
            f.mkdir();
        }
        else {
            System.out.println("Will store FITS images in:\n" + f.getAbsolutePath());
        }
        FITS_DIR = f.getAbsolutePath();
    }

    @Override public Response serve(IHTTPSession session) {
        Map<String, List<String>> decodedQueryParameters =
            decodeParameters(session.getQueryParameterString());
        String uri = session.getUri();
        String[] tokens = uri.split("/");
        try {
            File f = new File(FITS_DIR + File.separator + tokens[tokens.length-1]);
            //System.out.println("Serving "+f.getAbsolutePath());
            FileInputStream fis = new FileInputStream(f);
            return new Response(Response.Status.OK, FITS_MIME_TYPE, fis);
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return new Response(Response.Status.NOT_FOUND, FITS_MIME_TYPE, "");
        }

    }

}
