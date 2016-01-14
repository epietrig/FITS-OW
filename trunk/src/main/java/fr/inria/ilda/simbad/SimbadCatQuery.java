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

    private static String formatString = "output console=off script=off\n" +
      "format object \"%%IDLIST(1)#%%COO(d;A)#%%COO(d;D)"+
      "$%%OTYPE(V)#"+
      "%%COO(A,D,(W),Q,[E],B;ICRS;J2000)#"+
      "%%COO(A,D,(W),Q,[E],B;FK5;J2000;2000)#"+
      "%%COO(A,D,(W),Q,[E],B;FK4;B1950;1950)#"+
      "%%COO(A,D,(W),Q,[E],B;GAL;J2000)#"+
      "%%PM(A,D,Q,E)#%%RV(V,Z,W,Q,E)#%%SP(S,Q)#%%PLX(V,Q,E)#%%MT(M,Q)#";

    private static String formatString2 = "output console=off script=off\n" +
        "format object \"%IDLIST(1)#%COO(d;A)#%COO(d;D)"+
        "$%OTYPE(V)#"+
        "%COO(A,D,(W),Q,[E],B;ICRS;J2000)#"+
        "%COO(A,D,(W),Q,[E],B;FK5;J2000;2000)#"+
        "%COO(A,D,(W),Q,[E],B;FK4;B1950;1950)#"+
        "%COO(A,D,(W),Q,[E],B;GAL;J2000)#"+
        "%PM(A,D,Q,E)#%RV(V,Z,W,Q,E)#%SP(S,Q)#%PLX(V,Q,E)#%MT(M,Q)#"+
        "#%FLUXLIST(; N = F (Q) B,)$"+
        "#%MEASLIST(cel;AH)#%MEASLIST(cl.g;AH)#%MEASLIST(diameter;AH)"+
        "#%MEASLIST(distance;AH)#%MEASLIST(einstein;AH)#%MEASLIST(fe_h;AH)"+
        "#%MEASLIST(gcrv;AH)#%MEASLIST(gen;AH)#%MEASLIST(gj;AH)#%MEASLIST(hbet;AH)"+
        "#%MEASLIST(hbet1;AH)#%MEASLIST(herschel;AH)#%MEASLIST(hgam;AH)"+
        "#%MEASLIST(iras;AH)#%MEASLIST(irc;AH)#%MEASLIST(iso;AH)#%MEASLIST(iue;AH)"+
        "#%MEASLIST(jp11;AH)#%MEASLIST(mk;AH)#%MEASLIST(orv;AH)#%MEASLIST(plx;AH)"+
        "#%MEASLIST(pm;AH)#%MEASLIST(pos;AH)#%MEASLIST(posa;AH)#%MEASLIST(rot;AH)"+
        "#%MEASLIST(rvel;AH)#%MEASLIST(sao;AH)#%MEASLIST(td1;AH)#%MEASLIST(ubv;AH)"+
        "#%MEASLIST(uvby;AH)#%MEASLIST(uvby1;AH)#%MEASLIST(v*;AH)#%MEASLIST(velocities;AH)"+
        "#%MEASLIST(xmm;AH)#%MEASLIST(z;AH)#%MEASLIST(ze;AH)#$$ \"\n";

    public static List<AstroObject> makeSimbadCoordQuery(double ra, double dec, double radmin) throws IOException{
        URL queryUrl = makeSimbadCoordQueryUrl(ra, dec, radmin);
        List<String> objLines = SimbadParser.splitURLIntoStrings(queryUrl);
        List<AstroObject> astroObjs = SimbadParser.stringsToAstroObjects(objLines);
        return astroObjs;
    }
    public static List<AstroObject> makeSimbadCoordQuery(String ra, String dec, String radmin) throws IOException{
        URL queryUrl = makeSimbadCoordQueryUrl(ra, dec, radmin);
        List<String> objLines = SimbadParser.splitURLIntoStrings(queryUrl);
        List<AstroObject> astroObjs = SimbadParser.stringsToAstroObjects(objLines);
        return astroObjs;
    }
    public static List<AstroObject> makeSimbadIdQuery(String id) throws IOException{
        URL queryUrl = makeSimbadIdQueryUrl(id);
        List<String> objLines = SimbadParser.splitURLIntoStrings(queryUrl);
        List<AstroObject> astroObjs = SimbadParser.stringsToAstroObjects(objLines);
        return astroObjs;
    }

    public static List<AstroObject> makeSimbadScriptQuery(String script) throws IOException{
        String query = formatString2 +script+"\n";
        System.out.println("format+scritp:"+query);
        URL queryUrl = makeSimbadScriptQueryUrl(query);
        List<String> objLines = SimbadParser.splitURLIntoStrings(queryUrl);
        List<AstroObject> astroObjs = SimbadParser.stringsToAstroObjects(objLines);
        return astroObjs;
    }

    private static URL makeSimbadCoordQueryUrl(double ra, double dec,
            double radMin){
              // String fix_ra = "18 18 52.56";
              // String fix_dec = "-13 49 41.6";
              // String fix_radius = "150";
        try{
            Coordinates coords = new Coordinates(ra, dec);
            SimbadCriteria criteria = SimbadCriteria.getLastSimbadCriteria();
            String script =
            String.format(formatString+
                    queryOptionalFilters(criteria)+
                    "query sample region(%s%s,%sm)"
                    + queryOptionalCriteria(criteria),
                    // fix_ra,fix_dec,fix_radius
                    coords.raToString(),coords.decToString(),Config.ARCMIN_FORMATTER.format(radMin)
                    );
            // formatString+queryOptionalFilters(criteria)+"query sample "+ queryOptionalCriteria(criteria);
            // System.out.println("radius: "+fix_radius);
            // System.out.println("ra: "+coords.raToString());
            // System.out.println("dec: "+coords.decToString());
            System.out.println("script: "+script);
            return makeSimbadScriptQueryUrl(script);

        } catch (MalformedURLException ex){
            //we are supposed to create well-formed URLs here...
            throw new Error(ex);
        }
    }
    private static URL makeSimbadCoordQueryUrl(String ra, String dec,
            String radMin){
              // String fix_ra = "18 18 52.56";
              // String fix_dec = "-13 49 41.6";
              // String fix_radius = "150";
        try{
            // Coordinates coords = new Coordinates(ra, dec);
            SimbadCriteria criteria = SimbadCriteria.getLastSimbadCriteria();
            String script =
            String.format(formatString+
                    queryOptionalFilters(criteria)+
                    "query sample region(%s%s,%s)"
                    + queryOptionalCriteria(criteria),
                    // fix_ra,fix_dec,fix_radius
                    ra,dec,radMin
                    );
            // formatString+queryOptionalFilters(criteria)+"query sample "+ queryOptionalCriteria(criteria);
            // System.out.println("radius: "+fix_radius);
            // System.out.println("ra: "+coords.raToString());
            // System.out.println("dec: "+coords.decToString());
            System.out.println("script: "+script);
            return makeSimbadScriptQueryUrl(script);

        } catch (MalformedURLException ex){
            //we are supposed to create well-formed URLs here...
            throw new Error(ex);
        }
    }

    private static URL makeSimbadIdQueryUrl(String id){
        try{
            // Coordinates coords = new Coordinates(ra, dec);
            SimbadCriteria criteria = SimbadCriteria.getLastSimbadCriteria();
            String script =
            String.format(formatString+
                    queryOptionalFilters(criteria)+
                    "query sample region(%s)"
                    + queryOptionalCriteria(criteria),
                    // fix_ra,fix_dec,fix_radius
                    id
                    );

                    System.out.println("script: "+script);
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

    private static String queryOptionalFilters(SimbadCriteria criteria){
      String measurementsQuery = SimbadQueryConstructor.measurementSelector(criteria.getMeasurements().getMeasurementsSelected());
      SimbadFluxFilter fluxFilter = criteria.getFluxFilter();
      int[] fluxes = fluxFilter.getFluxesSelected();
      String fluxQuery = SimbadQueryConstructor.fluxSelector(fluxes);
      return "%%"+fluxQuery+"$#"+measurementsQuery +"$$ \"\n";
    }

    private static String queryOptionalCriteria(SimbadCriteria criteria){
      String measurementsQuery = SimbadQueryConstructor.measurementSelector(criteria.getMeasurements().getMeasurementsSelected());

      String objectTypeQuery = SimbadQueryConstructor.objectTypeSelector(criteria.getObjectTypeFilter().getOTSelected());
      if(!objectTypeQuery.equals("")) objectTypeQuery =
      //"&"+
      objectTypeQuery;

      String properMotionQuery = SimbadQueryConstructor.properMotionSelector(criteria.getPMFilter().getRaStr(),
        criteria.getPMFilter().getDecStr(),criteria.getPMFilter().getQualitiesSelected());
      if(!properMotionQuery.equals("")) properMotionQuery = "& "+ properMotionQuery;

      String parallaxQuery = SimbadQueryConstructor.parallaxSelector(criteria.getParallaxFilter().getParallaxStr(),
        criteria.getParallaxFilter().getQualitiesSelected());
      if(!parallaxQuery.equals("")) parallaxQuery = "& "+parallaxQuery;

      SimbadRVFilter rvFilter = criteria.getRVFilter();
      String rv = rvFilter.getRVStr();
      String z = rvFilter.getZStr();
      String cz = rvFilter.getCZStr();
      int[] q = rvFilter.getQualitiesSelected();
      String radialVelocityQuery = SimbadQueryConstructor.radialVelocitySelector(rv, z, cz,q);
      if(!radialVelocityQuery.equals("")) radialVelocityQuery = " & "+radialVelocityQuery;

      SimbadSTFilter stFilter = criteria.getSTFilter();
      String st = stFilter.getSTStr();
      String lc = stFilter.getLCStr();
      String pec = stFilter.getPecStr();
      int[] qst = stFilter.getQualitiesSelected();
      String spectralTypeQuery = SimbadQueryConstructor.spectralTypeSelector(st, lc, pec,qst);
      if(!spectralTypeQuery.equals("")) spectralTypeQuery = " & "+spectralTypeQuery;

      SimbadFluxFilter fluxFilter = criteria.getFluxFilter();
      String[] rangeStrs = fluxFilter.getRangeStrs();
      int[] qf = fluxFilter.getQualitiesSelected();
      int[] fluxes = fluxFilter.getFluxesSelected();
      String fluxRangeQuery = SimbadQueryConstructor.fluxRangeSelector(rangeStrs, qf);
      if(!fluxRangeQuery.equals("")) fluxRangeQuery = " & "+  fluxRangeQuery;
      String fluxQuery = SimbadQueryConstructor.fluxSelector(fluxes);


      return objectTypeQuery+properMotionQuery+parallaxQuery+
      radialVelocityQuery+spectralTypeQuery+fluxRangeQuery
      +"\n";
    }

}
