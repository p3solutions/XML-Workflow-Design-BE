/**
 * 
 */
package com.p3.archon.file_upload;

import java.io.*;
import java.util.*;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.xpath.DefaultXPath;
import org.json.simple.JSONObject;

/**
 * @author saideepak
 *
 */
public class XMLFileReader {

	public static void main(String[] args) {
		XMLFileReader reader = new XMLFileReader();
		reader.readXml("/Users/saideepak/Projects/JPMC/JPMC_-_XML_Files/CFW_DeceasedCaseHistory1.xsd");
	}

	public JSONObject readXml(String xml) {
		try {
			File inputFile = new File(xml);
			SAXReader reader = new SAXReader();
			Document document = reader.read(inputFile);
			System.out.println("Root element :" + document.getRootElement().getName());
			Element rootElement = document.getRootElement();
			System.out.println(rootElement.toString());
			String namespace = rootElement.getNamespaceURI();
			XPath xpath = new DefaultXPath("/xs:schema/xs:element");
			Map<String, String> namespaces = new HashMap<String, String>();
			namespaces.put("xs", namespace);
			xpath.setNamespaceURIs(namespaces);
			List<Node> nodes = xpath.selectNodes(document);
			int id = 1;
			List<JSONObject> jsonList = new ArrayList<JSONObject>();
			for (Node node : nodes) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", id);
				jsonObject.put("name", node.valueOf("@name"));
				id++;
				System.out.println("\nCurrent Element :" + node.valueOf("@name"));
				jsonList.add(jsonObject);
			}
			JSONObject treeJson = new JSONObject();
			treeJson.put("tree", jsonList);
			return treeJson;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
