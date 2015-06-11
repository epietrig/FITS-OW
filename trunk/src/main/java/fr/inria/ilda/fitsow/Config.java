/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.awt.Color;
import java.awt.Font;
import java.awt.BasicStroke;
import java.awt.image.RGBImageFilter;

import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Vector;

import fr.inria.zvtm.widgets.PieMenuFactory;
import fr.inria.zvtm.glyphs.JSkyFitsImage;
import fr.inria.zuist.engine.JSkyFitsResourceHandler;

import fr.inria.zvtm.fits.filters.Aips0Filter;
import fr.inria.zvtm.fits.filters.BackgrFilter;
import fr.inria.zvtm.fits.filters.BlueFilter;
import fr.inria.zvtm.fits.filters.BlulutFilter;
import fr.inria.zvtm.fits.filters.ColorFilter;
import fr.inria.zvtm.fits.filters.GreenFilter;
import fr.inria.zvtm.fits.filters.HeatFilter;
import fr.inria.zvtm.fits.filters.Idl11Filter;
import fr.inria.zvtm.fits.filters.Idl12Filter;
import fr.inria.zvtm.fits.filters.Idl14Filter;
import fr.inria.zvtm.fits.filters.Idl15Filter;
import fr.inria.zvtm.fits.filters.Idl2Filter;
import fr.inria.zvtm.fits.filters.Idl4Filter;
import fr.inria.zvtm.fits.filters.Idl5Filter;
import fr.inria.zvtm.fits.filters.Idl6Filter;
import fr.inria.zvtm.fits.filters.IsophotFilter;
import fr.inria.zvtm.fits.filters.LightFilter;
import fr.inria.zvtm.fits.filters.ManycolFilter;
import fr.inria.zvtm.fits.filters.PastelFilter;
import fr.inria.zvtm.fits.filters.RainbowFilter;
import fr.inria.zvtm.fits.filters.Rainbow1Filter;
import fr.inria.zvtm.fits.filters.Rainbow2Filter;
import fr.inria.zvtm.fits.filters.Rainbow3Filter;
import fr.inria.zvtm.fits.filters.Rainbow4Filter;
import fr.inria.zvtm.fits.filters.RampFilter;
import fr.inria.zvtm.fits.filters.RandomFilter;
import fr.inria.zvtm.fits.filters.Random1Filter;
import fr.inria.zvtm.fits.filters.Random2Filter;
import fr.inria.zvtm.fits.filters.Random3Filter;
import fr.inria.zvtm.fits.filters.Random4Filter;
// import fr.inria.zvtm.fits.filters.Random5Filter;
// import fr.inria.zvtm.fits.filters.Random6Filter;
import fr.inria.zvtm.fits.filters.RealFilter;
import fr.inria.zvtm.fits.filters.RedFilter;
import fr.inria.zvtm.fits.filters.SmoothFilter;
// import fr.inria.zvtm.fits.filters.Smooth1Filter;
// import fr.inria.zvtm.fits.filters.Smooth2Filter;
// import fr.inria.zvtm.fits.filters.Smooth3Filter;
import fr.inria.zvtm.fits.filters.StaircaseFilter;
import fr.inria.zvtm.fits.filters.Stairs8Filter;
import fr.inria.zvtm.fits.filters.Stairs9Filter;
import fr.inria.zvtm.fits.filters.StandardFilter;
import fr.inria.zvtm.fits.filters.ColorGradient;

public class Config {

    static final Color BACKGROUND_COLOR = Color.BLACK;
    static final Color CURSOR_COLOR = Color.WHITE;

    static boolean MASTER_ANTIALIASING = true;
    static boolean CLUSTER_ANTIALIASING = true;

    static final Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 12);
    static final Font GLASSPANE_FONT = new Font("Arial", Font.PLAIN, 12);

    /* PIEMENU */

    static final Font PIEMENU_FONT = DEFAULT_FONT;

    static Color PIEMENU_FILL_COLOR = Color.BLACK;
    static Color PIEMENU_BORDER_COLOR = Color.WHITE;
    static Color PIEMENU_INSIDE_COLOR = Color.DARK_GRAY;

    static {
        PieMenuFactory.setItemFillColor(PIEMENU_FILL_COLOR);
        PieMenuFactory.setItemBorderColor(PIEMENU_BORDER_COLOR);
        PieMenuFactory.setSelectedItemFillColor(PIEMENU_INSIDE_COLOR);
        PieMenuFactory.setSelectedItemBorderColor(null);
        PieMenuFactory.setLabelColor(PIEMENU_BORDER_COLOR);
        PieMenuFactory.setFont(PIEMENU_FONT);
        PieMenuFactory.setAngle(0);
    }

    /* ------------ Widget geometry and color ---------- */
    // color lookup table buttons
    static final int CLT_BTN_PADDING = 12;
    static final int CLT_BTN_W = 200;
    static final int CLT_BTN_H = 20;
    static final int CLT_BTN_HOFFSET = 6;
    static final int CLT_BTN_VOFFSET = 4;
    static final BasicStroke CLT_BTN_SEL_STROKE = new BasicStroke(3f);
    static final Color CLT_BTN_SEL_COLOR = Color.WHITE;
    static final Color CLT_BTN_BORDER_COLOR = Color.GRAY;

    /* ------------ Glyph z-index ---------- */
    // static final int Z_MPMI = 10;
    // static final int Z_SPMI = 12;
    // color lookup table buttons
    static final int Z_CLT_BKG = 100;
    static final int Z_CLT_BTN = 110;

    /* ------------ Glyph types ---------- */

    static final String T_MPMI = "mpm";
    static final String T_SPMI = "spm_";
    static final String T_SPMISc = T_SPMI + "sc";

    static final String T_FITS = "fits";

    static final String T_CLT_BTN = "clt";

    /* ------------ Scales and color mappings ---------- */

    public static final String SCALE_LINEAR = "LIN";
    public static final String SCALE_LOG = "LOG";
    public static final String SCALE_SQRT = "SQRT";
    public static final String SCALE_HISTEQ = "HIST";

    static LinkedHashMap<String,JSkyFitsImage.ScaleAlgorithm> SCALES = new LinkedHashMap(4,1);
    static {
        SCALES.put(SCALE_LINEAR, JSkyFitsImage.ScaleAlgorithm.LINEAR);
        SCALES.put(SCALE_LOG, JSkyFitsImage.ScaleAlgorithm.LOG);
        SCALES.put(SCALE_HISTEQ, JSkyFitsImage.ScaleAlgorithm.HIST_EQ);
        SCALES.put(SCALE_SQRT, JSkyFitsImage.ScaleAlgorithm.SQRT);
    };
    static final String[] SCALE_LIST = SCALES.keySet().toArray(new String[SCALES.size()]);

    static final String[][] COLOR_MAPPINGS = {
            {"Standard", "Aips0", "Background", "Color"},
            {"Red", "Green", "Blue", "Blulut", "Ramp", "Real", "Heat"},
            {"Light", "Pastel", "Smooth"},
            {"Idl2", "Idl4", "Idl5", "Idl6", "Idl11", "Idl12", "Idl14", "Idl15"},
            {"Isophot", "Manycolor", "Stairs8", "Stairs9"},
            {"Random", "Random1", "Random2", "Random3", "Random4"},
            {"Rainbow", "Rainbow1", "Rainbow2", "Rainbow3", "Rainbow4"}
    };
    static int LARGEST_COLOR_MAPPING_CAT;
    static {
        LARGEST_COLOR_MAPPING_CAT = COLOR_MAPPINGS[0].length;
        for (int i=1;i<COLOR_MAPPINGS.length;i++){
            if (LARGEST_COLOR_MAPPING_CAT < COLOR_MAPPINGS[i].length){
                LARGEST_COLOR_MAPPING_CAT = COLOR_MAPPINGS[i].length;
            }
        }
    }

    static String[] COLOR_MAPPING_LIST;
    static {
        Vector<String> v = new Vector();
        for (String[] aos:COLOR_MAPPINGS){
            for (String s:aos){
                v.add(s);
            }
        }
        COLOR_MAPPING_LIST = v.toArray(new String[v.size()]);
    }

    static final HashMap<String,RGBImageFilter> COLOR_MAPPING_GRADIENTS = new HashMap(40,1);
    static {
        COLOR_MAPPING_GRADIENTS.put("Standard", new StandardFilter());
        COLOR_MAPPING_GRADIENTS.put("Aips0", new Aips0Filter());
        COLOR_MAPPING_GRADIENTS.put("Background", new BackgrFilter());
        COLOR_MAPPING_GRADIENTS.put("Blue", new BlueFilter());
        COLOR_MAPPING_GRADIENTS.put("Blulut", new BlulutFilter());
        COLOR_MAPPING_GRADIENTS.put("Color", new ColorFilter());
        COLOR_MAPPING_GRADIENTS.put("Green", new GreenFilter());
        COLOR_MAPPING_GRADIENTS.put("Heat",  new HeatFilter());
        COLOR_MAPPING_GRADIENTS.put("Idl11", new Idl11Filter());
        COLOR_MAPPING_GRADIENTS.put("Idl12", new Idl12Filter());
        COLOR_MAPPING_GRADIENTS.put("Idl14", new Idl14Filter());
        COLOR_MAPPING_GRADIENTS.put("Idl15", new Idl15Filter());
        COLOR_MAPPING_GRADIENTS.put("Idl2", new Idl2Filter());
        COLOR_MAPPING_GRADIENTS.put("Idl4", new Idl4Filter());
        COLOR_MAPPING_GRADIENTS.put("Idl5", new Idl5Filter());
        COLOR_MAPPING_GRADIENTS.put("Idl6", new Idl6Filter());
        COLOR_MAPPING_GRADIENTS.put("Isophot", new IsophotFilter());
        COLOR_MAPPING_GRADIENTS.put("Light", new LightFilter());
        COLOR_MAPPING_GRADIENTS.put("Manycolor", new ManycolFilter());
        COLOR_MAPPING_GRADIENTS.put("Pastel", new PastelFilter());
        COLOR_MAPPING_GRADIENTS.put("Rainbow", new RainbowFilter());
        COLOR_MAPPING_GRADIENTS.put("Rainbow1", new Rainbow1Filter());
        COLOR_MAPPING_GRADIENTS.put("Rainbow2", new Rainbow2Filter());
        COLOR_MAPPING_GRADIENTS.put("Rainbow3", new Rainbow3Filter());
        COLOR_MAPPING_GRADIENTS.put("Rainbow4", new Rainbow4Filter());
        COLOR_MAPPING_GRADIENTS.put("Ramp", new RampFilter());
        COLOR_MAPPING_GRADIENTS.put("Random", new RandomFilter());
        COLOR_MAPPING_GRADIENTS.put("Random1", new Random1Filter());
        COLOR_MAPPING_GRADIENTS.put("Random2", new Random2Filter());
        COLOR_MAPPING_GRADIENTS.put("Random3", new Random3Filter());
        COLOR_MAPPING_GRADIENTS.put("Random4", new Random4Filter());
        // COLOR_MAPPING_GRADIENTS.put("Random5", new Random5Filter());
        // COLOR_MAPPING_GRADIENTS.put("Random6", new Random6Filter());
        COLOR_MAPPING_GRADIENTS.put("Real", new RealFilter());
        COLOR_MAPPING_GRADIENTS.put("Red", new RedFilter());
        COLOR_MAPPING_GRADIENTS.put("Smooth", new SmoothFilter());
        // COLOR_MAPPING_GRADIENTS.put("Smooth1", new Smooth1Filter());
        // COLOR_MAPPING_GRADIENTS.put("Smooth2", new Smooth2Filter());
        // COLOR_MAPPING_GRADIENTS.put("Smooth3", new Smooth3Filter());
        COLOR_MAPPING_GRADIENTS.put("Staircase", new StaircaseFilter());
        COLOR_MAPPING_GRADIENTS.put("Stairs8", new Stairs8Filter());
        COLOR_MAPPING_GRADIENTS.put("Stairs9", new Stairs9Filter());
    }

    public static JSkyFitsImage.ScaleAlgorithm DEFAULT_SCALE = JSkyFitsImage.ScaleAlgorithm.LINEAR;
    public static String DEFAULT_COLOR_LOOKUP_TABLE = "Ramp";
    static {
        JSkyFitsResourceHandler.DEFAULT_SCALE = DEFAULT_SCALE;
        JSkyFitsResourceHandler.DEFAULT_COLOR_LOOKUP_TABLE = DEFAULT_COLOR_LOOKUP_TABLE;
    }

}
