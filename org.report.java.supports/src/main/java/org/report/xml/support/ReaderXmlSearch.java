package org.report.xml.support;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ReaderXmlSearch {

	private static final String XML_ROW1 = "F2BCASE_BUSINESS_ID";
	private static final String XML_ROW2 = "F2BCASE_DETAIL_STATUS";
	private static final String XML_ROW4 = "CREATE_TIMESTAMP";
	private DocumentBuilderFactory docBuilderFactory;
	private DocumentBuilder docBuilder;

	public ReaderXmlSearch() throws ParserConfigurationException {

		docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilder = docBuilderFactory.newDocumentBuilder();
	}

	public void readXML(String xml, Map<String, Object> row, String searchValue)
			throws SAXException, IOException, ParserConfigurationException {

		Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));

		NodeList nodeList = doc.getChildNodes();

		printNote(nodeList, row, searchValue);

	}

	private void printNote(NodeList nodeList, Map<String, Object> row, String searchValue) {

		for (int temp = 0; temp < nodeList.getLength(); temp++) {

			Node tempNode = nodeList.item(temp);

			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

				String regex = searchValue;

				// Pattern pattern = Pattern.compile("^"+regex,
				// Pattern.CASE_INSENSITIVE); Check Regex with ^ search value
				// starts with ^
				Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(tempNode.getTextContent());

				if ((tempNode.getNodeName() != null) && (matcher.find())) {

					System.out.println("***********Found*********");
					System.out.println(row.get(XML_ROW1).toString() + "   " + row.get(XML_ROW2).toString() + "   "
							+ row.get(XML_ROW4).toString());

					break;
				}

			}
			if (tempNode.hasChildNodes()) {

				// loop again if has child nodes
				printNote(tempNode.getChildNodes(), row, searchValue);

			}

		}

	}

}
