package org.report.xml.support;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ReaderXmlRetrigger {

	private String xmlString = new String();
	private XMLObject xmlObject;
	private DocumentBuilderFactory docBuilderFactory;
	private DocumentBuilder docBuilder;
	private String xml_remove_spaces;

	public ReaderXmlRetrigger() throws ParserConfigurationException {

		docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);
		docBuilder = docBuilderFactory.newDocumentBuilder();
	}

	public void readXML(String xml, String id, String nodeXML, String action)
			throws SAXException, IOException, ParserConfigurationException {

		Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));

		NodeList nodeList = doc.getChildNodes();
		printNote(nodeList, nodeXML, action);

		xmlString = convertDOMtoString(nodeList);
		xmlObject = new XMLObject();
		xmlObject.setId(new Integer(id));
		xmlObject.setXmlObject(xmlString);

		setXmlObject(xmlObject);
	}

	public void readXML(String xml, String nodeXML, String exception)
			throws SAXException, IOException, ParserConfigurationException {

		Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));

		NodeList nodeList = doc.getChildNodes();
		printNote(nodeList, nodeXML, exception);

		xmlString = convertDOMtoString(nodeList);
		xmlObject = new XMLObject();
		xmlObject.setXmlObject(xmlString);

		setXmlObject(xmlObject);
	}

	public void readXMLF2B(String xml, List<String> columns, String exception) throws Exception {

		Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(new StreamSource(xml_remove_spaces));
		
		NodeList nodeList = doc.getChildNodes();

		for (String s : columns) {
			String nodeXML = s.replaceAll("[</>]", "");
			printNoteF2B(nodeList, nodeXML, exception);
		}
		xmlString = XML2String(doc);
		Document doc2 = docBuilder.parse(new InputSource(new StringReader(xmlString)));
		DOMSource source = new DOMSource(doc2);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);

		String formattedXML = XMLUtil.getPrettyString(writer.toString(), 2);
		xmlObject = new XMLObject();
		xmlObject.setXmlObject(formattedXML);

		setXmlObject(xmlObject);
	}

	public XMLObject getXmlObject() {
		return xmlObject;
	}

	public void setXmlObject(XMLObject xmlObject) {
		this.xmlObject = xmlObject;
	}

	private void printNote(NodeList nodeList, String nodeXML, String action) {

		String[] nodeParents = null;
		int lastElement = 0;
		String replaced = new String();
		String[] tempAction = action.split(",");

		if (nodeXML != null) {
			nodeParents = nodeXML.split("\\.");
			lastElement = nodeParents.length - 1;
		} else
			System.exit(1);

		for (int temp = 0; temp < nodeList.getLength(); temp++) {

			Node tempNode = nodeList.item(temp);

			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

				if (nodeParents[0].equals(tempNode.getNodeName())) {

					Element nodeElement = (Element) nodeList.item(temp);

					Node finalNode = nodeElement.getElementsByTagName(nodeParents[lastElement]).item(0);

					if ((finalNode != null) && (StringUtils.isAlphanumeric(finalNode.getTextContent())
							|| (StringUtils.isNotBlank(finalNode.getTextContent())))) {

						String replace = finalNode.getTextContent();

						switch (tempAction[0]) {
						case "1":
							replaced = XMLCleaner.removeNonAlphaNummerics(replace);
							break;
						case "2":
							replaced = XMLCleaner.addAlphaNummericsEnd(replace, tempAction[1]);
							break;
						case "3":
							replaced = XMLCleaner.addAlphaNummericsStart(replace, tempAction[1]);
							break;
						case "4":
							replaced = XMLCleaner.removeBetweenIndexes(replace, new Integer(tempAction[1]).intValue(),
									new Integer(tempAction[2]).intValue());
							break;
						case "5":
							replaced = XMLCleaner.insertBetweenIndexes(replace, new Integer(tempAction[1]).intValue(),
									tempAction[2]);
							break;
						case "6":
							replaced = XMLCleaner.removeCharacter(replace, tempAction[1]);
							break;
						case "7":
							replaced = XMLCleaner.replaceString(replace, tempAction[1]);
							break;
						default:
							System.out.println("Wrong Choice!");
							break;
						}

						finalNode.setTextContent(replaced);

					}

				}
				if (tempNode.hasChildNodes()) {

					// loop again if has child nodes
					printNote(tempNode.getChildNodes(), nodeXML, action);

				}

			}

		}

	}

	private void printNoteF2B(NodeList nodeList, String nodeXML, String exception) {

		String[] nodeParents = null;
		int lastElement = 0;
		int index = 1;
		if (nodeXML != null) {
			nodeParents = nodeXML.split("\\.");
			lastElement = nodeParents.length - 1;
		} else
			System.exit(1);

		for (int temp = 0; temp < nodeList.getLength(); temp++) {

			Node tempNode = nodeList.item(temp);

			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

				if (nodeParents[0].equals(tempNode.getNodeName())) {

					Element nodeElement = (Element) nodeList.item(temp);

					Node finalNode = nodeElement.getElementsByTagName(nodeParents[lastElement]).item(0);

					if (finalNode.getTextContent().equals(exception)) {

						if (index > 1) {
							Node parent = tempNode.getParentNode();
							parent.removeChild(tempNode);

						}
						++index;
					} else {

						Node parent = tempNode.getParentNode();
						parent.removeChild(tempNode);

					}

				}
				if (tempNode.hasChildNodes()) {

					// loop again if has child nodes
					printNoteF2B(tempNode.getChildNodes(), nodeXML, exception);

				}

			}

		}

	}

	private String convertDOMtoString(NodeList nodeList) {

		StringWriter xmlConverted = null;
		for (int temp = 0; temp < nodeList.getLength(); temp++) {

			Node tempNode = nodeList.item(temp);

			try {
				xmlConverted = new StringWriter();
				Transformer xform = TransformerFactory.newInstance().newTransformer();
				xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				xform.setOutputProperty(OutputKeys.INDENT, "yes");
				xform.transform(new DOMSource(tempNode), new StreamResult(xmlConverted));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return xmlConverted.toString();
	}

	private String XML2String(Document newDoc) {

		StringWriter sw = null;
		try {
			DOMSource domSource = new DOMSource(newDoc);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			sw = new StringWriter();
			StreamResult sr = new StreamResult(sw);

			transformer.transform(domSource, sr);
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return sw.toString();
	}

	public void setXml_remove_spaces(String xml_remove_spaces) {
		this.xml_remove_spaces = xml_remove_spaces;
	}
}
