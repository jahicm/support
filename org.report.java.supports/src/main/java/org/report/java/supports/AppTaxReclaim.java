package org.report.java.supports;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.report.java.interfaces.AbstractReport;
import org.report.java.interfaces.AppReportInterface;
import org.report.xml.support.InputProcessorTaxReclaim;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

public class AppTaxReclaim extends AbstractReport implements AppReportInterface {

	@Autowired(required = true)
	InputProcessorTaxReclaim inputProcessorTaxReclaim;

	private List<String> cifList = new ArrayList<String>();
	private List<String> productList = new ArrayList<String>();
	private static final int CIF_SIZE = 12;
	private String wfMessagesQuery;
	private String changeDomMessageQuery;
	private String activationErrorsListQuery;

	public void setScanner(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		System.out.println("*************************************************************************************");
		System.out.println("1. Cancel TRM Workflow");
		//System.out.println("2. Change Domicile ");
		System.out.println("3. Create Activation Error Report");
		System.out.println("*************************************************************************************");
		String option = scanner.nextLine().trim();
		if (option.equals("1")) {
			cancelTRM(scanner);
		} else if (option.equals("2")) {
			changeDomicile(scanner);
		} else if (option.equals("3")) {
			createActivationErrorReport(scanner);
		} else {
			System.out.println("Wrong option, only 1 or 2 are valid.");
			System.exit(0);

		}
	}

	public void setWfMessagesQuery(String wfMessagesQuery) {
		this.wfMessagesQuery = wfMessagesQuery;
	}

	public void setChangeDomMessageQuery(String changeDomMessageQuery) {
		this.changeDomMessageQuery = changeDomMessageQuery;
	}

	public void setActivationErrorsListQuery(String activationErrorsListQuery) {
		this.activationErrorsListQuery = activationErrorsListQuery;
	}

	private void cancelTRM(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		System.out.println("*************************************************************************************");
		System.out.println("Please make sure that all CIFs  are in internal format (no '-') and 12 digits long");
		System.out.println("*************************************************************************************");
		System.out.println("Please provide CIF(s), comma separated :");
		String cifs = scanner.nextLine().trim();
		String[] cifTemp = cifs.split(",");
		cifList = Arrays.asList(cifTemp);

		List<String> acceptedList = checkCIF(new ArrayList<String>(cifList));

		if (acceptedList.size() > 0) {
			for (String cif : acceptedList) {
				cif = cif.trim();

				try {
					inputProcessorTaxReclaim.cancelWF(cif);
					inputProcessorTaxReclaim.connectToSource(wfMessagesQuery, cif);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
		System.out.println("File saved at " + inputProcessorTaxReclaim.getFileName());
		scanner.nextLine();
	}

	private List<String> checkCIF(ArrayList<String> cifList) {
		List<String> acceptedList = new ArrayList<String>();
		for (String cif : cifList) {
			cif = cif.trim();
			if ((cif.length() != CIF_SIZE) || (cif.contains("-"))) {
				System.out.println("CIF " + cif + " not valid, 12 digit internal format required!");
			} else {
				acceptedList.add(cif);
			}
		}

		System.out.println("*************************************************************************************");
		return acceptedList;
	}

	private void changeDomicile(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {
		System.out.println("*************************************************************************************");
		System.out.println("Please keep CIF in original format provided.");
		System.out.println("*************************************************************************************");
		System.out.println("Please provide CIF(s), comma separated :");
		String cifs = scanner.nextLine().trim();
		String[] cifTemp = cifs.split(",");
		cifList = Arrays.asList(cifTemp);

		System.out.println("Please provide Product Id(s), comma separated :");
		String productIds = scanner.nextLine().trim();
		String[] productIdsTemp = cifs.split(",");
		productList = Arrays.asList(productIdsTemp);

		System.out.println("Please provide End Date (YYYY-MM-DD) :");
		String endDate = scanner.nextLine().trim();

		boolean check = checkDomicileInputs(cifs, productIds, endDate);

		if (check) {
			for (String cif : cifList) {
				for (String productId : productList) {
					try {
						inputProcessorTaxReclaim.changeDomicile(cif, productId, endDate);
						inputProcessorTaxReclaim.connectToSource(changeDomMessageQuery, cif, productId, endDate);
						Thread.sleep(2000);
					} catch (Exception ex) {
						System.out.println("Failed to change domicile");
						ex.printStackTrace();
					}

				}
			}
		}
		System.out.println("File saved at " + inputProcessorTaxReclaim.getFileName());
		scanner.nextLine();
	}

	private void createActivationErrorReport(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		System.out.println("Creating report 'Error Activation List'");
		System.out.println("*************************************************************************************");
		inputProcessorTaxReclaim.connectToSource(activationErrorsListQuery);
		scanner.nextLine();
	}

	private boolean checkDomicileInputs(String cif, String productCode, String endDate) {

		if ((cif == null) || (cif.length() < 7)) {
			System.out.println("Invalid CIF");
			return false;
		} else if ((productCode == null) || (productCode.length() < 2)) {
			System.out.println("Invalid Product Code");
			return false;
		} else if ((endDate == null) || (endDate.length() != 10)) {
			System.out.println("Invalid End Date");
			return false;
		} else
			return true;

	}
}
