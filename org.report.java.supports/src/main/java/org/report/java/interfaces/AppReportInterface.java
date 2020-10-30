package org.report.java.interfaces;

import java.io.Console;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public interface AppReportInterface {

	public void setScanner(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException;

	public static Console getConsole() {
		Console consoleInput = System.console();
		return consoleInput;
	}

}
