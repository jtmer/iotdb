/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.db.queryengine.plan.statement.metadata.view;

import org.apache.iotdb.common.rpc.thrift.TSStatus;
import org.apache.iotdb.commons.auth.entity.PrivilegeType;
import org.apache.iotdb.commons.path.PartialPath;
import org.apache.iotdb.db.auth.AuthorityChecker;
import org.apache.iotdb.db.queryengine.plan.analyze.QueryType;
import org.apache.iotdb.db.queryengine.plan.statement.IConfigStatement;
import org.apache.iotdb.db.queryengine.plan.statement.Statement;
import org.apache.iotdb.db.queryengine.plan.statement.StatementType;
import org.apache.iotdb.db.queryengine.plan.statement.StatementVisitor;
import org.apache.iotdb.rpc.TSStatusCode;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;

public class RenameLogicalViewStatement extends Statement implements IConfigStatement {

  private PartialPath oldName;

  private PartialPath newName;

  public RenameLogicalViewStatement() {
    super();
    this.statementType = StatementType.RENAME_LOGICAL_VIEW;
  }

  public PartialPath getOldName() {
    return oldName;
  }

  public void setOldName(PartialPath oldName) {
    this.oldName = oldName;
  }

  public PartialPath getNewName() {
    return newName;
  }

  public void setNewName(PartialPath newName) {
    this.newName = newName;
  }

  @Override
  public List<PartialPath> getPaths() {
    return Arrays.asList(oldName, newName);
  }

  @Override
  public TSStatus checkPermissionBeforeProcess(String userName) {
    if (AuthorityChecker.SUPER_USER.equals(userName)) {
      return new TSStatus(TSStatusCode.SUCCESS_STATUS.getStatusCode());
    }
    List<PartialPath> checkedPaths = ImmutableList.of(oldName, newName);
    return AuthorityChecker.getTSStatus(
        AuthorityChecker.checkFullPathOrPatternListPermission(
            userName, checkedPaths, PrivilegeType.WRITE_SCHEMA),
        checkedPaths,
        PrivilegeType.WRITE_SCHEMA);
  }

  @Override
  public <R, C> R accept(StatementVisitor<R, C> visitor, C context) {
    return visitor.visitRenameLogicalView(this, context);
  }

  @Override
  public QueryType getQueryType() {
    return QueryType.WRITE;
  }
}
