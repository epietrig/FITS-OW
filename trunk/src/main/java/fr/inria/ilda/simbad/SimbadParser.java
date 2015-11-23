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

public class SimbadParser{

  public SimbadParser(){}

  public static List<String> splitURLIntoStrings(URL url)throws IOException{
    List<String> result = new ArrayList<String>();
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
