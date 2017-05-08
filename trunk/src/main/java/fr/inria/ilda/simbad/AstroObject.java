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
import java.io.IOException;

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

    public void setRa(double ra){
      this.coords.setRa(ra);
    }

    public void setDec(double dec){
      this.coords.setDec(dec);
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

    public void setIdentifier(String id){
      this.identifier = id;
    }

    public void setCoords(Coordinates coords){
      this.coords = coords;
    }

    public void setBasicData(HashMap<String, String> bd){
      this.basicData = bd;
    }

    public void setFluxes(String[] fluxes){
      this.fluxes = fluxes;
    }

    public void setMeasurements(Vector<Measurement> m){
      this.measurements = m;
    }

    public String basicDataToString(){
      String retval = "ICRS coord. (ep=J2000) : "+Double.toString(getRa())+", "+Double.toString(getDec())+"\n";
      // String[] keys= Config.BASIC_DATA_KEYS;
      Object[] keys = basicData.keySet().toArray();

      // for(int i = 0; i < keys.length-1; i++){
      //   String value = basicData.get(keys[i]);
      //   if(value!=null) retval = retval + keys[i]+" : "+ value + "\n";
      // }
      for(int i = 0; i < keys.length-1; i++){
        String value = basicData.get((String)keys[i]);
        if(value!=null) retval = retval + (String)keys[i]+" : "+ value + "\n";
      }
      return retval;
    }

}
