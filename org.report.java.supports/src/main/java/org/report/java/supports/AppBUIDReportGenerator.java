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
import org.report.xml.support.InputProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

/**
 * version 1.0 Mirza Jahic  2018 Report Generator
 *
 */
public class AppBUIDReportGenerator extends AbstractReport implements AppReportInterface {

	@Autowired(required = true)
	InputProcessor inputProcessor;

	private String sql4a;
	private String sql4b;
	private String sql4c;
	private String sql5a;
	private String sql5b;
	private String sql5a_external;
	private String sql4a_external;
	private String lastStep = "'LAST_STEP'";
	public void setScanner(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		int cellIndex = 1;

		List<String> caseIdList = new ArrayList<String>();
		try {

			System.out.println(
					"***Please Input file name (at "+ path+") of F2BCASE_IDs, 'NO commas and apostrophes', separated and hit ENTER ***:");
			String fileName = scanner.nextLine().trim();

			caseIdList = generateQueryInputs(fileName);
			System.out.print(
					"***Please input 1 DETAIL_STATUS ('CASE_CREATED','CASE_FULLY_DRESSED','LAST_STEP'with apostrophes) and hit ENTER ***:");
			String status = scanner.nextLine().trim();

			System.out.println(
					"***Columns separated by , put with tags (ex.(parent.child node,*NO SPACES BETWEEN COMMAS*) <n1:Address>.<n1:LastName>) and hit ENTER ***");
			System.out.println(
					"***For getting latest process step for each case use predifined column names DETAIL_CREATE_TIMESTAMP,LAST_STEP_DESCRIPTION, LAST_STEP (no appostrophes) ***:");
			String columns = scanner.nextLine().trim();
			System.out.print(
					"Any Attribute (ex. isoCd in <base:Currency isoCd='CHF'/>,no apostrophes, if not just hit ENTER):");
			String attributeName = scanner.nextLine();
			inputProcessor.processColumns(columns.trim(), attributeName);
			System.out.print("Do you want to use 1 F2BCASE_BUSINESS_ID or 2 F2BCASE_EXTERNAL_ID :");
			String idValue= scanner.nextLine();
			System.out.print("Enter Excel padding index (defualt is 1):");
			cellIndex = scanner.nextInt();
			
			
			scanner.close();

			if(idValue.equals("2")){
				sql4a = sql4a_external;
				sql5a = sql5a_external;
			}else if (!idValue.equals("2") && !idValue.equals("1"))
			{
				System.out.println("Wrong Choice!");
				System.exit(0);
			}
							
			
			if(status.equals(lastStep))
			{
				sql4b = sql4c;
			}else
			{
				sql4b = sql4b.replace("status", status);
			}
			System.out.println("*******************************....IN PROGRESS....**********************************");
		} catch (InputMismatchException ime) {
			System.err.println("Incorrect entry.");
		}

		
		for (int i = 0, j = caseIdList.size(); i < j; i++) {

			String sql4 = sql4a + caseIdList.get(i) + sql4b;
			String sql5 = sql5a + caseIdList.get(i) + sql5b;
			inputProcessor.connectToSourceBUID(sql4, sql5, cellIndex, i);
		}

		inputProcessor.createExcel();
		System.exit(0);
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

	public void setSql4c(String sql4c) {
		this.sql4c = sql4c;
	}

	public void setSql5a_external(String sql5a_external) {
		this.sql5a_external = sql5a_external;
	}

	public void setSql4a_external(String sql4a_external) {
		this.sql4a_external = sql4a_external;
	}

}
