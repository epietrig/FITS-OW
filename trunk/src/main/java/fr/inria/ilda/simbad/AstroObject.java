/*  (c) COPYRIGHT INRIA (Institut National de Recherche en Informatique et en Automatique), 2010-2015.
 *  Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id:  $
 */

package fr.inria.ilda.simbad;

import jsky.science.Coordinates;
import java.util.HashMap;

public class AstroObject {

    private String identifier;
    private Coordinates coords;
    private HashMap<String, String> basicData;
    public static int BASIC_DATA_LENGTH = 5;

    public AstroObject(){}

    /**
     * @param simRowStr - simbad row formatted as per the CatQuery
     * format.
     */
    static AstroObject fromSimbadRow(String simRowStr){
        String[] keys = {"ID", "COORDA", "COORDD", "OTYPE","C1","C2","C3","C4","PM", "RV", "SP", "PLX", "MT", "DIM", "FLUXES"};
        AstroObject retval = new AstroObject();
        retval.basicData = new HashMap<String, String>();

        String[] elems = simRowStr.split("\\|");
        if(elems.length < 3){
            //this does not look like a valid row
            return null;
        }
        retval.identifier = elems[0];
        retval.coords = new Coordinates(Double.parseDouble(elems[1]),
                Double.parseDouble(elems[2]));
        //saving basic data
        for(int i = 3; i < elems.length-1; i++){
          String elementFirstComponent = elems[i].split(",")[0];
          if (!elementFirstComponent.trim().contains("~")){
            retval.basicData.put(keys[i], elems[i]);
          } //element is not empty
        }
        // saving fluxes in basic data
        retval.basicData.put(keys[elems.length-1],elems[elems.length-1]);
        return retval;
    }

    public Coordinates getCoords(){
        return coords;
    }

    /**
     * Returns the right ascension of the object, in degrees.
     */
    public double getRa(){
        return coords.getRa();
    }

    /**
     * Returns the declination of the object, in degrees.
     */
    public double getDec(){
        return coords.getDec();
    }

    public String getIdentifier(){
        return identifier;
    }

    public HashMap<String,String> getBasicData(){
      return basicData;
    }

    public String toString(){
        return
        "Object type: "+basicData.get("OTYPE")+"\n"+
        "C1: "+basicData.get("C1")+"\n"+
        "C2: "+basicData.get("C2")+"\n"+
        "C3: "+basicData.get("C3")+"\n"+
        "C4: "+basicData.get("C4")+"\n"+
        "Proper motion (mas/yr): " + basicData.get("PM") + "\n"+
               "Radial velocity (km/s): " + basicData.get("RV") + "\n"+
               "Spectral type :         " + basicData.get("SP") + "\n"+
               "Parallaxes (mas) :      " + basicData.get("PLX") +"\n"+
               "Fluxes :                \n"+ basicData.get("FLUXES");
    }
}
