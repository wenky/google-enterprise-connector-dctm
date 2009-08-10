package com.google.enterprise.connector.dctm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.AuthorizationResponse;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DctmAuthorizationManagerTest extends TestCase {

	public DctmAuthorizationManagerTest(String arg0) {
		super(arg0);
	}

	public final void testAuthorizeDocids() throws RepositoryException {

		AuthorizationManager authorizationManager;
		authorizationManager = null;
		Connector connector = new DctmConnector();

		((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
		((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
		((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
		((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
		((DctmConnector) connector)
				.setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
		((DctmConnector) connector).setIs_public("false");
		Session sess = (DctmSession) connector.login();
		authorizationManager = (DctmAuthorizationManager) sess
				.getAuthorizationManager();

		{
			String username = DmInitialize.DM_LOGIN_OK2;

			Map expectedResults = new HashMap();
			expectedResults.put(DmInitialize.DM_VSID1, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_VSID2, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_VSID3, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_VSID4, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_VSID5, Boolean.TRUE);
			testAuthorization((DctmAuthorizationManager) authorizationManager,
					expectedResults, username);
		}

		{
			String username = DmInitialize.DM_LOGIN_OK3;

			Map expectedResults = new HashMap();
			expectedResults.put(DmInitialize.DM_VSID1, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_VSID2, Boolean.FALSE);
			expectedResults.put(DmInitialize.DM_VSID3, Boolean.FALSE);
			expectedResults.put(DmInitialize.DM_VSID4, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_VSID5, Boolean.TRUE);
			testAuthorization((DctmAuthorizationManager) authorizationManager,
					expectedResults, username);
		}

		{
			String username = DmInitialize.DM_LOGIN_OK5;

			Map expectedResults = new HashMap();
			expectedResults.put(DmInitialize.DM_VSID1, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_VSID2, Boolean.FALSE);
			expectedResults.put(DmInitialize.DM_VSID3, Boolean.FALSE);
			expectedResults.put(DmInitialize.DM_VSID4, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_VSID5, Boolean.TRUE);
			testAuthorization((DctmAuthorizationManager) authorizationManager,
					expectedResults, username);
		}

	}

	private void testAuthorization(
			DctmAuthorizationManager authorizationManager, Map expectedResults,
			String username) throws RepositoryException {

		List docids = new LinkedList(expectedResults.keySet());

		assertNotNull(docids);
		List list = (List) authorizationManager.authorizeDocids(docids,
				new DctmAuthenticationIdentity(username, null));
		assertNotNull(list);
		for (Iterator i = list.iterator(); i.hasNext();) {
			AuthorizationResponse pm = (AuthorizationResponse) i.next();
			String uuid = pm.getDocid();
			boolean ok = pm.isValid();
			Boolean expected = (Boolean) expectedResults.get(uuid);
			Assert.assertEquals(username + " access to " + uuid, expected
					.booleanValue(), ok);
		}
	}

}