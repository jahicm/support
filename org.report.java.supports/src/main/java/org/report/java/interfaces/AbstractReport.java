package org.report.java.interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.collections4.ListUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.zeroturnaround.zip.ZipUtil;

import com.csg.cs.security.onepki.PKIConfig;
import com.csg.cs.security.onepki.PKIConstants;
import com.csg.cs.security.onepki.provider.PKIProvider;
import com.csg.cs.security.onepki.ssl.PKIX509TrustManagerFactoryParameters;

import iaik.security.provider.IAIK;

public class AbstractReport {

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	protected int sql_parameter_limit;
	protected String path;
	protected String spid;
	private static String F2BCASE_BUSINESS_ID = "F2BCASE_BUSINESS_ID";
	protected static int NUMBER_OF_THREAD_ITEMS = 5;
	private String caseID;
	protected SSLContext context;
	protected SSLSocketFactory sslSocketFactory;
	protected SSLConnectionSocketFactory sslsf;
	protected List<String> listOfFolders;
	private String folderName;
	private static String CLEANED_XML = "/CLEANED_XML";

	protected String loadRetriggeringIDs(String fileName) {

		StringBuffer strBuffer = new StringBuffer();

		try (BufferedReader br = new BufferedReader(new FileReader(path + fileName))) {

			String line = "";

			while (line != null) {
				line = br.readLine();

				if (line != null)
					strBuffer.append("'" + line.trim() + "',");

			}
			int length = strBuffer.length() - 1;
			if (length > 0)
				caseID = strBuffer.deleteCharAt(length).toString();

			caseID = strBuffer.toString();

		} catch (IOException e) {
			System.out.println("*********Error while reading text file **********");
			e.printStackTrace();
		}

		return caseID;
	}

	protected List<Map<String, Object>> returnSelectedCases(String sql3) {
		List<Map<String, Object>> listOfCaseIDs = jdbcTemplate.queryForList(sql3);
		return listOfCaseIDs;
	}

	protected List<String> generateQueryInputs(List<Map<String, Object>> queryResult) {
		StringBuffer strBuffer = new StringBuffer();
		List<String> caseIdList = new ArrayList<String>();
		String caseID = null;
		for (int i = 0, j = queryResult.size(); i < j; i++) {
			if ((i % sql_parameter_limit) != 0) {
				strBuffer.append("'" + queryResult.get(i).get(F2BCASE_BUSINESS_ID) + "',");
				caseID = strBuffer.toString();

			} else if (i % sql_parameter_limit == 0) {
				strBuffer.append("'" + queryResult.get(i).get(F2BCASE_BUSINESS_ID) + "'");
				caseID = strBuffer.toString();
				caseIdList.add(caseID);
				strBuffer.delete(0, strBuffer.length());
				caseID = null;

			}
		}
		int length = strBuffer.length() - 1;
		if (length > 0)
			caseID = strBuffer.deleteCharAt(length).toString();

		caseIdList.add(caseID);

		return caseIdList;
	}

	protected List<String> loadFile(String fileName) {

		List<String> loadedItems = new ArrayList<String>();

		try {

			try (BufferedReader br = new BufferedReader(new FileReader(path + fileName))) {

				String line = br.readLine();
				loadedItems.add(line.trim());
				while (line != null) {

					line = br.readLine();
					if ((line != null) && !(line.equals("")))
						loadedItems.add(line.trim());
				}

			} catch (IOException e) {
				System.out.println("*********Error while reading text file **********");
				e.printStackTrace();

			}

		} catch (InputMismatchException ime) {
			System.err.println("Incorrect entry.");

		}

		return loadedItems;
	}

	protected List<String> generateQueryInputs(String fileName) {

		int lineNumber = 0;
		List<String> caseIdList = new ArrayList<String>();
		String caseID = null;
		StringBuffer strBuffer = new StringBuffer();

		try (BufferedReader br = new BufferedReader(new FileReader(path + fileName))) {

			String line = "";

			while (line != null) {

				line = br.readLine();
				++lineNumber;

				if ((lineNumber % sql_parameter_limit) != 0 && (line != null)) {
					strBuffer.append("'" + line.trim() + "',");
					caseID = strBuffer.toString();

				} else if (lineNumber % sql_parameter_limit == 0 && (line != null)) {
					strBuffer.append("'" + line.trim() + "'");
					caseID = strBuffer.toString();
					caseIdList.add(caseID);
					strBuffer.delete(0, strBuffer.length());
					caseID = null;

				}
			}

			int length = strBuffer.length() - 1;
			if (length > 0)
				caseID = strBuffer.deleteCharAt(length).toString();

			caseIdList.add(caseID);

		} catch (IOException e) {
			System.out.println("*********Error while reading text file **********");
			e.printStackTrace();
		}
		return caseIdList;
	}

	protected List<List<String>> generateSearchInputList(List<String> listOfSearchValues) {

		List<List<String>> completeInputList = new ArrayList<List<String>>();

		if (listOfSearchValues.size() > 5) {
			NUMBER_OF_THREAD_ITEMS = listOfSearchValues.size() / 5;
			if (listOfSearchValues.size() % 5 > 0)
				NUMBER_OF_THREAD_ITEMS += 1;
		} else {
			NUMBER_OF_THREAD_ITEMS = 1;
		}

		completeInputList.addAll(ListUtils.partition(listOfSearchValues, NUMBER_OF_THREAD_ITEMS));
		return completeInputList;
	}

	public void setSql_parameter_limit(int sql_parameter_limit) {
		this.sql_parameter_limit = sql_parameter_limit;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setSpid(String spid) {
		this.spid = spid;
	}

	protected TrustManagerFactory getTrustManagerFactory() throws Exception {

		TrustManagerFactory tkmf = null;
		PKIX509TrustManagerFactoryParameters tspec = null;
		try {
			tkmf = TrustManagerFactory.getInstance("CS_PKI_CSINTERNAL");
			tspec = new PKIX509TrustManagerFactoryParameters(PKIConfig.getCoreConfig(),
					PKIConstants.TCAL_GROUP_CSINTERNAL, PKIConstants.POLICY_SSL_CLIENT_AUTH,
					PKIConstants.POLICY_SSL_SERVER_AUTH);
			tkmf.init(tspec);

		} catch (NoSuchAlgorithmException | IOException e) {
			throw new Exception("IOException or no such algorithm exception while developing ftps connection", e);
		} catch (final InvalidAlgorithmParameterException e) {
			throw new Exception("InvalidAlgorithmParameterException while developing https connection", e);
		}
		return tkmf;
	}

	protected KeyManagerFactory getKeyManagerFactory(String spid) throws Exception {

		KeyStore ks = null;
		KeyManagerFactory kmf = null;
		try (InputStream keyStorePropertiesStream = new FileInputStream(spid)) {
			Security.addProvider(new PKIProvider());
			IAIK.addAsProvider();

			ks = KeyStore.getInstance("CS_PKI");
			ks.load(keyStorePropertiesStream, null);
			kmf = KeyManagerFactory.getInstance("CS_PKI");
			kmf.init(ks, null);
		} catch (final KeyStoreException e) {
			throw new Exception("Key store exception while developing ftps connection for the key store: ",

			e);
		} catch (final CertificateException e) {
			throw new Exception("Certificate exception while developing ftps connection for the key store: ", e);
		} catch (final UnrecoverableKeyException e) {
			throw new Exception("Unrecoverable exception while developing ftps connection for the key store: ", e);
		} catch (NoSuchAlgorithmException | IOException e) {
			throw new Exception(
					"IOException or no such algorithm exception while developing ftps connection for the key store: ",
					e);
		}
		return kmf;
	}

	protected void initiateTLS(String spid) {

		try {

			final KeyManagerFactory kmf = getKeyManagerFactory(spid);
			final TrustManagerFactory tkmf = getTrustManagerFactory();
			context = SSLContext.getInstance("TLS");

			com.csg.cs.security.onepki.ssl.PKIX509TrustManager[] tm = (com.csg.cs.security.onepki.ssl.PKIX509TrustManager[]) tkmf
					.getTrustManagers();

			context.init(kmf.getKeyManagers(), tm, null);

			sslSocketFactory = context.getSocketFactory();

			sslsf = new SSLConnectionSocketFactory(context);

		} catch (Exception SSLException) {
			SSLException.printStackTrace();
		}
	}

	protected void unzipFolders(String path, boolean flag, Scanner... scanner) throws IOException {

		File dirZip = null;
		File dirCleanedZip = null;
		System.out.print("Please give folder name at " + path + " with zipped files:");
		folderName = scanner[0].nextLine().trim();
		System.out.println("*********Unzipping starting************");
		if (flag) {
			dirZip = new File(path + "/" + folderName);
			dirCleanedZip = dirZip;
		} else {
			dirZip = new File(path + "/" + folderName);
			dirCleanedZip = new File(path + "/" + folderName + CLEANED_XML);
			Files.createDirectories(dirCleanedZip.toPath());
		}

		listOfFolders = new ArrayList<String>();

		for (File file : dirZip.listFiles()) {

			if ((file != null) && (file.toString().endsWith(".zip"))) {

				StringBuffer buff = new StringBuffer(file.getName());
				String fileName = dirCleanedZip.toString() + "\\"
						+ buff.delete(file.getName().length() - 4, file.getName().length()).toString();
				ZipUtil.unpack(new File(file.getAbsoluteFile().toString()), new File(fileName));
				listOfFolders.add(fileName);
			}
		}
		System.out.println("*********Unzipping completed***********");
	}

	protected void zipFolders(String path, boolean flag, Scanner... scanner) throws IOException {

		File dirZip = null;
		System.out.println("*********Zipping starting************");
		if (flag) {
			System.out.print("Please give folder name at " + path + " with folders to zip:");
			folderName = scanner[0].nextLine().trim();
			dirZip = new File(path + "/" + folderName);
		} else {
			dirZip = new File(path + "/" + folderName + CLEANED_XML);
		}

		for (File file : dirZip.listFiles()) {

			if ((file != null) && (file.exists()) && (!file.toString().endsWith(".zip"))) {
				ZipUtil.pack(new File(file.toString()),
						new File(dirZip.getAbsoluteFile() + "/" + file.getName() + ".zip"));

			}
		}

		System.out.println("*********Zipping completed**********");
	}
}
