/*  (c) COPYRIGHT INRIA (Institut National de Recherche en Informatique et en Automatique), 2010-2015.
 *  Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id: $
 */

package fr.inria.ilda.simbad;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.DecimalFormat;

import jsky.science.Coordinates;

import fr.inria.ilda.fitsow.Config;
// import fr.inria.ilda.fitsow.MVEventListener;

/**
 * Performs queries on the Simbad catalog.
 */
public class SimbadCatQuery {

    private static String PREFIX = "output console=off script=off\n votable vot{main_id, ra(d), dec(d), otype,"
            + "pmra, pmdec, rv_value, z_value, sp, plx, mt,"
            + "flux(U), flux(V), flux(B), flux(R), flux(I), flux(J), flux(K), flux(H), flux(u), flux(g), flux(r), flux(i), flux(z), measurements}\n"
            + "votable open vot\n";
    private static String SUFIX = "\n" + "votable close";
    private static String SERVER_PREFIX = Config.SIMBAD_SERVER;

    public static List<AstroObject> makeSimbadCircleQuery(double ra, double dec, double radmin) throws IOException {
        Coordinates coords = new Coordinates(ra, dec);
        List<AstroObject> astroObjs = null;
        URL url;
        SimbadCriteria criteria = SimbadCriteria.getLastSimbadCriteria();
        String script = String.format(PREFIX + "query sample region(%s%s,%s)"+ queryOptionalCriteria(criteria) + SUFIX, coords.raToString(), coords.decToString(), Config.ARCMIN_FORMATTER.format(radmin));
        try {
            url = new URL(SERVER_PREFIX + URLEncoder.encode(script, "UTF-8"));
            astroObjs = SimbadParser.getVOTableAsAObjects(url);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
        return astroObjs;
    }


    public static List<AstroObject> makeSimbadCoordQuery(String ra, String dec, String radmin) throws IOException {
        Coordinates coords = new Coordinates(Double.parseDouble(ra), Double.parseDouble(dec));
        List<AstroObject> astroObjs = null;
        URL url;
        SimbadCriteria criteria = SimbadCriteria.getLastSimbadCriteria();
        String script = String.format(PREFIX + "query sample region(%s%s,%s)"+ queryOptionalCriteria(criteria) + SUFIX, coords.raToString(), coords.decToString(), Config.ARCMIN_FORMATTER.format(radmin));
        try {
            url = new URL(SERVER_PREFIX + URLEncoder.encode(script, "UTF-8"));
            astroObjs = SimbadParser.getVOTableAsAObjects(url);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
        return astroObjs;
    }

    public static List<AstroObject> makeSimbadIdQuery(String id) throws IOException {
        List<AstroObject> astroObjs = null;
        URL url;
        SimbadCriteria criteria = SimbadCriteria.getLastSimbadCriteria();
        String script = String.format(PREFIX +"query sample region(%s)" +queryOptionalCriteria(criteria) + SUFIX, id);
        try {
            url = new URL(SERVER_PREFIX + URLEncoder.encode(script, "UTF-8"));
            astroObjs = SimbadParser.getVOTableAsAObjects(url);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
        return astroObjs;
    }

    public static List<AstroObject> makeSimbadScriptQuery(String script) throws IOException {
        List<AstroObject> astroObjs = null;
        URL url;
        try {
            url = new URL(SERVER_PREFIX + URLEncoder.encode(script, "UTF-8"));
            astroObjs = SimbadParser.getVOTableAsAObjects(url);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
        return astroObjs;
    }

    private static String queryOptionalCriteria(SimbadCriteria criteria) {
        String measurementsQuery = SimbadQueryConstructor
                .measurementSelector(criteria.getMeasurements().getMeasurementsSelected());
        if (!measurementsQuery.equals(""))
            measurementsQuery = "&" + measurementsQuery;

        String objectTypeQuery = SimbadQueryConstructor
                .objectTypeSelector(criteria.getObjectTypeFilter().getOTSelected());
        if (!objectTypeQuery.equals(""))
            objectTypeQuery = "&" + objectTypeQuery;

        String properMotionQuery = SimbadQueryConstructor.properMotionSelector(criteria.getPMFilter().getRaStr(),
                criteria.getPMFilter().getDecStr(), criteria.getPMFilter().getQualitiesSelected());
        if (!properMotionQuery.equals(""))
            properMotionQuery = "& " + properMotionQuery;

        String parallaxQuery = SimbadQueryConstructor.parallaxSelector(criteria.getParallaxFilter().getParallaxStr(),
                criteria.getParallaxFilter().getQualitiesSelected());
        if (!parallaxQuery.equals(""))
            parallaxQuery = "& " + parallaxQuery;

        SimbadRVFilter rvFilter = criteria.getRVFilter();
        String rv = rvFilter.getRVStr();
        String z = rvFilter.getZStr();
        String cz = rvFilter.getCZStr();
        int[] q = rvFilter.getQualitiesSelected();
        String radialVelocityQuery = SimbadQueryConstructor.radialVelocitySelector(rv, z, cz, q);
        if (!radialVelocityQuery.equals(""))
            radialVelocityQuery = " & " + radialVelocityQuery;

        SimbadSTFilter stFilter = criteria.getSTFilter();
        String st = stFilter.getSTStr();
        String lc = stFilter.getLCStr();
        String pec = stFilter.getPecStr();
        int[] qst = stFilter.getQualitiesSelected();
        String spectralTypeQuery = SimbadQueryConstructor.spectralTypeSelector(st, lc, pec, qst);
        if (!spectralTypeQuery.equals(""))
            spectralTypeQuery = " & " + spectralTypeQuery;

        SimbadFluxFilter fluxFilter = criteria.getFluxFilter();
        String[] rangeStrs = fluxFilter.getRangeStrs();
        int[] qf = fluxFilter.getQualitiesSelected();
        int[] fluxes = fluxFilter.getFluxesSelected();
        String fluxRangeQuery = SimbadQueryConstructor.fluxRangeSelector(rangeStrs, qf);
        if (!fluxRangeQuery.equals(""))
            fluxRangeQuery = " & " + fluxRangeQuery;
        // String fluxQuery = SimbadQueryConstructor.fluxSelector(fluxes);

        return objectTypeQuery + properMotionQuery + parallaxQuery + radialVelocityQuery + spectralTypeQuery
                + fluxRangeQuery + measurementsQuery + "\n";
    }

}
