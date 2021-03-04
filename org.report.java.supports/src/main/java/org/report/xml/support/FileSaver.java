package org.report.xml.support;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileSaver {

	private static FileSaver fileSaver = null;
	private static File file = null;

	private String fileName;

	private FileSaver(){}
	private FileSaver(String fileName) {
		this.fileName = fileName;
		file = new File(fileName);
	}

	public static FileSaver getInstance(String fileName) {
		if (fileSaver == null) {
			fileSaver = new FileSaver(fileName);
			return fileSaver;
		} else {
			return fileSaver;
		}
	}

	public void savetToFile(Object value) {
		BufferedWriter bw;
		FileWriter fw;
		if (value == null)
			value = "";

		try {
			if (!file.exists()) {
				bw = new BufferedWriter(new FileWriter(fileName));
				bw.write(value.toString());
				bw.newLine();
				bw.flush();

			} else {
				fw = new FileWriter(fileName, true);
				bw = new BufferedWriter(fw);
				bw.write(value.toString());
				bw.newLine();
				bw.flush();

			}
			bw.close();
		} catch (IOException e) {
			System.out.println("Failed reading file");
			e.printStackTrace();
		}
	}
}
