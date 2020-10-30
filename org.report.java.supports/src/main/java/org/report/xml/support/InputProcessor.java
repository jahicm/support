package org.report.xml.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.ss.usermodel.Row;
import org.report.java.interfaces.AbstractInputProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class InputProcessor extends AbstractInputProcessor {

	@Autowired
	private ReaderXml readXML;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private Map<String, String> attrs = new HashMap<>();
	private Map<String, String> attrsXML = new HashMap<>();
	private static String XML_ROW = "F2BCASE_DETAIL_DATA";
	// private static String XML_ROW = "F2BCASE_DOC_ELEMENTS";
	private List<String> columnsOrder;
	private Object status = null;
	private Object lastStepDescription = null;
	private Object detailCreateTime = null;
	private static String F2BCASE_BUSINESS_ID = "F2BCASE_BUSINESS_ID";
	private static String F2BCASE_DETAIL_STATUS = "F2BCASE_DETAIL_STATUS";
	private static String LAST_STEP = "LAST_STEP";
	private static String LAST_STEP_DESCRIPTION = "LAST_STEP_DESCRIPTION";
	private static String STEP_DESCRIPTION = "STEP_DESCRIPTION";
	private static String DETAIL_CREATE_TIMESTAMP = "DETAIL_CREATE_TIMESTAMP";
	private static String CREATE_TIMESTAMP = "CREATE_TIMESTAMP";
	private static StringBuilder strBuilder = new StringBuilder();
	private static ApachePOIExcelWrite xslSheet = ApachePOIExcelWrite.getInstance();

	public void processColumns(String columnsInput, String attributeName) {

		String[] columns = columnsInput.split(",");

		System.out.println("Columns :" + Arrays.asList(columns));
		List<String> columnList = new ArrayList<String>(Arrays.asList(columns));
		columnsOrder = new ArrayList<String>(Arrays.asList(columns));
		Stream<String> xmlStream = columnList.stream().filter(predicate -> predicate.startsWith("<"));
		List<String> xmlColumns = xmlStream.collect(Collectors.toList());
		columnList.removeAll(xmlColumns);

		for (String key : columnList) {
			attrs.put(key, null);

			if (!(key.equals(LAST_STEP)) && !(key.equals(DETAIL_CREATE_TIMESTAMP))
					&& !(key.equals(LAST_STEP_DESCRIPTION))) {
				if (key.toUpperCase().equals(CREATE_TIMESTAMP))
					strBuilder.append("C." + key.toUpperCase() + ",");
				else
					strBuilder.append(key + ",");
			}
		}
		for (String key : xmlColumns)
			attrsXML.put(key, key.replaceAll("[</>]", ""));

		readXML.setAtrributeName(attributeName);
	}

	public List<String> getColumnsOrder() {
		return columnsOrder;
	}

	public String getSQLColumns() {
		return strBuilder.length() > 1 ? strBuilder.deleteCharAt(strBuilder.length() - 1).toString()
				: F2BCASE_BUSINESS_ID;
	}

	public void connectToSource(String sql, String sql2, int cellIndex)
			throws SAXException, IOException, ParserConfigurationException {

		List<Map<String, Object>> list = connectToSource(sql);
		List<Map<String, Object>> list2 = connectToSource(sql2);

		xslSheet.createHeader(columnsOrder);
		readXML.setHeaderRow(ApachePOIExcelWrite.getHeaderRow());

		for (Iterator<Map<String, Object>> iterator = list.listIterator(); iterator.hasNext();) {

			Map<String, Object> row = iterator.next();
			Row xlsRow = xslSheet.getNewRow();

			for (int i = 0, j = columnsOrder.size(); i < j; i++) {

				String columnName = columnsOrder.get(i);

				if (attrs.containsKey(columnName)) {

					if (columnName.equals(LAST_STEP)) {

						String column = row.get(F2BCASE_BUSINESS_ID).toString();

						list2.forEach(entry -> {
							if (entry.containsValue(column)) {
								status = entry.get(F2BCASE_DETAIL_STATUS);

							}
						});

						xslSheet.setCellValue(xlsRow, i, status);

					} else if (columnName.equals(LAST_STEP_DESCRIPTION)) {

						String column = row.get(F2BCASE_BUSINESS_ID).toString();

						list2.forEach(entry -> {
							if (entry.containsValue(column)) {
								lastStepDescription = entry.get(STEP_DESCRIPTION);

							}
						});

						xslSheet.setCellValue(xlsRow, i, lastStepDescription);

					} else if (columnName.equals(DETAIL_CREATE_TIMESTAMP)) {

						String column = row.get(F2BCASE_BUSINESS_ID).toString();

						list2.forEach(entry -> {
							if (entry.containsValue(column)) {
								detailCreateTime = entry.get(CREATE_TIMESTAMP);

							}
						});

						xslSheet.setCellValue(xlsRow, i, detailCreateTime);

					} else {
						xslSheet.setCellValue(xlsRow, i, row.get(columnName));
					}

				} else if (attrsXML.containsKey(columnName)) {

					try {
						readXML.readXML(row.get(XML_ROW).toString(), i, xlsRow, attrsXML.get(columnName), cellIndex);
					} catch (SAXParseException e) {
						System.out.println(row.get(F2BCASE_BUSINESS_ID).toString() + " has invalid XML");
					}

				}

			}

			iterator.remove();// Release heap space

		}
		createExcel();
	}

	public void connectToSourceBUID(String sql, String sql2, int cellIndex, int numberOfExcels)
			throws SAXException, IOException, ParserConfigurationException {

		List<Map<String, Object>> list = connectToSource(sql);
		List<Map<String, Object>> list2 = connectToSource(sql2);

		if (numberOfExcels == 0) {
			xslSheet.createHeader(columnsOrder);
			readXML.setHeaderRow(ApachePOIExcelWrite.getHeaderRow());
		}
		for (Iterator<Map<String, Object>> iterator = list.listIterator(); iterator.hasNext();) {

			Map<String, Object> row = iterator.next();
			Row xlsRow = xslSheet.getNewRow();

			for (int i = 0, j = columnsOrder.size(); i < j; i++) {

				String columnName = columnsOrder.get(i);

				if (attrs.containsKey(columnName)) {

					if (columnName.equals(LAST_STEP)) {

						String column = row.get(F2BCASE_BUSINESS_ID).toString();

						list2.forEach(entry -> {
							if (entry.containsValue(column)) {
								status = entry.get(F2BCASE_DETAIL_STATUS);

							}
						});

						xslSheet.setCellValue(xlsRow, i, status);

					} else if (columnName.equals(LAST_STEP_DESCRIPTION)) {

						String column = row.get(F2BCASE_BUSINESS_ID).toString();

						list2.forEach(entry -> {
							if (entry.containsValue(column)) {
								lastStepDescription = entry.get(STEP_DESCRIPTION);

							}
						});

						xslSheet.setCellValue(xlsRow, i, lastStepDescription);

					} else if (columnName.equals(DETAIL_CREATE_TIMESTAMP)) {

						String column = row.get(F2BCASE_BUSINESS_ID).toString();

						list2.forEach(entry -> {
							if (entry.containsValue(column)) {
								detailCreateTime = entry.get(CREATE_TIMESTAMP);

							}
						});

						xslSheet.setCellValue(xlsRow, i, detailCreateTime);

					} else {

						xslSheet.setCellValue(xlsRow, i, row.get(columnName));
					}

				} else if (attrsXML.containsKey(columnName)) {

					try {
						readXML.readXML(row.get(XML_ROW).toString(), i, xlsRow, attrsXML.get(columnName), cellIndex);
					} catch (SAXParseException e) {
						System.out.println(row.get(F2BCASE_BUSINESS_ID).toString() + " has invalid XML");
					}

				}

			}

			iterator.remove();// Release heap space

		}

	}

	public void createExcel() {

		xslSheet.createExcel();
	}

	@Override
	public List<Map<String, Object>> connectToSource(String... sql)
			throws SAXException, IOException, ParserConfigurationException {

		return jdbcTemplate.queryForList(sql[0]);
	}
}
