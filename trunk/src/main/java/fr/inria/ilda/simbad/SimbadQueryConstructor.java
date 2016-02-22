package fr.inria.ilda.simbad;

import fr.inria.ilda.fitsow.Config;

public class SimbadQueryConstructor{

/*This functions receives an array of ints containing either 1 or 0 at positions.
it checks at which positions 1 is stored, and it maps those positions to a
cataog name. It then constructus the part of the query string that concerns
measurements*/
  public static String measurementSelector(int[] mIndex){
    // String retval = "mes='rot'";
    String retval ="";
    // if(mIndex==null||(mIndex.length == 1 && mIndex[0] == 1)){
      // for(int i = 0; i < Config.CATALOGS.length; i++){
        // retval = retval + "%%MEASLIST("+Config.CATALOGS[i]+";AH)#";
      // }
    // }
    // else

    // if(mI)System.out.println(mIndex.length);
    if(mIndex!=null && mIndex.length > 1){
      for(int i = 0; i < mIndex.length; i++){
        if(mIndex[i] == 1)
          // retval = retval + "%%MEASLIST("+Config.CATALOGS[i]+";AH)#";
          retval = retval+"mes='"+Config.CATALOGS[i+1].toLowerCase()+"' |";
      }
      retval.trim();
      retval = retval.substring(0, retval.length()-1);
      retval.trim();
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
    if(retval.endsWith("|")){
      retval.trim();
      retval = retval.substring(0, retval.length()-1);
    }
    return retval;
  }

  public static String properMotionSelector(String ra, String dec, int[] qualities){
    String retval ="";
    if(!ra.equals("")) retval = "ra"+ra;
    if(!dec.equals("")){
      if(!retval.equals("")) retval = retval+" & dec"+dec;
      else retval = "dec"+dec;
    }
    String q=qualitySelector(qualities,"pmqual");
    if(!retval.equals("") && !q.equals("")) retval = retval+" & "+q;
    else if(retval.equals("")) retval = q;
    return retval;
  }
  public static String parallaxSelector(String parallax, int[] qualities){
    String retval="";
    if(!parallax.equals("")) retval = "plx"+parallax;
    String q = qualitySelector(qualities,"plxqual");
    if(!retval.equals("") && !q.equals("")) retval = retval+" & "+q;
    else if(retval.equals("")) retval = q;
    return retval;
  }
  public static String radialVelocitySelector(String rv, String z, String cz, int[] qualities){
    String retval="";
    if(!rv.equals("")) retval = "radvel"+rv;
    if(!z.equals("")){
      if(!retval.equals("")) retval =retval+ "& redshift"+z;
      else retval = "redshift"+z;
    }
    if(!cz.equals("")){
      if(!retval.equals("")) retval = retval+"& cz"+cz;
      else retval = "cz"+cz;
    }
    String q = qualitySelector(qualities, "rvqual");
    if(!retval.equals("") && !q.equals("")) retval = retval+" & "+q;
    else if(retval.equals("")) retval = q;
    return retval;
  }
  public static String spectralTypeSelector(String st, String lc, String pec, int[] qualities){
    String retval="";
    if(!st.equals("")) retval = "sptypes"+st;
    if(!lc.equals("")){
      if(!retval.equals("")) retval =retval+ "& splum"+lc;
      else retval = "splum"+lc;
    }
    if(!pec.equals("")){
      if(!retval.equals("")) retval = retval+"& sppec"+pec;
      else retval = "sppec"+pec;
    }
    String q = qualitySelector(qualities, "spqual");
    if(!retval.equals("") && !q.equals("")) retval = retval+" & "+q;
    else if(retval.equals("")) retval = q;
    return retval;
  }

  public static String fluxRangeSelector(String[] rStr, int[] qualities){
    String retval = "";
    for(int i = 0; i < rStr.length; i++){
      if(!rStr[i].equals("")){
        if(retval.equals("")) retval = Config.FLUX_TYPES[i]+"mag"+rStr[i];
        else retval = retval + " & "+ Config.FLUX_TYPES[i]+"mag"+rStr[i];
      }
    }
    String q = qualitySelector(qualities, "fluxqual");
    if(!retval.equals("") && !q.equals("")) retval = retval+" & "+q;
    else if(retval.equals("")) retval = q;
    return retval;
  }

  public static String fluxSelector(int[] fIndex){
    String retval = "FLUXLIST(";
    int count=0;
    for(int i = 0; i < fIndex.length; i++){
      if(fIndex[i] == 1){
        if(retval.equals("FLUXLIST(")) retval = retval + Config.FLUX_TYPES[i];
        else retval = retval + "," +Config.FLUX_TYPES[i];
      }
    }
    retval = retval + "; N = F (Q) B,)";
    return retval;
  }

  public static String qualitySelector(int[] qualities, String qName){
    String q="";
    for(int i = 0; i < qualities.length; i++){
      int ascii = i + 65;
      if(qualities[i] == 1){
        q = q + qName+"="+Character.toString((char) ascii)+" | ";
      }
    }
    q = q.trim();
    if(q.endsWith("|")){
      q = q.substring(0,q.length()-2);
    }
    if(!q.equals("")){
      q = "("+q+")";
    }
    return q;
  }

}
