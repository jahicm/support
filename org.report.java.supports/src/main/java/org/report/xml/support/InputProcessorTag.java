package org.report.xml.support;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.report.java.interfaces.AbstractInputProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xml.sax.SAXException;

public class InputProcessorTag extends AbstractInputProcessor {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private ReaderXmlTag readXMLTag;
	private static String XML_ROW = "F2BCASE_DETAIL_DATA";
	private static String F2BCASE_BUSINESS_ID = "F2BCASE_BUSINESS_ID";

	public void connectToSource(String sql, String searchValue)
			throws SAXException, IOException, ParserConfigurationException {

		List<Map<String, Object>> list = connectToSource(sql);
		try {
			for (Map<String, Object> row : list) {

				readXMLTag.readXML(row.get(XML_ROW).toString(), row.get(F2BCASE_BUSINESS_ID).toString(), searchValue);

			}
		} catch (Exception e) {

			System.out.println("Invalid XML");
		}

	}

	public List<Map<String, Object>> connectToSource(String... sql)
			throws SAXException, IOException, ParserConfigurationException {

		return jdbcTemplate.queryForList(sql[0]);
	}

}
