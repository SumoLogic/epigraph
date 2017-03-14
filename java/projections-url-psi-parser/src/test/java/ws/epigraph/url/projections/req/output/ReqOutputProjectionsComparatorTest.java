/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.url.projections.req.output;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.req.output.*;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static ws.epigraph.test.TestUtil.lines;
import static ws.epigraph.test.TestUtil.printReqOutputVarProjection;
import static ws.epigraph.url.projections.req.ReqTestUtil.parseOpOutputVarProjection;
import static ws.epigraph.url.projections.req.ReqTestUtil.parseReqOutputVarProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputProjectionsComparatorTest {
  // todo introduce separate projections-test module

  private final DataType dataType = new DataType(Person.type, Person.id);
  private final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      User2.type,
      UserId2.type,
      UserRecord2.type,
      UserRecord3.type,
      SubUser.type,
      SubUserId.type,
      SubUserRecord.type,
      epigraph.String.type
  );

  @Test
  public void testRec1() {
    OpOutputVarProjection op = parsePersonOpOutputVarProjection(
        "$rec = :`record`(firstName, lastName, bestFriend $rec)"
    );

    ReqOutputVarProjection vp1 = parseReqVarProjection(
        op,
        "$rec = :record( firstName, bestFriend:record( lastName,bestFriend $rec ) )"
    );

    ReqOutputVarProjection vp2 = parseReqVarProjection(
        op,
        "$rec = :record( lastName, bestFriend:record( firstName,bestFriend $rec ) )"
    );

    ReqOutputProjectionsComparator comparator = new ReqOutputProjectionsComparator(false, false);
    assertFalse(comparator.equals(vp1, vp2));

    final ReqOutputVarProjection vp1_1 =
        ((ReqOutputRecordModelProjection) vp1.tagProjection("record").projection()).fieldProjection("bestFriend")
            .fieldProjection()
            .varProjection();

    assertTrue(comparator.equals(vp1_1, vp2));
  }

  private @NotNull ReqOutputVarProjection parseReqVarProjection(@NotNull OpOutputVarProjection op, String s) {
    return parseReqOutputVarProjection(dataType, op, s, resolver).projection();
  }

  private @NotNull OpOutputVarProjection parsePersonOpOutputVarProjection(@NotNull String projectionString) {
    return parseOpOutputVarProjection(dataType, projectionString, resolver);
  }

  // todo more tests

}
