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

/**
 * Performs queries on the Simbad catalog.
 */
public class SimbadCatQuery {

    public static void main(String[] args) throws Exception{
        Locale.setDefault(new Locale("en", "US"));
        //attempt to retrieve objects from the Simbad catalog
        List<AstroObject> objs = makeSimbadCoordQuery(1, 4, 12);
        for(AstroObject obj: objs){
            System.err.println(obj);
        }
    }

    public static List<AstroObject> makeSimbadCoordQuery(double ra, double dec, double radmin) throws IOException{
        URL queryUrl = makeSimbadCoordQueryUrl(ra, dec, radmin);
        List<String> objLines = SimbadParser.splitURLIntoStrings(queryUrl);
        List<AstroObject> astroObjs = SimbadParser.stringsToAstroObjects(objLines);
        return astroObjs;
        // return parseObjectList(readLines(queryUrl));
    }

    private static URL makeSimbadCoordQueryUrl(double ra, double dec,
            double radMin){
        try{
            // Coordinates query script example:
            // format object "%IDLIST(1)|%COO(d;A)|%COO(d;D)"
            // query coo 12 30 +10 20 radius=6m

            Coordinates coords = new Coordinates(ra, dec);
            // look at http://simbad.u-strasbg.fr/simbad/sim-help?Page=sim-url
            // for more information about possible parameters
            String script = String.format(
                    "output console=off script=off\n" +
                    "format object \"%%IDLIST(1)#%%COO(d;A)#%%COO(d;D)"+
                    "_%%OTYPE(V)#"+
                    "%%COO(A,D,(W),Q,[E],B;ICRS;J2000)#"+
                    "%%COO(A,D,(W),Q,[E],B;FK5;J2000;2000)#"+
                    "%%COO(A,D,(W),Q,[E],B;FK4;B1950;1950)#"+
                    "%%COO(A,D,(W),Q,[E],B;GAL;J2000)#"+
                    "%%PM(A,D,Q,E)#%%RV(V,Z,W,Q,E)#%%SP(S,Q)#%%PLX(V,Q,E)#%%MT(M,Q)#"+
                    "%%FLUXLIST(U,B,V,R,I,J,H,K)_%%MEASLIST$ \"\n" +
                    "query coo %s %s radius=%sm",
                    //XXX the 'replace' operation is ugly, should be improved
                    // coords.raToString().replace(',', '.'),
                    // coords.decToString().replace(',','.')
                    // fixed by forcing the Locale to en/US
                    //|%%PM|%%RV|%%FLUXLIST|%%SP|%%MT|%%DIM
                    coords.raToString(),
                    coords.decToString(),
                    Config.ARCMIN_FORMATTER.format(radMin));
            return makeSimbadScriptQueryUrl(script);
        } catch (MalformedURLException ex){
            //we are supposed to create well-formed URLs here...
            throw new Error(ex);
        }
    }

    private static URL makeSimbadScriptQueryUrl(String script) throws MalformedURLException {
        //String prefix = "http://simbad.u-strasbg.fr/simbad/sim-script?script=";
        String prefix = "http://simbak.cfa.harvard.edu/simbad/sim-script?script=";
        try{
            return new URL(prefix + URLEncoder.encode(script, "UTF-8"));
        } catch (UnsupportedEncodingException eex){
            //Java implementations are required to offer UTF-8 encoding
            //support, so we should not trigger this.
            //See http://download.oracle.com/javase/1.3/docs/api/java/lang/package-summary.html#charenc
            throw new Error(eex);
        }

    }

    //A better version should deal with http errors
    // private static List<String> readLines(URL url) throws IOException{
    //     List<String> result = SimbadParser.separateObjects(url);
    //     for(String s:result){
    //       System.out.println("result: "+s);
    //     }

        // URLConnection uc = url.openConnection();
        // BufferedReader in = new BufferedReader(new InputStreamReader(
        //             uc.getInputStream()));
        // List<String> result = new ArrayList<String>();
        // String toAppend;
        // while((toAppend = in.readLine()) != null){
          // System.out.println("URL: "+toAppend);
            // result.add(toAppend);
        // }
        // in.close();
        // return result;
    // }

    // private static List<AstroObject> parseObjectList(List<String> strList){
    //     ArrayList<AstroObject> retval = new ArrayList<AstroObject>();
    //     try{
    //       for(String objStr: strList){
    //           AstroObject candidate = AstroObject.fromSimbadRow(objStr);
    //
    //           if(candidate != null){
    //               retval.add(candidate);
    //           }
    //       }
    //       return retval;
    //     }catch(Exception e){
    //       System.out.println("caught something!");
    //       e.printStackTrace();
    //     }
    //     return retval;
    // }
}
