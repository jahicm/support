package org.report.xml.support;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.report.java.interfaces.AbstractInputProcessor;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

public class InputProcessorTaxReclaim extends AbstractInputProcessor {

	private static ApachePOIExcelWrite xslSheet = ApachePOIExcelWrite.getInstance();
	private String CANCEL_WORKFLOW = "CancelWorkflow";
	private String CHANGE_DOMICILE = "ChangeDom";
	private String bpidPath;
	private String fileName;
	private String fileNameCancelWF = "TAX_RECLAIM_CANCEL_WF_";
	private String fileNameChangeDom = "TAX_RECLAIM_CHANGE_DOMICILE_";
	private String fileActivationErrors = "TAX_RECLAIM_ACTIVATION_ERRORS_";
	private String CIF = "cif";
	private String CIF_NUMBER = "cifNumber";
	private String PRODUCT_CODE = "ProductCode";
	private String END_DATE = "EndDate";

	private static String TIME = "TIME";
	private static String PROCESSMANAGERID = "PROCESSMANAGERID";
	private static String ID = "ID";
	private static String EXTERNALID = "EXTERNALID";
	private static String EXTERNALUSERPID = "EXTERNALUSERPID";
	private static String CUSTOMERAGGREGATEID = "CUSTOMERAGGREGATEID";
	private static String TYPEID = "TYPEID";
	private static String CREATEDON = "CREATEDON";
	private static String UPDATEDON = "UPDATEDON";
	private static String UPDATEDBY = "UPDATEDBY";
	private static String PAYLOADID = "PAYLOADID";
	private static String PARTIALACTIVATIONORDERPAYLOADID = "PARTIALACTIVATIONORDERPAYLOADID";
	private static String STATUSID = "STATUSID";
	private static String CODE = "CODE";
	private static String DESCRIPTION = "DESCRIPTION";

	private static String CLAIMSINVESTMENTMARKETCOUNTRYID = "CLAIMSINVESTMENTMARKETCOUNTRYID";
	private static String PRODUCTID = "PRODUCTID";
	private static String VALIDFROM = "VALIDFROM";
	private static String VALIDTO = "VALIDTO";
	private static String ORDERSTATUSID = "ORDERSTATUSID";
	private static String CANCELLATIONREASON = "CANCELLATIONREASON";
	private static String TRCAGREEMENTID = "TRCAGREEMENTID";
	private static String COUNTRYCOMBINATIONID = "COUNTRYCOMBINATIONID";
	private static String STARTDATE = "STARTDATE";
	private static String ENDDATE = "ENDDATE";
	private static String TAXORDERNO = "TAXORDERNO";
	private static String TAXORDERNUMBER = "TAXORDERNUMBER";
	private static String PARENTRECLAIMORDERID = "PARENTRECLAIMORDERID";
	private static String SUBCIFNUMBER = "SUBCIFNUMBER";
	private static String CUSTOMERACCOUNTID = "CUSTOMERACCOUNTID";
	private static String RECLAIMCOUNTRYID = "RECLAIMCOUNTRYID";

	private static String CIFNumber = "CIFNumber";
	private static String Domicile = "Domicile";
	private static String ExternalId = "ExternalId";
	private static String ExternalUserPid = "ExternalUserPid";
	private static String TypeId = "TypeId";
	private static String CreatedOn = "CreatedOn";
	private static String UpdatedOn = "UpdatedOn";
	private static String UpdatedBy = "UpdatedBy";
	private static String PayloadId = "PayloadId";
	private static String PartialActivationOrderPayloadId = "PartialActivationOrderPayloadId";
	private static String StatusId = "StatusId";

	public void init() {
		fileNameCancelWF += date.format(formatterTaxReclaim);
		fileNameChangeDom += date.format(formatterTaxReclaim);
	}

	public List<Map<String, Object>> connectToSource(String... sql) throws IOException {
		List<Map<String, Object>> list = null;
		try {
			String taxRecalimQuery = sql[0];

			list = jdbcTemplateTaxReclaim.queryForList(taxRecalimQuery);

			switch (sql.length) {

			case 1:
				exportToExcel(list);
				break;
			case 2:
				saveToFileCancel_WF(fileNameCancelWF, list, sql[1]);
				break;
			default:
				saveToFileChangeDomicile(fileNameChangeDom, list, sql[1]);
				break;

			}

		} catch (CannotGetJdbcConnectionException ex) {

			System.out.println("*************************************************************************************");
			System.out.println("Please run tax.cmd for option 12.Tax Reclaim. You must use BPID to log into DB.");
			System.out.println("*************************************************************************************");
		}

		return list;
	}

	public void cancelWF(String cif) {

		SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplateTaxReclaim).withProcedureName(CANCEL_WORKFLOW);
		SqlParameterSource in = new MapSqlParameterSource().addValue(CIF, cif);
		simpleJdbcCall.execute(in);
		System.out.println("WF for :" + cif + "  canceled.");

	}

	public void changeDomicile(String cif, String productCode, String endDate) throws ParseException {

		SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplateTaxReclaim).withProcedureName(CHANGE_DOMICILE);

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(CIF_NUMBER, cif);
		parameters.addValue(PRODUCT_CODE, productCode);
		Date endDateFormat = new SimpleDateFormat("YYYY-MM-DD").parse(endDate);
		parameters.addValue(END_DATE, endDateFormat);
		simpleJdbcCall.execute(parameters);

		System.out.println("Change Domicile for:" + cif + "  canceled.");

	}

	private void saveToFileCancel_WF(String fileNameCancelWF, List<Map<String, Object>> list, String cif)
			throws IOException {
		FileSaver fileSaver = FileSaver.getInstance(bpidPath + fileNameCancelWF);

		for (Iterator<Map<String, Object>> iterator = list.listIterator(); iterator.hasNext();) {
			Map<String, Object> row = iterator.next();
			fileSaver.savetToFile(row.get(TIME) + "   " + row.get(ID) + "   " + row.get(PROCESSMANAGERID) + "   "
					+ row.get(EXTERNALID) + "   " + row.get(EXTERNALUSERPID) + "   " + row.get(CUSTOMERAGGREGATEID)
					+ "   " + row.get(TYPEID) + "   " + row.get(CREATEDON) + "   " + row.get(UPDATEDON) + "   "
					+ row.get(UPDATEDBY) + "   " + row.get(PAYLOADID) + "   " + row.get(PARTIALACTIVATIONORDERPAYLOADID)
					+ "   " + row.get(STATUSID) + "   " + row.get(CODE) + "   " + row.get(DESCRIPTION));

		}
		fileSaver.savetToFile(CIF + ":" + cif + " "
				+ "---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		fileName = fileNameCancelWF;
	}

	private void saveToFileChangeDomicile(String fileNameChangeDom, List<Map<String, Object>> list, String cif)
			throws IOException {
		FileSaver fileSaver = FileSaver.getInstance(bpidPath + fileNameChangeDom);

		for (Iterator<Map<String, Object>> iterator = list.listIterator(); iterator.hasNext();) {
			Map<String, Object> row = iterator.next();
			fileSaver.savetToFile(row.get(TIME) + "   " + row.get(ID) + "   " + row.get(CLAIMSINVESTMENTMARKETCOUNTRYID)
					+ "   " + row.get(PRODUCTID) + "   " + row.get(VALIDFROM) + "   " + row.get(VALIDTO) + "   "
					+ row.get(ORDERSTATUSID) + "   " + row.get(CANCELLATIONREASON) + "   " + row.get(TRCAGREEMENTID)
					+ "   " + row.get(COUNTRYCOMBINATIONID) + "   " + row.get(STARTDATE) + "   " + row.get(ENDDATE)
					+ "   " + row.get(TAXORDERNO) + "   " + row.get(TAXORDERNUMBER) + "   "
					+ row.get(PARENTRECLAIMORDERID) + "   " + row.get(SUBCIFNUMBER) + "   " + row.get(CUSTOMERACCOUNTID)
					+ "   " + row.get(RECLAIMCOUNTRYID));

		}
		fileSaver.savetToFile(
				"------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

		fileName = fileNameChangeDom;
	}

	public void setBpidPath(String bpidPath) {
		this.bpidPath = bpidPath;
	}

	public String getFileName() {
		return bpidPath + fileName;
	}

	public void exportToExcel(List<Map<String, Object>> list) throws IOException {

		List<String> headers = Arrays.asList(CIFNumber, Domicile, ID, ExternalId, ExternalUserPid, CUSTOMERAGGREGATEID,
				TypeId, CreatedOn, UpdatedOn, UpdatedBy, PayloadId, PartialActivationOrderPayloadId, StatusId);
		xslSheet.createHeader(headers);

		for (Iterator<Map<String, Object>> iterator = list.listIterator(); iterator.hasNext();) {
			Row xlsRow = xslSheet.getNewRow();
			Map<String, Object> row = iterator.next();

			for (int i = 0, j = headers.size(); i < j; i++) {
				xslSheet.setCellValue(xlsRow, i, row.get(headers.get(i)));

			}

		}
		xslSheet.setFileName(bpidPath + fileActivationErrors);
		xslSheet.createExcel();

	}

}
