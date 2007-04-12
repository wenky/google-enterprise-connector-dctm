package com.google.enterprise.connector.dctm;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SpiConstants;

import junit.framework.TestCase;

public class DctmQueryTraversalManagerTest extends TestCase {

	Session session = null;

	Connector connector = null;

	DctmQueryTraversalManager qtm = null;

	protected void setUp() throws Exception {
		super.setUp();
		qtm = null;
		Connector connector = new DctmConnector();
		((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
		((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
		((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
		((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
		((DctmConnector) connector)
				.setWebtop_server_url(DmInitialize.DM_WEBTOP_SERVER_URL);
		Session sess = (DctmSession) connector.login();
		qtm = (DctmQueryTraversalManager) sess.getQueryTraversalManager();
	}

	public void testStartTraversal() throws RepositoryException {
		ResultSet resultset = null;
		int counter = 0;

		qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_UNBOUNDED);
		resultset = qtm.startTraversal();
		Iterator iter = resultset.iterator();
		while (iter.hasNext()) {
			iter.next();
			counter++;
		}
		assertEquals(DmInitialize.DM_RETURN_TOP_UNBOUNDED, counter);
	}

	public void testMakeCheckpointQueryString() {
		String uuid = "090000018000e100";
		String statement = "";
		try {
			statement = qtm.makeCheckpointQueryString(uuid,
					"2007-01-02 13:58:10");
		} catch (RepositoryException re) {
			re.printStackTrace();
		}

		assertNotNull(statement);
		assertEquals(DmInitialize.DM_CHECKPOINT_QUERY_STRING, statement);

	}

	public void testExtractDocidFromCheckpoint() {
		String checkPoint = "{\"uuid\":\"090000018000e100\",\"lastModified\":\"2007-01-02 13:58:10\"}";
		String uuid = null;
		JSONObject jo = null;

		try {
			jo = new JSONObject(checkPoint);
		} catch (JSONException e) {
			throw new IllegalArgumentException(
					"checkPoint string does not parse as JSON: " + checkPoint);
		}

		uuid = qtm.extractDocidFromCheckpoint(jo, checkPoint);
		assertNotNull(uuid);
		assertEquals(uuid, "090000018000e100");
	}

	public void testExtractNativeDateFromCheckpoint() {

		String checkPoint = "{\"uuid\":\"090000018000e100\",\"lastModified\":\"2007-01-02 13:58:10\"}";
		JSONObject jo = null;
		String modifDate = null;

		try {
			jo = new JSONObject(checkPoint);
		} catch (JSONException e) {
			throw new IllegalArgumentException(
					"checkPoint string does not parse as JSON: " + checkPoint);
		}

		modifDate = qtm.extractNativeDateFromCheckpoint(jo, checkPoint);
		assertNotNull(modifDate);
		assertEquals(modifDate, "2007-01-02 13:58:10");

	}

	public void testIDfetchAndVerifyValueForCheckpoint()
			throws RepositoryException {
		DctmSysobjectPropertyMap pm = null;
		pm = new DctmSysobjectPropertyMap("0900000180010b17", qtm
				.getSessionManager(), qtm.getClientX());

		String uuid = qtm.fetchAndVerifyValueForCheckpoint(pm,
				SpiConstants.PROPNAME_DOCID).getString();

		assertEquals(uuid, "0900000180010b17");
	}

	public void testDatefetchAndVerifyValueForCheckpoint()
			throws RepositoryException, ParseException {
		DctmSysobjectPropertyMap pm = null;
		pm = new DctmSysobjectPropertyMap("0900000180010b17", qtm
				.getSessionManager(), qtm.getClientX());
		Calendar calDate = null;

		Calendar c = qtm.fetchAndVerifyValueForCheckpoint(pm,
				SpiConstants.PROPNAME_LASTMODIFY).getDate();

		calDate = DctmSysobjectValue.iso8601ToCalendar("2007-01-02 14:19:29");
		assertEquals(c.getTimeInMillis(), calDate.getTimeInMillis());
		assertEquals(c, calDate);
	}

	public void testCheckpoint() throws RepositoryException {

		String checkPoint = null;
		DctmSysobjectPropertyMap pm = null;
		pm = new DctmSysobjectPropertyMap("0900000180010b17", qtm
				.getSessionManager(), qtm.getClientX());
		checkPoint = qtm.checkpoint(pm);

		assertNotNull(checkPoint);
		assertEquals(
				"{\"uuid\":\"0900000180010b17\",\"lastModified\":\"2007-01-02 14:19:29.000\"}",
				checkPoint);
	}

	public void testResumeTraversal() throws RepositoryException {
		ResultSet resultSet = null;

		String checkPoint = "{\"uuid\":\"090000018000e100\",\"lastModified\":\"2007-01-02 14:19:29.000\"}";

		qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_BOUNDED);
		resultSet = qtm.resumeTraversal(checkPoint);

		int counter = 0;

		for (Iterator iter = resultSet.iterator(); iter.hasNext();) {
			iter.next();
			counter++;
		}

		assertEquals(DmInitialize.DM_RETURN_TOP_BOUNDED, counter);
	}

	public void testResumeTraversalWithSimilarDate() throws RepositoryException {
		ResultSet resultSet = null;

		String checkPoint = "{\"uuid\":\"090000018000015d\",\"lastModified\":\"2006-12-14 20:09:13.000\"}";

		qtm.setBatchHint(1);
		resultSet = qtm.resumeTraversal(checkPoint);

		DctmSysobjectIterator iter = (DctmSysobjectIterator) resultSet
				.iterator();
		while (iter.hasNext()) {
			DctmSysobjectPropertyMap map = (DctmSysobjectPropertyMap) iter
					.next();
			String docId = map.getProperty(SpiConstants.PROPNAME_DOCID)
					.getValue().getString();
			String expectedid = "090000018000015e";
			assertEquals(expectedid, docId);
			String modifyDate = DctmSysobjectValue.calendarToIso8601(map
					.getProperty(SpiConstants.PROPNAME_LASTMODIFY).getValue()
					.getDate());
			String expecterModifyDate = "2006-12-14 20:09:13.000";
			assertEquals(expecterModifyDate, modifyDate);
		}
	}

}
