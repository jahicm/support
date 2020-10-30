package org.report.java.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.report.java.interfaces.AppReportInterface;
import org.report.java.supports.AppCSInvest;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xpp.csinvest.support.JobProcessor;

public class Test1 {

	private static JobProcessor jobProcessor;
	private static JdbcTemplate jdbcTemplate;
	private static String jobName = "JOBNAME";
	private static AppReportInterface appReport;

	@SuppressWarnings("resource")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("spring-config-test.xml");
		jdbcTemplate = (JdbcTemplate) appContext.getBean("jdbcTemplateCSInvest");
		appReport = (AppReportInterface) appContext.getBean("appCSInvest");
		jobProcessor = (JobProcessor) appContext.getBean("jobProcessor");

	}


	public void testPrecompileTMWBObjects() {
		System.out.println("***********testPrecompileTMWBObjects***********");
		String jobNameTemp = "PrecompileTMWBObjects";
		Map<String, Object> row = new HashMap<String, Object>();
		row.put(jobName, jobNameTemp);
		JobProcessor.isJunit = true;
		jobProcessor.processListOfJobs(jdbcTemplate, row);
		String expected = "UPDATE YPXVWG.EXECUTIONMESSAGE SET APPROVERPID='F944380', APPROVALTIME=CURRENT_TIMESTAMP WHERE CREATIONTIME > SYSDATE- 10 AND APPROVERPID IS NULL AND JOBNAME IN ('PrecompileTMWBObjects')";
		Assertions.assertEquals(expected, jobProcessor.getSqlTemp());
		Assertions.assertNotNull(jobProcessor.getSqlUpdate());
	}


	public void testCheckExecutionMessageIntraday() {

		System.out.println("***********testCheckExecutionMessageIntraday***********");
		String jobNameTemp = "CheckExecutionMessageIntraday";
		Map<String, Object> row = new HashMap<String, Object>();
		row.put(jobName, jobNameTemp);
		JobProcessor.isJunit = true;
		jobProcessor.processListOfJobs(jdbcTemplate, row);
		String expected = "UPDATE YPXVWG.EXECUTIONMESSAGE SET APPROVERPID='F944380', APPROVALTIME=CURRENT_TIMESTAMP WHERE CREATIONTIME > SYSDATE- 10 AND APPROVERPID IS NULL AND JOBNAME IN ('CheckExecutionMessageIntraday')";
		Assertions.assertEquals(expected, jobProcessor.getSqlTemp());
		Assertions.assertNotNull(jobProcessor.getSqlUpdate());
	}


	public void testProcessEventsInvestmentFee() {
		System.out.println("***********testProcessEventsInvestmentFee***********");
		String jobNameTemp = "ProcessEventsInvestmentFee";
		Map<String, Object> row = new HashMap<String, Object>();
		row.put(jobName, jobNameTemp);
		JobProcessor.isJunit = true;
		jobProcessor.processListOfJobs(jdbcTemplate, row);
		String expected = "UPDATE YPXVWG.EXECUTIONMESSAGE SET APPROVERPID='F944380', APPROVALTIME=CURRENT_TIMESTAMP WHERE CREATIONTIME > SYSDATE- 10 AND APPROVERPID IS NULL AND JOBNAME IN ('ProcessEventsInvestmentFee')";
		Assertions.assertEquals(expected, jobProcessor.getSqlTemp());
		Assertions.assertNotNull(jobProcessor.getSqlUpdate());
	}


	public void testCheckHeartBeat() {
		System.out.println("***********testCheckHeartBeat***********");
		String jobNameTemp = "CheckHeartBeat";
		Map<String, Object> row = new HashMap<String, Object>();
		row.put(jobName, jobNameTemp);
		JobProcessor.isJunit = true;
		jobProcessor.processListOfJobs(jdbcTemplate, row);
		String expected = "UPDATE YPXVWG.EXECUTIONMESSAGE SET APPROVERPID='F944380', APPROVALTIME=CURRENT_TIMESTAMP WHERE CREATIONTIME > SYSDATE- 10 AND APPROVERPID IS NULL AND JOBNAME IN ('CheckHeartBeat')";
		Assertions.assertEquals(expected, jobProcessor.getSqlTemp());
		Assertions.assertNotNull(jobProcessor.getSqlUpdate());
	}


	public void testCheckAssetValueOrderStatus() {
		System.out.println("***********testCheckAssetValueOrderStatus***********");
		String jobNameTemp = "CheckAssetValueOrderStatus";
		Map<String, Object> row = new HashMap<String, Object>();
		row.put(jobName, jobNameTemp);
		JobProcessor.isJunit = true;
		jobProcessor.processListOfJobs(jdbcTemplate, row);
		Assertions.assertEquals("Check with Marco", jobProcessor.getSqlTemp());

	}


	public void testProcessDebitEncashmentOrderResponse() {
		System.out.println("***********testProcessDebitEncashmentOrderResponse***********");
		String jobNameTemp = "ProcessDebitEncashmentOrderResponse";
		Map<String, Object> row = new HashMap<String, Object>();
		row.put(jobName, jobNameTemp);
		row.put("BUSINESSKEY", "20190513-e91f-4465-b5de-30f912ae9665");
		row.put("ID", "b74ce217-16df-41b8-b05f-10326f627abf");
		row.put("ERRORMESSAGE", "Processing Failure XPPM_ERR");
		JobProcessor.isJunit = true;
		jobProcessor.processListOfJobs(jdbcTemplate, row);
		String expected = "UPDATE YPXVWG.ENCASHMENTORDER EO SET COMMENTARY = 'Business Informed', LASTEDITOR = 'F944380' WHERE ID = '20190513-e91f-4465-b5de-30f912ae9665'";
		Assertions.assertEquals(expected, jobProcessor.getSqlTemp());

	}


	public void testProcessDebitEncashmentOrderResponse2() {
		System.out.println("***********testProcessDebitEncashmentOrderResponse2***********");
		String jobNameTemp = "ProcessDebitEncashmentOrderResponse";
		Map<String, Object> row = new HashMap<String, Object>();
		row.put(jobName, jobNameTemp);
		row.put("BUSINESSKEY", "20190605-40fe-405f-99be-ffc2e0c5718a");
		row.put("ID", "0817220f-fae8-4b5b-84f3-dd9d4ac14ff1");
		row.put("ERRORMESSAGE", "Processing Failure XPPM_ERR");
		JobProcessor.isJunit = true;
		jobProcessor.processListOfJobs(jdbcTemplate, row);
		String expected = "UPDATE YPXVWG.ENCASHMENTORDER EO SET COMMENTARY = 'Business Informed', LASTEDITOR = 'F944380' WHERE ID = '20190605-40fe-405f-99be-ffc2e0c5718a'";
		Assertions.assertEquals(expected, jobProcessor.getSqlTemp());

	}


	public void testProcessDebitEncashmentOrderResponseTotalAmountZERO() {
		System.out.println("***********testProcessDebitEncashmentOrderResponseZERO***********");
		String jobNameTemp = "ProcessDebitEncashmentOrderResponse";
		Map<String, Object> row = new HashMap<String, Object>();
		row.put(jobName, jobNameTemp);
		row.put("BUSINESSKEY", "20180130-6ddb-451c-adb6-8e69f922401b");
		JobProcessor.isJunit = true;
		jobProcessor.processListOfJobs(jdbcTemplate, row);
		String sqlTotalAmount = jobProcessor.getSqlTotalAmount().replace("ENCASHMENTORDERID",
				"'20180130-6ddb-451c-adb6-8e69f922401b'");
		double totalAmount = jdbcTemplate.queryForObject(sqlTotalAmount, Double.class);
		Assertions.assertEquals(0, totalAmount);

	}


	public void testProcessDebitEncashmentOrderResponseZERO_UPDATE() {
		System.out.println("***********testProcessDebitEncashmentOrderResponseZERO_UPDATE***********");
		String jobNameTemp = "ProcessDebitEncashmentOrderResponse";
		Map<String, Object> row = new HashMap<String, Object>();
		row.put(jobName, jobNameTemp);
		row.put("BUSINESSKEY", "20180130-6ddb-451c-adb6-8e69f922401b");
		JobProcessor.isJunit = true;
		jobProcessor.processListOfJobs(jdbcTemplate, row);
		String expected = "UPDATE YPXVWG.ENCASHMENTORDER EO SET COMMENTARY = 'Total amount is 0, no further action', LASTEDITOR = 'F944380' WHERE ID = '20180130-6ddb-451c-adb6-8e69f922401b'";
		Assertions.assertEquals(expected, jobProcessor.getSqlTemp());

	}


	public void testcalculatePriceComponentsAssetValueCalc() {
		System.out.println("***********testcalculatePriceComponentsAssetValueCalc***********");
		String jobNameTemp = "calculatePriceComponentsAssetValueCalc";
		JobProcessor.isJunit = true;
		Map<String, Object> row = new HashMap<String, Object>();
		row.put(jobName, jobNameTemp);
		JobProcessor.isJunit = true;
		jobProcessor.processListOfJobs(jdbcTemplate, row);
		String expected = "UPDATE YPXVWG.EXECUTIONMESSAGE SET APPROVERPID='F944380', APPROVALTIME=CURRENT_TIMESTAMP WHERE CREATIONTIME > SYSDATE- 10 AND APPROVERPID IS NULL AND JOBNAME IN ('calculatePriceComponentsAssetValueCalc')";
		Assertions.assertEquals(expected, jobProcessor.getSqlTemp());
		Assertions.assertNotNull(jobProcessor.getSqlUpdate());

	}


	public void testProcessRatedSecuritizedPosition() {
		System.out.println("***********testProcessRatedSecuritizedPosition***********");
		String jobNameTemp = "ProcessRatedSecuritizedPosition";
		Map<String, Object> row = new HashMap<String, Object>();
		row.put(jobName, jobNameTemp);
		JobProcessor.isJunit = true;
		jobProcessor.processListOfJobs(jdbcTemplate, row);
		String expected = "UPDATE YPXVWG.EXECUTIONMESSAGE SET APPROVERPID='F944380', APPROVALTIME=CURRENT_TIMESTAMP WHERE CREATIONTIME > SYSDATE- 10 AND APPROVERPID IS NULL AND JOBNAME IN ('ProcessRatedSecuritizedPosition')";
		Assertions.assertEquals(expected, jobProcessor.getSqlTemp());
		Assertions.assertNotNull(jobProcessor.getSqlUpdate());

	}


	public void testProcessEncashmentOrder() {
		System.out.println("***********testProcessEncashmentOrder***********");
		String jobNameTemp = "ProcessEncashmentOrder";
		Map<String, Object> row = new HashMap<String, Object>();
		row.put(jobName, jobNameTemp);
		JobProcessor.isJunit = true;
		jobProcessor.processListOfJobs(jdbcTemplate, row);
		String expected = "UPDATE YPXVWG.EXECUTIONMESSAGE SET APPROVERPID='F944380', APPROVALTIME=CURRENT_TIMESTAMP WHERE CREATIONTIME > SYSDATE- 10 AND APPROVERPID IS NULL AND JOBNAME IN ('ProcessEncashmentOrder')";
		Assertions.assertEquals(expected, jobProcessor.getSqlTemp());
		Assertions.assertNotNull(jobProcessor.getSqlUpdate());

	}


	public void testListUpdates() throws Exception {
		System.out.println("***********testListUpdates***********");
		List<Object> list = new ArrayList<Object>();
		list.add("'81b0d42d-e07d-4693-93a8-277819459e9d'");
		list.add("'297c3b0d-4bd1-4224-b76f-017f48d228bc'");
		list.add("'3c007710-89c3-432e-883c-5f7ad603a2b3'");
		Method method = AppCSInvest.class.getDeclaredMethod("listUpdates", List.class);
		method.setAccessible(true);

		method.invoke(appReport, list);
	}

}
