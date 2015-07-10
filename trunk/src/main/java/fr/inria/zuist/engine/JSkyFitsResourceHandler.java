/*   Copyright (c) INRIA, 2010-2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 */

package fr.inria.zuist.engine;

import java.awt.Color;

import java.net.URL;

import fr.inria.zvtm.glyphs.JSkyFitsImage;
import fr.inria.zuist.engine.SceneManager;
import fr.inria.zuist.engine.SceneBuilder;
import fr.inria.zuist.od.ResourceDescription;
import fr.inria.zuist.od.JSkyFitsImageDescription;

public class JSkyFitsResourceHandler implements ResourceHandler {

    public static final String RESOURCE_TYPE_FITS = "skyfits";

    public static JSkyFitsImage.ScaleAlgorithm DEFAULT_SCALE = JSkyFitsImage.ScaleAlgorithm.HIST_EQ;
    public static String DEFAULT_COLOR_LOOKUP_TABLE = "Ramp";

    private static final String SC_ID = "sc="; //scale factor in params
    private static final String SM_ID = "sm="; //scale method in params
    private static final String CF_ID = "cf="; //color filter in params
    private static final String MIN_VAL_ID = "minvalue="; //min value for rescale in params
    private static final String MAX_VAL_ID = "maxvalue="; //max value for rescale in params
    private static final String REF_ID = "reference"; // fits reference for wcs coordinates

    public ResourceDescription createResourceDescription(
            double x, double y, String id, int zindex, Region region,
            URL resourceURL, boolean sensitivity, Color stroke, String params){

        float scaleFactor = 1;

        JSkyFitsImage.ScaleAlgorithm scaleMethod = DEFAULT_SCALE;
        String colorLookupTable = DEFAULT_COLOR_LOOKUP_TABLE;

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        boolean reference = false;

        if (params != null){
            String[] paramTokens = params.split(SceneBuilder.PARAM_SEPARATOR);
            for (int i=0;i<paramTokens.length;i++) {
                if (paramTokens[i].startsWith(SC_ID)){
                    scaleFactor = Float.parseFloat(paramTokens[i].substring(SC_ID.length()));
                }
                else if (paramTokens[i].startsWith(SM_ID)){
                    try{
                        scaleMethod = JSkyFitsImage.ScaleAlgorithm.valueOf(paramTokens[i].substring(SM_ID.length()));
                    } catch(IllegalArgumentException ignored){
                        System.err.println("Incorrect scale method, using default instead");
                    }
                }
                else if (paramTokens[i].startsWith(CF_ID)){
                    try{
                        colorLookupTable = paramTokens[i].substring(CF_ID.length());
                    } catch(IllegalArgumentException ignored){
                        System.err.println("Incorrect color filter, using default instead");
                    }
                }
                else if (paramTokens[i].startsWith(MIN_VAL_ID)){
                    try{
                        min = Double.parseDouble(paramTokens[i].substring(MIN_VAL_ID.length()));
                    } catch(IllegalArgumentException ignored){
                        System.err.println("Incorrect min value, using default instead");
                    }
                }
                else if (paramTokens[i].startsWith(MAX_VAL_ID)){
                    try{
                        max = Double.parseDouble(paramTokens[i].substring(MAX_VAL_ID.length()));
                    } catch(IllegalArgumentException ignored){
                        System.err.println("Incorrect max value, using default instead");
                    }
                }
                else if(paramTokens[i].startsWith(REF_ID)){
                    reference = true;
                }
                else {
                    System.err.println("Unknown type of resource parameter: "+paramTokens[i]);
                }
            }
        }

        JSkyFitsImageDescription desc;
        if (max != Double.MIN_VALUE && min != Double.MAX_VALUE){
            desc = new JSkyFitsImageDescription(
                id, x, y, zindex, resourceURL, region,
                scaleFactor, scaleMethod, colorLookupTable, min, max
            );
        } else {
            desc = new JSkyFitsImageDescription(
                id, x, y, zindex, resourceURL, region,
                scaleFactor, scaleMethod, colorLookupTable
            );
        }
        if(reference){
            desc.setReference(reference);
        }

        region.addObject(desc);
        return desc;
    }


}
