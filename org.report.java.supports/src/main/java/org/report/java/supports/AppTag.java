package org.report.java.supports;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.report.java.interfaces.AbstractReport;
import org.report.java.interfaces.AppReportInterface;
import org.report.xml.support.InputProcessorTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

public class AppTag extends AbstractReport implements AppReportInterface {

	@Autowired(required = true)
	InputProcessorTag inputProcessorTag;
	private String sql;
	private String sqlFile2;
	private String sqlFile;

	public void setScanner(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		String processTypes = null;
		String status = null;
		String startDate = null;
		String endDate = null;
		String tagName = null;
		List<String> caseIdList = new ArrayList<String>();
		try {

			System.out.println(
					"1. Identify tags for all cases in certail timeframe.	2.Identify tags for only given cases (based on file input)");
			String option = scanner.nextLine().trim();

			if (option.equals("") || option == null || (!option.equals("1") && !option.equals("2"))) {
				System.out.println("Wrong option, only 1 or 2 are valid");
				System.exit(0);
			} else if (option.equals("2")) {
				System.out.println("***Please Input file name (at " + path
						+ ") of F2BCASE_IDs, 'NO commas and apostrophes', separated and hit ENTER ***:");
				String fileName = scanner.nextLine().trim();

				caseIdList = generateQueryInputs(fileName);

				System.out.print(
						"Please input 1 DETAIL_STATUS ('CASE_CREATED','CASE_FULLY_DRESSED'..with apostrophes, and hit ENTER:");
				status = scanner.nextLine();

				System.out.println(
						"Please input basic tag name to search (ex. <ns2:PostalCode> = PostalCode (no 'apostrophes') and hit ENTER");
				tagName = scanner.nextLine().trim();

				scanner.close();
				System.out.println(
						"*****************************....IN PROGRESS....**************************************");

				sqlFile = sqlFile.replace("status", status);

				for (int i = 0, j = caseIdList.size(); i < j; i++) {

					String sqlJoined = sqlFile + caseIdList.get(i) + sqlFile2;

					inputProcessorTag.connectToSource(sqlJoined, tagName);
				}

			} else if (option.equals("1")) {
				System.out.print(
						"Please Input Process Type(s), with apostrophes, ex. 'FBP_0001','FBP_0002' and hit ENTER:");
				processTypes = scanner.nextLine().trim();
				System.out.print(
						"Please input 1 DETAIL_STATUS ('CASE_CREATED','CASE_FULLY_DRESSED'..with apostrophes, and hit ENTER:");
				status = scanner.nextLine();
				System.out.print("Please input START DATE (ex. '01.03.18'..with apostrophes) and hit ENTER:");
				startDate = scanner.nextLine().trim();
				System.out.print("Please input END DATE (ex. '14.03.18'..with apostrophes) and hit ENTER:");
				endDate = scanner.nextLine().trim();
				System.out.println(
						"Please input basic tag name to search (ex. <ns2:PostalCode> = PostalCode (no 'apostrophes') and hit ENTER");
				tagName = scanner.nextLine().trim();

				scanner.close();
				System.out.println(
						"*****************************....IN PROGRESS....**************************************");

				sql = sql.replace("processTypes", processTypes);
				sql = sql.replace("status", status);
				sql = sql.replace("startDate", startDate);
				sql = sql.replace("endDate", endDate);

				inputProcessorTag.connectToSource(sql, tagName);
			}
		} catch (InputMismatchException ime) {
			System.err.println("Incorrect entry.");
		}
		System.exit(0);
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setSqlFile2(String sqlFile2) {
		this.sqlFile2 = sqlFile2;
	}

	public void setSqlFile(String sqlFile) {
		this.sqlFile = sqlFile;
	}

}
