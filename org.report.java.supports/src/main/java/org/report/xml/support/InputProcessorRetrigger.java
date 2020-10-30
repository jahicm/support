package org.report.xml.support;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;

import org.report.java.interfaces.AbstractInputProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.xml.sax.SAXException;

public class InputProcessorRetrigger extends AbstractInputProcessor {

	@Autowired
	private ReaderXmlRetrigger readXMLRetrigger;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private Map<String, String> attrsXML = new HashMap<>();

	private static String XML_ROW = "F2BCASE_DETAIL_DATA";
	private static String XML_ID = "ID";
	private List<String> columnsOrder;
	private XMLObject xmlObject;

	public void processColumns(String columnsInput) {

		String[] columns = columnsInput.split(",");

		List<String> columnList = new ArrayList<String>(Arrays.asList(columns));
		columnsOrder = new ArrayList<String>(Arrays.asList(columns));
		Stream<String> xmlStream = columnList.stream().filter(predicate -> predicate.startsWith("<"));
		List<String> xmlColumns = xmlStream.collect(Collectors.toList());
		columnList.removeAll(xmlColumns);

		for (String key : xmlColumns)
			attrsXML.put(key, key.replaceAll("[</>]", ""));

	}

	public List<String> getColumnsOrder() {
		return columnsOrder;
	}

	public void connectToSource(String sql, String action)
			throws SAXException, IOException, ParserConfigurationException {

		List<Map<String, Object>> list = connectToSource(sql);

		for (Map<String, Object> row : list) {

			for (int i = 0, j = columnsOrder.size(); i < j; i++) {

				String columnName = columnsOrder.get(i);

				if (attrsXML.containsKey(columnName)) {

					readXMLRetrigger.readXML(row.get(XML_ROW).toString(), row.get(XML_ID).toString(),
							attrsXML.get(columnName), action);
				}

			}

			XMLObject xmlObject = readXMLRetrigger.getXmlObject();
			updateByPreparedStatement(xmlObject);
			System.out.println(row.get(XML_ID).toString() + "********DONE**********");

		}

	}

	public void walkXMLTree(String xml, List<String> columns, String exception) {

		try {
			readXMLRetrigger.readXMLF2B(xml, columns, exception);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		xmlObject = readXMLRetrigger.getXmlObject();

	}

	private Boolean updateByPreparedStatement(final XMLObject xmlObject) {
		String query = "UPDATE CASUSER.F2BCASEDETAIL SET F2BCASE_DETAIL_DATA =?  WHERE ID =?";
		return jdbcTemplate.execute(query, new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {

				ps.setObject(1, xmlObject.getXmlObject());
				ps.setInt(2, xmlObject.getId());

				return ps.execute();

			}
		});
	}

	public XMLObject getXmlObject() {
		return xmlObject;
	}

	@Override
	public List<Map<String, Object>> connectToSource(String... sql)
			throws SAXException, IOException, ParserConfigurationException {

		return jdbcTemplate.queryForList(sql[0]);
	}
}
