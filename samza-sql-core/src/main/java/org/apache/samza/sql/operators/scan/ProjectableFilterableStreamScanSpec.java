/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.samza.sql.operators.scan;

import org.apache.samza.sql.api.data.EntityName;
import org.apache.samza.sql.api.data.Schema;
import org.apache.samza.sql.api.expressions.Expression;
import org.apache.samza.sql.api.operators.spec.OperatorSpec;
import org.apache.samza.sql.operators.factory.TypeAwareOperatorSpec;

public class ProjectableFilterableStreamScanSpec extends TypeAwareOperatorSpec implements OperatorSpec {

  public static final String OP_TYPE = "ProjectableFilterableStreamScan";

  /**
   * Filter condition
   */
  private Expression filterExpression;


  public ProjectableFilterableStreamScanSpec(EntityName input, EntityName output, Schema inputType, Schema outputType, Expression filterExpression) {
    super(genId(OP_TYPE), input, output, inputType, outputType);
    this.filterExpression = filterExpression;
  }

  public Expression getFilterExpression() {
    return filterExpression;
  }
}
