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
package org.apache.samza.sql.planner.logical;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexLiteral;
import org.apache.samza.sql.physical.PhysicalPlanCreator;
import org.apache.samza.sql.planner.common.SamzaWindowRelBase;

import java.util.List;

public class SamzaWindowRel extends SamzaWindowRelBase implements SamzaRel {
  public SamzaWindowRel(RelOptCluster cluster, RelTraitSet traits, RelNode child,
                        List<RexLiteral> constants, RelDataType rowType, List<Group> groups) {
    super(cluster, traits, child, constants, rowType, groups);
  }

  @Override
  public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
    return new SamzaWindowRel(getCluster(), traitSet, sole(inputs), constants, getRowType(), groups);
  }

  @Override
  public void physicalPlan(PhysicalPlanCreator physicalPlanCreator) {

  }

  @Override
  public <T> T accept(SamzaRelVisitor<T> visitor) {
    return null;
  }
}