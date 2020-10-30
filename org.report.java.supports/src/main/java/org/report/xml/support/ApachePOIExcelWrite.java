package org.report.xml.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ApachePOIExcelWrite {

	private String fileName = "C:\\data\\";
	private static ApachePOIExcelWrite instance = null;
	private XSSFSheet sheet;
	private XSSFWorkbook workbook;
	private static int index = 0;
	private static LocalTime localTime;
	private static Row headerRow;
	private static String SAFEKEEPINGACCOUNT = "SAFEKEEPINGACCOUNT";
	private static String BUNDLEID = "BUNDLEID";
	private static String CIF = "CIF";
	private static String SETTLEMENTACCOUNT = "SETTLEMENTACCOUNT";
	private static String TOTALFINALAMOUNTLBCCY = "TOTALFINALAMOUNTLBCCY";
	private static String ERRORMESSAGE = "ERRORMESSAGE";
	private static String OPEN = "OPEN";
	private static String COMMENTARY = "Business Informed";
	private String name = "Report";
	private static String F944380 ="F944380";
	protected ApachePOIExcelWrite() {

		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet(name);
	}

	public static ApachePOIExcelWrite getInstance() {
		if (instance == null) {
			instance = new ApachePOIExcelWrite();
		}
		return instance;
	}

	public Row getNewRow() {
		return sheet.createRow(index++);

	}

	public void setCellValue(Row row, int index, Object value) {

		if (value != null)
			row.createCell(index).setCellValue(value.toString());
		else
			row.createCell(index);
	}

	public void createHeader(List<String> columnList) {
		Row row = getNewRow();
		for (int i = 0, j = columnList.size(); i < j; i++)
			row.createCell(i).setCellValue(columnList.get(i));

		setHeaderRow(row);
	}

	public static Row getHeaderRow() {
		return headerRow;
	}

	public static void setHeaderRow(Row row) {
		headerRow = row;
	}

	public  void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void createExcel() {

		try {

			localTime = LocalTime.now();
			fileName += localTime.getHour() + "_" + localTime.getMinute() + "_" + localTime.getSecond() + ".xlsx";

			FileOutputStream outputStream = new FileOutputStream(fileName);
			workbook.write(outputStream);
			workbook.close();
			System.out.println("TOTAL:" + (sheet.getPhysicalNumberOfRows() - 1) + " ROWS");
			System.out.println("FILE LOCATION:" + fileName);

			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setName(String name) {
		this.name = name;
	}

	public static void updateExcel(String updateExcel, Map<String, Object> orderRow, Map<String, Object> row) {
		try {
			FileInputStream inputStream = new FileInputStream(new File(updateExcel));
			Workbook workbook = WorkbookFactory.create(inputStream);

			Sheet sheet = workbook.getSheetAt(0);

			int rowCount = sheet.getLastRowNum();

			System.out.println("***Excel row count before update***:" + rowCount);

			Row excelRow = sheet.createRow(++rowCount);
			Cell dateCell = excelRow.createCell(0);
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
			String todaysDateTime = now.format(formatter);
			dateCell.setCellValue(todaysDateTime);

			Cell pidCell = excelRow.createCell(1);
			pidCell.setCellValue(F944380);

			Cell safeKeepingAccounCell = excelRow.createCell(2);
			safeKeepingAccounCell.setCellValue(
					orderRow.get(SAFEKEEPINGACCOUNT) != null ? orderRow.get(SAFEKEEPINGACCOUNT).toString() : "");

			Cell bundelIDCell = excelRow.createCell(3);
			bundelIDCell.setCellValue(orderRow.get(BUNDLEID) != null ? orderRow.get(BUNDLEID).toString() : "");

			Cell cifCell = excelRow.createCell(4);
			cifCell.setCellValue(orderRow.get(CIF) != null ? orderRow.get(CIF).toString() : "");

			Cell settlementAccountCell = excelRow.createCell(5);
			settlementAccountCell.setCellValue(
					orderRow.get(SETTLEMENTACCOUNT) != null ? orderRow.get(SETTLEMENTACCOUNT).toString() : "");

			Cell finalPriceFccyCell = excelRow.createCell(6);
			finalPriceFccyCell.setCellValue(
					orderRow.get(TOTALFINALAMOUNTLBCCY) != null ? orderRow.get(TOTALFINALAMOUNTLBCCY).toString() : "");

			Cell errorDetailsCell = excelRow.createCell(7);
			errorDetailsCell.setCellValue(row.get(ERRORMESSAGE) != null ? row.get(ERRORMESSAGE).toString() : "");

			Cell statusCell = excelRow.createCell(8);
			statusCell.setCellValue(OPEN);

			Cell commentaryCell = excelRow.createCell(9);
			commentaryCell.setCellValue(COMMENTARY);

			inputStream.close();

			FileOutputStream outputStream = new FileOutputStream(updateExcel);
			workbook.write(outputStream);
			workbook.close();
			outputStream.close();
			rowCount = sheet.getLastRowNum();
			System.out.println("***Excel row count after update***:" + rowCount);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
