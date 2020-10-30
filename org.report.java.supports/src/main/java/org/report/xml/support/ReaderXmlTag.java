package org.report.xml.support;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ReaderXmlTag {

	private static final Set<String> duplicates = new HashSet<String>();

	private DocumentBuilderFactory docBuilderFactory;
	private DocumentBuilder docBuilder;

	public ReaderXmlTag() throws ParserConfigurationException {

		docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilder = docBuilderFactory.newDocumentBuilder();
	}

	public void readXML(String xml, String businessID, String searchValue)
			throws SAXException, IOException, ParserConfigurationException {

		Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));
		NodeList nodeList = doc.getChildNodes();
		printNote(nodeList, businessID, searchValue);

	}

	private void printNote(NodeList nodeList, String businessID, String searchValue) {

		for (int temp = 0; temp < nodeList.getLength(); temp++) {

			Node tempNode = nodeList.item(temp);

			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

				Element nodeElement = (Element) nodeList.item(temp);

				String finalNode = nodeElement.getTagName();

				if ((finalNode != null)) {

					if (finalNode.contains(searchValue) && (!duplicates.contains(finalNode))) {
						System.out.println("<" + nodeElement.getParentNode().getNodeName() + ">.<" + finalNode + ">"
								+ "  " + businessID);
						duplicates.add(finalNode);
					}

				}

				if (tempNode.hasChildNodes()) {

					// loop again if has child nodes
					printNote(tempNode.getChildNodes(), businessID, searchValue);

				}

			}

		}

	}

}
