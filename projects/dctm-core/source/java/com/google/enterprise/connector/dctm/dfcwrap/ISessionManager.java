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

package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.RepositoryLoginException;

public interface ISessionManager {
  ISession getSession(String docbase) throws RepositoryLoginException,
      RepositoryException;

  ISession newSession(String docbase) throws RepositoryLoginException,
      RepositoryException;

  void setIdentity(String docbase, ILoginInfo identity)
      throws RepositoryLoginException;

  void release(ISession session);

  ILoginInfo getIdentity(String docbase);

  /** @deprecated This method is unused */
  @Deprecated
  boolean authenticate(String docbaseName);

  void clearIdentity(String docbase);
}
