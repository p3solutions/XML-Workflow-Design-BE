package com.p3.archon.tree_structure;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.dom4j.xpath.DefaultXPath;
import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLFileReader {

	private Document xmlDocument;
	private Document xsdDocument;
	// Path to determine if there is a child
	private String sequenceChildPath = "xs:complexType/xs:sequence/xs:element";
	private String choiceChildPath = "xs:complexType/xs:choice/xs:element";

	public static void main(String[] args) {
		XMLFileReader xmlReader = new XMLFileReader(
				"/Users/saideepak/Projects/JPMC/JPMC_-_XML_Files/DeceasedCaseConfig.xml",
				"/Users/saideepak/Projects/JPMC/JPMC_-_XML_Files/DeceasedCaseConfig.xsd");
		xmlReader.readXml();
		// XMLFileReader.readXml("/Users/saideepak/Projects/JPMC/JPMC_-_XML_Files/CFW_DeceasedCase.xsd");
		// XMLFileReader fileReader = new XMLFileReader();
		// try {
		// fileReader.getXpath("BankNumber",
		// "/Users/saideepak/Projects/JPMC/JPMC_-_XML_Files/CFW_DeceasedCase.xml");
		// } catch (DocumentException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	/**
	 * 
	 */
	public XMLFileReader(String xml, String xsd) {
		this.xmlDocument = this.parseXML(xml);
		this.xsdDocument = this.parseXML(xsd);
	}

	public XMLFileReader(String file) {
		if (file.endsWith(".xml")) {
			this.xmlDocument = this.parseXML(file);
		} else if (file.endsWith(".xsd")) {
			this.xsdDocument = this.parseXML(file);
		}
	}

	private Document parseXML(String xml) {
		File inputFile = new File(xml);
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(inputFile);
			return document;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private String getXpath(String parent, String elementName) throws DocumentException {
		Node node = xmlDocument.selectSingleNode(parent + "/" + elementName);
		if (node != null) {
			System.out.println(node.getPath());
			return node.getPath();
		}
		return null;
	}

	public JSONObject readXml() {
		try {
			Document document = this.xsdDocument;
			// Find the root element
			Element rootElement = document.getRootElement();
			// Identify the name space
			String namespace = rootElement.getNamespaceURI();

			XPath xpath = new DefaultXPath("/xs:schema/xs:element");
			// Setup Map for storing namepsaces
			Map<String, String> namespaces = new HashMap<String, String>();
			namespaces.put("xs", namespace);
			xpath.setNamespaceURIs(namespaces);
			List<Node> nodes = xpath.selectNodes(document);
			int id = 1;
			JSONReturns jsonReturns = this.getJson(nodes, id, "/");
			List<JSONObject> jsonList = jsonReturns.getJsonList();
			JSONObject treeJson = new JSONObject();
			treeJson.put("tree", jsonList);
			System.out.println("treejson ---->  " + treeJson);
			return treeJson;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<Node> getchildren(Node parentNode, String childpath) {
		List<Node> nodes = parentNode.selectNodes(childpath);
		return nodes;
	}

	private JSONReturns getJson(List<Node> nodes, int id, String parentPath) throws DocumentException {
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (Node node : nodes) {
			JSONObject jsonObject = new JSONObject();
			String path = this.getXpath(parentPath, node.valueOf("@name"));
			String type = node.valueOf("@type");
			if (type != "") {
				type = type.substring(3);
			}
			jsonObject.put("name", node.valueOf("@name"));
			jsonObject.put("frompath", path);
			jsonObject.put("datatype", type);
			jsonObject.put("search", false);
			jsonObject.put("result", false);
			if (node.hasContent()) {
				List<Node> childNode = this.getchildren(node, this.sequenceChildPath);
				if(childNode.size()==0) {
					childNode = this.getchildren(node, this.choiceChildPath);
				}
				JSONReturns childJsonReturns = this.getJson(childNode, id, path);
				List<JSONObject> childJsonList = childJsonReturns.getJsonList();
				jsonObject.put("children", childJsonList);
				id = childJsonReturns.getId();
			}
			jsonObject.put("id", id);
			id++;
			jsonList.add(jsonObject);
		}
		JSONReturns jsonReturns = new JSONReturns();
		jsonReturns.setJsonList(jsonList);
		jsonReturns.setId(id);
		return jsonReturns;
	}

	@Getter
	@Setter
	private class JSONReturns {
		private int id;
		private List<JSONObject> jsonList;
	}
}
