package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.util.Hashtable;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILocalClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.mock.MockRepository;
import com.google.enterprise.connector.mock.MockRepositoryEventList;
import com.google.enterprise.connector.mock.jcr.MockJcrRepository;

public class DctmMockClient implements IClient, ILocalClient {

	
	private MockRepository mockRep;
	private MockRepositoryEventList mockRepEL;

	private DctmMockQuery query;
	
	private ILoginInfo iLI;
	
	private ISession session;
	
	public DctmMockClient(){
		this.mockRep = null;
		this.query = null;
	}
	
	DctmMockClient(MockRepository mock, DctmMockQuery query){
		this.mockRep = mock;
		this.query = query;
	}
	
	public ILocalClient getLocalClientEx(){
		return this;
	}
	
	public ISessionManager newSessionManager(){
		return new DctmMockSessionManager();
	}

	
	public IQuery getQuery() {
		if (query!=null) return query;
		else return new DctmMockQuery();
	}

	
	

	public void authenticate(String docbaseName, ILoginInfo loginInfo) {
		// TODO Auto-generated method stub
		
	}

	public ISession newSession(String string, ILoginInfo logInfo) {
		
		return null;
	}

	

	public ISession findSession(String dfcSessionId) {
		return this.session;
	}

	public MockRepository getMockRep() {
		return mockRep;
	}

	public String getSessionForUser(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setMockRep(MockRepository mockRep) {
		this.mockRep = mockRep;
	}


	public void setQuery(DctmMockQuery query) {
		this.query = query;
	}

	public ILoginInfo getLoginInfo() {
		if (iLI!=null)return iLI;
		else return new DctmMockLoginInfo();
	}

	public IId getId(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	public ISession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSession(ISession iS) {
		this.session = iS;
	}

}
