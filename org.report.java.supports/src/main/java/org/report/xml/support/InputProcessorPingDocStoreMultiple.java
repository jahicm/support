package org.report.xml.support;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

@Component
public class InputProcessorPingDocStoreMultiple implements Runnable {

	private final String F2BCASE_BUSINESS_ID = "F2BCASE_BUSINESS_ID";
	private final String F2BCASE_EXTERNAL_ID = "F2BCASE_EXTERNAL_ID";
	private final String DOCUMENT_NAME = "DOCUMENT_NAME";
	private final String DOC_URL = "DOC_URL";
	private static SSLSocketFactory sslSocketFactory;
	private static HttpsURLConnection urlConnection;
	private List<Map<String, Object>> list;
	private final String reportName;

	public InputProcessorPingDocStoreMultiple(List<Map<String, Object>> list, SSLSocketFactory sslSocketFactoryTemp,
			String reportName) throws SAXException, IOException, ParserConfigurationException {

		this.list = list;
		sslSocketFactory = sslSocketFactoryTemp;
		this.reportName = reportName;
	}

	public void run() {

		String businessId = null;
		String externalId = null;
		String documentName = null;
		String documentUrl = null;
		FileSaver fileSaver = null;
		synchronized (this) {

			for (Iterator<Map<String, Object>> iterator = list.listIterator(); iterator.hasNext();) {

				Map<String, Object> row = iterator.next();
				businessId = row.get(F2BCASE_BUSINESS_ID) == null ? "NA" : row.get(F2BCASE_BUSINESS_ID).toString();
				externalId = row.get(F2BCASE_EXTERNAL_ID) == null ? "NA" : row.get(F2BCASE_EXTERNAL_ID).toString();
				documentName = row.get(DOCUMENT_NAME) == null ? "NA" : row.get(DOCUMENT_NAME).toString();
				documentUrl = row.get(DOC_URL) == null ? "NA" : row.get(DOC_URL).toString();

				try {

					fileSaver = FileSaver.getInstance(reportName);

					URL link = new URL(documentUrl);
					urlConnection = (HttpsURLConnection) link.openConnection();
					urlConnection.setSSLSocketFactory(sslSocketFactory);

					if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

						fileSaver.savetToFile(
								businessId + "," + externalId + "," + documentName + "," + documentUrl + "," + "OK");
					} else {
						fileSaver.savetToFile(
								businessId + "," + externalId + "," + documentName + "," + documentUrl + "," + "NOK");
					}

				} catch (Exception e) {
					try {

						fileSaver.savetToFile(
								businessId + "," + externalId + "," + documentName + "," + documentUrl + "," + "NOK");
					} catch (Exception e1) {

						System.out.println("Failed reading file");
					}

					System.out.println("Failed to ping Doc Store for :" + businessId + "  " + documentUrl);
				}

			}

		}

	}

}
