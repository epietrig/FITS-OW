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
import fr.inria.ilda.fitsow.MVEventListener;

/**
 * Performs queries on the Simbad catalog.
 */
public class SimbadCatQuery {
    //
    // public static void main(String[] args) throws Exception{
    //     Locale.setDefault(new Locale("en", "US"));
    //     //attempt to retrieve objects from the Simbad catalog
    //     List<AstroObject> objs = makeSimbadCoordQuery(1, 4, 12);
    //     for(AstroObject obj: objs){
    //         System.err.println(obj);
    //     }
    // }
    public static List<AstroObject> makeSimbadCoordQuery(double ra, double dec, double radmin, MVEventListener listener) throws IOException{
        URL queryUrl = makeSimbadCoordQueryUrl(ra, dec, radmin, listener);
        List<String> objLines = SimbadParser.splitURLIntoStrings(queryUrl);
        List<AstroObject> astroObjs = SimbadParser.stringsToAstroObjects(objLines);
        return astroObjs;
        // return parseObjectList(readLines(queryUrl));
    }

    private static URL makeSimbadCoordQueryUrl(double ra, double dec,
            double radMin, MVEventListener listener){
        try{
            // Coordinates query script example:
            // format object "%IDLIST(1)|%COO(d;A)|%COO(d;D)"
            // query coo 12 30 +10 20 radius=6m

            Coordinates coords = new Coordinates(ra, dec);
            // look at http://simbad.u-strasbg.fr/simbad/sim-help?Page=sim-url
            // for more information about possible parameters
            SimbadCriteria criteria = listener.getLastSimbadCriteria();
            String measurementsQuery = SimbadQueryConstructor.measurementSelector(criteria.getMeasurements().getAllSelected());
            String objectTypeQuery = SimbadQueryConstructor.objectTypeSelector(criteria.getObjectTypeFilter().getAllSelected());
            if(!objectTypeQuery.equals("")) objectTypeQuery = "&"+objectTypeQuery;
            String properMotionQuery = SimbadQueryConstructor.properMotionSelector(criteria.getPMFilter().getRaStr(),
              criteria.getPMFilter().getDecStr(),criteria.getPMFilter().getQualitiesSelected());
            if(!properMotionQuery.equals("")) properMotionQuery = "& "+ properMotionQuery;
            String parallaxQuery = SimbadQueryConstructor.parallaxSelector(criteria.getParallaxFiler().getParallaxStr(),
              criteria.getParallaxFiler().getQualitiesSelected());
            if(!parallaxQuery.equals("")) parallaxQuery = "& "+parallaxQuery;
            SimbadRVFilter rvFilter = criteria.getRVFilter();
            String rv = rvFilter.getRVStr();
            String z = rvFilter.getZStr();
            String cz = rvFilter.getCZStr();
            int[] q = rvFilter.getQualitiesSelected();
            String radialVelocityQuery = SimbadQueryConstructor.radialVelocitySelector(rv, z, cz,q);
            if(!radialVelocityQuery.equals("")) radialVelocityQuery = " & "+radialVelocityQuery;
            String script =
             String.format(
                    "output console=off script=off\n" +
                    // "format ASCII "+
                    "format object \"%%IDLIST(1)#%%COO(d;A)#%%COO(d;D)"+
                    "$%%OTYPE(V)#"+
                    "%%COO(A,D,(W),Q,[E],B;ICRS;J2000)#"+
                    "%%COO(A,D,(W),Q,[E],B;FK5;J2000;2000)#"+
                    "%%COO(A,D,(W),Q,[E],B;FK4;B1950;1950)#"+
                    "%%COO(A,D,(W),Q,[E],B;GAL;J2000)#"+
                    "%%PM(A,D,Q,E)#%%RV(V,Z,W,Q,E)#%%SP(S,Q)#%%PLX(V,Q,E)#%%MT(M,Q)#"+
                    "%%FLUXLIST(; N = F (Q) B,)$#"+
                    measurementsQuery +
                    "$$ \"\n" +
                    // "query coo %s %s radius=%sm\n"+
                    "query sample region(%s%s,%sm)"+objectTypeQuery+properMotionQuery+parallaxQuery+
                    radialVelocityQuery+"\n",
                    //the 'replace' operation is ugly, should be improved
                    // coords.raToString().replace(',', '.'),
                    // coords.decToString().replace(',','.')
                    // fixed by forcing the Locale to en/US
                    //|%%PM|%%RV|%%FLUXLIST|%%SP|%%MT|%%DIM
                    coords.raToString(),
                    coords.decToString(),
                    Config.ARCMIN_FORMATTER.format(radMin)
                    );
                    System.out.println(script);
            return makeSimbadScriptQueryUrl(script);
        } catch (MalformedURLException ex){
            //we are supposed to create well-formed URLs here...
            throw new Error(ex);
        }
    }

    private static URL makeSimbadScriptQueryUrl(String script) throws MalformedURLException {
        String prefix = "http://simbad.u-strasbg.fr/simbad/sim-script?script=";
        // String prefix = "http://simbak.cfa.harvard.edu/simbad/sim-script?script=";
        try{
            return new URL(prefix + URLEncoder.encode(script, "UTF-8"));
        } catch (UnsupportedEncodingException eex){
            //Java implementations are required to offer UTF-8 encoding
            //support, so we should not trigger this.
            //See http://download.oracle.com/javase/1.3/docs/api/java/lang/package-summary.html#charenc
            throw new Error(eex);
        }

    }

}
