// Copyright (C) 2006-2009 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.dctm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;

import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.AuthenticationIdentity;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.AuthorizationResponse;

public class DctmAuthorizationManager implements AuthorizationManager {
  private static final String QUERY_STRING =
      "select for read i_chronicle_id from dm_sysobject "
      + "where i_chronicle_id in (";

  private final IClientX clientX;

  private final ISessionManager sessionManager;

  private final String docbase;
  
  private static Logger logger =
      Logger.getLogger(DctmAuthorizationManager.class.getName());

  public DctmAuthorizationManager(IClientX clientX,
      ISessionManager sessionManager, String docbase) {
    this.clientX = clientX;
    this.sessionManager = sessionManager;
    this.docbase = docbase;
  }

  public Collection<AuthorizationResponse> authorizeDocids(
      Collection<String> docids, AuthenticationIdentity identity)
      throws RepositoryException {
    String username = getCanonicalUsername(identity);
    logger.info("authorisation for: " + username + "; docbase: " + docbase);

    IQuery query = buildQuery(docids);

    List<AuthorizationResponse> authorized;
    ISession sessionUser = getSessionUser(username);
    try {
      authorized = getAuthorizedDocids(docids, query, sessionUser);
    } finally {
      sessionUser.getSessionManager().release(sessionUser);
      logger.finest("user session released");
    }
    return authorized;
  }

  private String getCanonicalUsername(AuthenticationIdentity identity) {
    String username = identity.getUsername();
    logger.fine("username: " + username);

    /// Makes the connector handle the patterns username@domain,
    /// domain\\username and username.
    int index = username.indexOf('@');
    if (index != -1) {
      username = username.substring(0, index);
      logger.fine("username contains @ and is now: " + username);
    }
    index = username.indexOf('\\');
    if (index != -1) {
      username = username.substring(index + 1);
      logger.fine("username contains \\ and is now: " + username);
    }

    return username;
  }

  private IQuery buildQuery(Collection<String> docidList) {
    StringBuilder queryString = new StringBuilder();
    queryString.append(QUERY_STRING);
    for (String docid : docidList) {
      queryString.append("'");
      queryString.append(docid);
      queryString.append("',");
    }
    queryString.setCharAt(queryString.length() - 1, ')');

    IQuery query = clientX.getQuery();
    logger.fine("dql: " + queryString);
    query.setDQL(queryString.toString());
    return query;
  }

  /**
   * Gets a session for the given user. The caller owns the session.
   *
   * @param username a user name
   * @return a session for the given user
   */
  private ISession getSessionUser(String username) throws RepositoryException {
    // Login tickets fail for superusers if restrict_su_ticket_login
    // is set to T in the server config object. This code at least
    // allows the configured superuser to perform searches.
    ISession sessionUser;
    String currentUsername = sessionManager.getIdentity(docbase).getUser();
    ISession session = sessionManager.getSession(docbase);
    if (username.equals(currentUsername)) {
      sessionUser = session;
    } else {
      String ticket;
      try {
        ticket = session.getLoginTicketEx(username, "docbase", 0, false, null);
      } finally {
        sessionManager.release(session);
      }

      ISessionManager sessionManagerUser =
          clientX.getLocalClient().newSessionManager();
      ILoginInfo loginInfo = clientX.getLoginInfo();
      loginInfo.setUser(username);
      loginInfo.setPassword(ticket);
      sessionManagerUser.setIdentity(docbase, loginInfo);

      sessionUser = sessionManagerUser.getSession(docbase);
    }
    return sessionUser;
  }

  private List<AuthorizationResponse> getAuthorizedDocids(
      Collection<String> docids, IQuery query, ISession sessionUser)
      throws RepositoryException {
    List<AuthorizationResponse> authorized;
    ICollection collec = query.execute(sessionUser, IQuery.READ_QUERY);
    try {
      ArrayList<String> object_id = new ArrayList<String>(docids.size());
      while (collec.next()) {
        object_id.add(collec.getString("i_chronicle_id"));
      }
      authorized = new ArrayList<AuthorizationResponse>(docids.size());
      for (String id : docids) {
        boolean isAuthorized = object_id.contains(id);
        logger.info("id " + id + " hasRight? " + isAuthorized);
        authorized.add(new AuthorizationResponse(isAuthorized, id));
      }
    } finally {
      collec.close();
      logger.finest("after collec.close");
    }
    return authorized;
  }
}
