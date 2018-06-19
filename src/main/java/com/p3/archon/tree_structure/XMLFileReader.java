package com.p3.archon.tree_structure;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.dom4j.xpath.DefaultXPath;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLFileReader {
	public static void main(String[] args) {
		XMLFileReader.readXml("/Users/saideepak/Projects/JPMC/JPMC_-_XML_Files/CFW_DeceasedCase.xsd");
	}

	public static JSONObject readXml(String xml) {
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
				jsonObject.put("path", node.getPath(rootElement));
				id++;
				List<Node> childNode = XMLFileReader.getchildren(node);
				jsonObject.put("children",XMLFileReader.getJson(childNode, id));
				jsonList.add(jsonObject);
			}
			JSONObject treeJson = new JSONObject();
			treeJson.put("tree", jsonList);
			System.out.println("treejson ---->  " + treeJson);
			return treeJson;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static List<Node> getchildren(Node parentNode) {
		List<Node> nodes = parentNode.selectNodes("xs:complexType/xs:sequence/xs:element");
		if (!nodes.isEmpty()) {
			return nodes;
		}
		return null;
	}

	private static List<JSONObject> getJson(List<Node> nodes, int id) {
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (Node node : nodes) {
			JSONObject jsonObject = new JSONObject();
			String type = node.valueOf("@type");
			if(type!="") {
				type = type.substring(3);
			}
			jsonObject.put("id", id);
			jsonObject.put("name", node.valueOf("@name"));
			jsonObject.put("path", node.getPath());
			jsonObject.put("datatype", type );
			jsonObject.put("search", false);
			jsonObject.put("result", false);
			id++;
			jsonList.add(jsonObject);
		}
		return jsonList;
	}
}
