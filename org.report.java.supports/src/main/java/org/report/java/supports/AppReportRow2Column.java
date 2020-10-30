package org.report.java.supports;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.report.java.interfaces.AbstractReport;
import org.report.java.interfaces.AppReportInterface;
import org.report.xml.support.ApachePOIExcelRead;
import org.report.xml.support.ApachePOIExcelWrite;
import org.report.xml.support.InputProcessorRow2Column;
import org.report.xml.support.SixModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

public class AppReportRow2Column extends AbstractReport implements AppReportInterface {

	@Autowired(required = true)
	private InputProcessorRow2Column inputProcessorRow2Column;

	private String option = "1";
	private String lsvIdentQuery;
	private String lsvIdentInsert;
	private String lsvCountQuery;
	private String lsvUpdateQuery;
	private String lsvExportQuery;

	public void setScanner(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		System.out.print("***Transpose style is default 1 for LSV Adress Change press any other number ***:");
		option = scanner.nextLine().trim();

		if ((option != null) && (option.equals("1"))) {

			setScannerTranspose(scanner);
		} else {
			setScannerLSV(scanner);
		}

	}

	public void setScannerLSV(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		System.out.print("Please insert LSV Ident:");
		String ident = scanner.next().trim();

		System.out.println("********************");
		System.out.println("1 List entry");
		System.out.println("2 Update entry");
		System.out.println("3 Insert new entry");
		System.out.println("4 Export table to excel (backup)");
		System.out.println("********************");
		System.out.print("Please select :");
		String choice = scanner.next();
		System.out.println();
		System.out.println(
				"*****************************************************************************************************************************************");
		lsvIdentQuery = lsvIdentQuery.replace("lsvIdent", ident);
		List<Map<String, Object>> returnedRecord = inputProcessorRow2Column.connectToSource(lsvIdentQuery);

		if (choice.equals("1")) {
			inputProcessorRow2Column.listEntry(returnedRecord);

		} else if (choice.equals("2")) {
			inputProcessorRow2Column.updateEntry(returnedRecord, lsvUpdateQuery, lsvIdentQuery, scanner);

		} else if (choice.equals("3")) {
			lsvIdentInsert = lsvIdentInsert.replace("CONFIG_KEY_NAME_", ident);
			inputProcessorRow2Column.insertEntry(lsvIdentInsert, lsvCountQuery, lsvIdentQuery, scanner);
		} else if (choice.equals("4")) {

			inputProcessorRow2Column.exportToExcel(lsvExportQuery);
		} else {
			System.out.println("Invalid choice");
			System.exit(1);
		}
	}

	public void setScannerTranspose(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		System.out.print("***Please Input XLS file name (at " + path + " ),  hit ENTER ***:");
		String xlsName = scanner.nextLine().trim();

		ApachePOIExcelRead xlsSheetRead = ApachePOIExcelRead.getInstance();
		ApachePOIExcelWrite xlsSheetWrite = ApachePOIExcelWrite.getInstance();

		try {
			XSSFSheet sheet = (XSSFSheet) xlsSheetRead.readExcel(xlsName);

			if (option.equals("1") || option.equals("")) {
				List<SixModel> report = xlsSheetRead.transposeReport(sheet);
				generateTransposeSheet(report, xlsSheetWrite);
			} else {

			}

		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}

	}

	public void setLsvIdentQuery(String lsvIdentQuery) {
		this.lsvIdentQuery = lsvIdentQuery;
	}

	public void setLsvIdentInsert(String lsvIdentInsert) {
		this.lsvIdentInsert = lsvIdentInsert;
	}

	public void setLsvCountQuery(String lsvCountQuery) {
		this.lsvCountQuery = lsvCountQuery;
	}

	public void setLsvUpdateQuery(String lsvUpdateQuery) {
		this.lsvUpdateQuery = lsvUpdateQuery;
	}

	public void setLsvExportQuery(String lsvExportQuery) {
		this.lsvExportQuery = lsvExportQuery;
	}

	private void generateTransposeSheet(List<SixModel> report, ApachePOIExcelWrite xlsSheetWrite) {

		for (int i = 0, j = report.size(); i < j; i++) {
			Row row = xlsSheetWrite.getNewRow();

			SixModel six = report.get(i);
			row.createCell(0)
					.setCellValue(six.getLsvIdentAddresses() == null ? null : six.getLsvIdentAddresses().toUpperCase());
			row.createCell(1).setCellValue(six.getName() == null ? null : six.getName().toUpperCase());
			row.createCell(2).setCellValue(six.getAnnotation() == null ? null : six.getAnnotation().toUpperCase());
			row.createCell(3).setCellValue(six.getStreet() == null ? null : six.getStreet().toUpperCase());
			row.createCell(4).setCellValue(six.getPostalCode() == null ? null : six.getPostalCode().toUpperCase());
			row.createCell(5).setCellValue(six.getPlace() == null ? null : six.getPlace().toUpperCase());
			row.createCell(6).setCellValue(six.getCountry() == null ? null : six.getCountry().toUpperCase());
			row.createCell(7).setCellValue(six.getPostBox() == null ? null : six.getPostBox().toUpperCase());
			row.createCell(8).setCellValue(six.getIsoCountryCd() == null ? null : six.getIsoCountryCd().toUpperCase());
			row.createCell(9).setCellValue(six.getCsCountryCd() == null ? null : six.getCsCountryCd().toUpperCase());

		}

		xlsSheetWrite.createExcel();
	}

}
