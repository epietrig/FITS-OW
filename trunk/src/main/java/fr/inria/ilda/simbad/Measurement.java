/*  (c) COPYRIGHT INRIA (Institut National de Recherche en Informatique et en Automatique) and INRIA Chile, 2015-2016.
 *  Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id:  $
 */

 package fr.inria.ilda.simbad;

public class Measurement{
  private String name;
  private String[][] table;

  public Measurement(String name, String[][] table){
    this.name = name;
    this.table = table;
  }

  public String getName(){
    return name;
  }

  public String[][] getTable(){
    return table;
  }
}
