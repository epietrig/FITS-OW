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
