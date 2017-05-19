/*  (c) COPYRIGHT INRIA (Institut National de Recherche en Informatique et en Automatique) and INRIA Chile, 2015-2016.
 *  Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id:  $
 */

package fr.inria.ilda.simbad;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import jsky.science.Coordinates;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.IOUtils;

public class SimbadParser {

  private static final String ERROR_404 = "No object found";
  private static final String XMLSTART = "<?xml";
  private static final String EMPTY = "";
  private static final String UTF_8 = "UTF-8";
  private static final String XML_OBJS = "TR";
    private static final String UNDERSCORE = "_";

  public static List<AstroObject> getVOTableAsAObjects(URL url) throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    List<AstroObject> astroObjs = null;
    try {
      InputStream in = url.openStream();
      String inStr = IOUtils.toString(in, UTF_8);
      if (!inStr.contains(ERROR_404)) {
        int xmlStart = inStr.indexOf(XMLSTART);
        inStr = inStr.substring(xmlStart);
        InputStream inFix = IOUtils.toInputStream(inStr, UTF_8);
        Document doc = builder.parse(inFix);
        Element element = doc.getDocumentElement();
        astroObjs = parseAstroObjects(element);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return astroObjs;

  }

  public static List<AstroObject> parseAstroObjects(Element element){
    List<AstroObject> astroObjs = new ArrayList<AstroObject>();
    String[][] fieldDescription = getFieldDescription(element);
    String[] fieldIDs = fieldDescription[0];
    String[] fieldDesc = fieldDescription[1];
    NodeList objs = element.getElementsByTagName(XML_OBJS);
    NodeList objAttrs;
    String newmname;
    String text;
        for (int i = 0; i < objs.getLength(); i++) {
          objAttrs = objs.item(i).getChildNodes();
          AstroObject astroObj = new AstroObject();
          HashMap<String, String> basicData = new HashMap<String, String>();
          ArrayList<String[]> mlist = new ArrayList<String[]>();
          for (int j = 0; j < objAttrs.getLength(); j++) {
            text = objAttrs.item(j).getTextContent();
            if (j == 0) {
              astroObj.setIdentifier(text);
            } else if (j == 1) {
              Coordinates coords = new Coordinates();
              astroObj.setCoords(coords);
              astroObj.setRa(Double.parseDouble(text));
            } else if (j == 2) {
              astroObj.setDec(Double.parseDouble(text));
            } else if (j < 24) {
              if (!text.trim().equals(EMPTY)) {
                basicData.put(fieldDesc[j], text);
              }
            } else {
              if (!text.trim().equals(EMPTY)) {
                newmname = fieldIDs[j].split(UNDERSCORE)[0];
                String[] newm = { newmname, fieldDesc[j], text };
                mlist.add(newm);
              }
            }
          }
          astroObj.setBasicData(basicData);
          astroObj.setMeasurements(groupMeasurements(mlist));
          astroObjs.add(astroObj);
        }
        return astroObjs;
  }

  public static String[][] getFieldDescription(Element element) {
    NodeList fields = element.getElementsByTagName("FIELD");
    NodeList fieldNames;
    String[] fieldDesc = new String[fields.getLength()];
    String[] fieldIDs = new String[fields.getLength()];
    String text;
    for (int i = 0; i < fields.getLength(); i++) {
      fieldNames = fields.item(i).getChildNodes();
      text = fieldNames.item(1).getTextContent().trim();
      if (!text.equals("")) {
        fieldDesc[i] = text;
        Element e = (Element) fields.item(i);
        fieldIDs[i] = e.getAttribute("ID");
      }
    }

    String[][] retval = { fieldIDs, fieldDesc };
    return retval;

  }

  public static Vector<Measurement> groupMeasurements(ArrayList<String[]> mlist) {
    Vector<Measurement> measurements = new Vector<Measurement>();
    ArrayList<String[]> list = new ArrayList<String[]>();
    String aux = "";
    for (int i = 0; i < mlist.size(); i++) {
      String[] m = mlist.get(i);
      String[] row = { m[1], m[2] };

      if (i == 0) {
        aux = m[0];
        list.add(row);
      }
      if (i == mlist.size() - 1) { 
        list.add(row);
        Measurement measurement = new Measurement(aux, toMatrix(list));
        measurements.add(measurement);
      } else if (!m[0].equals(aux)) {
        Measurement measurement = new Measurement(aux, toMatrix(list));
        measurements.add(measurement);
        list = new ArrayList<String[]>();
        list.add(row);
        aux = m[0];
      } else {
        list.add(row);
      }

    }
    return measurements;
  }

  public static String[][] toMatrix(ArrayList<String[]> mlist) {
    int size = mlist.size();
    String[][] matrix = new String[size][2];
    for (int i = 0; i < size; i++) {
      matrix[i][0] = mlist.get(i)[0];
      matrix[i][1] = mlist.get(i)[1];
    }
    return matrix;
  }

}
