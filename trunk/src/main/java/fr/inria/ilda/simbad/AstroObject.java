/*  (c) COPYRIGHT INRIA (Institut National de Recherche en Informatique et en Automatique), 2010-2015.
 *  Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id:  $
 */

package fr.inria.ilda.simbad;

import jsky.science.Coordinates;
import java.util.HashMap;
import fr.inria.ilda.fitsow.Config;
import java.util.Vector;

public class AstroObject {

    private String identifier;
    private Coordinates coords;
    private HashMap<String, String> basicData;
    private String[] fluxes;
    private Vector<Measurement> measurements;
    public static int BASIC_DATA_LENGTH = 5;

    public AstroObject(){}
    public AstroObject(String identifier, Coordinates coords, HashMap<String,String> basicData, String[] fluxes, Vector<Measurement> measurements){
      this.identifier = identifier;
      this.coords = coords;
      this.basicData = basicData;
      this.fluxes = fluxes;
      this.measurements = measurements;
    }
    /**
     * @param simRowStr - simbad row formatted as per the CatQuery
     * format.
     */
    static AstroObject fromSimbadRow(String simRowStr){
      String[] keys = Config.BD_KEYS;
      AstroObject retval = new AstroObject();
      retval.basicData = new HashMap<String, String>();

      String[] elems = simRowStr.split("\\|");
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

    public Vector<Measurement> getMeasurements(){
      return measurements;
    }

    public String basicDataToString(){
      String[] keys = Config.BD_KEYS;
      String retval = "";
      String fluxesStr = "";
      for(int i = 0; i < keys.length-1; i++){
        String value = basicData.get(keys[i]);
        if(value!=null) retval = retval + keys[i]+ value + "\n";
      }
      for(int i = 0; i < fluxes.length; i++)
        if(!fluxes[i].contains("~")) fluxesStr = fluxesStr + fluxes[i] + "\n";
      if(fluxesStr != "") retval = retval + keys[keys.length-1]+"\n"+fluxesStr;
      return retval;
    }
}
