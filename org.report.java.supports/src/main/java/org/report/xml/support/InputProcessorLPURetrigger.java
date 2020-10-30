package org.report.xml.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class InputProcessorLPURetrigger extends AbstractInputProcessor {

	@Autowired
	private ReaderXmlRetrigger readXMLRetrigger;

	private Map<String, String> attrsXML = new HashMap<>();

	private static String INPUT_ORDER_XML = "INPUT_ORDER_XML";
	private static String XML_ID = "ID";
	private List<String> columnsOrder;
	private String encodingType;
	private String encodingHeader;

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

		List<Map<Object, String>> list = deserializeObject(sql, INPUT_ORDER_XML, XML_ID);

		for (Map<Object, String> row : list) {

			Object key = row.keySet().toArray()[0];

			for (int i = 0, j = columnsOrder.size(); i < j; i++) {

				String columnName = columnsOrder.get(i);

				if (attrsXML.containsKey(columnName)) {

					try {
						if (key != null && row.get(key) != null)
							readXMLRetrigger.readXML(row.get(key), key.toString(), attrsXML.get(columnName), action);
					} catch (SAXParseException e) {
						System.out.println(" Iinvalid XML");
					}
				}

			}

			XMLObject xmlObject = readXMLRetrigger.getXmlObject();
			if (xmlObject != null) {
				updateByPreparedStatement(xmlObject);

				System.out.println(xmlObject.getId() + "-********DONE**********");
			}

		}

	}

	private Boolean updateByPreparedStatement(final XMLObject xmlObject) {
		String query = "UPDATE LPU.LETTER_ORDER SET INPUT_ORDER_XML =?  WHERE ID =?";
		return jdbcLPUTemplate.execute(query, new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				String xml = null;
				String header = null;
				String document = null;

				byte[] data = null;
				try {

					header = getEncodingHeader();
					xml = xmlObject.getXmlObject().toString().trim();
					document = header + xml;
					data = document.getBytes(encodingType);

				} catch (UnsupportedEncodingException e) {

					e.printStackTrace();
				}
				ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
				ps.setBlob(1, inputStream);
				ps.setInt(2, xmlObject.getId());

				return ps.execute();

			}
		});

	}

	public void setEncodingType(String encodingType) {
		this.encodingType = encodingType;
	}

	public void setEncodingHeader(String encodingHeader) {
		this.encodingHeader = encodingHeader;
	}

	public String getEncodingHeader() {
		return encodingHeader;
	}

	public String getEncodingType() {
		return encodingType;
	}

	@Override
	public List<Map<String, Object>> connectToSource(String... sql)
			throws SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

}
