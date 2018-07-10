package com.p3.archon.xsd_generation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p3.archon.common.beans.Pair;
import com.p3.archon.common.constants.Datatypes;
import com.p3.archon.common.utils.XPathUtils;
import com.p3.archon.xsd_generation.beans.Children;
import com.p3.archon.xsd_generation.beans.FileModel;
import com.p3.archon.xsd_generation.beans.FinalChildren;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.inst2xsd.Inst2Xsd;
import org.apache.xmlbeans.impl.inst2xsd.Inst2XsdOptions;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PdiSchemaGenerator {
  private static List<FinalChildren> resultList = new ArrayList<>();
  private static List<Pair> xpathList = new ArrayList<>();

  public static void generator(String fileName) {
    try {
      readJsonWithObjectMapper(fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      getFinalXsd("pdi-schema.xsd");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void readJsonWithObjectMapper(String fileName) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    FileModel result = objectMapper.readValue(new File(fileName), FileModel.class);

    List<Children> nodeList = result.getResult().getChildren();

    getFinalJson("/" + result.getResult().getName(), nodeList);
  }


  public static void getFinalXsd(String finalXsd) throws Exception {
    ArrayList<Pair> configXpathList = new ArrayList<Pair>();

    for (Pair xpath : xpathList) {
      configXpathList.add(xpath);
    }

    String tempXml = "temp.xml";
    XPathUtils.createXML(configXpathList, tempXml);

    try {
      File file = new File(tempXml);
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line = "";
      String oldtext = "";
      while ((line = reader.readLine()) != null) {
        oldtext += line + "\r\n";
      }
      reader.close();
      // replace a word in a file
      String startText = oldtext.replaceAll("<pagedata>", "<RECORDs>\n<RECORD>\n<pagedata>");
      String finalText = startText.replaceAll("</pagedata>", "</pagedata>\n</RECORD>\n</RECORDs>");

      FileWriter writer = new FileWriter(tempXml);
      writer.write(finalText);
      writer.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    getXmlToXsd(tempXml, finalXsd);

    //Temp xml deletion
    File file1 = new File(tempXml);
    file1.deleteOnExit();

    try {
      File file = new File(finalXsd);
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line = "";
      String oldtext = "";
      while ((line = reader.readLine()) != null) {
        oldtext += line + "\r\n";
      }
      reader.close();
      // replace a word in a file
      String newtext = oldtext.replaceAll("xs:float", "xs:double");

      FileWriter writer = new FileWriter(finalXsd);
      writer.write(newtext);
      writer.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  private static void getFinalJson(String name, List<Children> nodeList) {

    for (Children nodes : nodeList) {
      FinalChildren mNode = new FinalChildren();
      mNode.setTopath(name + nodes.getFrompath().substring(nodes.getFrompath().lastIndexOf("/"),
              nodes.getFrompath().length()));

      mNode.setFrompath(nodes.getFrompath());
      mNode.setDatatype(nodes.getDatatype());
      mNode.setName(nodes.getName());
      mNode.setSearch(nodes.isSearch());
      mNode.setResult(nodes.isResult());
      mNode.setId(nodes.getId());
      mNode.setFilename(nodes.getFilename());

      if (nodes.getChildren().size() > 0) {
        getFinalJson(name + "/" + nodes.getName(), nodes.getChildren());
      } else {
        getIADatatype(mNode);

        resultList.add(mNode);
      }
    }

  }


  private static void getIADatatype(FinalChildren mNode) {
    Pair nv;
    if (mNode.getDatatype().equalsIgnoreCase(Datatypes.INT)) {
      nv = new Pair(mNode.getTopath(), "2147483647");

    } else if (mNode.getDatatype().equalsIgnoreCase(Datatypes.INTEGER)) {
      nv = new Pair(mNode.getTopath(), "-123456789012345678901234567890");

    } else if (mNode.getDatatype().equalsIgnoreCase(Datatypes.DATE)) {
      nv = new Pair(mNode.getTopath(), "2002-09-24");

    } else if (mNode.getDatatype().equalsIgnoreCase(Datatypes.DATETIME)) {
      nv = new Pair(mNode.getTopath(), "2001-10-26T21:32:52");

    } else if (mNode.getDatatype().equalsIgnoreCase(Datatypes.LONG)) {
      nv = new Pair(mNode.getTopath(), "9223372036854775807");

    } else if (mNode.getDatatype().equalsIgnoreCase(Datatypes.DOUBLE) || mNode.getDatatype().equalsIgnoreCase(Datatypes.FLOAT)
            || mNode.getDatatype().equalsIgnoreCase(Datatypes.DECIMAL)) {
      nv = new Pair(mNode.getTopath(), "9007199254740992.45E-6");

    } else {
      nv = new Pair(mNode.getTopath(), "value");
    }
    xpathList.add(nv);

  }


  public static void getXmlToXsd(String xmlFileName, String xsdFileName) throws Exception {

    final Inst2XsdOptions options = new Inst2XsdOptions();
    options.setDesign(Inst2XsdOptions.DESIGN_VENETIAN_BLIND);

    XmlObject[] xml = null;
    try {
      xml = new XmlObject[] { XmlObject.Factory.parse(new File(xmlFileName)) };

    } catch (XmlException e) {
      e.printStackTrace();
    }
    final SchemaDocument[] schemaDocs = Inst2Xsd.inst2xsd(xml, options);
    PrintWriter out = new PrintWriter(xsdFileName);
    out.println(schemaDocs[0]);
    out.close();

  }
}
