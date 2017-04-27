/*  (c) COPYRIGHT INRIA (Institut National de Recherche en Informatique et en Automatique) and INRIA Chile, 2015-2016.
 *  Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id:  $
 */

package fr.inria.ilda.simbad;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.HashMap;
import jsky.science.Coordinates;
import fr.inria.ilda.fitsow.Config;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
public class SimbadParser{

  public SimbadParser(){}

  public static List<AstroObject> getVOTableAsString(URL url)throws ParserConfigurationException{
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    List<AstroObject> astroObjs = new ArrayList<AstroObject>();
   try {
      Document doc = builder.parse(url.openStream());
      Element element = doc.getDocumentElement();
      NodeList nodes = element.getChildNodes();
      NodeList nodes2 = element.getElementsByTagName("TR");
      NodeList nodes3;
      String text;
      String[] keys = Config.BD_KEYS;
      for (int i = 0; i < nodes2.getLength(); i++){
        nodes3 = nodes2.item(i).getChildNodes();
        AstroObject obj = new AstroObject();
        double ra = Double.NaN;
        double dec = Double.NaN;
        String cooICRS = "";
        String cooFK5 = "";
        String cooFK4 = "";
        String cooGal = "";
        String rv = "";
        String fluxes ="";
        HashMap basicData = new HashMap<String,String>();
        for(int j = 0; j < nodes3.getLength(); j++){
          text = nodes3.item(j).getTextContent();
          switch (j) {
           case 0: obj.setIdentifier(text); //obj identifier
                    break;
           case 1: ra = Double.parseDouble(text); //coordinates right ascention
                    break;
           case 2: dec = Double.parseDouble(text); //coordinates declination angle
                    break;
           case 3: basicData.put(keys[j-3], text); //type of obj
                    break;
           case 4:
           case 5: cooICRS = cooICRS+" "+text;//coordinates ICRS
                    break;
           case 6:
           case 7: cooFK5 = cooFK5+" "+text;//coordinates FK5
                    break;
           case 8:
           case 9: cooFK4 = cooFK4+" "+text;//coordinates FK4
                    break;
           case 10:
           case 11: cooGal = cooGal+" "+text;//Galactic coordinates
                    break;
           case 12:
            basicData.put(keys[j-7], text); //type of obj, proper motion, spectarl type, parallax, morphological type
                 break;
           case 13:
           case 14: rv = rv + " "+text;
                    break;
           case 15:
           case 16:
           case 17:
            basicData.put(keys[j-8], text); //type of obj, proper motion, spectarl type, parallax, morphological type
                 break;
           case 18: case 19: case 20: case 21: case 22: case 23:
           case 24: case 25: case 26: case 27: case 28: case 29: case 30:
                    fluxes = fluxes+" "+text;
                    break;
        }
          System.out.println(text);
        }
        if(!Double.isNaN(ra) && !Double.isNaN(dec)){
          Coordinates coords = new Coordinates(ra, dec);
          obj.setCoords(coords);
        }
        //build basic data
        basicData.put(keys[1], cooICRS);
        basicData.put(keys[2], cooFK5);
        basicData.put(keys[3], cooFK4);
        basicData.put(keys[4], cooGal);
        basicData.put(keys[6], rv);
        // basicData.put(keys[10], fluxes.split(" "));
        obj.setBasicData(basicData);
        obj.setFluxes(fluxes.split(" "));

        astroObjs.add(obj);
        System.out.println(" ");
        // System.out.println(nodes2.item(i).getTextContent());
        // toAppend = toAppend+nodes2.item(i).getTextContent();
        // System.out.println(nodes.item(i).getTextContent().trim());
      }
      // System.out.println(toAppend);
   }catch (Exception ex) {
      ex.printStackTrace();
   }
   return astroObjs;

  }
  public static List<String> get(URL url)throws IOException{
    List<String> result = new ArrayList<String>();
    long start_time = System.nanoTime();

    try{
      URLConnection uc = url.openConnection();
      BufferedReader in = new BufferedReader(new InputStreamReader(
                  uc.getInputStream()));
      String toAppend;
      String object = "";
      int length;
      while((toAppend = in.readLine()) != null){
        // System.out.println(toAppend);
        toAppend = toAppend.trim();
        object = object+toAppend;
        length = toAppend.length();
        if(length > 0 && toAppend.endsWith("$$")){
          // System.out.println("url obj : "+object+"\n");
          result.add(object);
          object = "";
        }
      }
      in.close();
      long end_time = System.nanoTime();
      // double difference = (end_time - start_time)/1e6;
      // double difference2 = (end_time - start_time)/1e6;
      // System.out.println("time in ms it took to execute query: "+difference);
      return result;
    }catch(Exception e){
      e.printStackTrace();
    }

    return result;
  }

  public static List<AstroObject> stringsToAstroObjects(List<String> strs){
    List<AstroObject> result = new ArrayList<AstroObject>();
    try{
    AstroObject obj = new AstroObject();
    for(String str : strs){
      obj = stringToAstroObject(str);
      if(obj != null) result.add(obj);
    }
  }catch(Exception e){
    System.out.println("caught sm!");
    e.printStackTrace();

  }
    return result;
  }

  private static AstroObject stringToAstroObject(String str){
    String obj = str.substring(0,str.length()-1);
    String[] attrs = obj.split("\\$");
    for(String atr : attrs){
      // System.out.println("attr: "+atr+"\n");
    }
    if(attrs.length >= 2){
      String[] idAndCoords = attrs[0].split("#");
      String[] basicData = attrs[1].split("#");
      // String[] measurements = attrs[2].split("#");
      if(idAndCoords.length >= 3){
        String identifier = idAndCoords[0];
        Coordinates coords =  new Coordinates(Double.parseDouble(idAndCoords[1]),
          Double.parseDouble(idAndCoords[2]));
        HashMap<String, String> basicDataHash = parseBasicData(basicData);
        String[] fluxes = basicData[basicData.length-1].split(",");
        // System.out.println("obj measurements: "+attrs[2]);
        Vector<Measurement> measurements = parseMeasurements(attrs[2]);
        return new AstroObject(identifier, coords, basicDataHash, fluxes, measurements);
      }
    }
    return null;
  }

 private static HashMap<String, String> parseBasicData(String[] attrs){
   HashMap<String, String> basicData = new HashMap<String, String>();
   for(int i = 0; i < attrs.length-1; i++){
     String elementFirstComponent = attrs[i].split(",")[0];
     if (!elementFirstComponent.trim().contains("~")){
        basicData.put(Config.BD_KEYS[i], attrs[i]);
     }
   }
   return basicData;
 }

 private static Vector<Measurement> parseMeasurements(String attrs){
   String[] measurements = attrs.split("#");
   Vector<Measurement> retval = new Vector();
   for(String measurement : measurements){
    //  System.out.println("m: "+ measurement +"\n");
     if(measurement.length()>0){
       Measurement table = buildTable(measurement.split("\\|"));
       retval.add(table);
     }
   }
   return retval;
 }

 private static Measurement buildTable(String[] measurement){
   String name = measurement[0].trim();
   int nRows = 1;
   int nCols = 0;
   for(int i = 1; i < measurement.length; i++){
     if(measurement[i].equals(name) || measurement[i].contains(name)) nRows++;
     else nCols++;
   }

   if(nRows > 0) nCols = nCols/nRows;
   String[][] table = new String[nRows][nCols];
   for(int i = 0 ; i < nRows; i++){
     for(int j = 0; j < nCols; j++){
       table[i][j] = measurement[i+1+j+nCols*i];
     }
   }

   Measurement retval = new Measurement(name, table);
   return retval;
 }

}
