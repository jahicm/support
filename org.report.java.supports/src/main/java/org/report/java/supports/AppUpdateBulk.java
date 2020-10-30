package org.report.java.supports;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.report.java.interfaces.AppReportInterface;
import org.report.xml.support.UpdateTableProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class AppUpdateBulk implements AppReportInterface {

	@Autowired
	private UpdateTableProcessor updateBulkProcessor;

	public void setScanner(Scanner scanner) {

		System.out.print("Input file name to read (at C:/data location) :");
		String fileName = scanner.nextLine().trim();
		System.out.print("Input SCHEMA.TABLE name (ex. CASUSER.F2BCASE) :");
		String schemaTable = scanner.nextLine().trim();

		System.out.print("COLUMN(s) to update and value (ex. STP_FLAG='YES' or STP_FLAG='YES', COLUMN='VALUE') :");
		String updateExpression = scanner.nextLine().trim();

		List<Object[]> collectionOfID = readTextFile("C:/data/" + fileName);

		updateBulkProcessor.setCollectionOfID(collectionOfID);
		updateBulkProcessor.setSchemaTable(schemaTable);
		updateBulkProcessor.setUpdateExpression(updateExpression);
		updateBulkProcessor.updateTable();
	}

	private ArrayList<Object[]> readTextFile(String fileName) {
		ArrayList<Object[]> collectionOfID = new ArrayList<Object[]>();
		System.out.println("****Load data from file*****");
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

			String line = br.readLine();

			while (line != null) {

				collectionOfID.add(new Object[] { line });
				line = br.readLine();
			}
		} catch (IOException e) {
			System.out.println("*********Error while reading text file **********");
			e.printStackTrace();
		}

		return collectionOfID;
	}
}
