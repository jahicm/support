package org.report.xml.support;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.ss.usermodel.Row;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ReaderXml {

	private int internalIndex;
	private Row headerRow;
	private String attributeName;
	private DocumentBuilderFactory docBuilderFactory;
	private DocumentBuilder docBuilder;

	public ReaderXml() throws ParserConfigurationException {

		docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilder = docBuilderFactory.newDocumentBuilder();
	}

	public void readXML(String xml, int index, Row xlsRow, String nodeXML, int cellIndex)
			throws SAXException, IOException, ParserConfigurationException {

		Document doc = docBuilder.parse(new InputSource(new StringReader(xml.replaceAll("&","&amp;"))));
		NodeList nodeList = doc.getChildNodes();
		internalIndex = index;

		printNote(nodeList, index, xlsRow, nodeXML, cellIndex);

	}

	private void printNote(NodeList nodeList, int index, Row xlsRow, String nodeXML, int cellIndex) {

		String[] nodeParents = null;
		int lastElement = 0;

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

					NodeList finalNodes = nodeElement.getChildNodes();
					recurseChildNodes(finalNodes, nodeParents[lastElement], 0, xlsRow, cellIndex, nodeParents);

				}

				if (tempNode.hasChildNodes()) {

					// loop again if has child nodes
					printNote(tempNode.getChildNodes(), index, xlsRow, nodeXML, cellIndex);

				}

			}

		}

	}

	private void recurseChildNodes(NodeList finalNodes, String nodeChild, int index, Row xlsRow, int cellIndex,
			String[] nodeParents) {

		String currencyCountryAttribute = null;

		if ((finalNodes == null) || (index >= finalNodes.getLength()))
			return;

		if ((finalNodes.item(index) != null) && (finalNodes.item(index).getNodeName().equals(nodeChild))) {

			if (finalNodes.item(index).hasAttributes()
					&& (finalNodes.item(index).getAttributes().getNamedItem(attributeName) != null)) {
				currencyCountryAttribute = finalNodes.item(index).getAttributes().getNamedItem(attributeName)
						.getNodeValue();
				xlsRow.createCell(internalIndex).setCellValue(currencyCountryAttribute);
				addHeaders(internalIndex, nodeParents);
				internalIndex += cellIndex;
			} else {
				xlsRow.createCell(internalIndex).setCellValue(finalNodes.item(index).getTextContent());
				addHeaders(internalIndex, nodeParents);
				internalIndex += cellIndex;
			}
		}

		recurseChildNodes(finalNodes, nodeChild, ++index, xlsRow, cellIndex, nodeParents);

	}

	public Row getHeaderRow() {
		return headerRow;
	}

	public void setHeaderRow(Row headerRow) {
		this.headerRow = headerRow;
	}

	private void addHeaders(int internalIndex, String[] nodeParents) {
		Row header = getHeaderRow();
		header.createCell(internalIndex).setCellValue("<" + nodeParents[0] + ">.<" + nodeParents[1] + ">");
	}

	public void setAtrributeName(String attributeName) {
		this.attributeName = attributeName;
	}
}
