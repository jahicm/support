package org.report.xml.support;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

@Component
public class InputProcessorSearchMultiple implements Runnable {

	private static String XML_ROW = "F2BCASE_DETAIL_DATA";
	private List<String> searchValueList;
	private List<Map<String, Object>> list;
	private String fileName;

	public InputProcessorSearchMultiple(List<Map<String, Object>> list, List<String> searchValueList, String fileName)
			throws SAXException, IOException, ParserConfigurationException {

		this.searchValueList = searchValueList;
		this.list = list;
		this.fileName = fileName;

	}

	public void run() {

		for (Iterator<Map<String, Object>> iterator = list.listIterator(); iterator.hasNext();) {

			try {

				Map<String, Object> row = iterator.next();
				for (String searchValue : searchValueList) {
					ReaderXmlSearchMultiple.readXML(row.get(XML_ROW).toString(), row, searchValue, fileName);

				}

			} catch (Exception e) {

				System.out.println("Invalid XML");
			}

		}

	}

}
