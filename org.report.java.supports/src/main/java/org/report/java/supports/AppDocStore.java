package org.report.java.supports;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.report.java.interfaces.AbstractReport;
import org.report.java.interfaces.AppReportInterface;
import org.report.xml.support.InputProcessorPingDocStoreMultiple;
import org.xml.sax.SAXException;
import org.zeroturnaround.zip.ZipUtil;

/**
 * version 1.0 Mirza Jahic  2018 Report Generator
 *
 */
public class AppDocStore extends AbstractReport implements AppReportInterface {

	private String option;
	private String optionDownload;

	private HttpsURLConnection urlConnection;

	private String sqlDownload;
	private String sqlDownload2;
	private String fileName;
	private final String F2BCASE_BUSINESS_ID = "F2BCASE_BUSINESS_ID";
	private final String DOCUMENT_EXTENSION = "DOCUMENT_EXTENSION";
	private final String DOCUMENT_NAME = "DOCUMENT_NAME";
	private final String DOC_URL = "DOC_URL";
	private final String F2BCASE_EXTERNAL_ID = "F2BCASE_EXTERNAL_ID";
	private final String F2BCASE_DOCUMENT = "F2BCASE_DOCUMENT";
	private final String STATUS = "STATUS";
	private final String XML = "xml";
	private final String PDF = "pdf";
	private final String JPG = "jpg";
	private final String JPEG = "jpeg";
	private final String DOCX = "docx";
	private final String DOC = "doc";
	private final String BMP = "bmp";
	private final String PNG = "png";
	private final String MSG = "msg";
	private String reportName;
	private String uploadUrl;
	private String sqlDownloadTemp;
	private Set<String> downloadedFolders = new HashSet<String>();


	public void init() {
		List<String> header = new ArrayList<String>();
		header.add(F2BCASE_BUSINESS_ID);
		header.add(F2BCASE_EXTERNAL_ID);
		header.add(DOCUMENT_NAME);
		header.add(DOC_URL);
		header.add(STATUS);

	}

	public void setScanner(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		System.out.println("***Please make sure you first run setenv.cmd before continuing***");
		System.out.println("***Please select option 1  Download from DocStore***");
		//System.out.println("***Please select option 2 ZIP Upload to DocStore***");
		System.out.println("***Please select option 3 Generate DocStore Ping report***");
		option = scanner.nextLine().trim();

		if ((option != null) && (option.equals("1"))) {
			setScannerDownload(scanner);
		} else if ((option != null) && (option.equals("2"))) {
			setScannerUpload(scanner, uploadUrl);
		} else if ((option != null) && (option.equals("3"))) {
			setScannerPingDocStore(scanner);

		} else {
			System.out.println("Wrong choice,choose option 1 , 2 or 3");
		}
	}

	private void setScannerDownload(Scanner scanner) {
		System.out.println("1 Download in ZIP or 2 Exploaded version:");
		optionDownload = scanner.nextLine().trim();

		System.out.print("***Please Input file name (at " + path
				+ ") of F2BCASE_IDs, 'NO commas and apostrophes', separated and hit ENTER ***:");
		fileName = scanner.nextLine().trim();
		System.out.print(
				"If you want to download all related files type * or just hit enter, else type in file types ('xml','pdf','jpg'):");
		String document_extensionTemp = scanner.nextLine().trim().toLowerCase();

		if (!document_extensionTemp.equals("*") && !document_extensionTemp.equals("")) {
			sqlDownloadTemp = sqlDownload.replace("document_extension", document_extensionTemp);
		} else {
			sqlDownloadTemp = sqlDownload;

		}
		if ((optionDownload != null) && (optionDownload.equals("1"))) {
			System.out
					.println("****************************************IN PROGRESS***********************************");
			setScannerDownloadZip(sqlDownloadTemp);
		} else if ((optionDownload != null) && (optionDownload.equals("2"))) {
			System.out
					.println("****************************************IN PROGRESS***********************************");
			setScannerDownloadExploaded(sqlDownloadTemp);
		} else {
			System.out.println("Wrong choice,choose option 1 or 2");
		}
	}

	private void setScannerDownloadZip(String sqlDownloadTemp) {

		setScannerDownloadExploaded(sqlDownloadTemp);

		for (String path : downloadedFolders) {
			ZipUtil.pack(new File(path), new File(path + ".zip"));
		}
	}

	private void setScannerDownloadExploaded(String sqlDownloadTemp) {

		List<String> listOfCaseIDs = generateQueryInputs(fileName);
		initiateTLS(spid);
		for (Iterator<String> iterator = listOfCaseIDs.listIterator(); iterator.hasNext();) {

			String sqlSearchDownload = sqlDownloadTemp + iterator.next() + sqlDownload2;
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlSearchDownload);

			processList(list);
			iterator.remove();

		}

	}

	private void processList(List<Map<String, Object>> list) {
		for (Iterator<Map<String, Object>> iterator = list.listIterator(); iterator.hasNext();) {
			Map<String, Object> row = iterator.next();

			String f2bCaseId = row.get(F2BCASE_BUSINESS_ID).toString();
			String f2bExternalId = row.get(F2BCASE_EXTERNAL_ID).toString();
			String docExtension = row.get(DOCUMENT_EXTENSION) != null ? row.get(DOCUMENT_EXTENSION).toString().trim()
					: null;
			String docName = row.get(DOCUMENT_NAME) != null ? row.get(DOCUMENT_NAME).toString().trim() : null;
			String url = row.get(DOC_URL) != null ? row.get(DOC_URL).toString().trim() : null;
			invokeDocStore(f2bCaseId, f2bExternalId, docExtension.toLowerCase(), docName, url);

		}
		System.out.println("Please check at : " + path + "F2BCASE_DOCUMENT");
	}

	private void invokeDocStore(String f2bCaseId, String f2bExternalId, String docExtension, String docName,
			String url) {

		try {

		

			URL link = new URL(url);
		
			urlConnection = (HttpsURLConnection) link.openConnection();

			sslSocketFactory = context.getSocketFactory();
			urlConnection.setSSLSocketFactory(sslSocketFactory);
			String docExtensionLowerCase = docExtension.toLowerCase();

			switch (docExtensionLowerCase) {

			case XML:
				readXML(urlConnection, f2bExternalId, docName, docExtension);
				break;
			case PDF:
				readBinary(urlConnection, f2bExternalId, docName, docExtension);
				break;
			case JPG:
			case DOCX:
			case DOC:
			case JPEG:
			case BMP:
			case PNG:
			case MSG:
				readBinary(urlConnection, f2bExternalId, docName, docExtension);
				break;
			default:
				System.out.println("File type unknow :" + docExtensionLowerCase);
				break;
			}

		}

		catch (Exception malformedInputException) {
			malformedInputException.printStackTrace();
			System.out.println("Invalid URL :" + url);
		}

	}

	private void saveToXMLFile(Path newDirectoryPath, String xml, String f2bExternalId, String docName,
			String docExtension) throws IOException {
		Path path2 = Paths.get(newDirectoryPath + "/" + f2bExternalId);
		Path path = Paths.get(path2 + "/" + docName + docExtension);

		Files.createDirectories(path2);

		try (BufferedWriter writer = Files.newBufferedWriter(path))

		{

			writer.write(xml);
			writer.close();
		} catch (IOException ioException) {
			System.out.println("Failed to create XML file. ");
		}
		downloadedFolders.add(path2.toString());
	}

	private void saveToBinary(Path newDirectoryPath, String f2bExternalId, String docName, byte[] binary,
			String docExtension) throws IOException {

		Path path2 = Paths.get(newDirectoryPath + "/" + f2bExternalId);
		Path path = Paths.get(path2 + "/" + docName + docExtension);

		Files.createDirectories(path2);

		try {

			FileOutputStream fos = new FileOutputStream(path.toString());
			fos.write(binary);
			fos.close();

		} catch (Exception e) {
			System.out.println("Failed to create binary document. ");
		}
		downloadedFolders.add(path2.toString());
	}

	private void readXML(HttpsURLConnection urlConnection, String f2bExternalId, String docName, String docExtension)
			throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		String inputLine;
		StringBuffer strBuffer = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			strBuffer.append(inputLine);
			strBuffer.append("\n");
		}

		Path newDirectoryPath = Paths.get(path + F2BCASE_DOCUMENT);
		saveToXMLFile(newDirectoryPath, strBuffer.toString(), f2bExternalId, docName, "." + docExtension);
		in.close();
		urlConnection.disconnect();

	}

	private void readBinary(HttpsURLConnection urlConnection, String f2bExternalId, String docName, String docExtension)
			throws IOException {

		InputStream fileInputStream = urlConnection.getInputStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = IOUtils.toByteArray(fileInputStream);
		out.write(buf);

		byte[] binary = out.toByteArray();

		out.close();
		fileInputStream.close();
		Path newDirectoryPath = Paths.get(path + F2BCASE_DOCUMENT);
		saveToBinary(newDirectoryPath, f2bExternalId, docName, binary, "." + docExtension);
	}

	private void setScannerUpload(Scanner scanner, String url) {

		System.out.print("Please give folder name at " + path + " with zipped files:");
		String folderName = scanner.nextLine().trim();

		File dir = new File(path + "/" + folderName);
		initiateTLS(spid);
		for (File file : dir.listFiles()) {

			if ((file != null) && !(file.isDirectory()) && (file.toString().endsWith(".zip"))) {

				FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);

				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				builder.addPart("file", fileBody);
				HttpEntity entity = builder.build();

				HttpPost request = new HttpPost(url);
				request.setEntity(entity);

				HttpClient httpClient = HttpClients.custom()
			    .setConnectionTimeToLive(20, TimeUnit.SECONDS)
			    .setMaxConnTotal(400).setMaxConnPerRoute(400)
			    .setDefaultRequestConfig(RequestConfig.custom()
			    .setSocketTimeout(30000).setConnectTimeout(5000).build())
			    .setSSLSocketFactory(sslsf)
			    .setRetryHandler(new DefaultHttpRequestRetryHandler(5, true))
			    .build();

				try {
					httpClient.execute(request);
					System.out.println("Uploaded " + file.getName());
				} catch (Exception e) {
					//System.out.println("Failed to upload " + file.getName());
					e.printStackTrace();
				}
			}
		}
	}

	public void setScannerPingDocStore(Scanner scanner) {

		System.out.print("***Please Input file name (at " + path
				+ ") of F2BCASE_IDs, 'NO commas and apostrophes', separated and hit ENTER ***:");
		fileName = scanner.nextLine().trim();
		ExecutorService executor = Executors.newCachedThreadPool();
		List<String> listOfCaseIDs = generateQueryInputs(fileName);
		initiateTLS(spid);

		sslSocketFactory = context.getSocketFactory();

		for (Iterator<String> iterator = listOfCaseIDs.listIterator(); iterator.hasNext();) {

			String sqlDocStoreValues = sqlDownload + iterator.next() + sqlDownload2;
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlDocStoreValues);

			try {
				Runnable worker = new InputProcessorPingDocStoreMultiple(list, sslSocketFactory, reportName);
				executor.execute(worker);
			} catch (SAXException | IOException | ParserConfigurationException e) {
				System.out.println("Error occured in AppDocStore");
			}

		}
		executor.shutdown();
		System.out.println("Report will be available at " + reportName);
	}

	public void setSql(String sql) {
		this.sqlDownload = sql;
	}

	public void setSqlDownload(String sqlDownload) {
		this.sqlDownload = sqlDownload;
	}

	public void setSqlDownload2(String sqlDownload2) {
		this.sqlDownload2 = sqlDownload2;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

}
