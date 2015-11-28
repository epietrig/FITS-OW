package fr.inria.ilda.simbad;

import fr.inria.ilda.fitsow.Config;

public class SimbadQueryConstructor{

/*This functions receives an array of ints containing either 1 or 0 at positions.
it checks at which positions 1 is stored, and it maps those positions to a
cataog name. It then constructus the part of the query string that concerns
measurements*/
  public static String measurementSelector(int[] mIndex){
    String retval = "";
    if(mIndex==null||(mIndex.length == 1 && mIndex[0] == 1)){
      for(int i = 0; i < Config.CATALOGS.length; i++){
        retval = retval + "%%MEASLIST("+Config.CATALOGS[i]+";AH)#";
      }
    }
    else if(mIndex.length > 0){
      for(int i = 0; i < mIndex.length; i++){
        if(mIndex[i] == 1)
          retval = retval + "%%MEASLIST("+Config.CATALOGS[i]+";AH)#";
      }
    }
    return retval;
  }

  public static String objectTypeSelector(int[] otIndex){
    String retval="";
    for(int i = 0; i< otIndex.length; i++){
      if(otIndex[i] == 1)
        retval = retval + "maintypes="+Config.OBJECT_TYPES[i].toLowerCase()+" | ";
    }
    retval = retval.trim();
    if(retval.endsWith("|")) retval = retval.substring(0, retval.length()-1);
    return retval;
  }

  public static String properMotionSelector(String ra, String dec, int[] qualities){
    String retval ="";
    if(!ra.equals("")) retval = "ra"+ra;
    if(!dec.equals("")) retval = retval+" & dec"+dec;
    String q="";
    for(int i = 0; i < qualities.length; i++){
      int ascii = i + 65;
      System.out.println("q("+i+"): "+qualities[i]);
      if(qualities[i] == 1){
        System.out.println("I shold be adding q "+Character.toString((char) ascii));
        q = q + "pmqual="+Character.toString((char) ascii)+" | ";
        System.out.println(q);
      }
    }
    q = q.trim();
    if(q.endsWith("|")){
      q = q.substring(0,q.length()-2);
      System.out.println(q);
    }
    if(!q.equals("")){
      q = "("+q+")";
      System.out.println(q);
    }
    if(!retval.equals("")) retval = retval+" & "+q;
    else retval = q;

    return retval;
  }

}
