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
        toAppend = toAppend.trim();
        object = object+toAppend;
        length = toAppend.length();
        if(length > 0 && toAppend.charAt(length-1) == '$'){
          System.out.println("url obj : "+object);
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
    String[] attrs = obj.split("#");
    for(String s: attrs){
      System.out.println("attr : "+s);
    }
    if(attrs.length >= 3){
      String identifier = attrs[0];
      Coordinates coords =  new Coordinates(Double.parseDouble(attrs[1]),
        Double.parseDouble(attrs[2]));
      HashMap<String, String> basicData = parseBasicData(attrs);
      return new AstroObject(identifier, coords, basicData);
    }
    return null;
  }

 private static HashMap<String, String> parseBasicData(String[] attrs){
   HashMap<String, String> basicData = new HashMap<String, String>();
   for(int i = 0; i < 11; i++){
     String elementFirstComponent = attrs[i].split(",")[0];
     if (!elementFirstComponent.trim().contains("~")){
       basicData.put(Config.BD_KEYS[i], attrs[i+3]);
     }
   }
   return basicData;
 }

}
