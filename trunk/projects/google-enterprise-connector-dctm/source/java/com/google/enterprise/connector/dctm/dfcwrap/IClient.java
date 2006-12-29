package com.google.enterprise.connector.dctm.dfcwrap;

public interface IClient{
	
	
	public ILocalClient getLocalClientEx();

	public String getSessionForUser(String userName);
	
	public IQuery getQuery();
	
	public ILoginInfo getLoginInfo();

	public ISession newSession(String string, ILoginInfo logInfo);

	public void authenticate(String docbaseName, ILoginInfo loginInfo);

	
	
}