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

import java.text.DecimalFormat;

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

    public static String HTTPD_IP = "127.0.0.1";
    public static int HTTPD_PORT = 8081;

    public static final float PAN_GESTURE_GAIN = 8f;

    public static final Color BACKGROUND_COLOR = Color.BLACK;
    public static final Color CURSOR_COLOR = Color.WHITE;

    public static final Color FITS_IMG_BORDER_COLOR = Color.GRAY;
    public static final Color FITS_IMG_BORDER_COLOR_CI = Color.LIGHT_GRAY;
    public static final Color PDF_BORDER_COLOR = Color.BLACK;
    public static final Color PDF_BORDER_COLOR_CI = Color.LIGHT_GRAY;

    public static boolean MASTER_ANTIALIASING = true;
    public static boolean CLUSTER_ANTIALIASING = true;

    public static final Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 12);
    public static final Font WALL_MENU_FONT = new Font("Dialog", Font.PLAIN, 32);
    public static final Font GLASSPANE_FONT = new Font("Arial", Font.PLAIN, 12);
    public static final Font SIMBAD_FONT = new Font("Arial", Font.PLAIN, 14);

    public static final Color INFO_BAR_BACKGROUND = Color.BLACK;
    public static final Color INFO_BAR_FOREGROUND = Color.LIGHT_GRAY;
    public static final int INFO_BAR_HEIGHT = 16;

    /* PIEMENU */

    public static final Font PIEMENU_FONT = WALL_MENU_FONT;

    public static Color PIEMENU_FILL_COLOR = Color.BLACK;
    public static Color PIEMENU_BORDER_COLOR = Color.WHITE;
    public static Color PIEMENU_INSIDE_COLOR = Color.DARK_GRAY;

    static {
        PieMenuFactory.setItemFillColor(PIEMENU_FILL_COLOR);
        PieMenuFactory.setItemBorderColor(PIEMENU_BORDER_COLOR);
        PieMenuFactory.setSelectedItemFillColor(PIEMENU_INSIDE_COLOR);
        PieMenuFactory.setSelectedItemBorderColor(null);
        PieMenuFactory.setLabelColor(PIEMENU_BORDER_COLOR);
        PieMenuFactory.setFont(PIEMENU_FONT);
        PieMenuFactory.setAngle(0);
    }

    /* ------------ Glyph z-index ---------- */
    // static final int Z_MPMI = 10;
    // static final int Z_SPMI = 12;
    // color lookup table buttons
    public static final int Z_CLT_BKG = 100;
    public static final int Z_CLT_BTN = 110;
    public static final int Z_QUERY_REGION = 50;
    public static final int Z_ASTRO_OBJ_CR = 100;
    public static final int Z_ASTRO_OBJ_LB = 99;
    public static final int Z_FITS_IMG = 10;
    public static final int Z_PDF_DOC = 10;

    /* ------------ Glyph types ---------- */

    public static final String T_MPMI = "mpm";
    public static final String T_SPMI = "spm_";
    public static final String T_SPMISc = T_SPMI + "sc";

    public static final String T_FITS = "fits";
    public static final String T_PDF = "pdf";

    public static final String T_CLT_BTN = "clt";

    public static final String T_ASTRO_OBJ_PREFIX = "ao";
    public static final String T_ASTRO_OBJ_CR = T_ASTRO_OBJ_PREFIX + "Cr";
    public static final String T_ASTRO_OBJ_LB = T_ASTRO_OBJ_PREFIX + "Lb";
    public static final String T_ASTRO_OBJ_SR = "SimbadResults";
    public static final String T_ASTRO_OBJ_BINFO = "SimbadInfo";
    public static final String T_ASTRO_OBJ_MT = "SimbadMeasurementTable";
    public static final String T_ASTRO_OBJ_SC = "SimbadCriteria";
    public static final String T_ASTRO_OBJ_SQTS = "SimbadQueryTypeSelector";
    public static final String T_ASTRO_OBJ_SCQ = "SimbadClearQuery";


    /* ------------ Scales and color mappings ---------- */

    public static final JSkyFitsImage.ScaleAlgorithm[] SCALE_LIST = {
        JSkyFitsImage.ScaleAlgorithm.LINEAR,
        JSkyFitsImage.ScaleAlgorithm.LOG,
        JSkyFitsImage.ScaleAlgorithm.HIST_EQ,
        JSkyFitsImage.ScaleAlgorithm.SQRT
    };

    public static final String[][] COLOR_MAPPINGS = {
            {"Standard", "Aips0", "Background", "Color"},
            {"Red", "Green", "Blue", "Blulut", "Ramp", "Real", "Heat"},
            {"Light", "Pastel", "Smooth"},
            {"Idl2", "Idl4", "Idl5", "Idl6", "Idl11", "Idl12", "Idl14", "Idl15"},
            {"Isophot", "Manycolor", "Stairs8", "Stairs9"},
            {"Random", "Random1", "Random2", "Random3", "Random4"},
            {"Rainbow", "Rainbow1", "Rainbow2", "Rainbow3", "Rainbow4"}
    };
    public static int LARGEST_COLOR_MAPPING_CAT;
    static {
        LARGEST_COLOR_MAPPING_CAT = COLOR_MAPPINGS[0].length;
        for (int i=1;i<COLOR_MAPPINGS.length;i++){
            if (LARGEST_COLOR_MAPPING_CAT < COLOR_MAPPINGS[i].length){
                LARGEST_COLOR_MAPPING_CAT = COLOR_MAPPINGS[i].length;
            }
        }
    }

    public static String[] COLOR_MAPPING_LIST;
    static {
        Vector<String> v = new Vector();
        for (String[] aos:COLOR_MAPPINGS){
            for (String s:aos){
                v.add(s);
            }
        }
        COLOR_MAPPING_LIST = v.toArray(new String[v.size()]);
    }

    public static final HashMap<String,RGBImageFilter> COLOR_MAPPING_GRADIENTS = new HashMap(40,1);
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

    public static JSkyFitsImage.ScaleAlgorithm DEFAULT_SCALE = JSkyFitsResourceHandler.DEFAULT_SCALE;
    public static String DEFAULT_COLOR_LOOKUP_TABLE = "Ramp";
    static {
        JSkyFitsResourceHandler.DEFAULT_SCALE = DEFAULT_SCALE;
        JSkyFitsResourceHandler.DEFAULT_COLOR_LOOKUP_TABLE = DEFAULT_COLOR_LOOKUP_TABLE;
    }

    /* ------------ Widget geometry and color ---------- */
    // color lookup table buttons
    public static final int CLT_BTN_PADDING = 18;
    public static final int CLT_BTN_W = 300;
    public static final int CLT_BTN_H = 30;
    public static final int CLT_BTN_HOFFSET = 9;
    public static final int CLT_BTN_VOFFSET = 6;
    public static final BasicStroke CLT_BTN_SEL_STROKE = new BasicStroke(3f);
    public static final Color CLT_BTN_SEL_COLOR = Color.WHITE;
    public static final Color CLT_BTN_BORDER_COLOR = Color.GRAY;

    public static final double CLT_MENU_W = COLOR_MAPPINGS.length * (CLT_BTN_W + 2*CLT_BTN_PADDING);
    public static final double CLT_MENU_H = LARGEST_COLOR_MAPPING_CAT * (CLT_BTN_H + 2*CLT_BTN_PADDING);

    public static final double FITS_THUMB_MARGIN = 60;

    /* ------------ Simbad queries ---------- */

    public static final String SIMBAD_SERVER_STRASBOURG = "http://simbad.u-strasbg.fr/simbad/sim-script?script=";
    public static final String SIMBAD_SERVER_HARVARD = "http://simbak.cfa.harvard.edu/simbad/sim-script?script=";
    public static final String SIMBAD_SERVER = SIMBAD_SERVER_STRASBOURG;

    public static final BasicStroke QUERY_REGION_STROKE = new BasicStroke(2f);
    public static final Color QUERY_REGION_COLOR = Color.RED;
    public static final float QUERY_REGION_ALPHA = .5f;
    public static final double QUERY_REGION_LB_OFFSET = 1.1;

    public static final BasicStroke SIMBAD_AO_STROKE = new BasicStroke(2f);
    public static final Color SIMBAD_AO_COLOR = Color.RED;
    public static final Color SIMBAD_AO_LBCOLOR = Color.WHITE;
    public static final Color SIMBAD_AO_BACKGROUND = Color.BLACK;
    public static final float SIMBAD_AO_ALPHA = .7f;

    public static final double TEXT_SIZE = 20;
    public static final double OFFSET = 5;
    public static final Color UNSELECTED_BACKGROUND_COLOR = Color.gray;
    public static final Color SELECTED_BACKGROUND_COLOR = new Color(195, 195, 195);
    public static final Color TEXT_COLOR = Color.black;
    public static final Color SELECTED_TEXT_COLOR = new Color(34,34,34);

    public static final DecimalFormat ARCMIN_FORMATTER = new DecimalFormat("#0.0000");

    public static final String BD_OT = "Object type :";
    public static final String BD_COORD_ICRS = "ICRS coord. (ep=J2000) :";
    public static final String BD_COORD_FK5 = "FK5 coord. (ep=J2000 eq=2000) :";
    public static final String BD_COORD_FK4 = "FK4 coord. (ep=B1950 eq=1950) :";
    public static final String BD_COORD_GAL = "Gal coord. (ep=J2000) :";
    public static final String BD_PM = "Proper motions mas/yr :";
    public static final String BD_RV = "Radial velocity / Redshift / cz :";
    public static final String BD_PLX = "Parallaxes mas :";
    public static final String BD_SP = "Spectral type :";
    public static final String BD_FLUXES = "Fluxes :";
    public static final String BD_MT = "Morpholocial type :";
    public static final String[] BD_KEYS = {BD_OT, BD_COORD_ICRS,BD_COORD_FK5,
      BD_COORD_FK4, BD_COORD_GAL, BD_PM, BD_RV, BD_SP, BD_PLX, BD_MT, BD_FLUXES};
    public static final String[] CATALOGS = {"cel","cl.g","diameter","distance","einstein",
      "fe_h","gcrv","gen","gj","hbet","hbet1","herschel","hgam","iras","irc","iso","iue","jp11", "mk",
      "orv","plx","pm","pos","posa","rot","rvel","sao","td1","ubv","uvby","uvby1",
      "v*","velocities","xmm","z","ze"};
    public static String[] OBJECT_TYPES = {"Star", "Galaxy", "InterStellar Matter",
      "Multiple Object", "Candidates", "Gravitation", "Inexistent", "Radio", "IR", "Red", "Blue", "UV", "X", "gamma"};
    public static String[] FLUX_TYPES = {"U", "V", "B", "R","I","J","K","H","u","g","r","i","z"};



}
