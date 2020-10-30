package org.report.java.supports;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

import org.report.java.interfaces.AbstractReport;
import org.report.java.interfaces.AppReportInterface;
import org.report.xml.support.InputProcessorSearchMultiple;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

/**
 * version 1.0 Mirza Jahic  2018 Report Generator
 *
 */
@Component
public class AppReportSearch extends AbstractReport implements AppReportInterface {

	private String sql1a;
	private String sql1b;
	private String sql1b2;
	private String sql4;
	private int reportSize = 100000;
	private int i = 1;
	private String fileName;

	public void setScanner(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		String status = null;
		String startDate = null;
		String endDate = null;
		String processTypes = null;
		List<String> searchValues = new ArrayList<String>();
		List<List<String>> completeSetOfsearchValues = new ArrayList<List<String>>();
		try {

			System.out.println("Search based on file load(XML Tags and Values Search).");

			System.out.println("***Please Input file name (at " + path
					+ " ) of (comma separated,line by line,no 'apostrophies'),  and hit ENTER ***:");
			String fileName = scanner.nextLine().trim();
			searchValues = loadFile(fileName);
			completeSetOfsearchValues = generateSearchInputList(searchValues);

			System.out.println(
					"***Please Input Process Type(s), with apostrophes, ex. 'FBP_0001','FBP_0002' and hit ENTER ***:");
			processTypes = scanner.nextLine().trim();
			System.out.print(
					"***Please input 1 DETAIL_STATUS ('CASE_FULLY_DRESSED' OR OTHER..with apostrophes) and hit ENTER ***:");
			status = scanner.nextLine().trim();
			System.out.print("***Please input START DATE (ex. '01.03.18'..with apostrophes) and hit ENTER ***:");
			startDate = scanner.nextLine();
			System.out.print("***Please input END DATE (ex. '14.03.18'..with apostrophes) and hit ENTER ***:");
			endDate = scanner.nextLine().trim();

			scanner.close();
			System.out.println("************************************************************************************");
		} catch (InputMismatchException ime) {
			System.err.println("Incorrect entry.");
		}

		sql1a += processTypes;
		sql1b = sql1b.replace("status", status);
		sql1b = sql1b.replace("startDate", startDate);
		sql1b = sql1b.replace("endDate", endDate);

		sql4 = sql4.replace("processTypes", processTypes);
		sql4 = sql4.replace("status", status);
		sql4 = sql4.replace("startDate", startDate);
		sql4 = sql4.replace("endDate", endDate);

		System.out.println("Calculating number of rows to process...moment.");
		List<Map<String, Object>> queryResult = returnSelectedCases(sql4);
		int sizeOfSet = queryResult.size();
		System.out.println("Total number of rows:" + sizeOfSet);
		System.out.println("*****************************....IN PROGRESS....************************************");

		List<String> listOfCaseIDs = generateQueryInputs(queryResult);
		queryResult = null;

		try {
			deleteExistingFile(fileName);
			for (Iterator<String> iterator = listOfCaseIDs.listIterator(); iterator.hasNext();) {

				String sqlSearch = sql1a + sql1b + iterator.next() + sql1b2;
				executeOption2(completeSetOfsearchValues, sqlSearch);
				iterator.remove();

				if ((i * 1000) % reportSize == 0)
					System.out.println((i * 1000) + " xmls  processed.");
				++i;
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void executeOption2(List<List<String>> completeSetOfsearchValues, String sql)
			throws SAXException, ParserConfigurationException, IOException, InterruptedException {
		ExecutorService executor = Executors.newCachedThreadPool();
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		for (List<String> searchValueList : completeSetOfsearchValues) {

			Runnable worker = new InputProcessorSearchMultiple(list, searchValueList, fileName);
			executor.execute(worker);

			if ((Thread.activeCount()) > 120)
				Thread.sleep(40000);
		}
		executor.shutdown();

	}

	public void setSql1a(String sql1a) {
		this.sql1a = sql1a;
	}

	public void setSql1b(String sql1b) {
		this.sql1b = sql1b;
	}

	public void setSql1b2(String sql1b2) {
		this.sql1b2 = sql1b2;
	}

	public void setSql4(String sql4) {
		this.sql4 = sql4;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private void deleteExistingFile(String fileName) {
		File file = new File(fileName);

		if (file.exists()) {
			System.out.println(fileName + " already exists,will be deleted!");
			file.delete();

		}
	}
}