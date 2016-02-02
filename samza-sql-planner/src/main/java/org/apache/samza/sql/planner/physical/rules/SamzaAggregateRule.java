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
package org.apache.samza.sql.planner.physical.rules;

import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.logical.LogicalAggregate;
import org.apache.samza.sql.planner.physical.SamzaAggregateRel;
import org.apache.samza.sql.planner.physical.SamzaLogicalConvention;

public class SamzaAggregateRule extends ConverterRule {
  public static final RelOptRule INSTANCE = new SamzaAggregateRule();

  private SamzaAggregateRule() {
    super(LogicalAggregate.class, Convention.NONE, SamzaLogicalConvention.INSTANCE, "SamzaAggregateRule");
  }

  @Override
  public RelNode convert(RelNode rel) {
    final LogicalAggregate aggregate = (LogicalAggregate)rel;
    final RelNode input = aggregate.getInput();

    return new SamzaAggregateRel(aggregate.getCluster(),
        aggregate.getTraitSet().replace(SamzaLogicalConvention.INSTANCE),
        convert(input, input.getTraitSet().replace(SamzaLogicalConvention.INSTANCE)),
        aggregate.indicator, aggregate.getGroupSet(), aggregate.getGroupSets(), aggregate.getAggCallList());
  }
}
