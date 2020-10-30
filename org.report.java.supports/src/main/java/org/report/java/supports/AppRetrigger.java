package org.report.java.supports;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.report.java.interfaces.AbstractReport;
import org.report.java.interfaces.AppReportInterface;
import org.report.xml.support.InputProcessorLPURetrigger;
import org.report.xml.support.InputProcessorRetrigger;
import org.report.xml.support.XMLObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

public class AppRetrigger extends AbstractReport implements AppReportInterface {

	@Autowired(required = true)
	InputProcessorRetrigger inputProcessorRetrigger;
	@Autowired(required = true)
	InputProcessorLPURetrigger inputProcessorLPURetrigger;
	private String sql;
	private String sql2;
	private String sql3;
	private String status;
	private String stepDescription;
	private String[] columnsArray;
	private String exceptionValue;

	private void runF2BRetriggering(Scanner scanner, String caseID)
			throws SAXException, IOException, ParserConfigurationException {

		String columns = null;
		String action = null;

		System.out.print(
				"***Please input 1 DETAIL_STATUS ('CASE_CREATED','CASE_FULLY_DRESSED'..with apostrophes) and hit ENTER ***:");
		status = scanner.nextLine().trim();

		System.out.println(
				"***Columns separated tag (ex.(parent.child node,NO SPACES BETWEEN COMMAS) <n1:Address>.<n1:LastName>) and hit ENTER ***");
		columns = scanner.nextLine();
		System.out.println(
				"***Please insert STEP_DESCRIPTION starting text portion (ex.PO or BESA has been.. no apostrophes ***:");
		stepDescription = scanner.nextLine().trim();

		System.out.println("1. Remove non alphanumeric characters(SPACE,-,.,,/)");
		System.out.println("2. Add characters in the end ex.(2,mirza)");
		System.out.println("3. Add characters in the begining ex.(3,mirza)");
		System.out.println("4. Remove characters in the index range ex.(4,5,8)");
		System.out.println("5. Insert characters in the index range ex.(5,5,mirza)");
		System.out.println("6. Remove character from string ex.(6,5)");
		System.out.println("7. Replace string(7,mirza)");
		action = scanner.nextLine();

		if (stepDescription.equals(""))
			stepDescription = null;

		if (action == null || action.equals(""))
			System.exit(0);

		inputProcessorRetrigger.processColumns(columns.trim());

		scanner.close();

		System.out.println("************************....IN PROGRESS....***************************");

		sql = sql.replace("caseID", caseID);
		sql = sql.replace("status", status);
		sql2 = sql2.replace("caseID", caseID);
		sql2 = sql2.replace("status", status);

		if (stepDescription != null)
			sql2 = sql2.replace("stepDescription", stepDescription);

		inputProcessorRetrigger.connectToSource(stepDescription == null ? sql : sql2, action);
	}

	private void runLPURetriggering(Scanner scanner, String caseID)
			throws SAXException, IOException, ParserConfigurationException {

		String columns = null;
		String action = null;
		int selectEncodingType;
		System.out.println("***It is recommended to crate backup for LPU by runing option 9 and then sub-option 2***");
		System.out.println("Select number for encoding type(default is UTF-8) (1.ISO-8859-1, 2.UTF-8)");
		selectEncodingType = new Integer(scanner.nextLine()).intValue();

		switch (selectEncodingType) {
		case 1:
			inputProcessorLPURetrigger.setEncodingType(StandardCharsets.ISO_8859_1.toString());
			inputProcessorLPURetrigger
					.setEncodingHeader("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\" ?>");
			break;
		case 2:
			inputProcessorLPURetrigger.setEncodingType(StandardCharsets.UTF_8.toString());
			inputProcessorLPURetrigger
					.setEncodingHeader("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>");
			break;
		default:
			System.out.println("Wrong choice!");
			System.exit(0);
		}

		System.out.println(
				"***Columns separated tag (ex.(parent.child node,NO SPACES BETWEEN COMMAS) <n1:Address>.<n1:LastName>) and hit ENTER ***");
		columns = scanner.nextLine();

		System.out.println("1. Remove non alphanumeric characters(SPACE,-,.,,/)");
		System.out.println("2. Add characters in the end ex.(2,mirza)");
		System.out.println("3. Add characters in the begining ex.(3,mirza)");
		System.out.println("4. Remove characters in the index range ex.(4,5,8)");
		System.out.println("5. Insert characters in the index range ex.(5,5,mirza)");
		System.out.println("6. Remove character from string ex.(6,5)");
		System.out.println("7. Replace string(7,mirza)");
		action = scanner.nextLine();

		if (action == null || action.equals(""))
			System.exit(0);

		inputProcessorLPURetrigger.processColumns(columns.trim());

		scanner.close();
		sql3 = sql3.replace("caseID", caseID);
		inputProcessorLPURetrigger.connectToSource(sql3, action);
	}

	public void setScanner(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		String caseID = null;
		System.out.println("1. Modify DB XML in F2B");
		System.out.println("2. Modify DB XML in LPU");
		System.out.println("3. F2B XML Clean up");
		System.out.println("4. Bulk Unzip");
		System.out.println("5. Bulk Zip");
		String option = scanner.nextLine().trim();

		if (option.equals("1") || option.equals("2")) {
			System.out.println(
					"***Please Input file name (at"+ path+") of F2BCASE_IDs, 'no comma or apostrophes', separated and hit ENTER ***:");
			String fileName = scanner.nextLine().trim();

			caseID = loadRetriggeringIDs(fileName);
		}

		switch (option) {
		case "1":
			runF2BRetriggering(scanner, caseID);
			break;
		case "2":
			runLPURetriggering(scanner, caseID);
			break;
		case "3":
			runF2BCleanup(scanner);
			break;
		case "4":
			unzipFolders(path, true, scanner);
			break;
		case "5":
			zipFolders(path, true, scanner);
			break;
		default:
			System.out.println("Wrong choice!");
			break;
		}
	}

	private void runF2BCleanup(Scanner scanner) throws IOException, ParserConfigurationException, SAXException {

		System.out.print("Example (<ch_2:auditData>.<ch_2:auditStep>):");
		String pattern = scanner.nextLine().trim();
		System.out.print("Exception Value :");
		exceptionValue = scanner.nextLine().trim();

		if (pattern == null || pattern.equals("")) {
			System.out.println("Wrong input!");
			System.exit(0);
		} else {
			columnsArray = pattern.split(",");
		}
		unzipFolders( path,false,scanner);// unzip folders
		for (String fullFileName : listOfFolders)// read files and prepare for
			readXML(fullFileName);
		zipFolders(path, false);// Zip updated folders back
	}

	private void readXML(String fullFileName) throws ParserConfigurationException, SAXException, IOException {
		System.out.println("*********Read XML and clean*************");
		String xml2String = null;
		File xmlFile = null;

		Path path = Paths.get(fullFileName);
		String file = path.getFileName().toString() + ".xml";
		xmlFile = new File(path + "\\" + file);
		Reader fileReader = new FileReader(xmlFile);
		BufferedReader bufReader = new BufferedReader(fileReader);

		StringBuilder sb = new StringBuilder();
		String line = bufReader.readLine();
		while (line != null) {
			sb.append(line).append("\n");
			line = bufReader.readLine();
		}
		xml2String = sb.toString();

		bufReader.close();

		List<String> columns = Arrays.asList(columnsArray);
		inputProcessorRetrigger.walkXMLTree(xml2String, columns, exceptionValue);
		XMLObject xmlObject = inputProcessorRetrigger.getXmlObject();

		System.out.println("*********Cleaning completed**********");
		updateXML(xmlObject, xmlFile);

	}

	private void updateXML(XMLObject xmlObject, File xmlFile) throws IOException {

		System.out.println("*********Updating XML file***********");
		BufferedWriter writer = new BufferedWriter(new FileWriter(xmlFile.getAbsolutePath()));
		writer.write(xmlObject.getXmlObject().toString());
		writer.close();
		System.out.println("*********Updating completed*********");

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

}
