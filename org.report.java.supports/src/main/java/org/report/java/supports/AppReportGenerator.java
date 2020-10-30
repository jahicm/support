package org.report.java.supports;

import java.io.IOException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.report.java.interfaces.AbstractReport;
import org.report.java.interfaces.AppReportInterface;
import org.report.xml.support.InputProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

public class AppReportGenerator extends AbstractReport implements AppReportInterface  {

	@Autowired(required = true)
	InputProcessor inputProcessor;
	
	private int max_rows;
	private String sql;
	private String sql2;
	private String sql3;
	private String sql4a;
	private String sql4b;
	private String sql5a;
	private String sql5b;
	private String processTypes;
	private String startDate;
	private String endDate;
	private String conditionX = "1=1";
	private int cellIndex = 1;
	private final int reportSize=50000;
	
	public void setScanner(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		try {
			System.out
					.print("Please Input Process Type(s), with apostrophes, ex. 'FBP_0001','FBP_0002' and hit ENTER:");
			processTypes = scanner.nextLine().trim();
			System.out.print(
					"Please input 1 DETAIL_STATUS ('CASE_CREATED','CASE_FULLY_DRESSED'..with apostrophes, and hit ENTER:");
			String status = scanner.nextLine();
			System.out.print("Please input START DATE (ex. '01.03.18'..with apostrophes) and hit ENTER:");
			startDate = scanner.nextLine().trim();
			System.out.print("Please input END DATE (ex. '14.03.18'..with apostrophes) and hit ENTER:");
			endDate = scanner.nextLine().trim();
			System.out.println(
					"Columns separated by , put with tags (ex.(parent.child node,NO SPACES BETWEEN COMMAS) <n1:Address>.<n1:LastName>) and hit ENTER");
			System.out.println(
					"For getting latest Process Step for each case use predifined column names DETAIL_CREATE_TIMESTAMP,LAST_STEP_DESCRIPTION,LAST_STEP (no apostrophes):");
			String columns = scanner.nextLine();
			System.out.print(
					"Any Attribute (ex. isoCd in <base:Currency isoCd='CHF'/>,no apostrophes, if not just hit ENTER):");
			String attributeName = scanner.nextLine();
			System.out.print("WHERE condition ,if any (ex. STP_FLAG='XYZ' AND F2BUNIT='BU0015') else hit ENTER:");
			String conditionTemp = scanner.nextLine();
			inputProcessor.processColumns(columns.trim(), attributeName.trim());
			System.out.print("Enter Excel padding index (defualt is 1):");
			cellIndex = scanner.nextInt();

			scanner.close();

			conditionX = conditionTemp.equals("") ? conditionX : conditionTemp;

			sql = sql.replace("inputProcessor.getSQLColumns()", inputProcessor.getSQLColumns());
			sql = sql.replace("condition", conditionX);
			sql = sql.replace("processTypes", processTypes);
			sql = sql.replace("status", status);
			sql = sql.replace("startDate", startDate);
			sql = sql.replace("endDate", endDate);

			sql2 = sql2.replace("condition", conditionX);
			sql2 = sql2.replace("processTypes", processTypes);
			sql2 = sql2.replace("status", status);
			sql2 = sql2.replace("startDate", startDate);
			sql2 = sql2.replace("endDate", endDate);

			sql3 = sql3.replace("condition", conditionX);
			sql3 = sql3.replace("processTypes", processTypes);
			sql3 = sql3.replace("status", status);
			sql3 = sql3.replace("startDate", startDate);
			sql3 = sql3.replace("endDate", endDate);

			sql4b = sql4b.replace("status", status);
			System.out.println("************************************************************************************");
			System.out.println("Calculating number of rows to process...moment.");
			List<Map<String, Object>> queryResult = returnSelectedCases(sql3);
			int sizeOfSet = queryResult.size();
			System.out.println("Total number of rows:" + sizeOfSet);
			System.out.println("*****************************....IN PROGRESS....************************************");

			if (sizeOfSet < max_rows) {
				queryResult = null;
				inputProcessor.connectToSource(sql, sql2, cellIndex);
			} else {
				List<String> listOfCaseIDs = generateQueryInputs(queryResult);

				for (int i = 0, j = listOfCaseIDs.size(); i < j; i++) {

					String sql4 = sql4a + listOfCaseIDs.get(i) + sql4b;
					String sql5 = sql5a + listOfCaseIDs.get(i) + sql5b;

					inputProcessor.connectToSourceBUID(sql4, sql5, cellIndex, i);
					
					if((i*1000) % reportSize == 0)
						System.out.println((i*1000)+" records done..so far.");
						
				}
				inputProcessor.createExcel();
			}

		} catch (InputMismatchException ime) {
			System.err.println("Incorrect entry.");
		}

		System.exit(0);
	}

	
	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setSql2(String sql2) {
		this.sql2 = sql2;
	}

	public void setSql3(String sql3) {
		this.sql3 = sql3;
	}

	public void setSql4a(String sql4a) {
		this.sql4a = sql4a;
	}

	public void setSql4b(String sql4b) {
		this.sql4b = sql4b;
	}

	public void setSql5a(String sql5a) {
		this.sql5a = sql5a;
	}

	public void setSql5b(String sql5b) {
		this.sql5b = sql5b;
	}

	public void setMax_rows(int max_rows) {
		this.max_rows = max_rows;
	}

	

}
