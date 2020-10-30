package org.report.java.supports;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.report.java.interfaces.AbstractReport;
import org.report.java.interfaces.AppReportInterface;
import org.report.xml.support.InputProcessorXMLExtract;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

/**
 * version 1.0 Mirza Jahic  2018 Report Generator
 *
 */
public class AppXMLExtractor extends AbstractReport implements AppReportInterface {

	@Autowired(required = true)
	InputProcessorXMLExtract inputProcessorXMLExtract;
	private String sql4a;
	private String sql4b;
	private String sql;

	private void runF2BBackUp(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		System.out.println(
				"***Please Input file name (at "+path+") of F2BCASE_IDs, 'NO commas and apostrophes', separated and hit ENTER ***:");
		String fileName = scanner.nextLine().trim();

		List<String> caseIdList = generateQueryInputs(fileName);
		System.out.print(
				"***Please input 1 DETAIL_STATUS ('CASE_CREATED','CASE_FULLY_DRESSED'..with apostrophes) and hit ENTER ***:");
		String status = scanner.nextLine().trim();

		scanner.close();

		sql4b = sql4b.replace("status", status);
		System.out.println("*********************************IN PROGRESS******************************************");

		for (int i = 0, j = caseIdList.size(); i < j; i++) {

			String sql4 = sql4a + caseIdList.get(i) + sql4b;

			inputProcessorXMLExtract.connectToSourceF2B(sql4);
		}

		System.exit(0);
	}

	private void runLPUBackUp(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		System.out.println(
				"***Please Input file name (at C:/data/) of LETTER_ORDER IDs, 'NO commas and apostrophes', separated and hit ENTER ***:");
		String fileName = scanner.nextLine().trim();

		String ids = loadRetriggeringIDs(fileName);

		sql = sql.replace("caseID", ids);
		inputProcessorXMLExtract.connectToSourceLPU(sql);
		System.out.println("************************....IN PROGRESS....***************************");

	}

	public void setScanner(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		System.out.println("1. Backup F2B (CLOB) F2BCASE_DETAIL_DATA");
		System.out.println("2. Backup LPU (BLOB) INPUT_ORDER_XML ");

		String option = scanner.nextLine().trim();
		if (option.equals("1")) {
			runF2BBackUp(scanner);
		} else if (option.equals("2")) {

			runLPUBackUp(scanner);
		} else {
			System.out.println("Wrong option, only 1 or 2 are valid.");
			System.exit(0);

		}
	}

	public void setSql4a(String sql4a) {
		this.sql4a = sql4a;
	}

	public void setSql4b(String sql4b) {
		this.sql4b = sql4b;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
}
