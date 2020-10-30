package org.xpp.csinvest.support;

import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.report.xml.support.ApachePOIExcelWrite;
import org.springframework.jdbc.core.JdbcTemplate;

public class JobProcessor {

	private final String PrecompileTMWBObjects = "PrecompileTMWBObjects";
	private final String CheckExecutionMessageIntraday = "CheckExecutionMessageIntraday";
	private final String ProcessEventsInvestmentFee = "ProcessEventsInvestmentFee";
	private final String CheckHeartBeat = "CheckHeartBeat";
	private final String CheckAssetValueOrderStatus = "CheckAssetValueOrderStatus";
	private final String ProcessDebitEncashmentOrderResponse = "ProcessDebitEncashmentOrderResponse";
	private final String calculatePriceComponentsAssetValueCalc = "calculatePriceComponentsAssetValueCalc";
	private final String ProcessRatedSecuritizedPosition = "ProcessRatedSecuritizedPosition";
	private final String ProcessEncashmentOrder = "ProcessEncashmentOrder";
	private final String AbandonStuckJobs = "AbandonStuckJobs";
	private final String calculatePriceComponentsInvestmentFee = "calculatePriceComponentsInvestmentFee";
	private final String ProcessEncashmentPosition = "ProcessEncashmentPosition";
	private final String GetEvents="GetEvents";
	private JdbcTemplate jdbcTemplateCSInvest;
	private final Object jobName = "JOBNAME";
	private String BUSINESSKEY = "BUSINESSKEY";
	private String REQUESTXMLINTERNAL = "REQUESTXMLINTERNAL";
	private String ID = "ID";
	private String sqlUpdate;
	private String sqlTemp;
	private String sqlEvent;
	private String sqlEventCount;
	private String sqlTotalAmount;
	private String updateDebitEOR;
	private String updateDebitEOROrder;
	private String updateExcel;
	private String sqlOrder;
	public static boolean isJunit = false;

	private String comment1 = "'Total amount is 0, no further action'";
	private String comment2 = "'Business Informed'";
	private Scanner scanner = new Scanner(System.in);

	public void processListOfJobs(JdbcTemplate jdbcTemplateCSInvest, Map<String, Object> row) {

		String jobNameTemp = row.get(jobName).toString();
		this.jdbcTemplateCSInvest = jdbcTemplateCSInvest;

		switch (jobNameTemp) {

		case PrecompileTMWBObjects:
			processPrecompileTMWBObjects(jobNameTemp, row);
			break;
		case CheckExecutionMessageIntraday:
			processCheckExecutionMessageIntraday(jobNameTemp, row);
			break;
		case ProcessEventsInvestmentFee:
			processProcessEventsInvestmentFee(jobNameTemp, row);
			break;
		case CheckHeartBeat:
			processCheckHeartBeat(jobNameTemp, row);
			break;
		case CheckAssetValueOrderStatus:
			processCheckAssetValueOrderStatus();
			break;
		case ProcessDebitEncashmentOrderResponse:
			processProcessDebitEncashmentOrderResponse(jobNameTemp, row);
			break;
		case calculatePriceComponentsAssetValueCalc:
			processCalculatePriceComponentsAssetValueCalc(jobNameTemp, row);
			break;
		case ProcessRatedSecuritizedPosition:
			processProcessRatedSecuritizedPosition(jobNameTemp, row);
			break;
		case ProcessEncashmentOrder:
			processProcessEncashmentOrder(jobNameTemp, row);
			break;
		case AbandonStuckJobs:
			processAbandonStuckJobs(jobNameTemp, row);
			break;
		case calculatePriceComponentsInvestmentFee:
			processCalculatePriceComponentsInvestmentFee(jobNameTemp, row);
			break;
		case ProcessEncashmentPosition:
			processEncashmentPosition(jobNameTemp, row);
			break;
		case GetEvents:
			processGetEvents(jobNameTemp, row);
			break;
		default:

			defaultJobs(jobNameTemp, row);
			break;
		}

	}

	private synchronized void processPrecompileTMWBObjects(String jobNameTemp, Map<String, Object> row) {

		sqlTemp = sqlUpdate.replace("jobName", "'" + jobNameTemp + "'");
		if (!isJunit)
			jdbcTemplateCSInvest.update(sqlTemp + " AND ID =" + "'" + row.get(ID) + "'");

		System.out.println("If this error PrecompileTMWBObjects happens every day contact 3L, Marco Ryll");

	}

	private synchronized void processCheckExecutionMessageIntraday(String jobNameTemp, Map<String, Object> row) {

		sqlTemp = sqlUpdate.replace("jobName", "'" + jobNameTemp + "'");
		if (!isJunit)
			jdbcTemplateCSInvest.update(sqlTemp + " AND ID =" + "'" + row.get(ID) + "'");

	}
	private synchronized void processGetEvents(String jobNameTemp, Map<String, Object> row) {

		sqlTemp = sqlUpdate.replace("jobName", "'" + jobNameTemp + "'");
		if (!isJunit)
			jdbcTemplateCSInvest.update(sqlTemp + " AND ID =" + "'" + row.get(ID) + "'");

	}
	private synchronized void processProcessEventsInvestmentFee(String jobNameTemp, Map<String, Object> row) {
		Integer count = jdbcTemplateCSInvest.queryForObject(sqlEventCount, Integer.class);
		sqlTemp = sqlUpdate.replace("jobName", "'" + jobNameTemp + "'");
		if (count == 0) {
			if (!isJunit)
				jdbcTemplateCSInvest.update(sqlTemp + " AND ID =" + "'" + row.get(ID) + "'");

		} else if (count > 0) {

			boolean approved = confirmation();

			if (approved) {
				jdbcTemplateCSInvest.update(sqlEvent);

				System.out.println("Please inform Marco Ryll about failed events");

			}
		}

	}

	private synchronized void processCheckHeartBeat(String jobNameTemp, Map<String, Object> row) {
		sqlTemp = sqlUpdate.replace("jobName", "'" + jobNameTemp + "'");
		if (!isJunit)
			jdbcTemplateCSInvest.update(sqlTemp + " AND ID =" + "'" + row.get(ID) + "'");

	}

	private synchronized void processCheckAssetValueOrderStatus() {
		sqlTemp = "Check with Marco";
		System.out.println("***Inform Marco Ryll***");

	}

	private synchronized void processProcessDebitEncashmentOrderResponse(String jobNameTemp, Map<String, Object> row) {

		String businessKey = row.get(BUSINESSKEY) != null ? row.get(BUSINESSKEY).toString() : null;

		if ((businessKey == null) && (row.get(REQUESTXMLINTERNAL) != null)) {

			String xml = row.get(REQUESTXMLINTERNAL).toString();
			String requestID = StringUtils.substringBetween(xml, "<a n=\"requestId\" v=\"", "\"/>");
			businessKey = requestID.trim();
		}
		if (businessKey != null) {

			String sqlTotalAmountTemp = sqlTotalAmount.replace("ENCASHMENTORDERID", "'" + businessKey + "'");
			double totalAmount = jdbcTemplateCSInvest.queryForObject(sqlTotalAmountTemp, Double.class);
			String sqlEncashOrder = sqlTotalAmountTemp.replace("TOTALFINALAMOUNTLBCCY", "*");

			if (totalAmount == 0) {
				sqlTemp = updateDebitEOR.replace("EXECUTIONMESSAGEID", "'" + row.get(BUSINESSKEY) + "'");
				sqlTemp = sqlTemp.replace("REF", comment1);
				if (!isJunit)
					jdbcTemplateCSInvest.update(sqlTemp);
				sqlTemp = updateDebitEOROrder.replace("ENCASHMENTORDERID", "'" + row.get(BUSINESSKEY) + "'");
				sqlTemp = sqlTemp.replace("REF", comment1);
				if (!isJunit)
					jdbcTemplateCSInvest.update(sqlTemp + " AND ID =" + "'" + row.get(ID) + "'");

			} else if (totalAmount > 0) {

				sqlTemp = updateDebitEOR.replace("EXECUTIONMESSAGEID", "'" + row.get(ID) + "'");
				sqlTemp = sqlTemp.replace("REF", comment2);
				if (!isJunit)
					jdbcTemplateCSInvest.update(sqlTemp);

				sqlTemp = updateDebitEOROrder.replace("ENCASHMENTORDERID", "'" + row.get(BUSINESSKEY) + "'");
				sqlTemp = sqlTemp.replace("REF", comment2);
				if (!isJunit)
					jdbcTemplateCSInvest.update(sqlTemp);

				Map<String, Object> encashmentRow = jdbcTemplateCSInvest.queryForMap(sqlEncashOrder);
				ApachePOIExcelWrite.updateExcel(updateExcel, encashmentRow, row);

				System.out.println(
						"Please email GG XPP FACHSTELLE <fachstelle.xpp@credit-suisse.com> about the update from the excel.");

			} else {
				System.out.println("Total Amount is not valid number");

			}
		} else {
			System.out.println(row.get(ID) + "  " + jobNameTemp
					+ " is missing BUSINESSKEY value both as a column and in XML (REQUESTXMLINTERNAL column, requestID tag). Please inform 3L.");
		}

	}

	private synchronized void processCalculatePriceComponentsAssetValueCalc(String jobNameTemp,
			Map<String, Object> row) {
		sqlTemp = sqlUpdate.replace("jobName", "'" + jobNameTemp + "'");
		if (!isJunit) {

			boolean approved = confirmation();

			if (approved) {
				jdbcTemplateCSInvest.update(sqlTemp + " AND ID =" + "'" + row.get(ID) + "'");

			} else {
				System.out.println("***Please confirm with 3L first***");
			}
		}

	}

	private synchronized void processProcessRatedSecuritizedPosition(String jobNameTemp, Map<String, Object> row) {
		sqlTemp = sqlUpdate.replace("jobName", "'" + jobNameTemp + "'");
		if (!isJunit) {

			boolean approved = confirmation();

			if (approved) {
				jdbcTemplateCSInvest.update(sqlTemp + " AND ID =" + "'" + row.get(ID) + "'");

			} else {
				System.out.println("***Please confirm with 3L first***");
			}
		}

	}

	private synchronized void processProcessEncashmentOrder(String jobNameTemp, Map<String, Object> row) {
		sqlTemp = sqlUpdate.replace("jobName", "'" + jobNameTemp + "'");

		if (!isJunit) {

			boolean approved = confirmation();

			if (approved) {
				jdbcTemplateCSInvest.update(sqlTemp + " AND ID =" + "'" + row.get(ID) + "'");

			} else {
				System.out.println("***Please confirm with 3L first***");
			}
		}

	}

	private synchronized void processAbandonStuckJobs(String jobNameTemp, Map<String, Object> row) {
		sqlTemp = sqlUpdate.replace("jobName", "'" + jobNameTemp + "'");
		boolean approved = confirmation();
		if (approved) {
			jdbcTemplateCSInvest.update(sqlTemp + " AND ID =" + "'" + row.get(ID) + "'");

		} else {
			System.out.println("***Please confirm with 3L first***");
		}

	}

	private synchronized void processCalculatePriceComponentsInvestmentFee(String jobNameTemp,
			Map<String, Object> row) {
		boolean approved = confirmation();
		sqlTemp = sqlUpdate.replace("jobName", "'" + jobNameTemp + "'");

		if (approved) {
			jdbcTemplateCSInvest.update(sqlTemp + " AND ID =" + "'" + row.get(ID) + "'");

		} else {
			System.out.println("***Please confirm with 3L first***");
		}

	}

	private synchronized void processEncashmentPosition(String jobNameTemp, Map<String, Object> row) {

		boolean approved = confirmation();
		sqlTemp = sqlUpdate.replace("jobName", "'" + jobNameTemp + "'");

		if (approved) {
			jdbcTemplateCSInvest.update(sqlTemp + " AND ID =" + "'" + row.get(ID) + "'");

		} else {
			System.out.println("***Please confirm with 3L first***");
		}
	}

	private synchronized void defaultJobs(String jobNameTemp, Map<String, Object> row) {

		boolean approved = confirmation();
		sqlTemp = sqlUpdate.replace("jobName", "'" + jobNameTemp + "'");

		if (approved) {
			jdbcTemplateCSInvest.update(sqlTemp + " AND ID =" + "'" + row.get(ID) + "'");

		} else {
			System.out.println("***Please confirm with 3L first***");
		}
	}

	public void setSqlUpdate(String sqlUpdate) {
		this.sqlUpdate = sqlUpdate;
	}

	public String getSqlUpdate() {
		return sqlUpdate;
	}

	public String getSqlTemp() {
		return sqlTemp;
	}

	public void setSqlEvent(String sqlEvent) {
		this.sqlEvent = sqlEvent;
	}

	public void setSqlEventCount(String sqlEventCount) {
		this.sqlEventCount = sqlEventCount;
	}

	public void setSqlTotalAmount(String sqlTotalAmount) {
		this.sqlTotalAmount = sqlTotalAmount;
	}

	public String getSqlTotalAmount() {
		return sqlTotalAmount;
	}

	public void setUpdateDebitEOR(String updateDebitEOR) {
		this.updateDebitEOR = updateDebitEOR;
	}

	public void setUpdateDebitEOROrder(String updateDebitEOROrder) {
		this.updateDebitEOROrder = updateDebitEOROrder;
	}

	public void setUpdateExcel(String updateExcel) {
		this.updateExcel = updateExcel;
	}

	public void setSqlOrder(String sqlOrder) {
		this.sqlOrder = sqlOrder;
	}

	public String getSqlOrder() {
		return sqlOrder;
	}

	private boolean confirmation() {

		String approved;
		System.out.println("Did you confirm update with 3L XPP (Marco Ryll) (y or n)?");
		approved = scanner.nextLine().toLowerCase().trim();

		if (approved.equals("y"))
			return true;
		else
			return false;

	}

}
