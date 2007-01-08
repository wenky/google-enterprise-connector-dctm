package com.google.enterprise.connector.dctm;

import junit.framework.TestCase;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

//import com.google.enterprise.connector.test.QueryTraversalUtil;

public class DctmQueryTraversalUtilCall extends TestCase {
	
	private final boolean DFC = true;
	private String user, password, client, docbase;

	public void testTraversal() {		
		if (DFC) {
			DctmInstantiator.isDFCavailable=true;
			user="user1";
			password="p@ssw0rd";
			client="com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmClient";
			docbase="gsadctm";
		} else {
			DctmInstantiator.isDFCavailable=false;
			user="mark";
			password="mark";
			client="com.google.enterprise.connector.dctm.dctmmockwrap.DctmMockClient";
			docbase="MockRepositoryEventLog7.txt";
		}

		Session session = null;
		Connector connector = null;
		QueryTraversalManager qtm = null;

		connector = new DctmConnector();

		
		/**
		 * Simulation of the setters used by Instance.xml
		 */
		((DctmConnector) connector).setLogin(user);
		((DctmConnector) connector).setPassword(password);
		((DctmConnector) connector).setDocbase(docbase);
		IClient cl = null;
		try {
			cl = (IClient) Class.forName(client).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((DctmConnector) connector).setClient(cl);
		/**
		 * End simulation
		 */
		
		
		try {
			session = (DctmSession) connector.login();
			qtm = (DctmQueryTraversalManager) session.getQueryTraversalManager();
			DctmQueryTraversalUtil.runTraversal(qtm, 15);

		} catch (LoginException le) {
			le.getMessage();
		} catch (RepositoryException re) {
			re.getMessage();
		}

	}
}