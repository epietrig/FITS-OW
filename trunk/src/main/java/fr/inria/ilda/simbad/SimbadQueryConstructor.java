package fr.inria.ilda.simbad;

import fr.inria.ilda.fitsow.Config;

public class SimbadQueryConstructor{

/*This functions receives an array of ints containing either 1 or 0 at positions.
it checks at which positions 1 is stored, and it maps those positions to a
cataog name. It then constructus the part of the query string that concerns
measurements*/
  public static String measurementSelector(int[] mIndex){
    String retval = "";
    if(mIndex.length == 1 && mIndex[0] == 1){
      for(int i = 0; i < Config.CATALOGS.length; i++){
        retval = retval + "%%MEASLIST("+Config.CATALOGS[i]+";AH)#";
      }
    }
    else{
      for(int i = 0; i < mIndex.length; i++){
        if(mIndex[i] == 1)
          retval = retval + "%%MEASLIST("+Config.CATALOGS[i]+";AH)#";
      }
    }
    // System.out.println(retval);
    return retval;
  }

}
