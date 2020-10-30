package org.report.xml.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.report.java.interfaces.AbstractInputProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xml.sax.SAXException;

public class InputProcessorXMLExtract extends AbstractInputProcessor {

	private static String F2BCASE_DETAIL_DATA = "F2BCASE_DETAIL_DATA";
	private static String F2BCASE_BUSINESS_ID = "F2BCASE_BUSINESS_ID";
	private static String F2BCASE_EXTERNAL_ID = "F2BCASE_EXTERNAL_ID";
	private static String INPUT_ORDER_XML = "INPUT_ORDER_XML";
	private static String LPU_ID = "ID";
	private static String PATH;

	public void connectToSourceF2B(String sql) throws SAXException, IOException, ParserConfigurationException {

		List<Map<String, Object>> list = connectToSource(sql);
		Path newDirectoryPath = Paths.get(PATH + F2BCASE_DETAIL_DATA);
		Files.createDirectories(newDirectoryPath);
		for (Iterator<Map<String, Object>> iterator = list.listIterator(); iterator.hasNext();) {

			Map<String, Object> row = iterator.next();

			saveToFile(newDirectoryPath, row.get(F2BCASE_DETAIL_DATA).toString(),
					row.get(F2BCASE_BUSINESS_ID).toString(), row.get(F2BCASE_EXTERNAL_ID).toString());

		}
		System.out.println("Saved to " + newDirectoryPath);
	}

	public void connectToSourceLPU(String sql) throws SAXException, IOException, ParserConfigurationException {

		List<Map<Object, String>> list = deserializeObject(sql, INPUT_ORDER_XML, LPU_ID);
		Path newDirectoryPath = Paths.get(PATH + INPUT_ORDER_XML);
		for (Iterator<Map<Object, String>> iterator = list.listIterator(); iterator.hasNext();) {

			Map<Object, String> row = iterator.next();

			Object key = row.keySet().toArray()[0];

			if (row.get(key) != null) {

				saveToFile(newDirectoryPath, row.get(key), key.toString(), key.toString());
			} else {
				System.out.println("XML for " + row.get(LPU_ID) + " does not exist.");
			}
		}
		System.out.println("Saved to " + newDirectoryPath);
	}

	public static void setPath(String path) {
		InputProcessorXMLExtract.PATH = path;
	}

	public void setJdbcLPUTemplate(JdbcTemplate jdbcLPUTemplate) {
		this.jdbcLPUTemplate = jdbcLPUTemplate;
	}

	@Override
	public List<Map<String, Object>> connectToSource(String... sql)
			throws SAXException, IOException, ParserConfigurationException {
		return jdbcTemplate.queryForList(sql[0]);
		
	}


}
