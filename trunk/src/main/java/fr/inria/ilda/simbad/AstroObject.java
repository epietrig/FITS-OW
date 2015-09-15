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

    public AstroObject(){}

    /**
     * @param simRowStr - simbad row formatted as per the CatQuery
     * format.
     */
    static AstroObject fromSimbadRow(String simRowStr){
        String[] keys = {"ID", "COORDA", "COORDD", "PM", "RV", "SP", "PLX", "MT", "DIM", "OTYPE", "FLUXES"};
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
        System.out.println(elems[elems.length-2]);
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
        return identifier + " | " + coords.getRa() + " | " + coords.getDec() +
        "|" + basicData.get("PM") + basicData.get("RV") + basicData.get("SP") +
        basicData.get("PLX") + basicData.get("MT") + basicData.get("DIM");
    }
}
