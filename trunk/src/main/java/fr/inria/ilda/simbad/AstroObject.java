/*  (c) COPYRIGHT INRIA (Institut National de Recherche en Informatique et en Automatique), 2010-2015.
 *  Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id:  $
 */

package fr.inria.ilda.simbad;

import jsky.science.Coordinates;
import java.util.HashMap;
import fr.inria.ilda.fitsow.Config;

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
      String[] keys = Config.BD_KEYS;
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
      for(int i = 3; i < 13; i++){
        String elementFirstComponent = elems[i].split(",")[0];
        if (!elementFirstComponent.trim().contains("~")){
          retval.basicData.put(keys[i-3], elems[i]);
        } //element is not empty
      }
      for(String el : elems){
        System.out.println(el);

      }
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

    public String basicDataToString(){
      String[] keys = Config.BD_KEYS;
      String retval = "";
      for(int i = 0; i < keys.length; i++){
        String value = basicData.get(keys[i]);
        if(value!=null){
          retval = retval + keys[i]+ value + "\n";
        }
      }
      return retval;
    }
}
