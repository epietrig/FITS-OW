/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.RGBImageFilter;

import java.util.LinkedHashMap;

import fr.inria.zvtm.widgets.PieMenuFactory;
import fr.inria.zvtm.glyphs.JSkyFitsImage;

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

class Config {

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

    // /* ------------ Glyph z-index ---------- */
    // static final int Z_MPMI = 10;
    // static final int Z_SPMI = 12;

    /* ------------ Glyph types ---------- */

    static final String T_MPMI = "mpm";
    static final String T_SPMI = "spm_";
    static final String T_SPMISc = T_SPMI + "sc";

    static final String T_FITS = "fits";


    /* ------------ Scales and color mappings ---------- */

    static final String SCALE_LINEAR = "LIN";
    static final String SCALE_LOG = "LOG";
    static final String SCALE_SQRT = "SQRT";
    static final String SCALE_HISTEQ = "HIST";

    static LinkedHashMap<String,JSkyFitsImage.ScaleAlgorithm> SCALES = new LinkedHashMap(4,1);
    static {
        SCALES.put(SCALE_LINEAR, JSkyFitsImage.ScaleAlgorithm.LINEAR);
        SCALES.put(SCALE_LOG, JSkyFitsImage.ScaleAlgorithm.LOG);
        SCALES.put(SCALE_HISTEQ, JSkyFitsImage.ScaleAlgorithm.HIST_EQ);
        SCALES.put(SCALE_SQRT, JSkyFitsImage.ScaleAlgorithm.SQRT);
    };
    static final String[] SCALE_LIST = SCALES.keySet().toArray(new String[SCALES.size()]);

    static final LinkedHashMap<String,RGBImageFilter> COLOR_MAPPINGS = new LinkedHashMap(40,1);
    static {
        COLOR_MAPPINGS.put("Standard", new StandardFilter());
        COLOR_MAPPINGS.put("Aips0", new Aips0Filter());
        COLOR_MAPPINGS.put("Background", new BackgrFilter());
        COLOR_MAPPINGS.put("Blue", new BlueFilter());
        COLOR_MAPPINGS.put("Blulut", new BlulutFilter());
        COLOR_MAPPINGS.put("Color", new ColorFilter());
        COLOR_MAPPINGS.put("Green", new GreenFilter());
        COLOR_MAPPINGS.put("Heat",  new HeatFilter());
        COLOR_MAPPINGS.put("Idl11", new Idl11Filter());
        COLOR_MAPPINGS.put("Idl12", new Idl12Filter());
        COLOR_MAPPINGS.put("Idl14", new Idl14Filter());
        COLOR_MAPPINGS.put("Idl15", new Idl15Filter());
        COLOR_MAPPINGS.put("Idl2", new Idl2Filter());
        COLOR_MAPPINGS.put("Idl4", new Idl4Filter());
        COLOR_MAPPINGS.put("Idl5", new Idl5Filter());
        COLOR_MAPPINGS.put("Idl6", new Idl6Filter());
        COLOR_MAPPINGS.put("Isophot", new IsophotFilter());
        COLOR_MAPPINGS.put("Light", new LightFilter());
        COLOR_MAPPINGS.put("Manycolor", new ManycolFilter());
        COLOR_MAPPINGS.put("Pastel", new PastelFilter());
        COLOR_MAPPINGS.put("Rainbow", new RainbowFilter());
        COLOR_MAPPINGS.put("Rainbow1", new Rainbow1Filter());
        COLOR_MAPPINGS.put("Rainbow2", new Rainbow2Filter());
        COLOR_MAPPINGS.put("Rainbow3", new Rainbow3Filter());
        COLOR_MAPPINGS.put("Rainbow4", new Rainbow4Filter());
        COLOR_MAPPINGS.put("Ramp", new RampFilter());
        COLOR_MAPPINGS.put("Random", new RandomFilter());
        COLOR_MAPPINGS.put("Random1", new Random1Filter());
        COLOR_MAPPINGS.put("Random2", new Random2Filter());
        COLOR_MAPPINGS.put("Random3", new Random3Filter());
        COLOR_MAPPINGS.put("Random4", new Random4Filter());
        //COLOR_MAPPINGS.put("Random5", new Random5Filter());
        //COLOR_MAPPINGS.put("Random6", new Random6Filter());
        COLOR_MAPPINGS.put("Real", new RealFilter());
        COLOR_MAPPINGS.put("Red", new RedFilter());
        COLOR_MAPPINGS.put("Smooth", new SmoothFilter());
        //COLOR_MAPPINGS.put("Smooth1", new Smooth1Filter());
        //COLOR_MAPPINGS.put("Smooth2", new Smooth2Filter());
        //COLOR_MAPPINGS.put("Smooth3", new Smooth3Filter());
        COLOR_MAPPINGS.put("Staircase", new StaircaseFilter());
        COLOR_MAPPINGS.put("Stairs8", new Stairs8Filter());
        COLOR_MAPPINGS.put("Stairs9", new Stairs9Filter());
    }
    static final String[] COLOR_MAPPING_LIST = COLOR_MAPPINGS.keySet().toArray(new String[COLOR_MAPPINGS.size()]);

}
