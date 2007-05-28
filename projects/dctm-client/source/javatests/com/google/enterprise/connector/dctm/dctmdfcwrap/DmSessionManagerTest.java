package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmSessionManagerTest extends TestCase {

	ISessionManager sessionManager;

	ILoginInfo loginInfo;

	String user = DmInitialize.DM_LOGIN_OK4;

	String password = DmInitialize.DM_PWD_OK4;

	String docbase = DmInitialize.DM_DOCBASE;

	private String userKO = DmInitialize.DM_LOGIN_KO;

	private String pwdKO = DmInitialize.DM_PWD_KO;

	public void setUp() throws Exception {

		super.setUp();

		IClientX dctmClientX;

		IClient localClient = null;

		dctmClientX = new DmClientX();

		localClient = dctmClientX.getLocalClient();

		sessionManager = localClient.newSessionManager();
		loginInfo = dctmClientX.getLoginInfo();
		loginInfo.setUser(user);

		loginInfo.setPassword(password);

		sessionManager.setIdentity(docbase, loginInfo);

	}

	public void testNewSession() throws RepositoryLoginException, RepositoryException {

		ISession session = null;
		try {
			session = sessionManager.newSession(docbase);
			Assert.assertNotNull(session);
			Assert.assertTrue(session instanceof DmSession);
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}
	}

	public void testAuthenticateOK() throws RepositoryLoginException {
		boolean rep = false;

		rep = sessionManager.authenticate(docbase);

		Assert.assertTrue(rep);

		sessionManager.clearIdentity(docbase);
		loginInfo.setUser(DmInitialize.DM_LOGIN_OK2);
		loginInfo.setPassword(DmInitialize.DM_PWD_OK2);

		sessionManager.setIdentity(docbase, loginInfo);
		rep = sessionManager.authenticate(docbase);

		Assert.assertTrue(rep);
	}

	public void testAuthenticateKO() throws RepositoryLoginException {
		boolean rep = false;

		rep = sessionManager.authenticate(docbase);

		Assert.assertTrue(rep);

		sessionManager.clearIdentity(docbase);

		loginInfo.setUser(userKO);

		loginInfo.setPassword(pwdKO);

		sessionManager.setIdentity(docbase, loginInfo);

		rep = sessionManager.authenticate(docbase);

		Assert.assertFalse(rep);
	}

	public void testClearIdentity() throws RepositoryLoginException {
		sessionManager.clearIdentity(docbase);
		ILoginInfo logInfo = sessionManager.getIdentity(docbase);
		Assert.assertNull(((DmLoginInfo) logInfo).getIdfLoginInfo());
	}

}
