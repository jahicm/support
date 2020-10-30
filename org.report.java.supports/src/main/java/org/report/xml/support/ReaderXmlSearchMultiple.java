package org.report.xml.support;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReaderXmlSearchMultiple {

	private static final String XML_ROW1 = "F2BCASE_BUSINESS_ID";
	private static BufferedWriter bw;

	public static void readXML(String xml, Map<String, Object> row, String searchValue, String fileName)
			throws IOException {

		String regex = searchValue;
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(xml);

		if ((matcher != null) && (matcher.find())) {
			File file = new File(fileName);

			if (!file.exists())
				bw = new BufferedWriter(new FileWriter(fileName));
			else
			{
				FileWriter fw = new FileWriter(fileName, true);
				bw = new BufferedWriter(fw);
			}
			synchronized (bw) {
				System.out.println("Found " + row.get(XML_ROW1).toString() + "   " + searchValue);
				bw.append(row.get(XML_ROW1).toString() + "," + searchValue);
				bw.newLine();
				bw.flush();
				
			}
		}

	}

}
