package com.p3.archon.common.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.dom4j.xpath.DefaultXPath;
import org.json.simple.JSONObject;

import com.p3.archon.common.beans.JSONReturns;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author saideepak
 *
 */
public class XMLFileReader {

	private Document xmlDocument;
	private Document xsdDocument;
	// Path to determine if there is a child
	private String sequenceChildPath = "xs:complexType/xs:sequence/xs:element";
	private String choiceChildPath = "xs:complexType/xs:choice/xs:element";
	// private String attributeChildPath1 = "xs:complexType/xs:simpleContent/xs:extension/xs:attribute";
	// private String attributeChildPath2 = "xs:complexType/xs:attribute";
	private String xmlFileName = "";
	@SuppressWarnings("unused")
	private String xsdFileName = "";
	private Utility utility = new Utility();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		XMLFileReader xmlReader = new XMLFileReader(
				// "C:\\\\users\\\\E843389\\\\Downloads\\\\Uploadedfiles\\\\\\\\Notification.xml",
				"/Users/saideepak/Projects/JPMC/JPMC_-_XML_Files/CFW_DeceasedCase.xml",
				"/Users/saideepak/Projects/JPMC/JPMC_-_XML_Files/CFW_DeceasedCase.xsd");
		xmlReader.readXml();
	}

	/**
	 * Constructor for parsing both XML and XSD
	 * 
	 * @param xmlFile
	 * @param xsdFile
	 */
	public XMLFileReader(String xmlFile, String xsdFile) {
		xmlFile = utility.changeURLIfNeeded(xmlFile);
		xsdFile = utility.changeURLIfNeeded(xsdFile);
		xmlFileName = xmlFile.substring(xmlFile.lastIndexOf("/") + 1, xmlFile.length());
		xsdFileName = xsdFile.substring(xsdFile.lastIndexOf("/") + 1, xsdFile.length());
		this.xmlDocument = this.parseXML(xmlFile);
		this.xsdDocument = this.parseXML(xsdFile);
	}

	/**
	 * Constructor for parsing either XML or XSD
	 * 
	 * @param file
	 */
	public XMLFileReader(String file) {
		file = utility.changeURLIfNeeded(file);
		if (file.endsWith(".xml")) {
			xmlFileName = file.substring(file.lastIndexOf("/") + 1, file.length());
			this.xmlDocument = this.parseXML(file);
		} else if (file.endsWith(".xsd")) {
			xsdFileName = file.substring(file.lastIndexOf("/") + 1, file.length());
			this.xsdDocument = this.parseXML(file);
		}
	}

	/**
	 * @param xml
	 * @return {@link Document}
	 */
	public Document parseXML(String xml) {
		File inputFile = new File(xml);
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(inputFile);
			return document;
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param parent
	 * @param elementName
	 * @return {@link String}
	 * @throws DocumentException
	 */
	public String getXpath(String parent, String elementName) throws DocumentException {
		Node node = xmlDocument.selectSingleNode(parent + "/" + elementName);
		if (node != null) {
			return node.getPath();
		}
		return null;
	}

	/**
	 * @return {@link JSONObject}
	 */
	@SuppressWarnings("unchecked")
	public JSONObject readXml() {
		try {
			Document document = this.xsdDocument;
			// Find the root element
			Element rootElement = document.getRootElement();
			// Identify the name space
			String namespace = rootElement.getNamespaceURI();

			XPath xpath = new DefaultXPath("/xs:schema/xs:element");
			// Setup Map for storing namespaces
			Map<String, String> namespaces = new HashMap<String, String>();
			namespaces.put("xs", namespace);
			xpath.setNamespaceURIs(namespaces);
			List<Node> nodes = xpath.selectNodes(document);
			int id = 1;
			JSONReturns jsonReturns = this.getJson(nodes, id, "/");
			List<JSONObject> jsonList = jsonReturns.getJsonList();
			JSONObject treeJson = new JSONObject();
			treeJson.put("tree", jsonList);
			return treeJson;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get the children of a node
	 * 
	 * @param parentNode
	 * @param childpath
	 * @return {@link List}
	 */
	public List<Node> getchildren(Node parentNode, String childpath) {
		List<Node> nodes = parentNode.selectNodes(childpath);
		return nodes;
	}

	/**
	 * Get JSON output of nodes
	 * 
	 * @param nodes
	 * @param id
	 * @param parentPath
	 * @return {@link JSONReturns}
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	private JSONReturns getJson(List<Node> nodes, int id, String parentPath) throws DocumentException {
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (Node node : nodes) {
			JSONObject jsonObject = new JSONObject();
			String name = node.valueOf("@name");
			String path = "";
			path = this.getXpath(parentPath, name);
			String type = node.valueOf("@type");
			if (type != "") {
				type = type.substring(3);
				switch (type) {
				case "string":
					type = "STRING";
					break;
				case "int":
					type = "INTEGER";
					break;
				case "byte":
					type = "INTEGER";
					break;
				case "short":
					type = "INTEGER";
					break;
				case "long":
					type = "LONG";
					break;
				case "float":
					type = "DOUBLE";
					break;
				case "datetime":
					type = "DATETIME";
					break;
				case "date":
					type = "DATE";
					break;
				default:
					type = "STRING";
					break;
				}
			}
			jsonObject.put("name", name);
			jsonObject.put("frompath", path);
			jsonObject.put("datatype", type);
			jsonObject.put("search", false);
			jsonObject.put("result", true);
			jsonObject.put("minoccurance", "0");
			jsonObject.put("maxoccurance", "");
			jsonObject.put("conditionpath", "");
			jsonObject.put("iscondition", false);
			jsonObject.put("condition", "");
			jsonObject.put("conditionvalue", "");
			jsonObject.put("filename", xmlFileName);

			if (node.hasContent()) {
//				Node attributeNode = node.selectSingleNode(attributeChildPath1);
				// CustomElement customElement = null;
				// if (attributeNode == null) {
				// attributeNode = node.selectSingleNode(attributeChildPath2);
				// }
				// if (attributeNode != null) {
				// customElement = (CustomElement) attributeNode;
				// customElement.setAttribute(true);
				// childNodes.add(customElement);
				// }
				//
				// if (childNodes.size() != 0) {
				// childNodes.addAll(this.getchildren(node, this.sequenceChildPath));
				// } else {
				// childNodes = this.getchildren(node, this.sequenceChildPath);
				// }
				List<Node> childNodes = this.getchildren(node, this.sequenceChildPath);
				if (childNodes.size() == 0) {
					childNodes = this.getchildren(node, this.choiceChildPath);
				}
				JSONReturns childJsonReturns = this.getJson(childNodes, id, path);
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
}
