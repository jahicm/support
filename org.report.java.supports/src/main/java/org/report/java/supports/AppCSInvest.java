package org.report.java.supports;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.report.java.interfaces.AppReportInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xml.sax.SAXException;
import org.xpp.csinvest.support.JobProcessor;

public class AppCSInvest implements AppReportInterface {

	@Autowired
	@Qualifier("jdbcTemplateCSInvest")
	private JdbcTemplate jdbcTemplateCSInvest;
	@Autowired
	private JobProcessor jobProcessor;
	private String sql;
	private List<Object> jobId = new ArrayList<Object>();
	private String ID = "ID";
	private String APPROVERPID = "APPROVERPID";
	private String APPROVALTIME = "APPROVALTIME";
	private String JOBNAME = "JOBNAME";
	private String COMMENTARY = "COMMENTARY";
	private String sql2;

	@Override
	public void setScanner(Scanner scanner)
			throws SQLException, SAXException, IOException, ParserConfigurationException {

		System.out.println("**********************************XPP CS Invest check**********************************");
		List<Map<String, Object>> listOfJobs = returnSelectedCases(sql);

		if (listOfJobs.size() == 0) {
			System.out.println(
					"******************** There are no messages in EXECUTIONMESSAGE table ******************");
			System.exit(0);
		} else {
			for (Map<String, Object> row : listOfJobs) {
				jobId.add("'" + row.get(ID) + "'");
				System.out.println(row.get(ID) + "  " + row.get(JOBNAME));
				jobProcessor.processListOfJobs(jdbcTemplateCSInvest, row);
			}
			listUpdates(jobId);
		}

	}

	public String getSql() {
		return sql;
	}

	public void setSql2(String sql2) {
		this.sql2 = sql2;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	private List<Map<String, Object>> returnSelectedCases(String sql) {
		List<Map<String, Object>> listOfJobs = jdbcTemplateCSInvest.queryForList(sql);
		return listOfJobs;
	}

	private void listUpdates(List<Object> jobId) {

		for (Object id : jobId) {
			String sql = sql2.replace("JOBID", id.toString());
			List<Map<String, Object>> listOfUpdates = jdbcTemplateCSInvest.queryForList(sql);
			Map<String, Object> row = listOfUpdates.get(0);

			if (row.get(APPROVERPID) == null) {
				System.out.println(row.get(ID) + "  " + row.get(JOBNAME) + " PENDING FOR APPROVAL");
			} else {
				System.out.println("*********************************UPDATED*********************************");
				System.out.println(row.get(ID) + "  " + row.get(JOBNAME) + "   " + row.get(APPROVERPID) + "  "
						+ row.get(APPROVALTIME) + "  " + row.get(COMMENTARY));
			}
		}
		System.out.println("*************************************************************************");
	}

}
