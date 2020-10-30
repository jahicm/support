package org.report.java.supports;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.report.java.interfaces.AppReportInterface;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

/**
 * version 1.0 Mirza Jahic  March 2018 Report Generator
 * 
 * 1. Generate F2B report , by pulling data from the columns and XML structure
 * 2. Generate F2B Report for specific CaseIDs, pulling info from the columns
 * 3. Search for for certain String in XML structure of database based on random value  (multi-threaded)
 * 4. Update Bulk database
 * 5. Extract XML CLOBs from F2BCASE_DETAIL_DATA or XML BLOBs from INPUT_ORDER_XML into XML files
 * 6. Download/Upload zips to document server aka. DocStore.
 * 7. Rows2Column transpose of report in Excel
 * etc...
 *
 */
public class App {

	private static String choice;

	private static AppReportInterface appReport;

	public static void main(String[] args)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("spring-config.xml");

		System.out.println("*************************************************************************************");
		System.out.println("1.	Create F2B Report");
		System.out.println("2.	Create F2B Report based on list of Business Case IDs");
		System.out.println("3.	Search for Case ID based on random value");
		System.out.println("4.	Bulk DB updates");
		System.out.println("5.	Identify report tags");
		System.out.println("6.	F2B & LPU XML retrigger,Bulk Unzip/Zip and clean up");
		System.out.println("7.	Rows2Column based report & LSV Address update");
		System.out.println("8.	XPP CSInvest update");
		System.out.println("9.	Extract XML CLOBs from F2BCASE_DETAIL_DATA or XML BLOBs from INPUT_ORDER_XML into XML files");
		System.out.println("11.	DocStore Download/Upload of zips");
		System.out.println("12.	Tax Reclaim");
		System.out.println("*************************************************************************************");
		Scanner scanner = new Scanner(System.in);
		choice = scanner.nextLine();
		
		switch (choice) {
		case "1":
			appReport = (AppReportGenerator) appContext.getBean("appGenerator");
			appReport.setScanner(scanner);
			break;
		case "2":
			appReport = (AppBUIDReportGenerator) appContext.getBean("appBUID");
			appReport.setScanner(scanner);
			break;
		case "3":
			appReport = (AppReportSearch) appContext.getBean("appSearch");
			appReport.setScanner(scanner);
			break;
		case "4":
			appReport = (AppUpdateBulk) appContext.getBean("appUpdateBulk");
			appReport.setScanner(scanner);
			break;
		case "5":
			appReport = (AppTag) appContext.getBean("appTag");
			appReport.setScanner(scanner);
			break;
		case "6":
			appReport = (AppRetrigger) appContext.getBean("appRetrigger");
			appReport.setScanner(scanner);
			break;
		case "7":
			appReport = (AppReportRow2Column) appContext.getBean("appReportRow2Column");
			appReport.setScanner(scanner);
			break;
		case "8":
			appReport = (AppCSInvest) appContext.getBean("appCSInvest");
			appReport.setScanner(scanner);
			break;
		case "9":
			appReport = (AppXMLExtractor) appContext.getBean("appXMLExtractor");
			appReport.setScanner(scanner);
			break;
		case "11":
			appReport = (AppDocStore) appContext.getBean("appDocStore");
			appReport.setScanner(scanner);
			break;
		case "12":
			appReport = (AppTaxReclaim) appContext.getBean("appTaxReclaim");
			appReport.setScanner(scanner);
			break;
		default:
			System.out.println("Wrong Choice!");
			break;
		}

		appContext.close();
	}

}
