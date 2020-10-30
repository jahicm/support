package org.report.xml.support;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.Console;
import org.apache.poi.ss.usermodel.Row;
import org.report.java.interfaces.AbstractInputProcessor;
import org.report.java.interfaces.AppReportInterface;

public class InputProcessorRow2Column extends AbstractInputProcessor {

	private String CONFIG_NAME = "CONFIG_NAME";
	private String CONFIG_VALUE = "CONFIG_VALUE";
	private String VALUE_ = "VALUE_";
	private String UPDATED_TIMESTAMP_ = "UPDATED_TIMESTAMP_";
	private String CASE_MANAGER_CONFIG_ID = "CASE_MANAGER_CONFIG_ID";
	private String CONFIG_KEY_NAME = "CONFIG_KEY_NAME";
	private String CREATED_TIMESTAMP = "CREATED_TIMESTAMP";
	private String UPDATED_TIMESTAMP = "UPDATED_TIMESTAMP";
	private String CONFIG_NAME_ = "CONFIG_NAME_";
	private String CONFIG_VALUE_ = "CONFIG_VALUE_";
	private String CREATE_TIMESTAMP_ = "CREATE_TIMESTAMP_";
	private String CASE_MANAGER_CONFIG_ID_ = "CASE_MANAGER_CONFIG_ID_";
	private String IDENTs = "IDENT";
	private String NAME = "NAME";
	private String VALUE = "VALUE";
	private String CREATED = "CREATED";
	private String UPDATED = "UPDATED";
	private String CONFIG_TABLE_NAME = "CONFIG_TABLE_NAME";

	private static ApachePOIExcelWrite xslSheet = ApachePOIExcelWrite.getInstance();

	public List<Map<String, Object>> connectToSource(String... sql) throws IOException {
		String lsvIdentQuery = sql[0];
		List<Map<String, Object>> list = jdbcTemplate.queryForList(lsvIdentQuery);

		return list;
	}

	public void updateEntry(List<Map<String, Object>> returnedRecord, String lsvUpdateQuery, String lsvIdentQuery,
			Scanner scanner) throws IOException {

		scanner.nextLine();
		Console consoleInput = AppReportInterface.getConsole();
		for (Iterator<Map<String, Object>> iterator = returnedRecord.listIterator(); iterator.hasNext();) {
			Map<String, Object> row = iterator.next();

			System.out.printf("%-15s%-15s\n", row.get(CONFIG_NAME) + ":", row.get(CONFIG_VALUE));
			System.out.print("Enter updated value or just hit enter:");

			String value = consoleInput.readLine();

			String lsvUpdateQueryTemp = lsvUpdateQuery.replace(VALUE_, value);
			String lsvUpdateQueryTemp2 = lsvUpdateQueryTemp.replace(UPDATED_TIMESTAMP_, date.format(formatter));

			if ((value != null) && (value.length() > 0)) {

				jdbcTemplate.update(lsvUpdateQueryTemp2, row.get(CASE_MANAGER_CONFIG_ID));

			}

		}
		System.out.println();
		System.out.println(
				"*******************************************************AFTER UPDATE**********************************************************************");

		listEntry(connectToSource(lsvIdentQuery));
	}

	public void listEntry(List<Map<String, Object>> returnedRecord) {

		System.out.printf("%-15s%-15s%-40s%-40s%-15s\n", IDENTs, NAME, VALUE, CREATED, UPDATED);
		System.out.println(
				"*****************************************************************************************************************************************");
		for (Iterator<Map<String, Object>> iterator = returnedRecord.listIterator(); iterator.hasNext();) {
			Map<String, Object> row = iterator.next();

			System.out.printf("%-15s%-15s%-40s%-40s%-15s\n", row.get(CONFIG_KEY_NAME), row.get(CONFIG_NAME),
					row.get(CONFIG_VALUE), row.get(CREATED_TIMESTAMP), row.get(UPDATED_TIMESTAMP));

		}
		System.out.println(
				"*****************************************************************************************************************************************");
		System.out.println();
	}

	public void insertEntry(String lsvIdentInsert, String lsvCountQuery, String lsvIdentQuery, Scanner scanner)
			throws IOException {

		scanner.nextLine();
		System.out.println("LSV Names available :" + java.util.Arrays.asList(IDENT.values()));
		System.out.print("Please insert LSV Name:");
		String name = scanner.nextLine();

		try {
			String configName = IDENT.valueOf(name).toString();
			System.out.print("Please insert LSV value:");
			Console consoleInput = AppReportInterface.getConsole();
			String lsvValue = consoleInput.readLine();

			Integer rowCount = jdbcTemplate.queryForObject(lsvCountQuery, null, Integer.class);
			++rowCount;

			lsvIdentInsert = lsvIdentInsert.replace(CONFIG_NAME_, configName);
			lsvIdentInsert = lsvIdentInsert.replace(CONFIG_VALUE_, lsvValue);
			lsvIdentInsert = lsvIdentInsert.replace(CREATE_TIMESTAMP_, date.format(formatter));
			lsvIdentInsert = lsvIdentInsert.replace(CASE_MANAGER_CONFIG_ID_, rowCount.toString());
			jdbcTemplate.execute(lsvIdentInsert);

		} catch (java.lang.IllegalArgumentException e) {
			System.out.println("Wrong LSV Name!");
			System.exit(1);
		}

		System.out.println(
				"*******************************************************AFTER INSERT**********************************************************************");
		System.out.println();
		listEntry(connectToSource(lsvIdentQuery));
	}

	public void exportToExcel(String lsvExportQuery) throws IOException {
		List<Map<String, Object>> list = connectToSource(lsvExportQuery);
		List<String> headers = Arrays.asList(CASE_MANAGER_CONFIG_ID, CONFIG_TABLE_NAME, CONFIG_KEY_NAME, CONFIG_NAME,
				CONFIG_VALUE, CREATED_TIMESTAMP, UPDATED_TIMESTAMP);
		xslSheet.createHeader(headers);

		for (Iterator<Map<String, Object>> iterator = list.listIterator(); iterator.hasNext();) {
			Row xlsRow = xslSheet.getNewRow();
			Map<String, Object> row = iterator.next();

			for (int i = 0, j = headers.size(); i < j; i++) {
				xslSheet.setCellValue(xlsRow, i, row.get(headers.get(i)));
			}

		}
		xslSheet.createExcel();
	}

}
