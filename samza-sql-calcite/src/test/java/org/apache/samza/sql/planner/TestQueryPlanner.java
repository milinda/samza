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
package org.apache.samza.sql.planner;

import junit.framework.Assert;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelVisitor;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelConversionException;
import org.apache.calcite.tools.ValidationException;
import org.apache.calcite.util.ReflectUtil;
import org.apache.calcite.util.ReflectiveVisitDispatcher;
import org.apache.calcite.util.ReflectiveVisitor;
import org.apache.samza.sql.expr.RexToJavaCompiler;
import org.apache.samza.sql.schema.CalciteModelProcessor;
import org.apache.samza.sql.utils.SamzaAbstractRelVisitor;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

public class TestQueryPlanner {
  public static final String STREAM_SCHEMA = "{\n"
      + "       name: 'KAFKA',\n"
      + "       tables: [ {\n"
      + "         type: 'custom',\n"
      + "         name: 'ORDERS',\n"
      + "         stream: {\n"
      + "           stream: true\n"
      + "         },\n"
      + "         factory: '" + OrderStreamFactory.class.getName() + "'\n"
      + "       },\n"
      + "       {\n"
      + "         type: 'custom',\n"
      + "         name: 'FILTEREDORDERS',\n"
      + "         stream: {\n"
      + "            stream: true\n"
      + "         },\n"
      + "         factory: '" + OrderStreamFactory.class.getName() + "'\n"
      + "       },"
      + "       {\n"
      + "         type: 'custom',\n"
      + "         name: 'FILTEREDPROJECTEDORDERS',\n"
      + "         stream: {\n"
      + "           stream: true\n"
      + "         },\n"
      + "         factory: '" + ProjectedOrdersStreamFactory.class.getName() + "'\n"
      + "       }]\n"
      + " }\n";

  public static final String STREAM_MODEL = "{\n"
      + "  version: '1.0',\n"
      + "  defaultSchema: 'KAFKA',\n"
      + "   schemas: [\n"
      + STREAM_SCHEMA
      + "   ]\n"
      + "}";

  public static final String SIMPLE_PROJECT =
      "select stream productId, units fromData orders";

  public static final String SIMPLE_FILTER =
      "select stream * fromData orders where units > 5";

  public static final String SIMPLE_WINDOW_AGGREGATE =
      "SELECT STREAM rowtime,\n" +
          "  productId,\n" +
          "  units,\n" +
          "  SUM(units + 20) OVER (PARTITION BY productId ORDER BY rowtime RANGE INTERVAL '1' HOUR PRECEDING) unitsLastHour\n" +
          "FROM Orders";

  private QueryContext queryContext;

  private class TestQueryContext implements QueryContext {

    private final SchemaPlus defaultSchema;

    public TestQueryContext(SchemaPlus defaultSchema) {
      this.defaultSchema = defaultSchema;
    }

    @Override
    public SchemaPlus getDefaultSchema() {
      return defaultSchema;
    }

    @Override
    public SqlOperatorTable getSamzaOperatorTable() {
      return SqlStdOperatorTable.instance();
    }
  }

  @Before
  public void setUp() throws IOException, SQLException {
    SchemaPlus rootSchema = Frameworks.createRootSchema(true);
    queryContext = new TestQueryContext(
        new CalciteModelProcessor("inline:" + STREAM_MODEL, rootSchema).getDefaultSchema());
  }

  @Test
  public void testSimpleProject() throws ValidationException, RelConversionException {
    QueryPlanner planner = new QueryPlanner(queryContext);
    RelNode plan = planner.getPlan(SIMPLE_PROJECT);

    Assert.assertNotNull(plan);

    System.out.println(RelOptUtil.toString(plan));
  }

  @Test
  public void testSimpleFilter() throws ValidationException, RelConversionException {
    QueryPlanner planner = new QueryPlanner(queryContext);
    RelNode plan = planner.getPlan(SIMPLE_FILTER);

    Assert.assertNotNull(plan);

    System.out.println(RelOptUtil.toString(plan));
  }

  @Test
  public void testSimpleWindowAggregate() throws ValidationException, RelConversionException {
    QueryPlanner planner = new QueryPlanner(queryContext);
    RelNode plan = planner.getPlan(SIMPLE_WINDOW_AGGREGATE);

    Assert.assertNotNull(plan);

    System.out.println(RelOptUtil.toString(plan));
  }

  @Test
  public void testVisitor() throws ValidationException, RelConversionException {
    QueryPlanner planner = new QueryPlanner(queryContext);
    RelNode plan = planner.getPlan(SIMPLE_WINDOW_AGGREGATE);

    new ProjectVisitor(plan.getCluster().getRexBuilder()).visit(plan, 0, null);
  }

  public static class ProjectVisitor extends SamzaAbstractRelVisitor {
    private final RexToJavaCompiler rexToJavaCompiler;

    public ProjectVisitor(RexBuilder rexBuilder) {
      this.rexToJavaCompiler = new RexToJavaCompiler(rexBuilder);
    }

    public void visit(Project project) {
      rexToJavaCompiler.compile(project.getInputs(), project.getProjects());
    }
  }

}
