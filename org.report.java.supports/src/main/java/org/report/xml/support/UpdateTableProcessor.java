package org.report.xml.support;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class UpdateTableProcessor {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private List<Object[]> collectionOfID;
	private String updateExpression;
	private String schemaTable;

	public String getUpdateExpression() {
		return updateExpression;
	}

	public void setUpdateExpression(String updateExpression) {
		this.updateExpression = updateExpression;
	}

	public String getSchemaTable() {
		return schemaTable;
	}

	public void setSchemaTable(String schemaTable) {
		this.schemaTable = schemaTable;
	}

	public List<Object[]> getCollectionOfID() {
		return collectionOfID;
	}

	public void setCollectionOfID(List<Object[]> collectionOfID) {
		this.collectionOfID = collectionOfID;
	}

	public JdbcTemplate getJdbcTemplates() {
		return jdbcTemplate;
	}

	public void setJdbcTemplates(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void updateTable() {

		String sql = "UPDATE " + schemaTable + " SET " + updateExpression + " WHERE F2BCASE_BUSINESS_ID=?";
		System.out.println(sql);
		int[] updatedNumber = jdbcTemplate.batchUpdate(sql, collectionOfID);

		System.out.println(updatedNumber.length + " updates!");
		System.exit(0);
	}
}
