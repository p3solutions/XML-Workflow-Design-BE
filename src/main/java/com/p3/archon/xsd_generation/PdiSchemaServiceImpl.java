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
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementer of {@link PdiSchemaService}
 *
 * @author omjigupta
 */
@Service("pdiSchemaService")
public class PdiSchemaServiceImpl implements PdiSchemaService{
  private static List<FinalChildren> resultList = new ArrayList<>();
  private static List<Pair> xpathList = new ArrayList<>();
  private static String xsdFile = "pdi-schema.xsd";

  @Override
  public void generator(String fileName) {
    try {
      readJsonWithObjectMapper(fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      getFinalXsd();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void readJsonWithObjectMapper(String fileName) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    FileModel result = objectMapper.readValue(new File(fileName), FileModel.class);

    List<Children> nodeList = result.getResult().getChildren();

    getFinalJson("/" + result.getResult().getName(), nodeList);
  }


  private void getFinalXsd() throws Exception {

    ArrayList<Pair> configXpathList = new ArrayList<>(xpathList);

    String tempXml = "temp.xml";
    XPathUtils.createXML(configXpathList, tempXml);

    try {
      File file = new File(tempXml);
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line;
      StringBuilder oldtext = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        oldtext.append(line).append("\r\n");
      }
      reader.close();
      // replace a word in a file
      String startText = oldtext.toString().replaceAll("<pagedata>", "<RECORDs>\n<RECORD>\n<pagedata>");
      String finalText = startText.replaceAll("</pagedata>", "</pagedata>\n</RECORD>\n</RECORDs>");

      FileWriter writer = new FileWriter(tempXml);
      writer.write(finalText);
      writer.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    getXmlToXsd(tempXml, xsdFile);

    //Temp xml deletion
    File file1 = new File(tempXml);
    file1.deleteOnExit();

    try {
      File file = new File(xsdFile);
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line;
      StringBuilder stringBuilder = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line).append("\r\n");
      }
      reader.close();
      // replace a word in a file
      String text = stringBuilder.toString().replaceAll("xs:float", "xs:double");

      FileWriter writer = new FileWriter(xsdFile);
      writer.write(text);
      writer.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  private void getFinalJson(String name, List<Children> nodeList) {

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
      mNode.setMinoccurance(nodes.getMinoccurance());
      mNode.setMaxoccurance(nodes.getMaxoccurance());

      if (nodes.getChildren().size() > 0) {
        getFinalJson(name + "/" + nodes.getName(), nodes.getChildren());
      } else {
        getIADatatype(mNode);

        resultList.add(mNode);
      }
    }

  }


  private void getIADatatype(FinalChildren mNode) {
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


  private void getXmlToXsd(String xmlFileName, String xsdFileName) throws Exception {

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
