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
package org.apache.samza.sql.operators.project;

import org.apache.samza.SamzaException;
import org.apache.samza.config.Config;
import org.apache.samza.sql.api.data.Relation;
import org.apache.samza.sql.api.data.Tuple;
import org.apache.samza.sql.api.expressions.Expression;
import org.apache.samza.sql.data.DataUtils;
import org.apache.samza.sql.data.IntermediateMessageTuple;
import org.apache.samza.sql.operators.factory.SimpleOperatorImpl;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.sql.SimpleMessageCollector;

public class ProjectOp extends SimpleOperatorImpl{

  private final ProjectSpec spec;

  public ProjectOp(ProjectSpec spec) {
    super(spec);
    this.spec = spec;
  }

  @Override
  protected void realRefresh(long timeNano, SimpleMessageCollector collector, TaskCoordinator coordinator) throws Exception {

  }

  @Override
  protected void realProcess(Relation rel, SimpleMessageCollector collector, TaskCoordinator coordinator) throws Exception {

  }

  @Override
  protected void realProcess(Tuple ituple, SimpleMessageCollector collector, TaskCoordinator coordinator) throws Exception {
    if(!DataUtils.isStruct(ituple.getMessage())){
      // Log to error topic.
      throw new SamzaException("Only messages of type struct is supported. Message type: " + ituple.getMessage().schema().getType());
    }

    Object[] inputValues = DataUtils.dataToObjectArray(ituple.getMessage(), spec.getInputType());
    Object[] results = new Object[spec.getOutputType().getFields().size()];
    Expression project = spec.getProjectExpression();
    project.execute(inputValues, results);

    collector.send(IntermediateMessageTuple.fromData(spec.getOutputType().from(results), false, spec.getOutputName(), ituple.getKey(), ituple.getCreateTimeNano(), ituple.getOffset()));
  }

  @Override
  public void init(Config config, TaskContext context) throws Exception {

  }
}
